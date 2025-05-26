/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;


import com.databaseInteractions.DBConnect;
import com.processing.Lote;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Label rolLabel;

    // ---------------------------------------------------------------------------------- Menu Lateral
    @FXML
    private AnchorPane menuBox;  // menu expandible

    @FXML
    private void menuBoxExpand() {
        menuBox.setVisible(!menuBox.isVisible());
        menuBox.setManaged(menuBox.isVisible());
    }

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
    // ----------------------------------------------------------------------------------------Fin Menu Lateral


    // ---------------------------------------------------------------------------------------Tool bar inferior
    @FXML
    private javafx.scene.control.Button userSceneToolBar;
    @FXML
    private javafx.scene.control.Button settingsSceneToolBar;
    @FXML
    private javafx.scene.control.Button aboutUsSceneToolBar;
    @FXML
    private javafx.scene.control.Button helpSceneToolBar;

    @FXML
    private void toolBarBoxClick(ActionEvent event) {
        try {
            // Obtener el HBox que disparó el evento
            javafx.scene.control.Button clickedButton = (Button) event.getSource();

            // Obtener el fx:id del HBox
            String buttonId = clickedButton.getId();

            // Obtener el Stage actual desde el HBox
            Stage stage = (Stage) clickedButton.getScene().getWindow();

            // Cambiar escena usando Utilities
            changeScene.changeScene(stage, buttonId);  // Asumiendo que el archivo fxml se llama igual que el id + ".fxml"

        } catch (Exception e) {
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje al usuario si quieres
        }
    }
    // ---------------------------------------------------------------------------------------Fin Tool bar inferior


    public void errorMessage(String message) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(message);

        alerta.showAndWait();
    }

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
        if (Objects.equals(UserSesionData.getRolUser(), "user")) {
            adminListUserHBox.setDisable(true);
        }
        rolLabel.setText("Rol: " + UserSesionData.getRolUser());

        cargarDatos();
    }

    public void setInfoReportHistory(TableUserHistoryReports u) {
        idLote = u.getIdLote();
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            datosPDFLote = DBConnect.getDataReporte(idLote);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (datosPDFLote.length == 0) {
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

        colOjos.setCellValueFactory(new PropertyValueFactory<>("calidadOjos"));
        colPiel.setCellValueFactory(new PropertyValueFactory<>("calidadPiel"));
        colAnomalias.setCellValueFactory(new PropertyValueFactory<>("anomaliasEncontradas"));
        colMuestras.setCellValueFactory(new PropertyValueFactory<>("cantidadMuestras"));

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
    //crear metodo para guardar excel

    @FXML
    private void generateExcel() throws IOException {

        String ruta;

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta para guardar el reporte Excel");


        // Puedes pasarle el Stage principal si lo tienes
        File selectedDirectory = directoryChooser.showDialog(null); // o reemplaza null con tu Stage si lo tienes

        if (selectedDirectory != null) {
            ruta = selectedDirectory.getAbsolutePath();
        } else {
            return;
        }

        String rutaGeneracionExcel = ruta;

        ReportGenerator reporte = new ReportGenerator();
        analisisIndividuales = DBConnect.getXLSXinfo(idLote);
        //reporte.PDFgenerator(idLote, datosPDFLote, rutaGeneracionExcel, analisisIndividuales);
        reporte.exportarExcel(rutaGeneracionExcel,analisisIndividuales);
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