import tensorflow as tf
from tensorflow.keras.preprocessing import image
from preprocess import preprocess_image
import numpy as np

# Cargar el modelo entrenado
model = tf.keras.models.load_model('modelo_entrenado.h5')

# Función para analizar una imagen
def analizar_imagen(ruta_imagen):
    img_array = preprocess_image(ruta_imagen)
    # Realizar la predicción
    predicciones = model.predict(img_array)
    clase_predicha = np.argmax(predicciones, axis=1)  # Obtener la clase con mayor probabilidad
    
    return clase_predicha

# Probar el análisis de una imagen
ruta_imagen = 'ruta/a/tu/imagen.jpg'
clase = analizar_imagen(ruta_imagen)
print(f'La clase predicha es: {clase}')
