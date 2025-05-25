package com.processing;

import com.databaseInteractions.DBConnect;
import com.mycompany.prototiposoftware.ReportGenerator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        List<String[]> data = DBConnect.getReportHistory(1);
        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i);
            for (int j = 0; j < row.length; j++) {
                System.out.print(row[j] + " ");
            }
            System.out.println();
        }


//        ReportGenerator report = new ReportGenerator();
//
//        // Datos de prueba
//        List<String[]> registros = new ArrayList<>();
//        registros.add(new String[]{"SkinMinecraft.jpg", "C:/imagenes/juan.jpg", "5", "Excelente", "Muy buena"});
//        registros.add(new String[]{"Fish2.jpg", "C:/imagenes/ana.jpg", "5", "Buena", "Buena"});
//        registros.add(new String[]{"Fish3.jpg", "C:/imagenes/carlos.jpg", "4", "Regular", "Regular"});
//
//        try {
//            report.exportarExcel("C:/Users/jesus/Downloads/tabla_individual.xlsx", registros);
//            System.out.println("Excel generado correctamente.");
//        } catch (IOException e) {
//            System.err.println("Error al generar el Excel: " + e.getMessage());
//        }
    }
}






