package com.mycompany.prototiposoftware;

import com.databaseInteractions.DBConnect;
import com.utils.HashUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NewUserPanelController implements Initializable {

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        if (Objects.equals(UserSesionData.getRolUser(), "user")) {
            adminListUserHBox.setDisable(true);
        }
        rolLabel.setText("Rol: " + UserSesionData.getRolUser());
    }

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


    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        UserSesionData.clearSession();
        App.setRoot("LoginScene");
    }


    @FXML
    private CheckBox checkBoxAdmin;
    @FXML
    private TextField newName;
    @FXML
    private TextField newApellido;
    @FXML
    private TextField newEmail;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField confirmNewPassword;

    @FXML
    private void irAdminUserList() throws IOException {
        App.setRoot("AdminUsersList");
    }

    @FXML
    private void userUpdate() throws IOException {
        String nombre = newName.getText();
        String apellido = newApellido.getText();
        String email = newEmail.getText();
        String password = newPassword.getText();
        String confirmPass = confirmNewPassword.getText();
        String rol = checkBoxAdmin.isSelected() ? "admin" : "user";

        if (!nombre.isEmpty() && !apellido.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPass.isEmpty()) {
            if (!password.equals(confirmPass)) {
                errorMessage("Las contraseñas no coinciden.");
                return;
            }
            DBConnect.registrarUsuario(nombre, apellido, UserSesionData.getEmpresa(), email, HashUtil.hashPassword(password), rol);
            successMessage("Usuario registrado con exito.");
            App.setRoot("MainScene");
        } else {
            errorMessage("Todos los campos son obligatorios.");
        }
    }
}
