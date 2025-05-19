package com.databaseInteractions;


import com.google.gson.JsonObject;
import com.processing.User;
import com.services.CloudinaryService;
import com.utils.HashUtil;
import com.processing.Imagen;
import com.processing.Lote;
import com.processing.ResultadoRegistro;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class DBConnect {

    private static final Dotenv dotenv = Dotenv.load();
    private static final CloudinaryService cloudinaryService = new CloudinaryService();



    // Métodos existentes...

    private static String subirImagenACloudinary(String rutaImagen) {
        return cloudinaryService.subirImagen(rutaImagen);
    }
    public static Connection getConnection() {
        // Cargar las variables de entorno desde el archivo .env
        String url = "jdbc:postgresql://" + dotenv.get("PGHOST") + ":" + dotenv.get("PGPORT") + "/" + dotenv.get("PGDATABASE") + "?sslmode=require&pool_mode=" + dotenv.get("POOL_MODE");
        try {
            return DriverManager.getConnection(url, dotenv.get("PGUSER"), dotenv.get("PGPASSWORD"));
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    public static int getNLotes(int idUser) {
        String query = "SELECT COUNT(*) AS total FROM Lote WHERE id_usuario = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUser);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el número de lotes: " + e.getMessage());
        }
        return 0; // Retorna 0 si ocurre un error o no hay lotes
    }

    public static User getInfo(String correo) {
        String query = "SELECT * FROM Usuario WHERE correo = ?";
        User info = null;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, correo);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                info = new User(resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("correo"),
                        resultSet.getString("password"),
                        resultSet.getString("rol")
                );

            }
        } catch (SQLException e) {
            System.out.println("Error al obtener información del usuario: " + e.getMessage());
        }

        return info;
    }
    public static boolean registrarResultadoAnalisis(JsonObject json) {
        // Extraer valores del JSON
        int idImagen = json.get("id").getAsInt();
        int calidadOjos = json.get("calidad_ojos").getAsInt();
        int calidadPiel = json.get("calidad_piel").getAsInt();
        String descripcionImg = json.get("descripcion_img").getAsString();

        // Calcular calidad promedio y redondear al entero más cercano
        double promedio = (calidadOjos + calidadPiel) / 2.0;
        int calidad = (int) Math.round(promedio);

        String query = "INSERT INTO resultadoanalisis (calidad_ojos, id_imagen, descripcion_img, calidad_piel, calidad) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, calidadOjos);
            stmt.setInt(2, idImagen);
            stmt.setString(3, descripcionImg);
            stmt.setInt(4, calidadPiel);
            stmt.setInt(5, calidad);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar resultado de análisis: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarUsuario(String nombre, String apellido, String correo, String passwordHash, String rol) {
        //la password debe estar hasheada antes de
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

    public boolean registrarLote(int idUsuario, String descripcion, boolean registradoInvima, String ciudad, String condiciones) {
        //meter fecha del sistema con java


        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaComoString = ahora.format(formato);
        String query = "INSERT INTO Lote (id_usuario, fecha, descripcion, registrado_invima, ciudad, condiciones) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            stmt.setString(2, fechaComoString);
            stmt.setString(3, descripcion);
            stmt.setBoolean(4, registradoInvima);
            stmt.setString(5, ciudad);
            stmt.setString(6, condiciones);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar lote: " + e.getMessage());
            return false;
        }
    }

   public static ResultadoRegistro registrarImagenesDeLote(Lote lote) {
       // Registrar el lote y obtener su ID
       int idLote = registrarLote(lote);
       if (idLote == -1) {
           return new ResultadoRegistro(false, -1);
       }

       boolean exito = true;
       for (Imagen imagen : lote.getImagenes()) {
           // Subir la imagen a Cloudinary usando su path actual
           String urlImagen = subirImagenACloudinary(imagen.getPath());
           if (urlImagen != null) {
               imagen.setPath(urlImagen);
               int idImagen = registrarImagenEnBaseDeDatos(idLote, imagen);
               if (idImagen != -1) {
                   boolean valoracionRegistrada = registrarResultadoAnalisisDesdeImagen(imagen, idImagen);
                   if (!valoracionRegistrada) {
                       System.out.println("Error al registrar la valoración de la imagen: " + imagen.getPath());
                       exito = false;
                       break;
                   }
               } else {
                   System.out.println("Error al registrar la imagen en la base de datos: " + imagen.getPath());
                   exito = false;
                   break;
               }
           } else {
               System.out.println("Error al subir la imagen a Cloudinary: " + imagen.getPath());
               exito = false;
               break;
           }
       }
       return new ResultadoRegistro(exito, idLote);
   }

    // Metodo auxiliar para registrar la valoración de la imagen
    private static boolean registrarResultadoAnalisisDesdeImagen(Imagen imagen, int idImagen) {
        JsonObject valoracion = imagen.getValoracion();
        if (valoracion == null) return false;

        JsonObject json = new JsonObject();
        json.addProperty("id", idImagen);
        json.addProperty("calidad_ojos", valoracion.get("calificacion_ojos").getAsInt());
        json.addProperty("calidad_piel", valoracion.get("calificacion_piel").getAsInt());
        json.addProperty("descripcion_img", valoracion.has("descripcion_img") ? valoracion.get("descripcion_img").getAsString() : "");

        return registrarResultadoAnalisis(json);
    }

   public static String[] getDataReporte(String id) {
       String[] data = new String[8];
       int idLote = Integer.parseInt(id);
       String queryLote = "SELECT fecha, condiciones, registrado_invima, ciudad FROM Lote WHERE id_lote = ?";
       String queryImagenes = "SELECT COUNT(*) AS total FROM Imagen WHERE id_lote = ?";
       String queryResultados = "SELECT AVG(calidad_ojos) AS prom_ojos, AVG(calidad_piel) AS prom_piel, AVG(calidad) AS prom_calidad, COUNT(*) AS total, SUM(CASE WHEN calidad < 3 THEN 1 ELSE 0 END) AS anomalias FROM resultadoanalisis WHERE id_imagen IN (SELECT id_imagen FROM Imagen WHERE id_lote = ?)";
       try (Connection conn = getConnection()) {
           // Datos del lote
           try (PreparedStatement stmt = conn.prepareStatement(queryLote)) {
               stmt.setInt(1, idLote);
               var rs = stmt.executeQuery();
               if (rs.next()) {
                   data[0] = rs.getString("fecha_creacion"); // fechaAnasis
                   data[2] = rs.getString("condiciones") + " | INVIMA: " + (rs.getBoolean("registrado_invima") ? "1" : "0"); // trazabilidadLote
                   data[3] = rs.getString("ciudad"); // ciudadLote
               }
           }

           // Cantidad de muestras (imagenes)
           try (PreparedStatement stmt = conn.prepareStatement(queryImagenes)) {
               stmt.setInt(1, idLote);
               var rs = stmt.executeQuery();
               if (rs.next()) {
                   data[1] = String.valueOf(rs.getInt("total")); // cantidadMuestras
               }
           }

           // Promedios y anomalías
           try (PreparedStatement stmt = conn.prepareStatement(queryResultados)) {
               stmt.setInt(1, idLote);
               var rs = stmt.executeQuery();
               if (rs.next()) {
                   data[4] = String.format("%.2f", rs.getDouble("prom_calidad")); // calidadPromedio
                   data[5] = String.valueOf(rs.getInt("anomalias")); // cantidadAnomalias
                   data[6] = String.format("%.2f", rs.getDouble("prom_ojos")); // calidadOjosProm
                   data[7] = String.format("%.2f", rs.getDouble("prom_piel")); // calidadPielProm
               }
           }
       } catch (SQLException e) {
           System.out.println("Error al obtener datos del reporte: " + e.getMessage());
       }
       return data;
   }


    public static int registrarLote(Lote lote) {
        String query = "INSERT INTO Lote (id_usuario, descripcion, ciudad) VALUES (?, ?, ?) RETURNING id_lote";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, lote.getIdUsuario());
            stmt.setString(2, lote.getDescripcion());
            stmt.setString(3, lote.getProcedencia());
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                lote.setId(resultSet.getInt("id_lote"));
                return resultSet.getInt("id_lote"); // Retorna el ID generado
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar lote: " + e.getMessage());
        }
        return -1; // Retorna -1 si ocurre un error
    }

    private static int registrarImagenEnBaseDeDatos(int idLote, Imagen imagen) {
        String query = "INSERT INTO Imagen (id_lote, ruta_servicio, fecha_carga) VALUES (?, ?, CURRENT_TIMESTAMP) RETURNING id_imagen";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idLote);
            stmt.setString(2, imagen.getPath());
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_imagen");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar imagen en la base de datos: " + e.getMessage());
        }
        return -1; // Retorna -1 si ocurre un error
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