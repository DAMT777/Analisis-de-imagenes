package com.practicas;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;


public class Segmento {
    private int idImg;
    private MatOfPoint contorno; // Puntos del contorno
    private double area; // Área del segmento
    private String tipo; // "ojo", "piel", etc.
    private Rect boundingRect; // Rectángulo delimitador

    public Segmento() {
        this.idImg = 0;
        this.contorno = new MatOfPoint();
        this.area = 0;
        this.tipo = "";
        this.boundingRect = new Rect();
    }

    public Segmento(int idImg) {
        this.idImg = idImg;
        this.contorno = new MatOfPoint();
        this.area = 0;
        this.tipo = "";
        this.boundingRect = new Rect();
    }

    // Getters y setters
    public void setContorno(MatOfPoint contorno) {
        this.contorno = contorno;
        this.boundingRect = Imgproc.boundingRect(contorno);
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }


    public int getIdImg() {
        return idImg;
    }

    public void setIdImg(int idImg) {
        this.idImg = idImg;
    }

}