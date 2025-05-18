package com.mycompany.prototiposoftware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;




/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("LoginScene"), 640, 480);
        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(600);


        stage.setWidth(900);
        stage.setHeight(600);
        stage.setTitle("Sistema de clasificación de calidad de peces");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/SecondaryLogo.png")));
        stage.setResizable(false);

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        Parent newRoot = loadFXML(fxml);         // 1. Carga la escena que se quiere invocar y la almacena en una clase generica (aun no se invoca en la ventana visual)
        newRoot.setOpacity(0);                   // 2. Establece la opacidad al maximo (inicia invisible)

        // 3. Crea una animación de desvanecimiento (fade out) para la escena actual. Es decir, transiciona aumentando la opacidad de la escena actual para luego incvocar a la almacenada en newRoot
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), scene.getRoot());
        fadeOut.setFromValue(1);                 // 4. Empieza desde opacidad 100% (1.0)
        fadeOut.setToValue(0);                   // 5. Va hasta opacidad 0% (0.0)

        // 6. Cuando termine el fade out:
        fadeOut.setOnFinished(e -> {
            scene.setRoot(newRoot);              // 7. Invoca en la ventana del programa la nueva ventana que esta invisble (paso 3-5)
            // 8. Se crea otra transicion al igual que en el paso 3, pero a la inversa, transiciona de invisible a visible
            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), newRoot);
            fadeIn.setFromValue(0);              // 9. Comienza desde opacidad 0%
            fadeIn.setToValue(1);                // 10. Aumenta hasta opacidad 100%
            fadeIn.play();                       // 11. Inicia la animación fade in
        });

        fadeOut.play();                          // 12. Inicia la animación fade out
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static FXMLLoader getLoader(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root);
        return loader;
    }


    public static void main(String[] args) {
        launch();
    }

}