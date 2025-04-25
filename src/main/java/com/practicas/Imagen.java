package com.practicas;

import java.util.List;

public class Imagen {
    private int id;
    private String path;
    private float brillo;
    private float contraste;
    private List<Segmento> segmentos;

    public Imagen(String path) {
        this.id = 0;
        this.path = path;
        this.brillo = 0;
        this.contraste = 0;
        this.segmentos = null;
    }

    public void segmentarImagen(Imagen img) {
        // Lógica para segmentar la imagen
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public float getBrillo() { return brillo; }
    public void setBrillo(float brillo) { this.brillo = brillo; }

    public float getContraste() { return contraste; }
    public void setContraste(float contraste) { this.contraste = contraste; }

    public List<Segmento> getSegmentos() { return segmentos; }
    public void setSegmentos(List<Segmento> segmentos) { this.segmentos = segmentos; }
}