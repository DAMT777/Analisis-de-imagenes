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
    public boolean registrarUsuario(String nombre,String apellido, String correo, String passwordHash, String rol) {
        String query = "INSERT INTO Usuario (nombre, apellido, correo, password, rol) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, correo);
            stmt.setString(4, passwordHash);
            stmt.setString(5, rol);
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
    public static ResultadoRegistro registrarImagenesDeLote(Lote lote) {
        String ruta = lote.getPath(); // Obtener la ruta del lote
        File fileOrDirectory = new File(ruta);

        // Verificamos si la ruta es válida
        if (fileOrDirectory.exists()) {
            int idLote = registrarLoteEnBaseDeDatos(lote); // Registrar el lote y obtener el ID
            if (idLote == -1) {
                return new ResultadoRegistro(false, -1); // Error al registrar el lote
            }

            // Si es un archivo individual
            if (fileOrDirectory.isFile()) {
                Imagen imagen = new Imagen(fileOrDirectory.getAbsolutePath());
                String urlImagen = subirImagenACloudinary(imagen.getPath());
                if (urlImagen != null) {
                    imagen.setPath(urlImagen);
                    boolean registrado = registrarImagenEnBaseDeDatos(idLote, imagen);
                    return new ResultadoRegistro(registrado, idLote);
                }
            }
            // Si es un directorio
            else if (fileOrDirectory.isDirectory()) {
                File[] archivos = fileOrDirectory.listFiles((dir, name) ->
                        name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
                );

                if (archivos != null && archivos.length > 0) {
                    for (File archivo : archivos) {
                        Imagen imagen = new Imagen(archivo.getAbsolutePath());
                        String urlImagen = subirImagenACloudinary(imagen.getPath());
                        if (urlImagen != null) {
                            imagen.setPath(urlImagen);
                            boolean registrado = registrarImagenEnBaseDeDatos(idLote, imagen);
                            if (!registrado) {
                                System.out.println("Error al registrar la imagen: " + archivo.getName());
                                return new ResultadoRegistro(false, idLote);
                            }
                        }
                    }
                    return new ResultadoRegistro(true, idLote);
                }
            }
        }
        return new ResultadoRegistro(false, -1); // Si la ruta no es válida
    }

    private static int registrarLoteEnBaseDeDatos(Lote lote) {
        String query = "INSERT INTO Lote (id_usuario, descripcion, procedencia) VALUES (?, ?, ?) RETURNING id_lote";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, lote.getIdUsuario());
            stmt.setString(2, "");
            stmt.setString(3, "");
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_lote"); // Retorna el ID generado
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar lote: " + e.getMessage());
        }
        return -1; // Retorna -1 si ocurre un error
    }

    private static boolean registrarImagenEnBaseDeDatos(int idLote, Imagen imagen) {
        String query = "INSERT INTO Imagen (id_lote, ruta_servicio, fecha_carga) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idLote);
            stmt.setString(2, imagen.getPath()); // Usar la ruta (URL) desde el objeto Imagen
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Si se insertó la imagen correctamente
        } catch (SQLException e) {
            System.out.println("Error al registrar imagen en la base de datos: " + e.getMessage());
            return false; // Si ocurre un error al insertar en la base de datos
        }
    }

    private static String subirImagenACloudinary(String rutaImagen) {
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


    public static boolean validarCredenciales(String correo, String password) {
        String query = "SELECT password FROM Usuario WHERE correo = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, correo);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");
                // Verificar la contraseña ingresada con el hash almacenado
                return HashUtil.verifyPassword(password, hashedPassword);
            }
            return false; // Usuario no encontrado
        } catch (SQLException e) {
            System.out.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }





}