package com.processing;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Imagen {
    private int id;
    private String path;
    private float brillo;
    private float contraste;
    private Valoracion valoracion; // Valoración de la imagen
    private List<Segmento> segmentos; // Lista de regiones segmentadas

    public Imagen(String path) {
        this.id = 0;
        this.path = path;
        this.brillo = 0;
        this.contraste = 0;
        this.segmentos = null;
    }

    // Método para segmentar la imagen y guardar los contornos
    public void segmentarImagen(String name) {
        Mat imagenOriginal = Imgcodecs.imread(this.path, Imgcodecs.IMREAD_COLOR);

        // 1. Preprocesamiento (escala de grises + desenfoque)
        Mat gray = new Mat();
        Imgproc.cvtColor(imagenOriginal, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // 2. Detección de bordes (Canny)
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);

        // 3. Encontrar contornos
        Mat hierarchy = new Mat();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 4. Identificar el contorno más circular (ojo)
        MatOfPoint contornoOjo = null;
        double mayorCircularidad = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            double perimetro = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
            if (perimetro > 0) {
                double circularidad = 4 * Math.PI * (area / (perimetro * perimetro));
                if (circularidad > mayorCircularidad) {
                    mayorCircularidad = circularidad;
                    contornoOjo = contour;
                }
            }
        }

        // 5. Dibujar rectángulo para el ojo
        if (contornoOjo != null) {
            Rect boundingRectOjo = Imgproc.boundingRect(contornoOjo);
            Point centroOjo = new Point(boundingRectOjo.x + boundingRectOjo.width / 2.0, boundingRectOjo.y + boundingRectOjo.height / 2.0);
            Point esquina1Ojo = new Point(centroOjo.x - 215 / 2.0, centroOjo.y - 190 / 2.0);
            Point esquina2Ojo = new Point(centroOjo.x + 215 / 2.0, centroOjo.y + 190 / 2.0);
            Imgproc.rectangle(imagenOriginal, esquina1Ojo, esquina2Ojo, new Scalar(0, 255, 0), 2);
            Imgproc.putText(imagenOriginal, "ojo", new Point(esquina2Ojo.x + 5, esquina2Ojo.y), Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 255, 0), 2);
        }

        // 6. Identificar el contorno más central (cuerpo)
        MatOfPoint contornoCuerpo = null;
        double distanciaMinimaCentro = Double.MAX_VALUE;
        Point centroImagen = new Point(imagenOriginal.cols() / 2.0, imagenOriginal.rows() / 2.0);
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            Point centroContorno = new Point(boundingRect.x + boundingRect.width / 2.0, boundingRect.y + boundingRect.height / 2.0);
            double distancia = Math.sqrt(Math.pow(centroContorno.x - centroImagen.x, 2) + Math.pow(centroContorno.y - centroImagen.y, 2));
            if (distancia < distanciaMinimaCentro) {
                distanciaMinimaCentro = distancia;
                contornoCuerpo = contour;
            }
        }

        // 7. Dibujar rectángulo para el cuerpo
        if (contornoCuerpo != null) {
            Rect boundingRectCuerpo = Imgproc.boundingRect(contornoCuerpo);
            Point centroCuerpo = new Point(boundingRectCuerpo.x + boundingRectCuerpo.width / 2.0, boundingRectCuerpo.y + boundingRectCuerpo.height / 2.0);
            Point esquina1Cuerpo = new Point(centroCuerpo.x - 600 / 2.0, centroCuerpo.y - 800 / 2.0);
            Point esquina2Cuerpo = new Point(centroCuerpo.x + 600 / 2.0, centroCuerpo.y + 800 / 2.0);
            Imgproc.rectangle(imagenOriginal, esquina1Cuerpo, esquina2Cuerpo, new Scalar(255, 0, 0), 2);
            Imgproc.putText(imagenOriginal, "cuerpo", new Point(esquina2Cuerpo.x + 5, esquina2Cuerpo.y), Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 0, 0), 2);
        }

        // 8. Guardar la imagen con los rectángulos dibujados
        Imgcodecs.imwrite(name, imagenOriginal);
    }


    public Mat quitBackground(Mat img) {
        // 1. Segmentación inicial por color (HSV)
        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Mat maskColor = new Mat();
        Core.inRange(hsv, new Scalar(0, 70, 50), new Scalar(10, 255, 150), maskColor);

        // 2. Crear una máscara inicial para GrabCut
        Mat maskGrabCut = new Mat(img.size(), CvType.CV_8UC1, new Scalar(Imgproc.GC_BGD));
        Rect rect = new Rect(50, 50, img.cols() - 100, img.rows() - 100);
        maskColor.copyTo(maskGrabCut.submat(rect));

        // 3. Aplicar GrabCut para refinar
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Imgproc.grabCut(img, maskGrabCut, rect, bgModel, fgModel, 3, Imgproc.GC_INIT_WITH_RECT);

        // 4. Convertir la máscara a binaria
        Mat maskFinal = new Mat();
        Core.compare(maskGrabCut, new Scalar(Imgproc.GC_PR_FGD), maskFinal, Core.CMP_EQ);

        // 5. Encontrar contornos y eliminar los pequeños
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(maskFinal, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area < 500) { // Umbral para eliminar contornos pequeños
                Imgproc.drawContours(maskFinal, Arrays.asList(contour), -1, new Scalar(0), -1);
            }
        }

        // 6. Aplicar la máscara sobre la imagen original para conservar los colores
        Mat result = new Mat();
        Imgproc.cvtColor(maskFinal, maskColor, Imgproc.COLOR_GRAY2BGR); // Convertir la máscara a BGR
        img.copyTo(result, maskFinal); // Aplicar la máscara sobre la imagen original

        return result;
    }


    private float calcularContraste(Mat imagen) {
        Mat gray = new Mat();
        Imgproc.cvtColor(imagen, gray, Imgproc.COLOR_BGR2GRAY);
        MatOfFloat hist = new MatOfFloat();
        Imgproc.calcHist(Arrays.asList(gray), new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
        return (float) Core.norm(hist, Core.NORM_L2);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getBrillo() {
        return brillo;
    }

    public void setBrillo(float brillo) {
        this.brillo = brillo;
    }

    public float getContraste() {
        return contraste;
    }

    public void setContraste(float contraste) {
        this.contraste = contraste;
    }

    public List<Segmento> getSegmentos() {
        return segmentos;
    }

    public void setSegmentos(List<Segmento> segmentos) {
        this.segmentos = segmentos;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }
}