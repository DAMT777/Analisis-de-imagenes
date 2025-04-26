package com.practicas;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carga la librería nativa
    }

    public static void main(String[] args) {

        try (Connection conn = DBConnect.getConnection()) {
            if (conn != null) {
                System.out.println("¡Conexión exitosa a PostgreSQL desde Main!");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }

        System.out.println("Prueba finalizada");



    }
}

