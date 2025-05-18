package com.processing;

import com.databaseInteractions.DBConnect;
import com.google.gson.JsonObject;
import com.services.PythonCNNService;
import java.util.ArrayList;
import java.util.List;

public class LoteProcessor {

    /**
     * Procesa un lote de imágenes para un usuario dado.
     * @param idUsuario ID del usuario que sube el lote.
     * @param path Ruta donde están las imágenes.
     * @return ResultadoRegistro con el resultado del registro en la base de datos.
     */
    public static ResultadoRegistro procesarLote(int idUsuario, String path) {
        Lote lote = new Lote(idUsuario, path);

        List<Imagen> imagenes = lote.getImagenes();
        List<Imagen> imagenesProcesadas = new ArrayList<>();

        String rutaProcesada = "";
        for (Imagen imagen : imagenes) {
            JsonObject json = PythonCNNService.communicate(imagen.getPath());
            json.addProperty("descripcion_img", "");
            imagen.setValoracion(json);
            imagenesProcesadas.add(imagen);
            rutaProcesada = json.get("processed_image_path").getAsString();
        }

        lote.setImagenes(imagenesProcesadas);
        lote.setPath(obtenerCarpetaDeImagen(rutaProcesada));

        return DBConnect.registrarImagenesDeLote(lote);
    }

    public static String obtenerCarpetaDeImagen(String rutaImagen) {
        java.io.File archivo = new java.io.File(rutaImagen);
        String carpeta = archivo.getParent();
        if (carpeta != null && !carpeta.endsWith(java.io.File.separator)) {
            carpeta += java.io.File.separator;
        }
        return carpeta;
    }
}