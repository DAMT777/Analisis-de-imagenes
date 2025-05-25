from preprocess import preprocess_image, preprocess_image_batch
import numpy as np
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  # Solo muestra warnings y errores (no info)
import tensorflow as tf
from tensorflow.keras.preprocessing import image

# Cargar el modelo entrenado
model = tf.keras.models.load_model('../modelo_entrenado.h5')

# Procesa una sola imagen
def analyze_image(ruta_imagen, solo_ojo=False):
    seg_path = preprocess_image(ruta_imagen, solo_ojo=solo_ojo)
    img = image.load_img(seg_path, target_size=(224, 224))
    img_array = image.img_to_array(img) / 255.0
    img_array = np.expand_dims(img_array, axis=0)
    predicciones = model.predict(img_array)
    ojos_score = float(predicciones[0][0])
    piel_score = float(predicciones[0][1])
    return {
        "calificacion_ojos": round(ojos_score * 5, 2),
        "calificacion_piel": round(piel_score * 5, 2),
        "processed_image_path": seg_path
    }

# Procesa un batch de imágenes
def analyze_image_batch(rutas_imagen, solo_ojo=False):
    seg_paths, batch_imgs = preprocess_image_batch(rutas_imagen, solo_ojo=solo_ojo)
    batch_imgs = np.array(batch_imgs)
    predicciones = model.predict(batch_imgs)
    results = []
    for i, pred in enumerate(predicciones):
        ojos_score = float(pred[0])
        piel_score = float(pred[1])
        results.append({
            "image": rutas_imagen[i],
            "result": {
                "calificacion_ojos": ojos_score * 5,
                "calificacion_piel": piel_score * 5,
                "processed_image_path": seg_paths[i]
            }
        })
    return results