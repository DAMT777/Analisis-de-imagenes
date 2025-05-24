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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
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


    // Variables para exponer los datos fuera
    private String datoCiudad;
    private String datoInvima;
    private String datoCondicion;
    private String datoTiempoPesca;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
