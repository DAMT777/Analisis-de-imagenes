import io
from rembg import remove, new_session, session_factory
import cv2
from PIL import Image
import numpy as np
import os

os.environ["ORT_LOG_LEVEL"] = "ERROR"
import warnings
warnings.filterwarnings("ignore")


def preprocess_image(image_path, solo_ojo=False):
    if not solo_ojo:
        # ...código actual para quitar fondo...
        try:
            session = new_session("cuda")
            _ = session_factory("cuda")
        except Exception:
            session = new_session("cpu")
        with open(image_path, "rb") as f:
            image_data = f.read()
        image_no_bg = remove(image_data, session=session)
        image = Image.open(io.BytesIO(image_no_bg)).convert("RGB")
    else:
        # Solo cargar la imagen sin quitar fondo
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

    # Si no es solo ojo, segmenta el cuerpo usando la máscara de rembg
    body_box = None
    if not solo_ojo:
        mask_img = Image.open(io.BytesIO(image_no_bg)).convert("RGBA")
        mask = np.array(mask_img)[:, :, 3]
        _, mask_bin = cv2.threshold(mask, 10, 255, cv2.THRESH_BINARY)
        contours, _ = cv2.findContours(mask_bin, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        if contours:
            largest = max(contours, key=cv2.contourArea)
            x, y, w, h = cv2.boundingRect(largest)
            body_box = (x, y, w, h)

    # Dibuja los cuadros
    for (x, y, w, h) in eyes:
        cv2.rectangle(image_cv, (x, y), (x+w, y+h), (255, 0, 0), 2)  # Azul para ojo
    if body_box is not None:
        x, y, w, h = body_box
        cv2.rectangle(image_cv, (x, y), (x+w, y+h), (0, 255, 0), 2)  # Verde para cuerpo

    # Guarda la imagen segmentada
    Image.fromarray(image_cv).save(seg_path)

    # Preprocesado para el modelo (opcional, si lo necesitas)
    image_resized = cv2.resize(image_cv, (224, 224))
    image_array = image_resized / 255.0
    image_array = np.expand_dims(image_array, axis=0)

    # Devuelve solo la ruta de la imagen segmentada
    return os.path.abspath(seg_path)

def preprocess_image_batch(image_paths, solo_ojo=False):
    seg_paths = []
    batch_imgs = []
    for image_path in image_paths:
        seg_path = preprocess_image(image_path, solo_ojo=solo_ojo)
        seg_paths.append(seg_path)
        # Prepara la imagen para el modelo
        image_cv = cv2.imread(seg_path)
        image_resized = cv2.resize(image_cv, (224, 224))
        image_array = image_resized / 255.0
        batch_imgs.append(image_array)
    return seg_paths, batch_imgs