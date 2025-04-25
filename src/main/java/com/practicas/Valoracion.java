package com.practicas;

public class Valoracion {
    private int idImagen;
    private int califColor;
    private int califOjos;
    private int califPiel;
    private String descripcion;

    public Valoracion(int idImagen, int califColor, int califOjos, int califPiel, String descripcion) {
        this.idImagen = idImagen;
        this.califColor = califColor;
        this.califOjos = califOjos;
        this.califPiel = califPiel;
        this.descripcion = descripcion;
    }

    public int getIdImagen() { return idImagen; }
    public void setIdImagen(int idImagen) { this.idImagen = idImagen; }

    public int getCalifColor() { return califColor; }
    public void setCalifColor(int califColor) { this.califColor = califColor; }

    public int getCalifOjos() { return califOjos; }
    public void setCalifOjos(int califOjos) { this.califOjos = califOjos; }

    public int getCalifPiel() { return califPiel; }
    public void setCalifPiel(int califPiel) { this.califPiel = califPiel; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}