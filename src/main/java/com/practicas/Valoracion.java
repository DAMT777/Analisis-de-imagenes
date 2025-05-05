package com.practicas;

public class Valoracion {
    private int idImagen;
    private int cantidadKeypoints;
    private float brilloPromedio;
    private float desviacionBrillo;
    private int califColor;
    private int califOjos;
    private int califPiel;
    private String descripcion;

    public Valoracion(int idImagen, int califColor, int califOjos, int califPiel) {
        this.idImagen = idImagen;
        this.califColor = califColor;
        this.califOjos = califOjos;
        this.califPiel = califPiel;
        this.descripcion = "";
    }

    public Valoracion(int cantidadKeypoints, float brilloPromedio, float desviacionBrillo) {
        this.cantidadKeypoints = cantidadKeypoints;
        this.brilloPromedio = brilloPromedio;
        this.desviacionBrillo = desviacionBrillo;
    }

    public void mostrar() {
        System.out.println("Keypoints detectados: " + cantidadKeypoints);
        System.out.println("Brillo promedio (canal V): " + brilloPromedio);
        System.out.println("Desviación del brillo: " + desviacionBrillo);
    }


    public int getCantidadKeypoints() {
        return cantidadKeypoints;
    }

    public void setCantidadKeypoints(int cantidadKeypoints) {
        this.cantidadKeypoints = cantidadKeypoints;
    }

    public float getBrilloPromedio() {
        return brilloPromedio;
    }

    public void setBrilloPromedio(float brilloPromedio) {
        this.brilloPromedio = brilloPromedio;
    }

    public float getDesviacionBrillo() {
        return desviacionBrillo;
    }

    public void setDesviacionBrillo(float desviacionBrillo) {
        this.desviacionBrillo = desviacionBrillo;
    }

    public int getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(int idImagen) {
        this.idImagen = idImagen;
    }

    public int getCalifColor() {
        return califColor;
    }

    public void setCalifColor(int califColor) {
        this.califColor = califColor;
    }

    public int getCalifOjos() {
        return califOjos;
    }

    public void setCalifOjos(int califOjos) {
        this.califOjos = califOjos;
    }

    public int getCalifPiel() {
        return califPiel;
    }

    public void setCalifPiel(int califPiel) {
        this.califPiel = califPiel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}