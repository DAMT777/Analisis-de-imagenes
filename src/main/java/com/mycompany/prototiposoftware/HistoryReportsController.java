package com.mycompany.prototiposoftware;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.databaseInteractions.DBConnect;

public class HistoryReportsController implements Initializable {

    /*companyNameTableUserList.setText("Unillanos");
        reportIdLote.setCellValueFactory(new PropertyValueFactory<>("IdLote"));
        reportFecha.setCellValueFactory(new PropertyValueFactory<>("FechaAnalisis"));
        reportCondition.setCellValueFactory(new PropertyValueFactory<>("CondicionesLote"));
        reportCity.setCellValueFactory(new PropertyValueFactory<>("CiudadAnalisis"));
        reportFishTime.setCellValueFactory(new PropertyValueFactory<>("InvimaLote"));
        reportInvima.setCellValueFactory(new PropertyValueFactory<>("TiempoPescaLote"));
        reportCalificacion.setCellValueFactory(new PropertyValueFactory<>("CalificacionLote"));*/
    List<String[]> datoHistoryReport = DBConnect.getReportHistory(UserSesionData.getIdUser());

    String idLote;
    String fecha;
    String condition;
    String city;
    String fishTime;
    String invima;
    String calificacion;

    @FXML
    private Label companyNameTableUserList;

    @FXML
    private AnchorPane menuBox;  // menu expandible

    @FXML
    private TableView<TableUserHistoryReports> tableUserHistoryReports;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportIdLote;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportFecha;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportCondition;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportCity;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportFishTime;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportInvima;

    @FXML
    private TableColumn<TableUserHistoryReports, String> reportCalificacion;

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
        reportIdLote.setCellValueFactory(new PropertyValueFactory<>("IdLote"));
        reportFecha.setCellValueFactory(new PropertyValueFactory<>("FechaAnalisis"));
        reportCondition.setCellValueFactory(new PropertyValueFactory<>("CondicionesLote"));
        reportCity.setCellValueFactory(new PropertyValueFactory<>("CiudadAnalisis"));
        reportFishTime.setCellValueFactory(new PropertyValueFactory<>("InvimaLote"));
        reportInvima.setCellValueFactory(new PropertyValueFactory<>("TiempoPescaLote"));
        reportCalificacion.setCellValueFactory(new PropertyValueFactory<>("CalificacionLote"));
        List<String> flatList = new ArrayList<>();
        for (String[] fila : datoHistoryReport) {
            for (String dato : fila) {
                flatList.add(dato);
            }
        }


        ObservableList<TableUserHistoryReports> users = FXCollections.observableArrayList(
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5"),
                new TableUserHistoryReports("12313","12-12-12", "refrigerado", "Villavicencio", "2-3 Dias", "Si", "5")
        );


        for (int i = 0; i + 6 < flatList.size(); i += 7) {
            idLote = flatList.get(i);
            fecha = flatList.get(i + 1);
            condition = flatList.get(i + 2);
            city = flatList.get(i + 3);
            fishTime = flatList.get(i + 4);
            invima = flatList.get(i + 5);
            calificacion = flatList.get(i + 6);

            users.add(new TableUserHistoryReports(idLote, fecha, condition, city, fishTime,invima,calificacion));
        }

        tableUserHistoryReports.setItems(users);
    }

}
