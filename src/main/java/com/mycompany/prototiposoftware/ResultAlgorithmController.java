/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;


import com.databaseInteractions.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.processing.Lote;

public class ResultAlgorithmController implements Initializable {

    String[] datosPDFLote = new String[7]; // debes asignar algo



    String idLote = "" + Lote.getId();
    String []params = DBConnect.getDataReporte("" + Lote.getId());
    String fechaAnalisis = params[0];
    String cantidadMuestras = params[1];
    String ciudadLote = params[3];
    String trazabilidadLote = params[2];
    String calidadPromedio = params[4];
    String calidadOjosProm = params[6];
    String calidadPielProm = params[7];
    String cantidadAnomalias = "0";



    List<String[]> analisisIndividuales = new ArrayList<>();

    @FXML
    private AnchorPane menuBox;  // menu expandible

    @FXML
    private Label labelCalificacionPromedio;

    @FXML
    private Label labelCalidadLote;

    @FXML
    private TableView<TableViewData> tablaRegistros;

    @FXML
    private TableColumn<TableViewData, String> colOjos;

    @FXML
    private TableColumn<TableViewData, String> colPiel;

    @FXML
    private TableColumn<TableViewData, Integer> colAnomalias;

    @FXML
    private TableColumn<TableViewData, Integer> colMuestras;


    String calidadFinalTextual = determinarCalidad(calidadPromedio);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("valor de calidadpronedio : " + calidadPromedio + " texto  "+ calidadFinalTextual);
        System.out.println(idLote);
        datosPDFLote = DBConnect.getDataReporte(idLote);
        System.out.println("bien el metodo");
        if (datosPDFLote == null || datosPDFLote.length == 0) {
            // Está vacío o no inicializado
            System.out.println("El array está vacío o es null");
        }

        fechaAnalisis = datosPDFLote[0];
        trazabilidadLote = datosPDFLote[3];
        ciudadLote = datosPDFLote[2];
        cantidadMuestras = datosPDFLote[1];
        calidadPromedio = datosPDFLote[4];
        cantidadAnomalias = datosPDFLote[5];
        calidadOjosProm = datosPDFLote[6];
        calidadPielProm = datosPDFLote[7];



        // Enlazar columnas con las propiedades del modelo
        colOjos.setCellValueFactory(new PropertyValueFactory<>("calidadOjos"));
        colPiel.setCellValueFactory(new PropertyValueFactory<>("calidadPiel"));
        colAnomalias.setCellValueFactory(new PropertyValueFactory<>("anomaliasEncontradas"));
        colMuestras.setCellValueFactory(new PropertyValueFactory<>("cantidadMuestras"));

        // Visual de los datos en la tabla de resultados
        ObservableList<TableViewData> datos = FXCollections.observableArrayList(
                new TableViewData(calidadOjosProm, calidadPielProm, cantidadAnomalias, cantidadMuestras)
        );

        tablaRegistros.setItems(datos);
        tablaRegistros.getStylesheets().add(getClass().getResource("/com/mycompany/prototiposoftware/styles/tableStyles.css").toExternalForm());

        labelCalificacionPromedio.setText(calidadPromedio);
        labelCalidadLote.setText(calidadFinalTextual);
    }


    @FXML
    private void generatePdf() throws IOException {

        String ruta;

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta para guardar el PDF");


        // Puedes pasarle el Stage principal si lo tienes
        File selectedDirectory = directoryChooser.showDialog(null); // o reemplaza null con tu Stage si lo tienes

        if (selectedDirectory != null) {
            ruta = selectedDirectory.getAbsolutePath();
        } else {
            return;
        }

        String rutaGeneracionPDF = ruta;

        ReportGenerator reporte = new ReportGenerator();

        reporte.PDFgenerator(idLote, fechaAnalisis, cantidadMuestras, trazabilidadLote, ciudadLote, calidadPromedio, calidadOjosProm, calidadPielProm, cantidadAnomalias, rutaGeneracionPDF ,analisisIndividuales, calidadFinalTextual);
        analisisIndividuales.clear(); // Limpia la lista

        File carpeta = new File(ruta);

        if (carpeta.exists() && carpeta.isDirectory()) {
            try {
                Desktop.getDesktop().open(carpeta);
            } catch (IOException e) {
                System.out.println("⚠️ Error al abrir la carpeta: " + e.getMessage());
            }
        } else {
            System.out.println("❌ La ruta no es válida o no existe: " + ruta);
        }
    }

    @FXML
    private void menuBoxExpand() {
        menuBox.setVisible(!menuBox.isVisible());
        menuBox.setManaged(menuBox.isVisible());
    }

    @FXML
    private void irAnalisisImg() throws IOException {
        App.setRoot("MainScene");
    }

    @FXML
    private void irALoginController() throws IOException {
        UserSesionData.clearSession();
        App.setRoot("LoginScene");
    }


    public static String determinarCalidad(String valorString) {

        try {
            double valor = Double.parseDouble(valorString);
            System.out.println("el valor es: " + valor);
            if (valor < 1.0 || valor > 5.0) {
                return "Valor fuera de rango";
            }

            if (valor >= 1.0 && valor < 2.0) {
                return "Baja";
            } else if (valor >= 2.0 && valor < 3.0) {
                return "Media";
            } else if (valor >= 3.0 && valor < 4.0) {
                return "Aceptable";
            } else if (valor >= 4.0 && valor < 5.0) {
                return "Buena";
            } else {
                return "Perfecta";
            }

        } catch (NumberFormatException e) {
            return "Valor inválido";
        }
    }

}