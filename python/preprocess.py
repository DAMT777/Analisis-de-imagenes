import io
import os
import cv2
import numpy as np
from PIL import Image
from rembg import remove, new_session
import onnxruntime as ort
import warnings
import time
import tensorflow as tf
import subprocess

os.environ["ORT_LOG_LEVEL"] = "ERROR"
warnings.filterwarnings("ignore")




# === CONFIGURAR LA SESIÓN ===
def obtener_sesion_rembg():
    providers = ort.get_available_providers()
    print("Proveedores ONNX disponibles:")
    for idx, prov in enumerate(providers):
        print(f"  [{idx}] {prov}")
    if "TensorrtExecutionProvider" in providers:
        print("¡Hardware compatible con TensorRT detectado! Usando TensorrtExecutionProvider para sesion CNN")
        return new_session("tensorrt")
    elif "CUDAExecutionProvider" in providers:
        print("¡Hardware compatible con CUDA detectado! Usando CUDAExecutionProvider para sesion CNN")
        return new_session("cuda")
    else:
        print("Se usara CPUExecutionProvider para sesion CNN")
        return new_session("cpu")












# Crear la sesión global una vez
t0 = time.time()
print("===========================")
print("Escaneando hardware...")

rembg_session = obtener_sesion_rembg()
print("Sesión CNN creada")
print(f"Tiempo en crear la sesión: {time.time() - t0:.2f} segundos")
print("Inicialiazando API...")
print("==========================")













def preprocess_image(image_path, solo_ojo=False):
    if not solo_ojo:
        try:
            with open(image_path, "rb") as f:
                image_data = f.read()
            image_no_bg = remove(image_data, session=rembg_session)
            image = Image.open(io.BytesIO(image_no_bg)).convert("RGB")
        except Exception as e:
            print(f"Error al remover fondo: {e}")
            return None
    else:
        image = Image.open(image_path).convert("RGB")
    
    temp_dir = "./temp"
    os.makedirs(temp_dir, exist_ok=True)
    nombre_original = os.path.basename(image_path)
    seg_filename = f"seg_{nombre_original}"
    seg_path = os.path.join(temp_dir, seg_filename)

    image_cv = np.array(image)
    gray = cv2.cvtColor(image_cv, cv2.COLOR_RGB2GRAY)
    eye_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_eye.xml')
    eyes = eye_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)

    body_box = None
    if not solo_ojo:
        try:
            mask_img = Image.open(io.BytesIO(image_no_bg)).convert("RGBA")
            mask = np.array(mask_img)[:, :, 3]
            _, mask_bin = cv2.threshold(mask, 10, 255, cv2.THRESH_BINARY)
            contours, _ = cv2.findContours(mask_bin, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
            if contours:
                largest = max(contours, key=cv2.contourArea)
                x, y, w, h = cv2.boundingRect(largest)
                body_box = (x, y, w, h)
        except Exception as e:
            print(f"Error al detectar cuerpo: {e}")

    for (x, y, w, h) in eyes:
        cv2.rectangle(image_cv, (x, y), (x+w, y+h), (255, 0, 0), 2)  # Azul para ojos
    if body_box is not None:
        x, y, w, h = body_box
        cv2.rectangle(image_cv, (x, y), (x+w, y+h), (0, 255, 0), 2)  # Verde para cuerpo

    Image.fromarray(image_cv).save(seg_path)

    image_resized = cv2.resize(image_cv, (224, 224))
    image_array = image_resized / 255.0
    image_array = np.expand_dims(image_array, axis=0)

    return os.path.abspath(seg_path)














def preprocess_image_batch(image_paths, solo_ojo=False):
    seg_paths = []
    batch_imgs = []
    for image_path in image_paths:
        seg_path = preprocess_image(image_path, solo_ojo=solo_ojo)
        if seg_path is not None:
            seg_paths.append(seg_path)
            image_cv = cv2.imread(seg_path)
            image_resized = cv2.resize(image_cv, (224, 224))
            image_array = image_resized / 255.0
            batch_imgs.append(image_array)
    return seg_paths, batch_imgs

