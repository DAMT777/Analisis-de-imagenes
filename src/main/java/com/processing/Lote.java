package com.processing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Lote {
    private static int id;
    private List<Imagen> imagenes;
    private String fecha;
    private int idUsuario;
    private String path;
    private String condicion;
    private String procedencia;
    private boolean registradoInvima = true;
    private String tiempoPesca = "3 dias";

    public Lote(int idUsuario, String fecha, String path, String[] contexto) {
        this.imagenes = new ArrayList<Imagen>();
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.path = path;
        this.condicion = contexto[0];
        // actualizar data en el array
        //this.tiempoPesca = contexto[1];
        this.procedencia = contexto[1];
        //this.registradoInvima = contexto[3].equals("true") ;

        // Lógica para carga las imágenes desde el path
        getImgsFromPath(path);
    }


    public Lote(int idUsuario, String path) {
        this.path = path;
        this.fecha = java.time.LocalDate.now().toString(); // Fecha actual en formato "YYYY-MM-DD"
        this.idUsuario = idUsuario;
        this.imagenes = new ArrayList<Imagen>();
        getImgsFromPath(path);
    }

    public String getCondiciones() {
        return condicion;
    }
    public void setDescripcion(String condicion) {
        this.condicion = condicion;
    }
    public String getProcedencia() {
        return procedencia;
    }
    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getPath() {
        return path;
    }

    private void getImgsFromPath(String path) {
        File carpeta = new File(path);
        imagenes.clear();

        if (!carpeta.exists()) {
            System.err.println("La ruta no existe: " + path);
            return;
        }
        if (!carpeta.isDirectory()) {
            System.err.println("La ruta no es un directorio: " + path);
            return;
        }

        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            System.err.println("No se pudieron listar los archivos en: " + path);
            return;
        }

        for (File archivo : archivos) {
            if (!archivo.isFile()) {
                System.out.println("Omitiendo (no es archivo): " + archivo.getName());
                continue;
            }
            if (!archivo.canRead()) {
                System.err.println("No se puede leer el archivo: " + archivo.getName());
                continue;
            }
            if (esImagen(archivo)) {
                Imagen imagen = new Imagen(archivo.getPath());
                imagenes.add(imagen);
            }else {
                System.out.println("Omitiendo (no es imagen): " + archivo.getName());
            }
        }
    }

    private boolean esImagen(File archivo) {
        String nombreArchivo = archivo.getName().toLowerCase();
        return nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg") || nombreArchivo.endsWith(".png");
    }


    public static int getId() {
        return id;
    }

    public void setId(int id) {
        Lote.id = id;
    }

    public List<Imagen> getImagenes() {
        return imagenes;
    }


    public void setPath(String path) {
        this.path = path;
    }
    public void setImagenes(List<Imagen> imagenes) {
        this.imagenes = imagenes;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isRegistradoInvima() { return registradoInvima; }

    public void setRegistradoInvima(boolean registradoInvima) { this.registradoInvima = registradoInvima; }

    public String getTiempoPesca() { return tiempoPesca; }

    public void setTiempoPesca(String tiempoPesca) { this.tiempoPesca = tiempoPesca; }
}