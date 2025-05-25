package com.mycompany.prototiposoftware;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserInfoPanelController implements Initializable {

    //------------------------------------------------------------------------------------------Menu lateral
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
    private void irMainScene() throws IOException {
        App.setRoot("MainScene");
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
    private PasswordField confirmNewEmail;

    @FXML
    private void userUpdate() throws IOException {
        /*
         * incluir logica de del crud desde la basde de datos acá
         * */







        irMainScene();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources){
        actualEmail.setText(UserSesionData.getEmailUser());
        actualName.setText(UserSesionData.getFirtsNameUser());
        actualLastName.setText(UserSesionData.getLastNameUser());
    }

}
