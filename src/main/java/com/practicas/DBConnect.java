package com.practicas;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class DBConnect {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLOUD_NAME = dotenv.get("CLOUDINARY_CLOUD_NAME");
    private static final String API_KEY = dotenv.get("CLOUDINARY_API_KEY");
    private static final String API_SECRET = dotenv.get("CLOUDINARY_API_SECRET");

    public static Connection getConnection() {
        // Cargar las variables de entorno desde el archivo .env
        String url = "jdbc:postgresql://"+dotenv.get("PGHOST")+":"+ dotenv.get("PGPORT")+"/"+ dotenv.get("PGDATABASE")+"?sslmode=require&pool_mode=" + dotenv.get("POOL_MODE");
        try {
            return DriverManager.getConnection(url, dotenv.get("PGUSER"), dotenv.get("PGPASSWORD"));
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    public boolean registrarUsuario(String nombre, String correo, String passwordHash, String rol) {
        String query = "INSERT INTO Usuario (nombre, correo, password, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, passwordHash);
            stmt.setString(4, rol);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {

            System.out.println("Error al registrar usuario: " + e.getMessage());
            throw new RuntimeException("Error al registrar usuario", e);
        }
    }
    public boolean registrarLote(int idUsuario, String descripcion, String procedencia) {
        String query = "INSERT INTO Lote (id_usuario, descripcion, procedencia) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            stmt.setString(2, descripcion);
            stmt.setString(3, procedencia);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar lote: " + e.getMessage());
            return false;
        }
    }
    public boolean registrarImagenesDeLote(int idLote, String ruta) {
        File fileOrDirectory = new File(ruta);

        // Verificamos si la ruta es válida
        if (fileOrDirectory.exists()) {
            // Si es un archivo individual
            if (fileOrDirectory.isFile()) {
                // Subir la imagen y registrar su URL en la base de datos
                String urlImagen = subirImagenACloudinary(fileOrDirectory.getAbsolutePath()); // Método que sube la imagen a Cloudinary y devuelve la URL
                if (urlImagen != null) {
                    // Registrar la imagen en la base de datos
                    return registrarImagenEnBaseDeDatos(idLote, urlImagen);
                }
            }
            // Si es un directorio
            else if (fileOrDirectory.isDirectory()) {
                File[] archivos = fileOrDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");  // Validación para imágenes
                    }
                });

                if (archivos != null && archivos.length > 0) {
                    for (File archivo : archivos) {
                        String urlImagen = subirImagenACloudinary(archivo.getAbsolutePath()); // Subir cada imagen y obtener la URL
                        if (urlImagen != null) {
                            // Registrar cada imagen en la base de datos
                            boolean registrado = registrarImagenEnBaseDeDatos(idLote, urlImagen);
                            if (!registrado) {
                                System.out.println("Error al registrar la imagen: " + archivo.getName());
                                return false;  // Si algún archivo no se registra correctamente, se devuelve false
                            }
                        }
                    }
                    return true;  // Si todas las imágenes fueron procesadas correctamente
                }
            }
        }
        return false;  // Si la ruta no es un archivo ni un directorio válido
    }

    private boolean registrarImagenEnBaseDeDatos(int idLote, String urlImagen) {
        String query = "INSERT INTO Imagen (id_lote, ruta_servicio, fecha_carga) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idLote);
            stmt.setString(2, urlImagen);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;  // Si se insertó la imagen correctamente
        } catch (SQLException e) {
            System.out.println("Error al registrar imagen en la base de datos: " + e.getMessage());
            return false;  // Si ocurre un error al insertar en la base de datos
        }
    }

    private String subirImagenACloudinary(String rutaImagen) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET));

        File archivoImagen = new File(rutaImagen);

        if (!archivoImagen.exists()) {
            System.out.println("La imagen no existe en la ruta especificada.");
            return null;
        }

        try {
            // Subir la imagen a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(archivoImagen, ObjectUtils.emptyMap());
            String urlImagen = (String) uploadResult.get("secure_url"); // URL segura de la imagen subida
            return urlImagen;
        } catch (IOException e) {
            System.out.println("Error al subir la imagen a Cloudinary: " + e.getMessage());
            return null;
        }
    }
    // En DBConnect.java


    public boolean validarCredenciales(String correo, String passwordHash) {
        String query = "SELECT * FROM Usuario WHERE correo = ? AND password = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, correo);
            stmt.setString(2, passwordHash);

            return stmt.executeQuery().next(); // Si hay resultado, las credenciales son válidas
        } catch (SQLException e) {
            System.out.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }





}