package com.practicas;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Lote {
    private int id;
    private List<Imagen> imagenes;
    private String fecha;
    private int idUsuario;
    private String path;

    public Lote(int id, String fecha, int idUsuario, String path) {
        this.id = id;
        this.imagenes = new ArrayList<Imagen>();
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.path = path;

        // Lógica para cargar las imágenes desde el path
        getImgsFromPath(path);
    }
    private void getImgsFromPath(String path){
        File carpeta = new File(path);

        if(carpeta.isDirectory()){

            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                // Recorrer todos los archivos
                for (File archivo : archivos) {
                    // Verificar si es un archivo de imagen por su extensión
                    if (esImagen(archivo)) {
                        // Crear una nueva instancia de Imagen con la ruta del archivo
                        Imagen imagen = new Imagen(archivo.getPath());
                        // Agregar la imagen a la lista
                        imagenes.add(imagen);
                    }
                }
            }

        }
    }
    private boolean esImagen(File archivo) {
        String nombreArchivo = archivo.getName().toLowerCase();
        return nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg") || nombreArchivo.endsWith(".png");
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