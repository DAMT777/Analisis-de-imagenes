package com.practicas;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public static Connection getConnection() {
        // Cargar las variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.load();
        String url = "jdbc:postgresql://"+dotenv.get("PGHOST")+":"+ dotenv.get("PGPORT")+"/"+ dotenv.get("PGDATABASE")+"?sslmode=require&pool_mode=" + dotenv.get("POOL_MODE");

        try {
            return DriverManager.getConnection(url, dotenv.get("PGUSER"), dotenv.get("PGPASSWORD"));
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DBConnect.getConnection()) {
            if (conn != null) {
                System.out.println("¡Conexión exitosa a PostgreSQL!");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}