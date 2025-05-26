/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

import com.databaseInteractions.DBConnect;
import com.processing.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> anchorPaneLogin.requestFocus());
    }

    @FXML
    private AnchorPane anchorPaneLogin;

    @FXML
    private Label labelError;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private PasswordField passwordField;

    private boolean esEmailValido(String email) {
        // Expresión regular simple para validar estructura de email
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    @FXML
    private void userValidation() throws IOException {
        String email = textFieldEmail.getText();
        String password = passwordField.getText();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                labelError.setText("Ninguno de los campos puede estar vacío");
                return;
        } else {
            // Validar que el email tenga formato correcto
            if (!esEmailValido(email)) {
                labelError.setText("El correo ingresado no es válido.");
                return;
            } else {
                labelError.setText(""); // Limpia el error si ya es válido
            }
        }



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