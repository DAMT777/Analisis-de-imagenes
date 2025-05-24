package com.processing;

public class ResultadoRegistro {
    private boolean exito;
    private int idLote;

    public ResultadoRegistro(boolean exito, int idLote) {
        this.exito = exito;
        this.idLote = idLote;
    }

    public boolean isExito() {
        return exito;
    }

    public int getIdLote() {
        return idLote;
    }
}
