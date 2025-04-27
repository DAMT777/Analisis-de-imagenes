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

    public static Valoracion analizar(String rutaImagen) {
        // Leer la imagen
        Mat imagen = Imgcodecs.imread(rutaImagen);
        if (imagen.empty()) {
            System.out.println("Error: No se pudo cargar la imagen: " + rutaImagen);
            return null;
        }

        // ------------------------
        // 1. Análisis con ORB
        // ------------------------
        ORB detector = ORB.create();
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(imagen, keypoints);
        int cantidadKeypoints = (int) keypoints.size().height;

        // ------------------------
        // 2. Análisis de color y opacidad
        // ------------------------
        Mat imagenHSV = new Mat();
        Imgproc.cvtColor(imagen, imagenHSV, Imgproc.COLOR_BGR2HSV);

        List<Mat> canales = new ArrayList<>();
        Core.split(imagenHSV, canales);
        Mat canalV = canales.get(2); // Canal de brillo

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(canalV, mean, stddev);

        double brilloPromedio = mean.toArray()[0];
        double desviacionBrillo = stddev.toArray()[0];

        return new Valoracion(cantidadKeypoints ,(float) brilloPromedio, (float) desviacionBrillo);
    }
    public static void evaluarCalidad(Valoracion resultado) {
        System.out.println("\nEvaluación rápida:");

        if (resultado.getBrilloPromedio() < 80) {
            System.out.println("- Bajo brillo: posible ojo/cuerpo opaco.");
        } else {
            System.out.println("- Buen brillo detectado.");
        }

        if (resultado.getDesviacionBrillo() > 25) {
            System.out.println("- Alta variación en brillo: posible deterioro.");
        } else {
            System.out.println("- Brillo uniforme: buen estado.");
        }

        if (resultado.getCantidadKeypoints() < 100) {
            System.out.println("- Pocos keypoints: posible estructura dañada.");
        } else {
            System.out.println("- Estructura visible.");
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