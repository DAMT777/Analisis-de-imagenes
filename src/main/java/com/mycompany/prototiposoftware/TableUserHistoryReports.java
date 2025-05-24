package com.mycompany.prototiposoftware;

public class TableUserHistoryReports {
    private String idLote;
    private String fechaAnalisis;
    private String condicionesLote;
    private String ciudadAnalisis;
    private String tiempoPescaLote;
    private String invimaLote;
    private String calificacionLote;

    public TableUserHistoryReports(String idLote, String fechaAnalisis, String condicionesLote, String ciudadAnalisis, String tiempoPescaLote, String invimaLote, String calificacionLote) {
        this.idLote = idLote;
        this.fechaAnalisis = fechaAnalisis;
        this.condicionesLote = condicionesLote;
        this.ciudadAnalisis = ciudadAnalisis;
        this.tiempoPescaLote = tiempoPescaLote;
        this.invimaLote = invimaLote;
        this.calificacionLote = calificacionLote;
    }

    public String getIdLote() {
        return idLote;
    }

    public void setIdLote(String idLote) {
        this.idLote = idLote;
    }

    public String getFechaAnalisis() {
        return fechaAnalisis;
    }

    public void setFechaAnalisis(String fechaAnalisis) {
        this.fechaAnalisis = fechaAnalisis;
    }

    public String getCondicionesLote() {
        return condicionesLote;
    }

    public void setCondicionesLote(String condicionesLote) {
        this.condicionesLote = condicionesLote;
    }

    public String getCiudadAnalisis() {
        return ciudadAnalisis;
    }

    public void setCiudadAnalisis(String ciudadAnalisis) {
        this.ciudadAnalisis = ciudadAnalisis;
    }

    public String getTiempoPescaLote() {
        return tiempoPescaLote;
    }

    public void setTiempoPescaLote(String tiempoPescaLote) {
        this.tiempoPescaLote = tiempoPescaLote;
    }

    public String getInvimaLote() {
        return invimaLote;
    }

    public void setInvimaLote(String invimaLote) {
        this.invimaLote = invimaLote;
    }

    public String getCalificacionLote() {
        return calificacionLote;
    }

    public void setCalificacionLote(String calificacionLote) {
        this.calificacionLote = calificacionLote;
    }
}
