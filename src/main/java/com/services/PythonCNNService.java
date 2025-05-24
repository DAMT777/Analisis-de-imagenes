
package com.services;

import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;

public class PythonCNNService {

    private static String ProcessingEndpoint = "http://127.0.0.1:8001/procesar/";
    private static String ClassifierEndpoint = "http://127.0.0.1:8001/es_pez/";

    /**
     * Envía la ruta de una imagen al servicio Python mediante una solicitud POST
     * y devuelve la respuesta como un objeto JsonObject.
     *
     * @param imagePath Ruta de la imagen a procesar.
     * @return JsonObject con la respuesta del servicio Python.
     */
    public static JsonObject communicate(String imagePath) {
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("image_path", imagePath);
        jsonData.addProperty("solo_ojo", false);

        String data = jsonData.toString();
        String response = sendPostRequest(ProcessingEndpoint, data);

        return JsonParser.parseString(response).getAsJsonObject();
    }


    public static JsonObject classifyImage(String imagePath) {
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("image_path", imagePath);

        String data = jsonData.toString();
        String response = sendPostRequest(ClassifierEndpoint, data);

        return JsonParser.parseString(response).getAsJsonObject();
    }

    /**
     * Envía una solicitud HTTP POST al endpoint especificado con los datos JSON proporcionados.
     * Devuelve la respuesta del servidor como un String.
     * Si ocurre un error, retorna un JSON con el mensaje de error.
     *
     * @param endpoint URL del servicio al que se enviará la solicitud POST.
     * @param jsonData Datos en formato JSON que se enviarán en el cuerpo de la solicitud.
     * @return Respuesta del servidor como String, o un JSON con el error si ocurre una excepción.
     */
    public static String sendPostRequest(String endpoint, String jsonData) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonData.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();
        } catch (IOException e) {
            return "{\"error\": \"Error al enviar la solicitud POST: " + e.getMessage() + "\"}";
        }
    }
}