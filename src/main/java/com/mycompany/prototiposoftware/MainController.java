/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;


/*
 *
 */

//import com.processing.Analizador;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.databaseInteractions.DBConnect;
import com.processing.Lote;
import com.processing.LoteProcessor;
import com.processing.ResultadoRegistro;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MainController {
    String [] contextoLote = new String[5];

    @FXML
    private HBox analisisHbox;
    @FXML
    private HBox reportsHBox;
    @FXML
    private HBox userHBox;
    @FXML
    private HBox adminListUserHBox;



    @FXML
    private AnchorPane menuBox;  // menu expandible

    @FXML
    private HBox hBoxUploadLoteImage;

    @FXML //cargar en la escena la imagen subida por el usuario
    private ImageView imageViewMain;

    @FXML // panel en el que se muestran todas las imagenes seleccionadas
    private TilePane tilePaneLoteImages;

    @FXML
    private GridPane gridPaneResultAlgorithm; // aun no se utiliza

    @FXML
    private Label progressLabel;  // menu expandible

    // esta funcion abre una ventana emergente y extrae el texto digitado en los textField que tiene dentro (correspondiente a origen del lote y descripcion)
    private String[] dialogScene() {
        try {
            //FXMLLoader carga un archivo fxml y lo interpreta.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogStage.fxml")); //


            //el metodo load carga en memoria los nodos que se hayan interpretado en el objeto loader (que es el archivo fxml interpretado) que se definio antes.
            //Parent es un contenedor generico, la escena hecha con fxml siempre inicia con un contenedor raiz, en caso de no saber cual es, con Parent, puede generalizar.
            //No es obligaotrio usar panel, por ejemplo, yo que fui el que arme el fxml desde scene builder, se que el contenedor raiz de la ventana es un anchorPane
            //entonces, en vez de la linea de abajo, podria decir AnchorPane root = loader.load().
            Parent root = loader.load();


            Stage dialog = new Stage(); //Esta linea crea un stage, que sería como tal la ventana vacia

            dialog.setWidth(545);
            dialog.setHeight(435);
            dialog.setResizable(false);

            //initModality() es un metodo que indica como se comportara la ventana respecto a las demas
            //en este caso, el metodo especial Modality cuenta con las siguientes opciones a elegir:
            //   NONE: la ventana no bloquea nada; puedes seguir usando libremente las demás ventanas.
            //   WINDOW_MODAL: bloquea sólo su “ventana padre” (la ventana que la abrió), pero deja libres otras ventanas de la aplicación.
            //   APPLICATION_MODAL: bloquea toda la aplicación hasta que cierres esta ventana. (en este caso debe bloquear las demas ventanas, para evitar errores o bugs
            dialog.initModality(Modality.APPLICATION_MODAL);


            dialog.setTitle("Datos de contexto"); //Establece el titulo de la ventana
            dialog.setScene(new Scene(root)); //En la ventana creada, se crea una escena, la cual parte del contenedor generico que se establecio anteriormente

            //Muestra la ventana emergente, y pausa la ventana principal hasta que se cierre la emergente.
            dialog.showAndWait();


            //dialogController es la clase que se creo en dialogController.java, como es publica, podemos utilizarla desde este mainController
            //recuerde que el controlador es el encargado de la logica y control de una escena en este contexto
            //recuerde tambien que FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogStage.fxml")); es el archivo fmxl cargado en memoria.
            //en la seccion loader.getController();, el metodo getController simplemente extrae la clase del controlador que ya esta cargado (y esta referenciado en el fxml).
            //vamos, que simplemente extrae la clase dialogController que esta usando el loader en ese momento y lo trae aca xd
            //Es decir, extraes el controlador de la escena que cargaste, y lo copias (no es una copia si no el mismo objeto en memoria pero bueno,asi lo puede entender) aca en la variable ctrl que debe ser del mismo tipo que el controlador de loader
            dialogController ctrl = loader.getController();


            //ya tienes la copia
            String ciudadLote = ctrl.getCiudad();
            String registradoInvima = ctrl.getInvima();
            String condicionLote = ctrl.getCondicion();
            String tiempoPescaLote = ctrl.getTiempoPesca();

            return new String[]{ciudadLote, registradoInvima, condicionLote, tiempoPescaLote};

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String[]{"", ""};
    }

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


    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        UserSesionData.clearSession();
        App.setRoot("LoginScene");
    }

    //cambio de escena a resultAlgorithmScene
    private void irAResultAlgorithmScene() throws IOException {
        App.setRoot("ResultAlgorithmScene");
    }


    @FXML
    private void menuBoxExpand() {
        menuBox.setVisible(!menuBox.isVisible());
        menuBox.setManaged(menuBox.isVisible());
    }

    private void UploadLoteIamgeHideUnHide() {
        hBoxUploadLoteImage.setVisible(!hBoxUploadLoteImage.isVisible());
        hBoxUploadLoteImage.setManaged(hBoxUploadLoteImage.isVisible());
    }

    public void errorMessage(String message) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(message);

        alerta.showAndWait();
    }

    @FXML
    private void cargarImagen() {
        contextoLote = dialogScene();
        DBConnect.contextoLote = contextoLote;
        System.out.println("Ciudad: " + contextoLote[0] + " / Registrado invima: " + contextoLote[1]);
        System.out.println("Condicion lote: " + contextoLote[2] + " / Tiempo de pesca: " + contextoLote[3]);

        tilePaneLoteImages.setHgap(2);
        //tilePaneLoteImages.setVgap(10);
        tilePaneLoteImages.setPrefColumns(9);

        eraseFiles();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG Files (*.jpg)", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png")
        );

        List<File> archivos = fileChooser.showOpenMultipleDialog(null);

        if (archivos != null) {
            if (archivos.size() > 50) {
                errorMessage("El límite de imágenes es máximo 50");
            } else {
                UploadLoteIamgeHideUnHide();

                for (File archivo : archivos) {
                    String imagePath = archivo.toURI().toString();
                    Image image = new Image(imagePath);

                    ImageView iv = new ImageView(image);
                    iv.setFitWidth(70);
                    iv.setFitHeight(70);
                    iv.setPreserveRatio(false); // Queremos clip cuadrado uniforme

                    // Clip de esquinas redondeadas
                    Rectangle clip = new Rectangle(70, 70);
                    clip.setArcWidth(14);
                    clip.setArcHeight(14);
                    iv.setClip(clip);

                    // Contenedor con estilo visual limpio
                    StackPane imageContainer = new StackPane(iv);
                    imageContainer.setPrefSize(70, 70);
                    imageContainer.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 20;" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);" +
                                    "-fx-padding: 5;"
                    );

                    tilePaneLoteImages.getChildren().add(imageContainer);

                    saveImage(archivo);
                }
            }
        }
    }


    //esta funcion guarda la imagen cargada por el usuario, en la carpeta imgFish, desde esa carpeta se extrae la imagen para el analisis
    private void saveImage(File archivo) {
        String pathFile = "src/main/imgFish";
        Path path = Path.of(pathFile, archivo.getName());

        try {
            Files.copy(archivo.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Imagen guardada correctamente en: " + path);

        } catch (IOException e) {

            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
    }


    //esta funcion elimina las imagenes que se almacenen en imgFish, que es la carpeta donde se almacena la foto del pescado que se analiza
    private void eraseFiles() {
        File filePath = new File("src/main/imgFish");

        if (filePath.exists() && filePath.isDirectory()) {
            File[] files = filePath.listFiles();


            if (files != null) {
                for (File file : files) {
                    boolean delete = file.delete();

                    if (delete) {
                        System.out.println("Archivos eliminados: " + file.getName());
                    } else {
                        System.out.println("Error al elimnar archivos: " + file.getName());
                    }
                }
            }
        }
        Image image = new Image(getClass().getResourceAsStream("/img/UploadLote.png"));
        imageViewMain.setImage(image);
    }


    @FXML
    private void MCalgorithm() throws IOException {

        //recordar que los datos de contexto que se piden en la ventana flotante los puede extraer como string con -> contextoLote[0], contextoLote[1]
        /*contextoLote[0] = "Refrigerado"; //Condicion lote
        contextoLote[1] = "2-3 dias";   //Tiempo de pezca
        contextoLote[2] = "Villavicencio"; //
        contextoLote[3] = "true"; //registrado INVIMA
        */
        Task<Boolean> tareaAnalisis = new Task<>() {


            @Override
            protected Boolean call() throws Exception {


                LocalDate fechaActual = LocalDate.now();

                // Formatear a "yyyy-MM-dd"
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String fechaFormateada = fechaActual.format(formato);



                // mensaje de progreso
                updateMessage("Cargando imágenes...");


                String[] contextoLote  = DBConnect.contextoLote;

               /* contextoLote[0] = "Refrigerado"; //Condicion lote
                contextoLote[1] = "2-3 dias";   //Tiempo de pezca
                contextoLote[2] = "Villavicencio"; //
                contextoLote[3] = "true"; //registrado INVIMA
                */
                System.out.println("extra id, if");
                if(UserSesionData.getIdUser() == null){
                    System.out.println("nulo");
                }

                System.out.println("no nulo, es " + UserSesionData.getIdUser());
                //Lote lote = new Lote(UserSesionData.getIdUser(), fechaActual.format(formato), "./src/main/imgFish/", contextoLote);
                Lote lote = new Lote(UserSesionData.getIdUser(), fechaFormateada, "./src/main/imgFish/", contextoLote);
                UserSesionData.setLotesCountUser(UserSesionData.getLotesCountUser()+1); //actualizar el contador sin tener que acceder a la base de datos


                //bool, integer id
                System.out.println("procesando imagenes afuera");
                ResultadoRegistro resultRegister = LoteProcessor.procesarLote(lote, this::updateMessage);
                System.out.println("procesando imagenes despues");
                boolean ok = resultRegister.isExito();
                lote.setId(resultRegister.getIdLote());

                return ok;
            }
        };

        FXMLLoader loader = new FXMLLoader(App.class.getResource("loadScene.fxml"));
        Parent root = loader.load();
        LoadSceneController controller = loader.getController();
        controller.bindTask(tareaAnalisis); // Primero haces el binding
        App.scene.setRoot(root); // Luego cambias la escena


        tareaAnalisis.setOnSucceeded(e -> {
            boolean ok = tareaAnalisis.getValue();

            Alert alerta = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alerta.setTitle(ok ? "Éxito" : "Error");
            alerta.setHeaderText(null);
            alerta.setContentText(ok ? "Las imágenes se guardaron correctamente." : "Hubo un error al guardar las imágenes.");
            alerta.showAndWait();

            eraseFiles(); // borra las muestras de src/main/imgFish
            UploadLoteIamgeHideUnHide();
            tilePaneLoteImages.getChildren().clear();

            try {
                App.setRoot("MainScene");
                irAResultAlgorithmScene();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        new Thread(tareaAnalisis).start();
    }

}

