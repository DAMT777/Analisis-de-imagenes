package com.practicas;

import java.util.List;
import java.util.ArrayList;

public class Reporte {
    private int id;
    private int idUsuario;
    private Lote lote;
    private double califFinal;
    private double avgColor;
    private double avgOjos;
    private double avgPiel;
    private String descripcion;
    private List<Valoracion> anomalías;

    public Reporte() {
        this.anomalías = new ArrayList<Valoracion>();
    }

    public void getPDF() {
        // Exportar PDF
    }

    public void getXLSX() {
        // Exportar XLSX
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public double getCalifFinal() { return califFinal; }
    public void setCalifFinal(double califFinal) { this.califFinal = califFinal; }

    public double getAvgColor() { return avgColor; }
    public void setAvgColor(double avgColor) { this.avgColor = avgColor; }

    public double getAvgOjos() { return avgOjos; }
    public void setAvgOjos(double avgOjos) { this.avgOjos = avgOjos; }

    public double getAvgPiel() { return avgPiel; }
    public void setAvgPiel(double avgPiel) { this.avgPiel = avgPiel; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<Valoracion> getAnomalias() { return anomalías; }
    public void setAnomalias(List<Valoracion> anomalías) { this.anomalías = anomalías; }
}