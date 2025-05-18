import onnxruntime as ort
print("ONNXRuntime device:", ort.get_device())  # Debe decir 'GPU'