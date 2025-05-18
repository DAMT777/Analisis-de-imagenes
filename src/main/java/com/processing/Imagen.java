package com.processing;
import com.google.gson.JsonObject;



public class Imagen {
    private int id;
    private String path;
    private JsonObject valoracion; // Valoración de la imagen

    public Imagen(String path) {
        this.id = 0;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setValoracion(JsonObject valoracion) {
        this.valoracion = valoracion;
    }
    public JsonObject getValoracion() {
        return valoracion;
    }
}