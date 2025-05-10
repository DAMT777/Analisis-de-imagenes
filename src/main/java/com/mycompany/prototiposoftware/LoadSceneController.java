package com.mycompany.prototiposoftware;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class LoadSceneController {

    @FXML
    private Label progressLabel;

    public void bindTask(Task<?> task) {
        progressLabel.textProperty().bind(task.messageProperty());
    }
}