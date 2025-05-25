package com.mycompany.prototiposoftware;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class changeScene {
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
                fxmlFile = "ResultAlgorithmScene";
                break;
            case "adminListUserHBox":
                fxmlFile = "AdminUsersList";
                break;
            // ---------------------------------------------------ToolBar
            case "userSceneToolBar":
                fxmlFile = "UserScene";
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
            App.setRoot(fxmlFile);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Aquí podrías mostrar un error visual al usuario
        }
    }
}
