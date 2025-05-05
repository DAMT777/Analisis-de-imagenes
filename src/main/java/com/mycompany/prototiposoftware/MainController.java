/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;


/*
 *
 */

import com.practicas.DBConnect;
import java.io.IOException;

import com.practicas.Analizador;
//import com.practicas.DBConnect;
import com.practicas.Lote;
import javafx.fxml.FXML;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView; //importa el elemento ImageView de SceneBuilder, para comunicar elementos de FXML con java

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.io.File; // libreria para pedir al usuario subir un archivo
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;


//import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;



public class MainController {

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
            String descripcionLote = ctrl.getDescripcion();
            String origenLote = ctrl.getOrigen();

            return new String[]{descripcionLote,origenLote};

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String[]{ "", "" };
    }




    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
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

    private void UploadLoteIamgeHideUnHide(){
        hBoxUploadLoteImage.setVisible(!hBoxUploadLoteImage.isVisible());
        hBoxUploadLoteImage.setManaged(hBoxUploadLoteImage.isVisible());
    }

    public void errorMessage(String message){
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(message);

        alerta.showAndWait();
    }




    @FXML    // carga la imagen pidiendo al usuario un archivo .jpg, o png
    private void cargarImagen(){

        String[] contextoLote = dialogScene();
        System.out.println("Descripcion del lote: " + contextoLote[0] + " / Origen del lote: " + contextoLote[1]);


        tilePaneLoteImages.setHgap(10); // Espacio horizontal entre imágenes
        tilePaneLoteImages.setVgap(10); // Espacio vertical
        tilePaneLoteImages.setPrefColumns(3); // Hasta 5 imágenes por fila (luego baja de línea)

        eraseFiles(); // borra la imagen que esté en la carpeta imgFish, si hay alguna, esto por si el usuario se equivoca de imagen, simplemente sube otra y se elimina la anterior.

        FileChooser fileChooser= new FileChooser();

        FileChooser.ExtensionFilter extFilterJpg = new FileChooser.ExtensionFilter("JPG Files (*.jpg)","*.jpg");
        FileChooser.ExtensionFilter extFilterPng = new FileChooser.ExtensionFilter("PNG Files (*.png)","*.png");

        fileChooser.getExtensionFilters().addAll(extFilterJpg, extFilterPng);
        List<File> archivos = fileChooser.showOpenMultipleDialog(null);

        if (archivos != null){
            if(archivos.size()>10){
               errorMessage("El limite de imagenes son maximo 10");
            } else {
                UploadLoteIamgeHideUnHide();
                for (File archivo: archivos){
                    String imagePath = archivo.toURI().toString();

                    Image image = new Image(imagePath);

                    //double withPref = 300;

                    //imageViewMain.setFitWidth(withPref);
                    //imageViewMain.setPreserveRatio(true);


                    ImageView iv = new ImageView(image);
                    iv.setFitWidth(100);
                    iv.setFitHeight(100);
                    iv.setPreserveRatio(false);


                    tilePaneLoteImages.getChildren().add(iv);


                    //imageViewMain.setImage(image);

                    saveImage(archivo);
                }
            }

        }
    }


    //esta funcion guarda la imagen cargada por el usuario, en la carpeta imgFish, desde esa carpeta se extrae la imagen para el analisis
    private void saveImage(File archivo){
        String pathFile = "src/main/imgFish";
        Path path = Path.of(pathFile,archivo.getName());

        try{
            Files.copy(archivo.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Imagen guardada correctamente en: "+path);

        } catch (IOException e){

            System.err.println("Error al guardar la imagen: "+ e.getMessage());
        }
    }



    //esta funcion elimina las imagenes que se almacenen en imgFish, que es la carpeta donde se almacena la foto del pescado que se analiza
    private void eraseFiles(){
        File filePath = new File("src/main/imgFish");

      if (filePath.exists() && filePath.isDirectory()){
          File[] files = filePath.listFiles();


          if (files != null){
              for (File file : files){
                  boolean delete = file.delete();

                  if(delete){
                      System.out.println("Archivos eliminados: "+ file.getName());
                  } else {
                      System.out.println("Error al elimnar archivos: "+ file.getName());
                  }
              }
          }
      }
      Image image = new Image(getClass().getResourceAsStream("/img/UploadLote.png"));
      imageViewMain.setImage(image);
    }



    @FXML
    private void MCalgorithm() throws IOException{

        //acá se llama al algoritmo, la imagen está guardada en src/main/imgFish.

        Lote lote = new Lote(0, "2023-10-01", 2, "./src/main/imgFish/");

        Analizador.analizar(lote);

        boolean ok = DBConnect.registrarImagenesDeLote(lote).isExito();

        Alert alerta = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alerta.setTitle(ok ? "Éxito" : "Error");
        alerta.setHeaderText(null);
        alerta.setContentText(ok ? "Las imágenes se guardaron correctamente." : "Hubo un error al guardar las imágenes.");
        alerta.showAndWait();
        //siempre se almacena 1 unica imagen en esta carpeta, al terminar el algoritmo debería eliminarse.

        eraseFiles();
        UploadLoteIamgeHideUnHide();
        tilePaneLoteImages.getChildren().clear();
        irAResultAlgorithmScene();
    }



}

