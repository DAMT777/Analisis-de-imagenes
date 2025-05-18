/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

/**
 * @author PC
 */

import com.databaseInteractions.DBConnect;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

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


        if (login) {

        /*Es necesario invocar en esta parte una funcion por parte del backend que reciba un objeto UserSesionData
        por ejemplo, un metodo DBConnect.loginUser(email)

        Este metodo deberia (segun yo) extraer la info del usuario desde la base de datos filtrando por correo
        y debe almacenar la info en la clase UserSesionData mediante su metodo setAllUserData();

        Por ejemplo, asumiendo que esta dentro del metodo DBConnect.loginUser(email)

        DBConnect.loginUser(email){
            UserSesionData.setAllUserData(db.getUserID(email),
                                          db.getUserFirtsName(email),
                                          db.getUserSecondName(email),
                                          db.getUserLastName(email) o algo asi
                                          ...
                                          ...
                                          db.getUserCantLotes(email));
        }
        */
            irAnalisisImg();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null);
            alert.setContentText("Credenciales incorrectas. Inténtalo de nuevo.");
            alert.showAndWait();
        }
    }


    @FXML
    private void irAnalisisImg() throws IOException {
        App.setRoot("MainScene"); // Cambias a la segunda escena
    }


}