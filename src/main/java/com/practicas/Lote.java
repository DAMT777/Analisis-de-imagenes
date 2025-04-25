package com.practicas;
import java.util.List;

public class Lote {
    private int id;
    private List<Imagen> imagenes;
    private String fecha;
    private int idUsuario;

    public Lote(int id, List<Imagen> imagenes, String fecha, int idUsuario) {
        this.id = id;
        this.imagenes = imagenes;
        this.fecha = fecha;
        this.idUsuario = idUsuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<Imagen> getImagenes() { return imagenes; }
    public void setImagenes(List<Imagen> imagenes) { this.imagenes = imagenes; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}