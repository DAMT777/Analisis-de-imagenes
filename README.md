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

## 🛠️ Instalación

1. Clonar este repositorio:
   ```bash
   git clone https://github.com/tu_usuario/analisis-de-imagenes.git

2. Ir a la ubicacion del proyecto:
   ```bash
   cd analisis-de-imagenes

3. Instalar dependencias
    ```bash 
    mvn install

---

## ▶️ Ejecución del Proyecto

- **Opción 1:** Desde la terminal
    ```bash 
    mvn clean javafx:run

- **Opción 2:** Desde un IDE

1. Abra el proyecto como un proyecto en maven. 
2. Asegurarse que las dependencias de JavaFX se están importando correctamente 
3. Ejecutar la clase App.java

---

## 📄 Licencia 
Este proyecto está licenciado bajo la Licencia MIT.

---

## 💻 Equipo de desarrollo:

- **Diego Machado** - Líder de proyecto
- **Duvan Baquero** - Diseñador
- **Carlos Barrera** - Analista
- **Jesus Delgado** - Backend
- **Johan Forero** - Backend
- **César Pérez** - Frontend 
- **Fabián Santofimio** - Tester

---

## 🎓 Créditos
Proyecto desarrollado como parte del trabajo académico en la Universidad de los Llanos – Unillanos
Facultad de Ingeniería – Ingeniería de Sistemas

