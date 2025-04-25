package com.practicas;

import java.util.List;
import java.util.ArrayList;

import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Analizador {
    private List<Valoracion> valoraciones;

    public Analizador() {
        this.valoraciones = new ArrayList<>();
    }

    public void orb(Lote lote) {
        // Algoritmo ORB

        String ruta = "";
        int i =0;
        for (Imagen imagen: lote.getImagenes()){
            ruta = imagen.getPath();
            Mat img = Imgcodecs.imread(ruta, Imgcodecs.IMREAD_GRAYSCALE);
            if (img.empty()) {
                System.out.println("No se pudo cargar la imagen.");
                return;
            }
            ORB orb = ORB.create();
            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            orb.detect(img, keypoints);
            Mat imagenSalida = new Mat();
            Features2d.drawKeypoints(img, keypoints, imagenSalida, new Scalar(0, 255, 0), Features2d.DrawMatchesFlags_DEFAULT);

            // Guardar resultado
            Imgcodecs.imwrite("./src/main/files/outs/foto_orb"+ i +".jpg", imagenSalida);
            System.out.println("Proceso ORB completado. Imagen guardada como foto_orb"+ i +".jpg");
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