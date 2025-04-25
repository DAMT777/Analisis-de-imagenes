package com.practicas;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carga la librería nativa
    }

    public static void main(String[] args) {
        // Cargar la imagen original
        String rutaImagen = "./src/main/files/Cachamas/1744934697729.jpg";
        Mat imagenColor = Imgcodecs.imread(rutaImagen);

        if (imagenColor.empty()) {
            System.out.println("No se pudo cargar la imagen desde: " + rutaImagen);
            return;
        }

        // Convertir a escala de grises
        Mat imagenGris = new Mat();
        Imgproc.cvtColor(imagenColor, imagenGris, Imgproc.COLOR_BGR2GRAY);

        // Guardar la imagen convertida
        boolean resultado = Imgcodecs.imwrite("./src/main/files/outs/foto_gris.jpg", imagenGris);
        if (resultado) {
            System.out.println("Imagen convertida y guardada exitosamente.");
        } else {
            System.out.println("No se pudo guardar la imagen.");
        }
    }
}

