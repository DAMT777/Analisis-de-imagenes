/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

import com.databaseInteractions.DBConnect;
import com.processing.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

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
        // en el objeto user están todos los datos del usuario
        //listo el requerimiento
        User user = DBConnect.getInfo(email);
        UserSesionData.setAllUserData(user, DBConnect.getNLotes(user.getId()) );
            loginSucces();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null);
            alert.setContentText("Credenciales incorrectas. Inténtalo de nuevo.");
            alert.showAndWait();
        }
    }

    @FXML
    private void loginSucces() throws IOException {

        if (Objects.equals(UserSesionData.getRolUser(), "admin")){
            App.setRoot("AdminUsersList");
        } else {
            App.setRoot("MainScene"); // Cambias a la segunda escena
        }
    }
}