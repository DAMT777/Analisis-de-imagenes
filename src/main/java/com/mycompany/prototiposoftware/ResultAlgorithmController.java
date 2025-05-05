/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 *
 * @author PC
 */


public class ResultAlgorithmController {
    
    @FXML
    private AnchorPane menuBox;  // menu expandible
    
    @FXML
    private void menuBoxExpand() {
        menuBox.setVisible(!menuBox.isVisible());
        menuBox.setManaged(menuBox.isVisible());
    }
    
    @FXML
    private void irAnalisisImg() throws IOException {
        App.setRoot("MainScene"); // Cambias a la segunda escena
    }
    
    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        App.setRoot("LoginScene"); 
    }    
}
