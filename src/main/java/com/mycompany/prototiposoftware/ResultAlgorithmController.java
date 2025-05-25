/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;


import com.databaseInteractions.DBConnect;
import com.processing.Lote;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.processing.Lote;
import javafx.stage.Stage;

public class ResultAlgorithmController implements Initializable {

    String[] datosPDFLote = new String[9]; // debes asignar algo

    String idLote = "" + Lote.getId();
    String fechaAnalisis;
    String cantidadMuestras;
    String ciudadLote;
    String trazabilidadLote;
    String calidadPromedio;
    String calidadOjosProm;
    String calidadPielProm;
    String cantidadAnomalias;
    String tiempoPesca;
    List<String[]> analisisIndividuales;


    @FXML
    private HBox analisisHbox;
    @FXML
    private HBox reportsHBox;
    @FXML
    private HBox userHBox;
    @FXML
    private HBox adminListUserHBox;

    @FXML
    private void handleHBoxClick(MouseEvent event) {
        try {
            // Obtener el HBox que disparó el evento
            HBox clickedHBox = (HBox) event.getSource();

            // Obtener el fx:id del HBox
            String hboxId = clickedHBox.getId();

            // Obtener el Stage actual desde el HBox
            Stage stage = (Stage) clickedHBox.getScene().getWindow();

            // Cambiar escena usando Utilities
            changeScene.changeScene(stage, hboxId);  // Asumiendo que el archivo fxml se llama igual que el id + ".fxml"

        } catch (Exception e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje al usuario si quieres
        }
    }





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


    String calidadFinalTextual;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datosPDFLote = DBConnect.getDataReporte(idLote);
        if (datosPDFLote.length == 0) {
            // Está vacío o no inicializado
            System.out.println("El array está vacío o es null");
        }

        fechaAnalisis = datosPDFLote[0];
        cantidadMuestras = datosPDFLote[1];
        trazabilidadLote = datosPDFLote[2];
        ciudadLote = datosPDFLote[3];
        calidadPromedio = datosPDFLote[4];
        cantidadAnomalias = datosPDFLote[5];
        calidadOjosProm = datosPDFLote[6];
        calidadPielProm = datosPDFLote[7];
        tiempoPesca = datosPDFLote[8];
        calidadFinalTextual = determinarCalidad(calidadPromedio);
        // Enlazar columnas con las propiedades del modelo
        colOjos.setCellValueFactory(new PropertyValueFactory<>("calidadOjos"));
        colPiel.setCellValueFactory(new PropertyValueFactory<>("calidadPiel"));
        colAnomalias.setCellValueFactory(new PropertyValueFactory<>("anomaliasEncontradas"));
        colMuestras.setCellValueFactory(new PropertyValueFactory<>("cantidadMuestras"));

        // Visual de los datos en la tabla de resultados


        // añadir el tiempo de pesca a la tabla
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
        analisisIndividuales = DBConnect.getXLSXinfo(idLote);
        reporte.PDFgenerator(idLote, datosPDFLote, rutaGeneracionPDF, analisisIndividuales);
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