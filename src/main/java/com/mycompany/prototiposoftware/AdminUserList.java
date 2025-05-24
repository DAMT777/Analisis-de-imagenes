package com.mycompany.prototiposoftware;

import com.processing.User;
import com.databaseInteractions.DBConnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import com.mycompany.prototiposoftware.TableUserListView;
import javafx.scene.layout.AnchorPane;



public class AdminUserList implements Initializable {

    List<String[]> users =  DBConnect.getUsuariosEmpresa(UserSesionData.getEmpresa());

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
    private TableColumn<TableUserListView, String> userDateCreation;

    @FXML
    private TableColumn<TableUserListView, String> userRol;

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

        LocalDate today = LocalDate.now();

        companyNameTableUserList.setText("Unillanos");
        userId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        userName.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        userLastName.setCellValueFactory(new PropertyValueFactory<>("Apellido"));
        userEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        userDateCreation.setCellValueFactory(new PropertyValueFactory<>("FechaCreacion"));
        userRol.setCellValueFactory(new PropertyValueFactory<>("Rol"));

        List<String> flatList = new ArrayList<>();
        for (String[] fila : users) {
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
    }
}
