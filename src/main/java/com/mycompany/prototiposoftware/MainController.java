/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prototiposoftware;

/**
 *
 * @author PC
 */

import java.io.IOException;

import com.practicas.Analizador;
//import com.practicas.DBConnect;
import com.practicas.Lote;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
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




public class MainController {

    @FXML
    private AnchorPane menuBox;  // menu expandible

    @FXML
    private HBox hBoxUploadLoteImage;

    @FXML //cargar en la escena la imagen subida por el usuario
    private ImageView imageViewMain;

    @FXML // panel en el que se muestran todas las imagenes seleccionadas
    private TilePane tilePaneLoteImages;





    @FXML    //cambio de escena al hacer lcick en salir
    private void irALoginController() throws IOException {
        App.setRoot("LoginScene");
    }

    @FXML
    private void menuBoxExpand() {
        menuBox.setVisible(!menuBox.isVisible());
        menuBox.setManaged(menuBox.isVisible());
    }

    @FXML
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
    private void MCalgorithm(){

        //acá se llama al algoritmo, la imagen está guardada en src/main/imgFish.

        Lote lote = new Lote(0, "2023-10-01", 2, "./src/main/imgFish/");

        Analizador.analizar(lote);
        
        /*
        boolean ok = DBConnect.registrarImagenesDeLote(lote).isExito();

        Alert alerta = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alerta.setTitle(ok ? "Éxito" : "Error");
        alerta.setHeaderText(null);
        alerta.setContentText(ok ? "Las imágenes se guardaron correctamente." : "Hubo un error al guardar las imágenes.");
        alerta.showAndWait();
        //siempre se almacena 1 unica imagen en esta carpeta, al terminar el algoritmo debería eliminarse.
*/
        eraseFiles();
        UploadLoteIamgeHideUnHide();
        tilePaneLoteImages.getChildren().clear();
    }



}

