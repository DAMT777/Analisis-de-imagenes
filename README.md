# Sistema de Análisis de la Calidad del Pescado (S.A.C.P)

El **Sistema de Análisis de la Calidad del Pescado (S.A.C.P.)** es una aplicación de escritorio en **Java** (JavaFX + Scene Builder) que, mediante **OpenCV**, permite:

* Cargar imágenes de lotes de peces
* Analizar y clasificar su calidad según la norma colombiana **NTC 1443**
* Gestionar usuarios y reportes en la nube, con tres roles (Administrador, Usuario, Piscultor)

---

## ⚡ Funcionalidades principales

1. **Carga de imágenes**
   Multiselección de fotos de un mismo lote para análisis en lote.
2. **Análisis de calidad**
   Algoritmos SURF y herramientas de OpenCV para determinar calidad según NTC 1443.
3. **Roles de usuario**

  * **Administrador/Investigador**:

    * Acceso total a la base de datos y reportes globales
    * *No* puede ejecutar análisis
  * **Usuario/Piscultor**:

    * Ejecutar análisis
    * Registrar y consultar sus resultados
4. **Generación y gestión de reportes**

  * Guardado y consulta de análisis realizados
  * Historial completo accesible para administradores

---

## ⚙️ Requisitos

### Java y herramienta gráfica

* **JDK 17** o superior (recomendado **Java 21+**)
* **Maven**
* **Scene Builder** (para editar interfaz)

> El IDE debe estar configurado para proyectos JavaFX + Maven.

### CUDA, cuDNN y TensorRT (para procesamiento en GPU)

1. **CUDA Toolkit 11.2**

  * Descarga: [https://developer.nvidia.com/cuda-11.2.0-download-archive](https://developer.nvidia.com/cuda-11.2.0-download-archive)
2. **cuDNN 8.1.1** (compatible con CUDA 11.2)

  * Descarga: [https://developer.nvidia.com/rdp/cudnn-archive](https://developer.nvidia.com/rdp/cudnn-archive)
  * Copiar:

    * `*.dll` → `C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.2\bin`
    * `*.lib` → `C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.2\lib\x64`
    * `*.h`   → `C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.2\include`
3. **TensorRT 7.2.2**

  * Instalar en:

    ```
    C:\Program Files\NVIDIA GPU Computing Toolkit\TensorRT-7.2.2\
    ```
  * Asegúrate de que el subdirectorio `python/onnx_graphsurgeon/onnx_graphsurgeon-0.2.6-py2.py3-none-any.whl` exista para la instalación de dependencias Python.

### Python y dependencias de IA

* **Python 3.9.x**

  * Descarga: [https://www.python.org/downloads/release/python-390/](https://www.python.org/downloads/release/python-390/)
* Crear entorno virtual (opcional pero recomendado):

  ```powershell
  py -3.9 -m venv tf-env
  .\tf-env\Scripts\activate
  ```
* Instalar dependencias:

  ```powershell
  pip install -r requirements.txt
  ```

---

## 🚀 Ejecución

### Módulo Python (API IA)

1. Activar entorno virtual (si aplica).
2. Ejecutar:

   ```powershell
   python run_api.bat
   ```

  * Por defecto expone el puerto **8001**.

### Proyecto Java

* **Opción 1: Terminal**

  ```bash
  mvn clean javafx:run
  ```
* **Opción 2: IDE**

  1. Abrir como proyecto Maven.
  2. Verificar dependencias JavaFX.
  3. Ejecutar la clase `App.java`.

---

## 📄 Licencia

Este proyecto está bajo licencia **MIT**.

---

## 💻 Equipo de desarrollo

* **Diego Machado** – Líder de proyecto
* **Duvan Baquero** – Diseñador UI/UX
* **Carlos Barrera** – Analista
* **Jesús Delgado** – Backend
* **Johan Forero** – Backend
* **César Pérez** – Frontend
* **Fabián Santofimio** – Tester

---

## 🎓 Créditos

Desarrollado como parte del trabajo académico en la
**Universidad de los Llanos – Unillanos**,
Facultad de Ingeniería – Ingeniería de Sistemas

