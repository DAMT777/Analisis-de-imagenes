import time
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from analyze import analyze_image, analyze_image_batch
from pydantic import BaseModel
from PIL import Image
import os
import glob
import sys
from tensorflow.keras.models import load_model
import numpy as np
from fish_classifier import is_fish_image  # Asegúrate de importar la función

# Cargar el modelo de clasificación de imágenes
model = load_model('fish_binary_classifier.h5')

# Función para verificar si una imagen contiene un pescado
def is_fish_image(image_path: str, threshold: float = 0.5) -> bool:
    img = Image.open(image_path).convert("RGB").resize((224, 224))
    x = np.array(img) / 255.0
    x = np.expand_dims(x, axis=0)
    prob = model.predict(x)[0][0]
    return prob > threshold

os.environ["ORT_LOG_LEVEL"] = "ERROR"  # Solo errores críticos de ONNXRuntime
import warnings
warnings.filterwarnings("ignore")

app = FastAPI()

# Endpoint de health check
@app.get("/health/")
async def health():
    return {"status": "ok"}

# Endpoint para apagar el servidor (solo para desarrollo)
@app.post("/shutdown/")
async def shutdown():
    # Esto funciona solo si ejecutas con "python api.py", no con uvicorn --reload
    sys.exit(0)
    return {"status": "Shutting down..."}
    

# Manejo global de excepciones
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content={"error": f"Error interno del servidor: {str(exc)}"}
    )

class ImageRequest(BaseModel):
    image_path: str
    solo_ojo: bool = False

class BatchRequest(BaseModel):
    folder_path: str
    solo_ojo: bool = False

@app.post("/procesar/")
async def process_image(request: ImageRequest):
    try:
        image = Image.open(request.image_path)
        # Verificar si la imagen es de un pescado usando fish_classifier.py
        if not is_fish_image(request.image_path):
            return {"error": "La imagen no corresponde a un pescado."}
        start_time = time.time()
        result = analyze_image(request.image_path, solo_ojo=request.solo_ojo)
        elapsed_time = time.time() - start_time
        result["processing_time_seconds"] = round(elapsed_time, 3)
        return result
    except FileNotFoundError:
        return {"error": "La imagen no se encontró en la ruta especificada."}
    except Exception as e:
        return {"error": f"Error al procesar la imagen: {str(e)}"}

@app.post("/procesar_batch/")
async def process_batch(request: BatchRequest):
    try:
        image_paths = glob.glob(os.path.join(request.folder_path, "*.jpg")) + \
                      glob.glob(os.path.join(request.folder_path, "*.png")) + \
                      glob.glob(os.path.join(request.folder_path, "*.jpeg"))
        if not image_paths:
            return {"error": "No se encontraron imágenes en la carpeta especificada."}

        # Filtrar solo imágenes que sean de pescado usando fish_classifier.py
        fish_image_paths = [img_path for img_path in image_paths if is_fish_image(img_path)]
        if not fish_image_paths:
            return {"error": "No se encontraron imágenes de pescado en la carpeta especificada."}

        start_time = time.time()
        results = analyze_image_batch(fish_image_paths, solo_ojo=request.solo_ojo)
        elapsed_time = time.time() - start_time

        return {
            "batch_results": results,
            "processing_time_seconds": round(elapsed_time, 3)
        }
    except Exception as e:
        return {"error": f"Error al procesar el batch: {str(e)}"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)