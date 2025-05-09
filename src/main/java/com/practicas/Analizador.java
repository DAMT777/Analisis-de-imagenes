package com.practicas;

import com.google.gson.JsonObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Analizador {
    //static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);} // esta linea causa un error al impotar las dependencias dinamicas de OpenCV

    static {
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.opencv_java.class);
    } // esta linea soluciona el error de arriba


    private List<Valoracion> valoraciones;

    public Analizador() {
        this.valoraciones = new ArrayList<>();
    }

    public static void analizar(Lote lote, Consumer<String> updateStatus) {
        List<Imagen> imagenes = lote.getImagenes();
        updateStatus.accept("Segmentando y evaluando lote");
        for (Imagen imagen : imagenes) {
            String rutaImagen = imagen.getPath();
            Valoracion valoracion = analizar(rutaImagen); // Llamada al método recursivo

            if (valoracion != null) {
                imagen.setValoracion(valoracion); // Asignar la valoración a la imagen
            } else {
                System.out.println("No se pudo analizar la imagen: " + rutaImagen);
            }
        }
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

        return new Valoracion(cantidadKeypoints, (float) brilloPromedio, (float) desviacionBrillo);
    }

    public static String evaluarCalidad(Valoracion resultado) {
        JsonObject json = new JsonObject();

        if (resultado.getBrilloPromedio() < 80) {
            json.addProperty("brillo", "Bajo brillo: posible ojo/cuerpo opaco.");
        } else {
            json.addProperty("brillo", "Buen brillo detectado.");
        }

        if (resultado.getDesviacionBrillo() > 25) {
            json.addProperty("variacionBrillo", "Alta variación en brillo: posible deterioro.");
        } else {
            json.addProperty("variacionBrillo", "Brillo uniforme: buen estado.");
        }

        if (resultado.getCantidadKeypoints() < 100) {
            json.addProperty("keypoints", "Pocos keypoints: posible estructura dañada.");
        } else {
            json.addProperty("keypoints", "Estructura visible.");
        }

        return json.toString();
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

    public List<Valoracion> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(List<Valoracion> valoraciones) {
        this.valoraciones = valoraciones;
    }
}