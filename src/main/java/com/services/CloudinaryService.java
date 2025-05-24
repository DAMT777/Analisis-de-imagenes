package com.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        Dotenv dotenv = Dotenv.load();
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"),
                "api_key", dotenv.get("CLOUDINARY_API_KEY"),
                "api_secret", dotenv.get("CLOUDINARY_API_SECRET")
        ));
    }

    public String subirImagen(String rutaImagen) {
        File archivoImagen = new File(rutaImagen);

        if (!archivoImagen.exists()) {
            System.out.println("La imagen no existe en la ruta especificada.");
            return null;
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(archivoImagen, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url"); // URL segura de la imagen subida
        } catch (IOException e) {
            System.out.println("Error al subir la imagen a Cloudinary: " + e.getMessage());
            return null;
        }
    }
}