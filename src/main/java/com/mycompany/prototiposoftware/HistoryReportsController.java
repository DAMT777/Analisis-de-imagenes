package com.mycompany.prototiposoftware;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.databaseInteractions.DBConnect;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class HistoryReportsController implements Initializable {

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

    @FXML private ComboBox<String> comboCity;
    @FXML private ComboBox<String> comboCondition;
    @FXML private ComboBox<String> comboInvima;
    @FXML private ComboBox<String> comboFishTime;

    private ObservableList<TableUserHistoryReports> masterData;
    private FilteredList<TableUserHistoryReports> filteredData;

    @FXML private void onFiltrarVillavicencio() {
        filteredData.setPredicate(r -> "Villavicencio".equalsIgnoreCase(r.getCiudadAnalisis()));
    }

    @FXML private void onMostrarTodo() {
        filteredData.setPredicate(p -> true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        /*
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

        ObservableList<TableUserHistoryReports> users = FXCollections.observableArrayList();

        for (int i = 0; i + 6 < flatList.size(); i += 7) {
            idLote = flatList.get(i);
            fecha = flatList.get(i + 1);
            condition = flatList.get(i + 2);
            city = flatList.get(i + 3);
            fishTime = flatList.get(i + 4);
            invima = flatList.get(i + 5);
            invima = invima.equals("true") ? "Si" : "No";
            calificacion = flatList.get(i + 6);

            users.add(new TableUserHistoryReports(idLote, fecha, condition, city, invima,fishTime,calificacion));
        }
        tableUserHistoryReports.setItems(users);


        tableUserHistoryReports.setRowFactory(tv -> {
            TableRow<TableUserHistoryReports> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getClickCount() == 2) {
                    TableUserHistoryReports seleccionado = row.getItem();
                    abrirResultAlgorithmConUsuario(seleccionado);
                }
            });
            return row;
        });

         */

        // 1) Inicializar columnas del TableView
        reportIdLote      .setCellValueFactory(new PropertyValueFactory<>("idLote"));
        reportFecha       .setCellValueFactory(new PropertyValueFactory<>("fechaAnalisis"));
        reportCondition   .setCellValueFactory(new PropertyValueFactory<>("condicionesLote"));
        reportCity        .setCellValueFactory(new PropertyValueFactory<>("ciudadAnalisis"));
        reportInvima      .setCellValueFactory(new PropertyValueFactory<>("invimaLote"));
        reportFishTime    .setCellValueFactory(new PropertyValueFactory<>("tiempoPescaLote"));
        reportCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacionLote"));

        // 2) Construir la lista maestra desde datoHistoryReport
        masterData = FXCollections.observableArrayList();
        List<String> flatList = new ArrayList<>();
        for (String[] fila : datoHistoryReport) {
            Collections.addAll(flatList, fila);
        }
        for (int i = 0; i + 6 < flatList.size(); i += 7) {
            String id       = flatList.get(i);
            String fecha    = flatList.get(i + 1);
            String cond     = flatList.get(i + 2);
            String ciudad   = flatList.get(i + 3);
            String invima   = flatList.get(i + 4).equals("true") ? "Si" : "No";
            String tiempo   = flatList.get(i + 5);
            String calif    = flatList.get(i + 6);
            masterData.add(new TableUserHistoryReports(id, fecha, cond, ciudad, invima, tiempo, calif));
        }

        // 3) Extraer valores únicos de cada columna
        Set<String> cities     = masterData.stream().map(TableUserHistoryReports::getCiudadAnalisis)
                .collect(Collectors.toCollection(TreeSet::new));
        Set<String> conditions = masterData.stream().map(TableUserHistoryReports::getCondicionesLote)
                .collect(Collectors.toCollection(TreeSet::new));
        Set<String> invimas    = masterData.stream().map(TableUserHistoryReports::getInvimaLote)
                .collect(Collectors.toCollection(TreeSet::new));
        Set<String> times      = masterData.stream().map(TableUserHistoryReports::getTiempoPescaLote)
                .collect(Collectors.toCollection(TreeSet::new));

        // 4) Poblar ComboBoxes con "Todos" + valores únicos
        List<String> list = new ArrayList<>();
        list.add("Todos");
        list.addAll(cities);
        comboCity.setItems(FXCollections.observableArrayList(list));

        list = new ArrayList<>();
        list.add("Todos");
        list.addAll(conditions);
        comboCondition.setItems(FXCollections.observableArrayList(list));

        list = new ArrayList<>();
        list.add("Todos");
        list.addAll(invimas);
        comboInvima.setItems(FXCollections.observableArrayList(list));

        list = new ArrayList<>();
        list.add("Todos");
        list.addAll(times);
        comboFishTime.setItems(FXCollections.observableArrayList(list));

        // Seleccionar "Todos" por defecto
        comboCity.getSelectionModel().selectFirst();
        comboCondition.getSelectionModel().selectFirst();
        comboInvima.getSelectionModel().selectFirst();
        comboFishTime.getSelectionModel().selectFirst();

        // 5) Crear FilteredList y SortedList para la tabla
        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<TableUserHistoryReports> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableUserHistoryReports.comparatorProperty());
        tableUserHistoryReports.setItems(sortedData);

        // 6) Listener que aplica los filtros combinados
        ChangeListener<String> listener = (obs, old, val) -> applyFilters();
        comboCity.valueProperty().addListener(listener);
        comboCondition.valueProperty().addListener(listener);
        comboInvima.valueProperty().addListener(listener);
        comboFishTime.valueProperty().addListener(listener);

        // 7) Doble clic en fila para abrir siguiente escena
        tableUserHistoryReports.setRowFactory(tv -> {
            TableRow<TableUserHistoryReports> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getClickCount() == 2) {
                    abrirResultAlgorithmConUsuario(row.getItem());
                }
            });
            return row;
        });

    }
    /** Reconstruye el Predicate según las selecciones de los ComboBoxes */
    private void applyFilters() {
        String selCity  = comboCity.getValue();
        String selCond  = comboCondition.getValue();
        String selInv   = comboInvima.getValue();
        String selTime  = comboFishTime.getValue();

        filteredData.setPredicate(report -> {
            boolean okCity = selCity.equals("Todos") || selCity.equals(report.getCiudadAnalisis());
            boolean okCond = selCond.equals("Todos") || selCond.equals(report.getCondicionesLote());
            boolean okInv  = selInv.equals("Todos")  || selInv.equals(report.getInvimaLote());
            boolean okTime = selTime.equals("Todos") || selTime.equals(report.getTiempoPescaLote());
            return okCity && okCond && okInv && okTime;
        });
    }

    /**
     * Carga AdminPanel.fxml, obtiene su controlador y le pasa
     * el usuario seleccionado antes de mostrar la nueva escena.
     */
    private void abrirResultAlgorithmConUsuario(TableUserHistoryReports report) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultAlgorithmScene.fxml"));
            Parent root = loader.load();

            // Obtén el controlador de AdminPanel y pásale el usuario
            ResultAlgorithmController ctrl = loader.getController();
            ctrl.setInfoReportHistory(report);

            // Cambia la escena
            Stage stage = (Stage) tableUserHistoryReports.getScene().getWindow();
            App.scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
