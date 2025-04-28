/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

/**
 *
 * @author PC
 */

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.practicas.DBConnect;

public class LoginController {
    
    @FXML
    private TextField textFieldEmail;
    
    @FXML
    private PasswordField passwordField;
    
    
    @FXML
    private void userValidation() throws IOException {
       String email = textFieldEmail.getText();
       String password = passwordField.getText();
       
       
      //en este espacio se realiza la validacion de los datos, email y password en la base de datos.
      boolean login = DBConnect.validarCredenciales(email, password);


      if(login) {
          irAnalisisImg();
      } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error de inicio de sesión");
          alert.setHeaderText(null);
          alert.setContentText("Credenciales incorrectas. Inténtalo de nuevo.");
          alert.showAndWait();
      }
       //si la validacion es correcta se llama a la siguiente escena
       //si no es correcta se muestra un mensaje de error

       //llama a la siguiente escena
       //App.setRoot("MainScene");

       //llama a la funcion que cambia de escena
      

    }
    
    
    @FXML
    private void irAnalisisImg() throws IOException {
        App.setRoot("MainScene"); // Cambias a la segunda escena
    }
    
    
}