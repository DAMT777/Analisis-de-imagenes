package com.mycompany.prototiposoftware;

import com.databaseInteractions.DBConnect;
import com.processing.User;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminPanelController  implements Initializable {


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

    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        UserSesionData.clearSession();
        App.setRoot("LoginScene");
    }
    //------------------------------------------------------------------------------------------Reutilizar






    @FXML
    private TextField actualName;
    @FXML
    private TextField actualApellido;
    @FXML
    private TextField actualEmail;

    @FXML
    private TextField newName;
    @FXML
    private TextField newApellido;
    @FXML
    private TextField newEmail;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmNewPassword;

    @FXML
    private void irAdminUserList() throws IOException {
        App.setRoot("AdminUsersList");
    }

    String idUser = "";

    @FXML
    private void userUpdate() throws IOException {
        boolean nombreVacio = newName.getText().isEmpty();
        boolean apellidoVacio = newApellido.getText().isEmpty();
        boolean emailVacio = newEmail.getText().isEmpty();
        boolean passwordVacio = newPassword.getText().isEmpty();

        if (nombreVacio && apellidoVacio && emailVacio && passwordVacio) {
            errorMessage("Al menos un campo debe ser modificado.");
            return;
        }

        int userId = Integer.parseInt(idUser);
        String nombre = newName.getText().isEmpty() ? actualName.getText() : newName.getText();
        String apellido = newApellido.getText().isEmpty() ? actualApellido.getText() : newApellido.getText();
        String email = newEmail.getText().isEmpty() ? actualEmail.getText() : newEmail.getText();

        // Actualizar datos del usuario
        DBConnect.actualizarUsuario(userId, nombre, apellido, UserSesionData.getEmpresa(), email, "user");

        // Si la contraseña nueva no está vacía y coincide con la confirmación, actualizarla
        if (!newPassword.getText().isEmpty() && newPassword.getText().equals(confirmNewPassword.getText())) {
            DBConnect.actualizarPasswordUsuario(userId, newPassword.getText());
        }

        irAdminUserList();
    }

    /**
     * Este método lo invocamos desde el AdminUserListController
     * para inyectar los datos del usuario seleccionado.
     */
    public void setUsuarioSeleccionado(TableUserListView u) {
        actualName.setText(u.getNombre());
        actualApellido.setText(u.getApellido());
        actualEmail.setText(u.getEmail());
        idUser = u.getId();
    }


    public boolean showQuestion(String question) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Pregunta");
        alerta.setHeaderText(null);
        alerta.setContentText(question);

        // Botones por defecto: OK y Cancel
        Optional<ButtonType> result = alerta.showAndWait();

        // Devuelve true si el usuario presionó OK
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        actualName.setText(actualName.getText());
        actualApellido.setText(actualApellido.getText());
        actualEmail.setText(actualEmail.getText());

    }
    @FXML
    private void eliminarUser() throws IOException {
        if (showQuestion("Se eliminará el usuario. ¿Está seguro?")) {
            if (Objects.equals(idUser, UserSesionData.getIdUser() + "")){
                errorMessage("No puedes eliminar tu propio usuario.");
            }
            else {
                DBConnect.eliminarUsuario(Integer.parseInt(idUser));
            }
        }
    }
}
