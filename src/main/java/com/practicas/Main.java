package com.practicas;

import org.opencv.core.Core;
import org.opencv.dnn.*;
public class Main {
    public static void main(String[] args) {
        // Cargar librería nativa de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Ruta de la imagen
        String rutaImagen = "./src/main/files/Cachamas/ojos/1744934698503.jpg";
        Analizador analizador = new Analizador();
        // Crear un lote de imágenes
        //Lote lote = new Lote(1, "2023-10-01", 1, "./src/main/files/Cachamas/dataset/");
        Valoracion resultado = analizador.analizar(rutaImagen);

        if (resultado != null) {
            resultado.mostrar();
            analizador.evaluarCalidad(resultado);
        }
    }


}

