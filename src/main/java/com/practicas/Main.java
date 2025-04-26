package com.practicas;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carga la librería nativa
    }

    public static void main(String[] args) {
    String path = "./src/main/files/Cachamas/pez";
        File carpeta = new File(path);

        if(carpeta.isDirectory()){

            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                // Recorrer todos los archivos
                int i = 0;
                for (File archivo : archivos) {
                    // Verificar si es un archivo de imagen por su extensión
                        // Crear una nueva instancia de Imagen con la ruta del archivo
                        Imagen imagen = new Imagen(archivo.getPath());

                        imagen.segmentarPorBordes("./src/main/files/outs/pruebafondo"+ i +".jpg");
                        i++;

                }
            }

        }
        System.out.println("Prueba finalizada");



    }
}

