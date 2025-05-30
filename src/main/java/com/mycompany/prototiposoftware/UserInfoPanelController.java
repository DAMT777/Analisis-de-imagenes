package com.mycompany.prototiposoftware;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserInfoPanelController implements Initializable {

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
    private Button userSceneToolBar;
    @FXML
    private Button settingsSceneToolBar;
    @FXML
    private Button aboutUsSceneToolBar;
    @FXML
    private Button helpSceneToolBar;

    @FXML
    private void toolBarBoxClick(ActionEvent event) {
        try {
            // Obtener el HBox que disparó el evento
            Button clickedButton = (Button) event.getSource();

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

    public void successMessage(String message) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Cambiamos de ERROR a INFORMATION
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(message);

        alerta.showAndWait();
    }
    @FXML
    private void irMainScene() throws IOException {
        if (Objects.equals(UserSesionData.getRolUser(), "user")){
            App.setRoot("MainScene");
        } else {
            App.setRoot("AdminUsersList");
        }
    }

    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        UserSesionData.clearSession();
        App.setRoot("LoginScene");
    }

    // ------------------------------- variables actuales
    @FXML
    private TextField actualName;
    @FXML
    private TextField actualLastName;
    @FXML
    private TextField actualEmail;

    //-------------------------------- variables de cambio
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmNewPassword;
    @FXML
    private TextField newEmail;
    @FXML
    private TextField confirmNewEmail;

    private boolean esEmailValido(String email) {
        // Expresión regular simple para validar estructura de email
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    @FXML
    private void userUpdate() throws IOException {
        // Validar si los campos no están vacíos
        boolean cambioRealizado = false;

        if (!newPassword.getText().isEmpty()) {
            if (newPassword.getText().equals(confirmNewPassword.getText())) {
                // Aquí iría la lógica para actualizar la contraseña
                UserSesionData.setPasswordUser(UserSesionData.getIdUser(),newPassword.getText());
                cambioRealizado = true;
            }
        }

        if (!newEmail.getText().isEmpty() && !confirmNewEmail.getText().isEmpty()) {
            if (newEmail.getText().equals(confirmNewEmail.getText())) {
                if(!esEmailValido(newEmail.getText())) {
                    errorMessage("Email Invalido");
                    return;
                }
                UserSesionData.setEmailUser(UserSesionData.getIdUser(),newEmail.getText());
                cambioRealizado = true;
            }
        }
        if (cambioRealizado) {
            successMessage("Cambios realizados con éxito");
            irMainScene();
        } else {
            errorMessage("Los campos no pueden estar vacíos o no coinciden.");
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources){
        if (Objects.equals(UserSesionData.getRolUser(), "user")) {
            adminListUserHBox.setDisable(true);
        }
        rolLabel.setText("Rol: " + UserSesionData.getRolUser());

        actualEmail.setText(UserSesionData.getEmailUser());
        actualName.setText(UserSesionData.getFirtsNameUser());
        actualLastName.setText(UserSesionData.getLastNameUser());
    }

}
