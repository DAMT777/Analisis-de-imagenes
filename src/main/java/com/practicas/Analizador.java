package com.practicas;

import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.List;

public class Analizador {
    private List<Valoracion> valoraciones;

    public Analizador() {
        this.valoraciones = new ArrayList<>();
    }

    public void orb(Lote lote) {
        // Algoritmo ORB
        List<Imagen> imagenes = lote.getImagenes();
        int i = 0;

        for (Imagen imagen : imagenes) {
            String ruta = imagen.getPath();

            // Cargar imagen en color
            Mat imgColor = Imgcodecs.imread(ruta, Imgcodecs.IMREAD_COLOR);
            if (imgColor.empty()) {
                System.out.println("No se pudo cargar la imagen: " + ruta);
                continue;
            }

            // Convertir a escala de grises para detectar keypoints
            Mat imgGray = new Mat();
            Imgproc.cvtColor(imgColor, imgGray, Imgproc.COLOR_BGR2GRAY);

            // Crear ORB detector
            ORB orb = ORB.create();
            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            orb.detect(imgGray, keypoints);

            // Dibujar keypoints sobre la imagen en color
            Mat imagenSalida = new Mat();
            Features2d.drawKeypoints(imgColor, keypoints, imagenSalida, new Scalar(0, 255, 0), Features2d.DrawMatchesFlags_DEFAULT);

            // Guardar imagen de salida
            Imgcodecs.imwrite("./src/main/files/outs/foto_orb" + i + ".jpg", imagenSalida);
            System.out.println("Proceso ORB completado. Imagen guardada como foto_orb" + i + ".jpg");

            // Análisis de color en los keypoints
            KeyPoint[] arrayKeypoints = keypoints.toArray();
            for (KeyPoint kp : arrayKeypoints) {
                int x = (int) Math.round(kp.pt.x);
                int y = (int) Math.round(kp.pt.y);

                if (x >= 0 && x < imgColor.cols() && y >= 0 && y < imgColor.rows()) {
                    double[] color = imgColor.get(y, x); // [fila, columna]
                    double blue = color[0];
                    double green = color[1];
                    double red = color[2];

                    System.out.println("Keypoint en (" + x + ", " + y + ") - Color BGR: (" + blue + ", " + green + ", " + red + ")");
                }
            }

            i++;
        }
    }


    public void pcVision(Lote lote) {
        // Procesamiento visión por computadora
    }

    public Reporte generarReporte() {
        return new Reporte();
    }

    public void clearValoraciones() {
        valoraciones.clear();
    }

    public List<Valoracion> getValoraciones() { return valoraciones; }
    public void setValoraciones(List<Valoracion> valoraciones) { this.valoraciones = valoraciones; }
}