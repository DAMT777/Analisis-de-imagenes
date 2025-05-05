/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

/**
 *
 * @author PC
 */


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class dialogController {

    @FXML private TextField textField1;
    @FXML private TextField textField2;

    // Variables para exponer los datos fuera
    private String datoDescripcion;
    private String datoOrigen;

    // Handler del botón “Enviar”
    @FXML
    private void onEnviar() {
        datoDescripcion  = textField1.getText();
        datoOrigen = textField2.getText();
        // Cerrar la ventana
        Stage stage = (Stage) textField1.getScene().getWindow();
        stage.close();
    }

    // Getters para que el controlador principal acceda
    public String getDescripcion() {
        return datoDescripcion;
    }
    public String getOrigen() {
        return datoOrigen;
    }
    
}
