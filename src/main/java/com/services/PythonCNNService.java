
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

    public static JsonObject communicate(String imagePath) {
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("image_path", imagePath);
        jsonData.addProperty("solo_ojo", false);

        String data = jsonData.toString();
        String response = sendPostRequest("http://127.0.0.1:8001/procesar/", data);
        return JsonParser.parseString(response).getAsJsonObject();
    }

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