package com.practicas;

public class Segmento {
    private int idImg;
    private byte[] datosDeContorno;

    public Segmento(int idImg, byte[] datosDeContorno) {
        this.idImg = idImg;
        this.datosDeContorno = datosDeContorno;
    }

    public int getIdImg() { return idImg; }
    public void setIdImg(int idImg) { this.idImg = idImg; }

    public byte[] getDatosDeContorno() { return datosDeContorno; }
    public void setDatosDeContorno(byte[] datosDeContorno) { this.datosDeContorno = datosDeContorno; }
}