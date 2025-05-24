package com.mycompany.prototiposoftware;

/**
 * @author PC
 */

public class TableViewData {
    private String calidadOjos;
    private String calidadPiel;
    private String anomaliasEncontradas;
    private String cantidadMuestras;

    public TableViewData(String calidadOjos, String calidadPiel, String anomaliasEncontradas, String cantidadMuestras) {
        this.calidadOjos = calidadOjos;
        this.calidadPiel = calidadPiel;
        this.anomaliasEncontradas = anomaliasEncontradas;
        this.cantidadMuestras = cantidadMuestras;
    }

    public String getCalidadOjos() {
        return calidadOjos;
    }

    public void setCalidadOjos(String calidadOjos) {
        this.calidadOjos = calidadOjos;
    }

    public String getCalidadPiel() {
        return calidadPiel;
    }

    public void setCalidadPiel(String calidadPiel) {
        this.calidadPiel = calidadPiel;
    }

    public String getAnomaliasEncontradas() {
        return anomaliasEncontradas;
    }

    public void setAnomaliasEncontradas(String anomaliasEncontradas) {
        this.anomaliasEncontradas = anomaliasEncontradas;
    }

    public String getCantidadMuestras() {
        return cantidadMuestras;
    }

    public void setCantidadMuestras(String cantidadMuestras) {
        this.cantidadMuestras = cantidadMuestras;
    }
}
