package com.processing;

import net.sf.jasperreports.engine.JRException;
import java.util.ArrayList;
import java.util.List;


public class Reporte {
    private int id;
    private int idUsuario;
    private Lote lote;
    private double califFinal;
    private String descripcion;
    private List<Valoracion> anomalías;

    public Reporte() {
        this.anomalías = new ArrayList<Valoracion>();
    }

    public void getPDF(String rutaSalida) throws JRException {
        // Exportar PDF
        //si hay anomalias, deben incluirse, hasta maximo 8 imagenes de estas anomalias
        //if( isThereAnomalies() )

    }

    public void getXLSX(String rutaSalida){
        // Exportar XLSX
        //si hay anomalias, deben incluirse los nombres de los archivos
        //if( isThereAnomalies() )
    }
    public boolean isThereAnomalies() {
        return !anomalías.isEmpty();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public double getCalifFinal() {
        return califFinal;
    }

    public void setCalifFinal(double califFinal) {
        this.califFinal = califFinal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Valoracion> getAnomalias() {
        return anomalías;
    }

    public void setAnomalias(List<Valoracion> anomalías) {
        this.anomalías = anomalías;
    }
}