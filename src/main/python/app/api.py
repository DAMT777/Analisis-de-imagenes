import time
from fastapi import FastAPI
from analyze import analyze_image, analyze_image_batch
from pydantic import BaseModel
from PIL import Image
import os
import glob

os.environ["ORT_LOG_LEVEL"] = "ERROR"  # Solo errores críticos de ONNXRuntime
import warnings
warnings.filterwarnings("ignore")

app = FastAPI()
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
    except FileNotFoundError:
        return {"error": "La imagen no se encontró en la ruta especificada."}
    except Exception as e:
        return {"error": f"Error al abrir la imagen: {str(e)}"}

    start_time = time.time()
    result = analyze_image(request.image_path, solo_ojo=request.solo_ojo)
    elapsed_time = time.time() - start_time

    return {
        "analysis_results": result,
        "processing_time_seconds": round(elapsed_time, 3)
    }

@app.post("/procesar_batch/")
async def process_batch(request: BatchRequest):
    # Busca imágenes en la carpeta
    image_paths = glob.glob(os.path.join(request.folder_path, "*.jpg")) + \
                  glob.glob(os.path.join(request.folder_path, "*.png")) + \
                  glob.glob(os.path.join(request.folder_path, "*.jpeg"))
    if not image_paths:
        return {"error": "No se encontraron imágenes en la carpeta especificada."}

    # Opcional: filtra solo imágenes válidas
    valid_paths = []
    for img_path in image_paths:
        try:
            Image.open(img_path)
            valid_paths.append(img_path)
        except Exception:
            pass

    if not valid_paths:
        return {"error": "No se encontraron imágenes válidas en la carpeta especificada."}

    start_time = time.time()
    results = analyze_image_batch(valid_paths, solo_ojo=request.solo_ojo)
    elapsed_time = time.time() - start_time

    return {
        "batch_results": results,
        "processing_time_seconds": round(elapsed_time, 3)
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)