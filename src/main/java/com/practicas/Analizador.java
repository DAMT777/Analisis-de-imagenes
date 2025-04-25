package com.practicas;

import java.util.List;
import java.util.ArrayList;



public class Analizador {
    private List<Valoracion> valoraciones;

    public Analizador() {
        this.valoraciones = new ArrayList<>();
    }

    public void surf(Lote lote) {
        // Algoritmo SURF
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