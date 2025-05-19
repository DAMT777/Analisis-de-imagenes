package com.processing;

//ROLES
//user
//academico
//admin

import com.databaseInteractions.DBConnect;
import com.google.gson.JsonObject;
import com.services.PythonCNNService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    //static {
     //   org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.opencv_java.class);
    //}

 public static void main(String[] args) {
     // Paso 1: Crear un lote vacío con un usuario y una ruta (ambos vacíos por ahora)
     /*int idUsuario = 0; // ID del usuario que sube el lote (debe asignarse el real)
     String path = "";  // Ruta donde están las imágenes (debe asignarse la real)
     Lote lote = new Lote(idUsuario, path); // Se crea el objeto Lote

     // Paso 2: Obtener la lista de imágenes asociadas al lote
     List<Imagen> imagenes = lote.getImagenes(); // Se asume que el lote ya tiene imágenes cargadas
     List<Imagen> imagenes2 = new ArrayList<Imagen>(); // Lista para almacenar imágenes procesadas

     // Paso 3: Procesar cada imagen del lote
     String ruta = ""; // Variable para guardar la ruta de la imagen procesada
     for (Imagen imagen : imagenes) {
         // Se comunica con el servicio Python (CNN) para analizar la imagen
         JsonObject json = PythonCNNService.communicate(imagen.getPath());
         json.addProperty("descripcion_img", "")
         // Se guarda la valoración (resultado del análisis) en el objeto Imagen
         imagen.setValoracion(json);

         // Se agrega la imagen procesada a la nueva lista
         imagenes2.add(imagen);

         // Se obtiene la ruta de la imagen procesada (es útil para saber dónde se guardó)
         ruta = json.get("processed_image_path").getAsString();
     }

     // Paso 4: Actualizar el lote con las imágenes procesadas y la nueva ruta
     lote.setImagenes(imagenes2); // Se actualiza la lista de imágenes del lote
     lote.setPath(obtenerCarpetaDeImagen(ruta)); // Se actualiza la ruta del lote a la carpeta donde están las imágenes procesadas

     // Paso 5: Registrar el lote y sus imágenes en la base de datos
     ResultadoRegistro registro = DBConnect.registrarImagenesDeLote(lote);
     // El objeto ResultadoRegistro indica si el registro fue exitoso y el ID del lote

     // Paso 6: El frontend puede usar el resultado para mostrar alertas o mensajes al usuario

     // Nota: Después de este proceso, todos los datos quedan almacenados en la base de datos,
     // lo que permite exportarlos o consultarlos posteriormente desde el frontend.
     */
 }





}


