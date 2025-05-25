/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

/**
 * @author PC
 */


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class dialogController implements Initializable {

    @FXML
    private ComboBox<String> comboBoxCiudad;
    @FXML
    private ComboBox<String> comboBoxInvima;
    @FXML
    private ComboBox<String> comboBoxCondicion;
    @FXML
    private ComboBox<String> comboBoxTiempoPesca;
    @FXML
    private VBox warning;


    // Variables para exponer los datos fuera
    private String datoCiudad;
    private String datoInvima;
    private String datoCondicion;
    private String datoTiempoPesca;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warning.setVisible(false);
        comboBoxCiudad.setItems(FXCollections.observableArrayList(
                "Leticia",
                "Medellín",
                "Cali",
                "Bogotá",
                "Barranquilla",
                "Cartagena",
                "Cúcuta",
                "Bucaramanga",
                "Pereira",
                "Santa Marta",
                "Ibagué",
                "Manizales",
                "Neiva",
                "Villavicencio",
                "Montería",
                "Armenia",
                "Popayán",
                "Valledupar",
                "Sincelejo",
                "Tunja",
                "Florencia",
                "Quibdó",
                "San Andrés",
                "Yopal",
                "Mitú",
                "Puerto Carreño"
        ));
        comboBoxCiudad.getItems().sort(String::compareToIgnoreCase);

        comboBoxInvima.setItems(FXCollections.observableArrayList(
                "Si",
                "No"
        ));

        comboBoxCondicion.setItems(FXCollections.observableArrayList(
                "Recien Pescado",
                "Refrigerado",
                "Almacenado en hielo",
                "No Refrigerado"
        ));

        comboBoxTiempoPesca.setItems(FXCollections.observableArrayList(
                "1 Día",
                "2-3 Dias",
                "3-4 Dias",
                "4-5 Dias",
                "5-6 Dias",
                "1 Semana",
                "Mas de 1 Semana"
        ));
    }

    // Handler del botón “Enviar”
    @FXML
    private void onEnviar() {


        List<ComboBox<String>> comboBoxes = Arrays.asList(
                comboBoxCiudad, comboBoxInvima, comboBoxCondicion, comboBoxTiempoPesca
        );

        boolean todosSeleccionados = comboBoxes.stream().allMatch(cb -> cb.getValue() != null && !cb.getValue().isEmpty());
        if (!todosSeleccionados) {
            warning.setVisible(true);
            return;
        }

        datoCiudad = comboBoxCiudad.getValue();
        datoInvima = comboBoxInvima.getValue();
        datoCondicion = comboBoxCondicion.getValue();
        datoTiempoPesca = comboBoxTiempoPesca.getValue();

        // Cerrar la ventana
        Stage stage = (Stage) comboBoxTiempoPesca.getScene().getWindow();
        stage.close();
    }

    // Getters para que el controlador principal acceda
    public String getCiudad() {return datoCiudad;}
    public String getInvima() {
        return datoInvima;
    }
    public String getCondicion() {return datoCondicion;}
    public String getTiempoPesca() {return datoTiempoPesca;}
}
