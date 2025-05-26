package com.databaseInteractions;


import com.google.gson.JsonObject;
import com.processing.User;
import com.services.CloudinaryService;
import com.utils.HashUtil;
import com.processing.Imagen;
import com.processing.Lote;
import com.processing.ResultadoRegistro;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.hc.client5.http.io.ConnectionEndpoint;


public class DBConnect {
    private static final Dotenv dotenv = Dotenv.load();
    private static final CloudinaryService cloudinaryService = new CloudinaryService();
    public static String[] contextoLote = new String[5];

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://" + dotenv.get("PGHOST") + ":" + dotenv.get("PGPORT") + "/" + dotenv.get("PGDATABASE") + "?sslmode=require";
        String user = dotenv.get("PGUSER");
        String password = dotenv.get("PGPASSWORD");
        return java.sql.DriverManager.getConnection(url, user, password);
    }
    /**
     * Obtiene una conexión a la base de datos PostgreSQL usando las variables de entorno del archivo .env.
     *
     * @return Un objeto Connection si la conexión es exitosa, o null si ocurre un error.
     */
















    /**
     * Sube una imagen a Cloudinary usando el servicio CloudinaryService.
     *
     * @param rutaImagen Ruta local de la imagen a subir.
     * @return URL de la imagen subida en Cloudinary, o null si ocurre un error.
     */
    private static String subirImagenACloudinary(String rutaImagen) {
        return cloudinaryService.subirImagen(rutaImagen);
    }






















    /**
     * Obtiene el número total de lotes registrados por un usuario.
     *
     * @param idUser ID del usuario.
     * @return La cantidad de lotes registrados por el usuario, o 0 si ocurre un error.
     */
    public static int getNLotes(int idUser) {
        String query = "SELECT COUNT(*) AS total FROM Lote WHERE id_usuario = ?";
        try (Connection conn = getConnection();
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



















    /**
     * Obtiene la información de un usuario a partir de su correo electrónico.
     *
     * @param correo Correo electrónico del usuario a consultar.
     * @return Un objeto User con los datos del usuario si existe, o null si no se encuentra o hay error.
     */
    public static User getInfo(String correo) {
        String query = "SELECT * FROM Usuario WHERE correo = ?";
        User info = null;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, correo);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                info = new User(resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("correo"),
                        resultSet.getString("empresa"),
                        resultSet.getString("password"),
                        resultSet.getString("rol")
                );

            }
        } catch (SQLException e) {
            System.out.println("Error al obtener información del usuario: " + e.getMessage());
        }

        return info;
    }
    public static List<String[]> getUsuariosEmpresa(String empresa) {
        List<String[]> usuarios = new ArrayList<>();
        String query = "SELECT id_usuario, nombre, apellido, correo, rol FROM usuario WHERE empresa = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, empresa);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                String[] usuario = new String[5];
                usuario[0] = String.valueOf(rs.getInt("id_usuario"));
                usuario[1] = rs.getString("nombre");
                usuario[2] = rs.getString("apellido");
                usuario[3] = rs.getString("correo");
                usuario[4] = rs.getString("rol");
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios por empresa: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Registra el resultado del análisis de una imagen en la base de datos.
     *
     * @param json Objeto JsonObject con los datos del análisis: id de imagen, calificación de ojos, calificación de piel y descripción.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public static boolean registrarResultadoAnalisis(JsonObject json) {
        // Extraer valores del JSON
        int idImagen = json.get("id").getAsInt();
        int calidadOjos = json.get("calidad_ojos").getAsInt();
        int calidadPiel = json.get("calidad_piel").getAsInt();
        String descripcionImg = json.get("descripcion_img").getAsString();
        boolean anomalia = json.get("anomalia").getAsBoolean();

        // Calcular calidad promedio y redondear al entero más cercano
        double promedio = (calidadOjos + calidadPiel) / 2.0;
        int calidad = (int) Math.round(promedio);

        String query = "INSERT INTO resultadoanalisis (calidad_ojos, id_imagen, descripcion_img, calidad_piel, calidad) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
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
















    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param nombre Nombre del usuario.
     * @param apellido Apellido del usuario.
     * @param correo Correo electrónico del usuario.
     * @param passwordHash Contraseña ya hasheada del usuario.
     * @param rol Rol asignado al usuario (por ejemplo, "admin" o "usuario").
     * @return true si el registro fue exitoso, false en caso contrario.
     * @throws RuntimeException si ocurre un error al registrar el usuario.
     */
    public static boolean registrarUsuario(String nombre, String apellido, String empresa, String correo, String passwordHash, String rol) {
        String query = "INSERT INTO usuario (nombre, apellido, empresa, correo, password, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, empresa);
            stmt.setString(4, correo);
            stmt.setString(5, passwordHash);
            stmt.setString(6, rol);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }


    /**
     * Obtiene un usuario de la base de datos a partir de su correo electrónico.
     *
     * @param correo Correo electrónico del usuario a buscar.
     * @return Un objeto User si el usuario existe, o null si no se encuentra o ocurre un error.
     */
    public static User obtenerUsuarioPorCorreo(String correo) {
        String query = "SELECT * FROM usuario WHERE correo = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, correo);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("correo"),
                        rs.getString("empresa"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza los datos de un usuario en la base de datos.
     *
     * @param id ID del usuario a actualizar.
     * @param nombre Nuevo nombre del usuario.
     * @param apellido Nuevo apellido del usuario.
     * @param empresa Nueva empresa del usuario.
     * @param correo Nuevo correo electrónico del usuario.
     * @param rol Nuevo rol asignado al usuario.
     * @return true si la actualización fue exitosa, false si ocurrió un error.
     */
    public static boolean actualizarUsuario(int id, String nombre, String apellido, String empresa, String correo, String rol) {
        String query = "UPDATE usuario SET nombre = ?, apellido = ?, empresa = ?, correo = ?, rol = ? WHERE id_usuario = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, empresa);
            stmt.setString(4, correo);
            stmt.setString(5, rol);
            stmt.setInt(6, id);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un usuario de la base de datos según su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @return true si el usuario fue eliminado correctamente, false si ocurrió un error o no se eliminó ningún usuario.
     */
    public static boolean eliminarUsuario(int id) {
        String query = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

















    /**
     * Obtiene el historial paginado de reportes (lotes) de un usuario.
     *
     * @param idUsuario ID del usuario del cual se consultan los reportes.
     * @return Lista de arreglos de String, donde cada arreglo representa un reporte con los campos:
     *         [0] id_lote
     *         [1] fecha_creacion
     *         [2] condiciones
     *         [3] ciudad
     *         [4] tiempo_pesca
     *         [5] registrado_invima.
     */
    public static List<String[]> getReportHistory(int idUsuario) {
        List<String[]> historial = new ArrayList<>();

        String query = "SELECT id_lote, fecha_creacion, condiciones, ciudad, tiempo_pesca, registrado_invima " +
                "FROM Lote WHERE id_usuario = ? " +
                "ORDER BY fecha_creacion DESC ";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);

            var rs = stmt.executeQuery();
            while (rs.next()) {
                String[] reporte = new String[7];
                reporte[0] = String.valueOf(rs.getInt("id_lote"));
                reporte[1] = rs.getString("fecha_creacion");
                reporte[2] = rs.getString("condiciones");
                reporte[3] = rs.getString("ciudad");
                reporte[4] = rs.getString("tiempo_pesca");
                reporte[5] = String.valueOf(rs.getBoolean("registrado_invima"));
                reporte[6] = "5";
                historial.add(reporte);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener historial de reportes: " + e.getMessage());
        }
        return historial;
    }


















    /**
     * Registra un lote y sus imágenes asociadas en la base de datos y en Cloudinary.
     * Por cada imagen, la sube a Cloudinary, la registra en la base de datos y almacena su valoración.
     *
     * @param lote Objeto Lote que contiene los datos del lote y la lista de imágenes a registrar.
     * @return Un objeto ResultadoRegistro que indica si la operación fue exitosa y el id del lote registrado.
     */
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









    /**
     * Registra el resultado del análisis de una imagen en la base de datos.
     * Extrae la valoración de la imagen, construye un objeto JSON con los datos relevantes
     * y llama al método que inserta el resultado en la base de datos.
     *
     * @param imagen Objeto Imagen que contiene la valoración a registrar.
     * @param idImagen ID de la imagen en la base de datos.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    private static boolean registrarResultadoAnalisisDesdeImagen(Imagen imagen, int idImagen) {
        JsonObject valoracion = imagen.getValoracion();
        if (valoracion == null) return false;

        JsonObject json = new JsonObject();
        json.addProperty("id", idImagen);
        json.addProperty("calidad_ojos", valoracion.get("calificacion_ojos").getAsInt());
        json.addProperty("calidad_piel", valoracion.get("calificacion_piel").getAsInt());
        json.addProperty("descripcion_img", valoracion.has("descripcion_img") ? valoracion.get("descripcion_img").getAsString() : "");
        json.addProperty("anomalia", valoracion.toString());
        return registrarResultadoAnalisis(json);
    }













    /**
     * Obtiene datos detallados de un lote para generar un reporte.
     *
     * @param id ID del lote a consultar.
     * @return Un arreglo de String con la siguiente información:
     *         [0] Fecha de creación del lote,
     *         [1] Cantidad de imágenes asociadas,
     *         [2] Condiciones y estado INVIMA del lote,
     *         [3] Ciudad de procedencia,
     *         [4] Calidad promedio,
     *         [5] Cantidad de anomalías (calidad < 3),
     *         [6] Promedio de calidad de ojos,
     *         [7] Promedio de calidad de piel.
     *         [8] Tiempo de pesca.
     */
   public static String[] getDataReporte(String id) throws SQLException {
       String[] data = new String[9];
       int idLote = Integer.parseInt(id);
       String queryLote = "SELECT fecha_creacion, condiciones, registrado_invima, ciudad, tiempo_pesca FROM Lote WHERE id_lote = ?";
       String queryImagenes = "SELECT COUNT(*) AS total FROM Imagen WHERE id_lote = ?";
       String queryResultados = "SELECT AVG(calidad_ojos) AS prom_ojos, AVG(calidad_piel) AS prom_piel, AVG(calidad) AS prom_calidad, COUNT(*) AS total, SUM(CASE WHEN calidad < 3 THEN 1 ELSE 0 END) AS anomalias FROM resultadoanalisis WHERE id_imagen IN (SELECT id_imagen FROM Imagen WHERE id_lote = ?)";

           // Datos del lote
           try (Connection conn = getConnection();
                   PreparedStatement stmt = conn.prepareStatement(queryLote)) {
               stmt.setInt(1, idLote);
               var rs = stmt.executeQuery();
               if (rs.next()) {
                   data[0] = rs.getString("fecha_creacion"); // fechaAnasis
                   data[2] = rs.getString("condiciones") + " | Registrado en INVIMA: " + (rs.getBoolean("registrado_invima") ? "Si" : "No"); // trazabilidadLote
                   data[3] = rs.getString("ciudad"); // ciudadLote
                   data[8] = rs.getString("tiempo_pesca"); // tiempoPesca
               }
           }

           // Cantidad de muestras (imagenes)
           try (Connection conn = getConnection();
                   PreparedStatement stmt = conn.prepareStatement(queryImagenes)) {
               stmt.setInt(1, idLote);
               var rs = stmt.executeQuery();
               if (rs.next()) {
                   data[1] = String.valueOf(rs.getInt("total")); // cantidadMuestras
               }
           }

           // Promedios y anomalías
           try (Connection conn = getConnection();
                   PreparedStatement stmt = conn.prepareStatement(queryResultados)) {
               stmt.setInt(1, idLote);
               var rs = stmt.executeQuery();
               if (rs.next()) {

                   data[4] = String.format(Locale.US, "%.2f", rs.getDouble("prom_calidad")); // calidadPromedio
                   data[5] = String.valueOf(rs.getInt("anomalias")); // cantidadAnomalias
                   data[6] = String.format("%.2f", rs.getDouble("prom_ojos")); // calidadOjosProm
                   data[7] = String.format("%.2f", rs.getDouble("prom_piel")); // calidadPielProm
               }
           }
        catch (SQLException e) {
           System.out.println("Error al obtener datos del reporte: " + e.getMessage());
       }
       return data;
   }

    /**
     * Obtiene los reportes de lotes de un usuario.
     *
     * @param idUsuario ID del usuario del cual se consultan los reportes.
     * @return Lista de listas de String, donde cada sublista contiene:
     *         [0] id_lote,
     *         [1] fecha_analisis,
     *         [2] condicion,
     *         [3] ciudad,
     *         [4] tiempo_pesca,
     *         [5] Invima,
     *         [6] promediocalificacion.
     */
   public static List<List<String>> getReportesUsuario(int idUsuario) {
       List<List<String>> reportes = new ArrayList<>();
       String query = "SELECT l.id_lote, l.fecha_creacion, l.condiciones, l.ciudad, l.tiempo_pesca, l.registrado_invima, " +
                      "COALESCE(AVG(r.calidad), 0) AS promedio_calificacion " +
                      "FROM Lote l " +
                      "LEFT JOIN Imagen i ON l.id_lote = i.id_lote " +
                      "LEFT JOIN resultadoanalisis r ON i.id_imagen = r.id_imagen " +
                      "WHERE l.id_usuario = ? " +
                      "GROUP BY l.id_lote, l.fecha_creacion, l.condiciones, l.ciudad, l.tiempo_pesca, l.registrado_invima " +
                      "ORDER BY l.fecha_creacion DESC";
       try (Connection conn = getConnection();
               PreparedStatement stmt = conn.prepareStatement(query)) {
           stmt.setInt(1, idUsuario);
           var rs = stmt.executeQuery();
           while (rs.next()) {
               List<String> reporte = new ArrayList<>();
               reporte.add(String.valueOf(rs.getInt("id_lote")));
               reporte.add(rs.getString("fecha_creacion"));
               reporte.add(rs.getString("condiciones"));
               reporte.add(rs.getString("ciudad"));
               reporte.add(rs.getString("tiempo_pesca"));
               reporte.add(String.valueOf(rs.getBoolean("registrado_invima")));
               reporte.add(String.format(Locale.US, "%.2f", rs.getDouble("promedio_calificacion")));
               reportes.add(reporte);
           }
       } catch (SQLException e) {
           System.out.println("Error al obtener reportes del usuario: " + e.getMessage());
       }
       return reportes;
   }






   /**
    * Obtiene todos los reportes (lotes) de la base de datos donde el usuario dueño del lote
    * tiene el atributo `consentimiento_informado` establecido en true.
    *
    * <p>
    * Cada reporte es una lista de Strings con la siguiente información:
    * <ul>
    *   <li>[0] id_lote</li>
    *   <li>[1] fecha_creacion</li>
    *   <li>[2] condiciones</li>
    *   <li>[3] ciudad</li>
    *   <li>[4] tiempo_pesca</li>
    *   <li>[5] registrado_invima</li>
    *   <li>[6] promedio_calificacion</li>
    * </ul>
    * </p>
    *
    * @return Lista de reportes filtrados por consentimiento informado.
    *         Cada reporte es una lista de Strings con los campos mencionados.
    */
    public static List<List<String>> getReportesConConsentimiento() {
        List<List<String>> reportes = new ArrayList<>();
        String query = "SELECT l.id_lote, l.fecha_creacion, l.condiciones, l.ciudad, l.tiempo_pesca, l.registrado_invima, " +
                       "COALESCE(AVG(r.calidad), 0) AS promedio_calificacion " +
                       "FROM Lote l " +
                       "JOIN usuario u ON l.id_usuario = u.id_usuario " +
                       "LEFT JOIN Imagen i ON l.id_lote = i.id_lote " +
                       "LEFT JOIN resultadoanalisis r ON i.id_imagen = r.id_imagen " +
                       "WHERE u.consentimiento_informado = TRUE " +
                       "GROUP BY l.id_lote, l.fecha_creacion, l.condiciones, l.ciudad, l.tiempo_pesca, l.registrado_invima " +
                       "ORDER BY l.fecha_creacion DESC";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                List<String> reporte = new ArrayList<>();
                reporte.add(String.valueOf(rs.getInt("id_lote")));
                reporte.add(rs.getString("fecha_creacion"));
                reporte.add(rs.getString("condiciones"));
                reporte.add(rs.getString("ciudad"));
                reporte.add(rs.getString("tiempo_pesca"));
                reporte.add(String.valueOf(rs.getBoolean("registrado_invima")));
                reporte.add(String.format(Locale.US, "%.2f", rs.getDouble("promedio_calificacion")));
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener reportes con consentimiento informado: " + e.getMessage());
        }
        return reportes;
    }









    /**
     * Inserta un nuevo lote en la base de datos y retorna su ID generado.
     *
     * @param lote Objeto Lote con los datos a registrar.
     * @return El ID del lote registrado si la operación fue exitosa, o -1 si ocurrió un error.
     */
    public static int registrarLote(Lote lote) {
        String query = "INSERT INTO Lote (id_usuario, condiciones, ciudad, fecha_creacion, tiempo_pesca, registrado_invima) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_lote";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, lote.getIdUsuario());
            stmt.setString(2, lote.getCondiciones());
            stmt.setString(3, lote.getProcedencia());

            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(ahora);
            stmt.setTimestamp(4, timestamp);

            stmt.setString(5, lote.getTiempoPesca());
            stmt.setBoolean(6, lote.isRegistradoInvima());

            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                lote.setId(resultSet.getInt("id_lote"));
                return resultSet.getInt("id_lote");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar lote: " + e.getMessage());
        }
        return -1;
    }




    /**
     * Inserta una imagen asociada a un lote en la base de datos y retorna su ID generado.
     *
     * @param idLote ID del lote al que pertenece la imagen.
     * @param imagen Objeto Imagen que contiene la ruta de la imagen a registrar.
     * @return El ID de la imagen registrada si la operación fue exitosa, o -1 si ocurrió un error.
     */
    private static int registrarImagenEnBaseDeDatos(int idLote, Imagen imagen) {
        String query = "INSERT INTO Imagen (id_lote, ruta_servicio, nombre_archivo, fecha_carga) VALUES (?, ?, ?, CURRENT_TIMESTAMP) RETURNING id_imagen";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idLote);
            stmt.setString(2, imagen.getPath());
            stmt.setString(3, imagen.getFilename());
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_imagen");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar imagen en la base de datos: " + e.getMessage());
        }
        return -1; // Retorna -1 si ocurre un error
    }


















    /**
     * Valida las credenciales de un usuario verificando el correo y la contraseña.
     *
     * @param correo Correo electrónico del usuario.
     * @param password Contraseña ingresada por el usuario.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    public static boolean validarCredenciales(String correo, String password) {
        String query = "SELECT password FROM Usuario WHERE correo = ?";

        try (Connection conn = getConnection();
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



    public static String getProfilePath(int idUsuario) {
        String query = "SELECT img_profile_path FROM usuario WHERE id_usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("img_profile_path");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener profile_path: " + e.getMessage());
        }
        return null;
    }























   public static List<String[]> getXLSXinfo(String id) {
       int idLote = Integer.parseInt(id);
       List<String[]> info = new ArrayList<>();
       String query = "SELECT i.nombre_archivo, i.ruta_servicio, r.calidad_piel, r.calidad_ojos, r.calidad " +
                      "FROM Imagen i " +
                      "LEFT JOIN resultadoanalisis r ON i.id_imagen = r.id_imagen " +
                      "WHERE i.id_lote = ?";

       try (Connection conn = getConnection();
               PreparedStatement stmt = conn.prepareStatement(query)) {
           stmt.setInt(1, idLote);
           var rs = stmt.executeQuery();
           while (rs.next()) {
               String[] fila = new String[5];
               fila[0] = rs.getString("nombre_archivo");
               fila[1] = rs.getString("ruta_servicio");
               fila[2] = rs.getString("calidad_piel");
               fila[3] = rs.getString("calidad_ojos");
               fila[4] = rs.getString("calidad");
               info.add(fila);
           }
       } catch (SQLException e) {
           System.out.println("Error al obtener informacion para XLSX: " + e.getMessage());
       }
       return info;
   }

// Actualiza el correo electrónico de un usuario por su ID
public static boolean actualizarCorreoUsuario(int idUsuario, String nuevoCorreo) {
    String query = "UPDATE usuario SET correo = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, nuevoCorreo);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.out.println("Error al actualizar correo: " + e.getMessage());
        return false;
    }
}

// Actualiza la contraseña (ya hasheada) de un usuario por su ID
public static boolean actualizarPasswordUsuario(int idUsuario, String nuevoPasswordHash) {
    String query = "UPDATE usuario SET password = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, nuevoPasswordHash);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.out.println("Error al actualizar contraseña: " + e.getMessage());
        return false;
    }
}

// Actualiza el nombre de un usuario por su ID
public static boolean actualizarNombreUsuario(int idUsuario, String nuevoNombre) {
    String query = "UPDATE usuario SET nombre = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, nuevoNombre);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.out.println("Error al actualizar nombre: " + e.getMessage());
        return false;
    }
}

// Actualiza el apellido de un usuario por su ID
public static boolean actualizarApellidoUsuario(int idUsuario, String nuevoApellido) {
    String query = "UPDATE usuario SET apellido = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, nuevoApellido);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.out.println("Error al actualizar apellido: " + e.getMessage());
        return false;
    }
}

// Actualiza la empresa de un usuario por su ID
public static boolean actualizarEmpresaUsuario(int idUsuario, String nuevaEmpresa) {
    String query = "UPDATE usuario SET empresa = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, nuevaEmpresa);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.out.println("Error al actualizar empresa: " + e.getMessage());
        return false;
    }
}

// Actualiza el rol de un usuario por su ID
public static boolean actualizarRolUsuario(int idUsuario, String nuevoRol) {
    String query = "UPDATE usuario SET rol = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, nuevoRol);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.out.println("Error al actualizar rol: " + e.getMessage());
        return false;
    }
}
public static String actualizarImagenPerfil(int idUsuario, String rutaImagenLocal) {
    // Subir la imagen a Cloudinary
    String urlImagen = cloudinaryService.subirImagen(rutaImagenLocal);
    if (urlImagen == null) {
        System.out.println("No se pudo subir la imagen de perfil a Cloudinary.");
        return null;
    }

    // Actualizar la URL en la base de datos
    String query = "UPDATE usuario SET img_profile_path = ? WHERE id_usuario = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, urlImagen);
        stmt.setInt(2, idUsuario);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0 ? urlImagen : null;
    } catch (SQLException e) {
        System.out.println("Error al actualizar la imagen de perfil: " + e.getMessage());
        return null;
    }
}




}