package com.processing;

import com.databaseInteractions.DBConnect;
import com.google.gson.JsonObject;
import com.services.PythonCNNService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoteProcessor {

    /**
     * Procesa un lote de imágenes para un usuario dado.
     * @param flag ID del usuario que sube el lote.
     * @return ResultadoRegistro con el resultado del registro en la base de datos.
     */
    public static ResultadoRegistro procesarLote(Lote lote, Consumer <String> flag) {

        List<Imagen> imagenes = lote.getImagenes();
        List<Imagen> imagenesProcesadas = new ArrayList<>();
        flag.accept("Procesando imagenes");
        String rutaProcesada = "";
        String mensaje = "";
        for (Imagen imagen : imagenes) {
            // ../../../
            JsonObject json1 = PythonCNNService.classifyImage(".\\."+imagen.getPath());
            System.out.println(json1.toString());
            if(!json1.get("es_pez").toString().equals("true")){
                mensaje =  "Imagen "+ imagen.getFilename() + " no es un pescado, se descartara";
                continue;
            }
            JsonObject json = PythonCNNService.communicate(".\\."+ imagen.getPath());
            System.out.println(json.toString());
            System.out.println(".\\."+imagen.getPath());
            json.addProperty("descripcion_img", "");
            imagen.setValoracion(json);
            rutaProcesada = json.get("processed_image_path").getAsString();
            imagen.setPath(rutaProcesada);
            imagenesProcesadas.add(imagen);
        }
        flag.accept("Subiendo imagenes y resultados . " + mensaje);
        lote.setImagenes(imagenesProcesadas);
        lote.setPath(obtenerCarpetaDeImagen(rutaProcesada));

        return DBConnect.registrarImagenesDeLote(lote);
    }

    public static String obtenerCarpetaDeImagen(String rutaImagen) {
        File archivo = new File(rutaImagen);
        String carpeta = archivo.getParent();
        if (carpeta != null && !carpeta.endsWith(File.separator)) {
            carpeta += File.separator;
        }
        return carpeta;
    }
}