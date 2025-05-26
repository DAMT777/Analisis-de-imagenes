package com.mycompany.prototiposoftware;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class changeScene {

    public static void errorMessage(String message) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(message);

        alerta.showAndWait();
    }

    public static void successMessage(String message) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Cambiamos de ERROR a INFORMATION
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(message);

        alerta.showAndWait();
    }

    public static void changeScene(Stage stage, String nodeId) {
        String fxmlFile;


        switch (nodeId) {
            //-----------------------------------------------------Menu lateral
            case "analisisHbox":
                fxmlFile = "MainScene";
                break;
            case "reportsHBox":
                fxmlFile = "HistoryReports";
                break;
            case "userHBox":
                fxmlFile = "UserInfoPanelScene";
                break;
            case "adminListUserHBox":
                fxmlFile = "AdminUsersList";
                break;
            // ---------------------------------------------------ToolBar
            case "userSceneToolBar":
                fxmlFile = "UserInfoPanelScene";
                break;
            case "settingsSceneToolBar":
                fxmlFile = "SettingsScene";
                break;
            case "aboutUsSceneToolBar":
                fxmlFile = "AboutUsScene";
                break;
            case "helpSceneToolBar":
                fxmlFile = "HelpScene";
                break;
            default:
                System.err.println("ID no reconocida: " + nodeId);
                return;
        }
        try {

            if(fxmlFile.equals("HelpScene")){

                String rutaArchivo;

                if (Objects.equals(UserSesionData.getRolUser(), "user")) {
                    rutaArchivo = "./info usuarios/Manual de usuario.docx";
                } else {
                    rutaArchivo = "./info usuarios/Manual de Administrador.docx";
                }

                try {

                    File file = new File(rutaArchivo);

                    if (file.exists()) {
                        successMessage("se abrirá un documento de texto a continuación...");
                        Desktop.getDesktop().open(file);
                    } else {
                        errorMessage("El archivo no se encontró: " + file.getAbsolutePath());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    errorMessage("Error al intentar abrir el archivo.");
                }
                return;
            } else if (fxmlFile.equals("SettingsScene")){
                errorMessage("No disponible. Proximamente :(");
                return;
            }


            App.setRoot(fxmlFile);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Aquí podrías mostrar un error visual al usuario
        }
    }
}
