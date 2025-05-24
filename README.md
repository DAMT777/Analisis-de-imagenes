# Sistema de Análisis de la Calidad del Pescado (S.A.C.P)

El **Sistema de Análisis de la Calidad del Pescado (S.A.C.P)** es una aplicación de escritorio desarrollada en **Java** utilizando **JavaFX** y **Scene Builder**, con integración de **OpenCV** para el análisis y clasificación de la calidad del pescado. Este software permite a los usuarios cargar imágenes de peces pertenecientes a un lote y determinar su calidad según la norma colombiana **NTC 1443**.

Además, cuenta con una base de datos en la nube para la gestión de usuarios y reportes. Los usuarios se dividen en tres roles: **Administrador**, **Usuario** y **Piscultor**, cada uno con diferentes permisos y funcionalidades dentro del sistema.

## ⚡ Funcionalidades Principales

- **Carga de imágenes**: Los usuarios pueden cargar múltiples imágenes de un mismo lote de peces.
- **Análisis de calidad**: A través de algoritmos como **SURF** y **OpenCV**, el sistema determina la calidad del pescado basándose en la **NTC 1443**.
- **Roles de usuario**:
  - **Administrador/Investigador**: Acceso completo a la base de datos. Puede ver reportes de todos los usuarios, pero **no puede utilizar el sistema de análisis**.
  - **Usuario**: Puede utilizar el sistema de análisis y registrar los resultados.
- **Generación de reportes**:
  - Los usuarios pueden guardar y consultar análisis realizados.
  - Los administradores pueden gestionar el historial completo de todos los usuarios.

---

## ⚙️ Requisitos

- **JDK 17** o superior (recomendado Java 21+)
- **Maven**
- **Scene builder** (si desea cambiar aspectos visuales de la interfaz gráfica)

> Nota: El IDE de su preferencia debe estar configurado para ejecutar proyectos **JavaFX** con **Maven** y sus dependencias.

---

## 🧠 Módulo de Análisis por IA (Python)

Para ejecutar correctamente el módulo de análisis basado en TensorFlow y ONNX, sigue estos pasos:

### 1. Python

- **Versión requerida**: Python 3.9.x  
- Descarga desde: https://www.python.org/downloads/release/python-390/

### 2. Crear un entorno virtual (opcional pero recomendado)

```powershell
py -m venv tf-env
.\tf-env\Scripts\activate
```
### 3. CUDA y cuDNN (para uso con GPU)
CUDA Toolkit 11.2 (es posible que necesiten crear una cuenta developer de nvidia)
https://developer.nvidia.com/cuda-11.2.0-download-archive

cuDNN 8.1.1 (compatible con CUDA 11.2)
https://developer.nvidia.com/rdp/cudnn-archive

Descomprime y copia los archivos en las rutas respectivas:

*.dll → C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.2\bin

*.lib → C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.2\lib\x64

*.h → C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v11.2\include

### 4. Instalación de dependencias
Descargar el archivo [requirements.txt](requirements.txt)
Una vez activado el entorno virtual, y desde el mismo ejecutar: 
```powershell
pip install -r requirements.txt
```
Para instalar todas las dependencias necesarias.


### 5. Ejecucion API Python
Basta con ejecutar el Script [run_api.bat](python/run_api.bat) para correr la API, por defecto expone el puerto 8001. Recuerda tener VRAM suficiente jeje.


▶️ Ejecución del Proyecto Java
Opción 1: Desde la terminal

```bash
mvn clean javafx:run
```

Opción 2: Desde un IDE
Abrir el proyecto como proyecto Maven.

Verificar que las dependencias de JavaFX se importen correctamente.

Ejecutar la clase App.java.



📄 Licencia
Este proyecto está licenciado bajo la Licencia MIT.

💻 Equipo de desarrollo
Diego Machado – Líder de proyecto

Duvan Baquero – Diseñador

Carlos Barrera – Analista

Jesus Delgado – Backend

Johan Forero – Backend

César Pérez – Frontend

Fabián Santofimio – Tester

🎓 Créditos
Proyecto desarrollado como parte del trabajo académico en la Universidad de los Llanos – Unillanos
Facultad de Ingeniería – Ingeniería de Sistemas
