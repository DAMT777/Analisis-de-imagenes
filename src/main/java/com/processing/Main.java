package com.processing;
import org.opencv.core.Core;
import java.awt.*;
import java.io.File;

//ROLES
//user
//academico
//admin

public class Main {
    static {
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.opencv_java.class);
    }

    public static void main(String[] args) {
        String inputFolder = "C:/Users/sjoha/Downloads/Cachamas/cuerpo/";
        String outputFolder = "C:/Users/sjoha/Downloads/Cachamas/outs/";

        File folder = new File(inputFolder);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (files != null) {
            for (File file : files) {
                String inputPath = file.getAbsolutePath();
                String outputPath = outputFolder + file.getName();

                Imagen img = new Imagen(inputPath);
                img.segmentarImagen(outputPath);
                System.out.println("Procesada: " + inputPath);
            }
        } else {
            System.err.println("No se encontraron imágenes en la carpeta: " + inputFolder);
        }
    }
}