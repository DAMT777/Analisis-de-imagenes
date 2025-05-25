package com.mycompany.prototiposoftware;

import com.databaseInteractions.DBConnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class AdminUserListController implements Initializable {

    String id;
    String name;
    String lastName;
    String email;
    String rol;

    /*companyNameTableUserList.setText("Unillanos");
    userId.setCellValueFactory(new PropertyValueFactory<>("Id"));
     userName.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
     userLastName.setCellValueFactory(new PropertyValueFactory<>("Apellido"));
     userEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
     userDateCreation.setCellValueFactory(new PropertyValueFactory<>("FechaCreacion"));
     userRol.setCellValueFactory(new PropertyValueFactory<>("Rol"));*/

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
    private Label companyNameTableUserList;

    @FXML
    private AnchorPane menuBox;  // menu expandible

    @FXML
    private TableView<TableUserListView> tableUserList;

    @FXML
    private TableColumn<TableUserListView, String> userId;

    @FXML
    private TableColumn<TableUserListView, String> userName;

    @FXML
    private TableColumn<TableUserListView, String> userLastName;

    @FXML
    private TableColumn<TableUserListView, String> userEmail;

    @FXML
    private TableColumn<TableUserListView, String> userRol;


    @FXML
    private void irAdminPanel() throws IOException{
        App.setRoot("AdminPanel");
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





    @Override
    public void initialize(URL location, ResourceBundle resources){

        List<String[]> usersData =  DBConnect.getUsuariosEmpresa(UserSesionData.getEmpresa());
        System.out.println("Empresa: " + UserSesionData.getEmpresa());

        LocalDate today = LocalDate.now();

        companyNameTableUserList.setText(UserSesionData.getEmpresa());
        userId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        userName.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        userLastName.setCellValueFactory(new PropertyValueFactory<>("Apellido"));
        userEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        userRol.setCellValueFactory(new PropertyValueFactory<>("Rol"));

        List<String> flatList = new ArrayList<>();
        for (String[] fila : usersData) {
            for (String dato : fila) {
                flatList.add(dato);
            }
        }

        ObservableList<TableUserListView> users = FXCollections.observableArrayList();

        for (int i = 0; i + 4 < flatList.size(); i += 5) {
            id = flatList.get(i);
            name = flatList.get(i + 1);
            lastName = flatList.get(i + 2);
            email = flatList.get(i + 3);
            rol = flatList.get(i + 4);

            users.add(new TableUserListView(id, name, lastName, email, rol));
        }
        tableUserList.setItems(users);

        tableUserList.setRowFactory(tv -> {
            TableRow<TableUserListView> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getClickCount() == 2) {
                    TableUserListView seleccionado = row.getItem();
                    abrirAdminPanelConUsuario(seleccionado);
                }
            });
            return row;
        });
    }


    /**
     * Carga AdminPanel.fxml, obtiene su controlador y le pasa
     * el usuario seleccionado antes de mostrar la nueva escena.
     */
    private void abrirAdminPanelConUsuario(TableUserListView usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminPanel.fxml"));
            Parent root = loader.load();

            // Obtén el controlador de AdminPanel y pásale el usuario
            AdminPanelController ctrl = loader.getController();
            ctrl.setUsuarioSeleccionado(usuario);

            // Cambia la escena
            Stage stage = (Stage) tableUserList.getScene().getWindow();
            App.scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
