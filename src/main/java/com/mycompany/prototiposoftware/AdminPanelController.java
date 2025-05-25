package com.mycompany.prototiposoftware;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminPanelController  implements Initializable {


    @FXML
    private TextField actualName;
    @FXML
    private TextField actualApellido;
    @FXML
    private TextField actualEmail;
    @FXML
    private TextField actualPassword;
    @FXML
    private TextField newName;
    @FXML
    private TextField newApellido;
    @FXML
    private TextField newEmail;
    @FXML
    private TextField newPassword;


    @FXML
    private AnchorPane menuBox;  // menu expandible

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

    @FXML
    private void irAdminUserList() throws IOException {
        App.setRoot("AdminUsersList");
    }

    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        UserSesionData.clearSession();
        App.setRoot("LoginScene");
    }

    @FXML
    private void menuBoxExpand() {
        menuBox.setVisible(!menuBox.isVisible());
        menuBox.setManaged(menuBox.isVisible());
    }

    @FXML
    private void userUpdate() {

    }

    /**
     * Este método lo invocamos desde el AdminUserListController
     * para inyectar los datos del usuario seleccionado.
     */
    public void setUsuarioSeleccionado(TableUserListView u) {
        actualName.setText(u.getNombre());
        actualApellido.setText(u.getApellido());
        actualEmail.setText(u.getEmail());
        actualPassword.setText(u.getRol());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){



        actualName.setText(actualName.getText());
        actualApellido.setText(actualApellido.getText());
        actualEmail.setText(actualEmail.getText());
        actualPassword.setText(actualPassword.getText());


        newName.setText(newName.getText());
        newApellido.setText(newApellido.getText());
        newEmail.setText(newEmail.getText());
        newPassword.setText(newPassword.getText());
    }

}
