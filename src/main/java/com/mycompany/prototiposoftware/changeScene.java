package com.mycompany.prototiposoftware;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class changeScene {
    public static void changeScene(Stage stage, String hboxId) {
        String fxmlFile;

        switch (hboxId) {
            case "analisisHbox":
                fxmlFile = "/com/mycompany/prototiposoftware/MainScene.fxml";
                break;
            case "reportsHBox":
                fxmlFile = "/com/mycompany/prototiposoftware/HistoryReports.fxml";
                break;
            case "userHBox":
                fxmlFile = "/com/mycompany/prototiposoftware/ResultAlgorithmScene.fxml";
                break;
            case "adminListUserHBox":
                fxmlFile = "/com/mycompany/prototiposoftware/AdminUsersList.fxml";
                break;
            default:
                System.err.println("ID de HBox no reconocido: " + hboxId);
                return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(changeScene.class.getResource(fxmlFile));
            Parent root = loader.load();
            App.scene.setRoot(root);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Aquí podrías mostrar un error visual al usuario
        }
    }
}
