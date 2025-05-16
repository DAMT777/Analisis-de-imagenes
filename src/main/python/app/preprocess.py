import io
from rembg import remove
import cv2
from PIL import Image
import numpy as np

def preprocess_image(image_path):
    # Eliminar el fondo
    with open(image_path, "rb") as f:
        image_data = f.read()
    image_no_bg = remove(image_data)
    image = Image.open(io.BytesIO(image_no_bg)).convert("RGB")
    
    # Guardar la imagen sin fondo para pruebas
    image.save("./temp/image_no_bg.png")
    
    # Convertir a formato OpenCV
    image_cv = np.array(image)
    gray = cv2.cvtColor(image_cv, cv2.COLOR_RGB2GRAY)
    
    # Detectar ojos (usando un clasificador preentrenado de OpenCV)
    eye_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_eye.xml')
    eyes = eye_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)
    
    # Dibujar contornos de los ojos (opcional)
    for (x, y, w, h) in eyes:
        cv2.rectangle(image_cv, (x, y), (x+w, y+h), (255, 0, 0), 2)
    
    # Mostrar la imagen preprocesada
    
    # Redimensionar y normalizar
    image_resized = cv2.resize(image_cv, (256, 256))
    image_array = image_resized / 255.0
    image_array = np.expand_dims(image_array, axis=0)
    
    return image_array

preprocess_image("C:/Users/jesus/Pictures/cachamas/pez/1744934699463.jpg")