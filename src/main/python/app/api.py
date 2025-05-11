# api.py
from fastapi import FastAPI
from analyze import analyze_image
from preprocess import preprocess_image
from PIL import Image

app = FastAPI()
#ejemplo de uso con curl:
# curl -X POST "http://127.0.0.1:8000/procesar/" -H "Content-Type: application/json" -d '{"image_path": "ruta/a/tu/imagen.jpg"}'
@app.post("/procesar/")
async def process_image(image_path: str):
    # Abrir la imagen desde la ruta proporcionada
    try:
        image = Image.open(image_path)
    except FileNotFoundError:
        return {"error": "La imagen no se encontró en la ruta especificada."}
    except Exception as e:
        return {"error": f"Error al abrir la imagen: {str(e)}"}
    
    # Preprocesar la imagen (si es necesario)
    preprocessed_image = preprocess_image(image_path)
    
    # Analizar la imagen con el modelo entrenado
    result = analyze_image(image_path)
    
    # Retornar la ruta de la imagen procesada y los resultados
    return {"processed_image_path": "temp_images/processed_image.jpg", "analysis_results": result}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)