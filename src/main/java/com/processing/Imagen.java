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

    private float calcularBrillo(Mat imagen) {
        Mat gray = new Mat();
        Imgproc.cvtColor(imagen, gray, Imgproc.COLOR_BGR2GRAY);
        Scalar mean = Core.mean(gray);
        return (float) mean.val[0];
    }

    public void segmentarImagen(String name) {
        // Cargar la imagen original solo para análisis
        Mat imagenOriginal = Imgcodecs.imread(this.path, Imgcodecs.IMREAD_COLOR);
        if (imagenOriginal.empty()) {
            System.err.println("Error: No se pudo cargar la imagen desde la ruta: " + this.path);
            return;
        }
        System.out.println("Imagen cargada correctamente desde: " + this.path);

        // Quitar el fondo de la imagen
        Mat imagenSinFondo = quitBackground(imagenOriginal);

        // Calcular brillo y contraste solo con la original
        float brillo = calcularBrillo(imagenOriginal);
        float contraste = calcularContraste(imagenOriginal);

        // Ajustar parámetros dinámicamente
        int gaussianKernelSize = (contraste > 50) ? 5 : 7;
        double cannyThreshold1 = brillo > 100 ? 50 : 30;
        double cannyThreshold2 = cannyThreshold1 * 3;
        double minContourArea = (contraste > 50) ? 200 : 150;
        double maxCircularity = 1.0;

        // Procesar la imagen sin fondo para hallar contornos
        Mat gray = new Mat();
        Imgproc.cvtColor(imagenSinFondo, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(gaussianKernelSize, gaussianKernelSize), 0);

        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, cannyThreshold1, cannyThreshold2);

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Trabajar sobre una copia de la imagen sin fondo para dibujar
        Mat imagenParaDibujar = imagenSinFondo.clone();

        // --- Detección del ojo ---
        MatOfPoint contornoOjo = null;
        double mayorCircularidad = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            double perimetro = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
            if (perimetro > 0 && area > minContourArea && area < 1000) {
                double circularidad = 4 * Math.PI * (area / (perimetro * perimetro));
                Rect boundingRect = Imgproc.boundingRect(contour);
                Point centroContorno = new Point(boundingRect.x + boundingRect.width / 2.0, boundingRect.y + boundingRect.height / 2.0);

                boolean enRegionSuperior = centroContorno.y < imagenSinFondo.rows() * 0.5;
                boolean enRegionInferior = centroContorno.y > imagenSinFondo.rows() * 0.5;

                if (circularidad > mayorCircularidad && circularidad > 0.7 && circularidad <= maxCircularity &&
                        (enRegionSuperior || enRegionInferior)) {
                    mayorCircularidad = circularidad;
                    contornoOjo = contour;
                }
            }
        }

        if (contornoOjo != null) {
            Rect boundingRectOjo = Imgproc.boundingRect(contornoOjo);
            Imgproc.rectangle(imagenParaDibujar, boundingRectOjo.tl(), boundingRectOjo.br(), new Scalar(0, 255, 0), 2);
            Imgproc.putText(imagenParaDibujar, "ojo", boundingRectOjo.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 255, 0), 2);
        } else {
            System.out.println("No se detectó ningún ojo.");
        }

        // --- Detección del cuerpo ---
        MatOfPoint contornoCuerpo = null;
        double distanciaMinimaCentro = Double.MAX_VALUE;
        Point centroImagen = new Point(imagenSinFondo.cols() / 2.0, imagenSinFondo.rows() / 2.0);
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            Point centroContorno = new Point(boundingRect.x + boundingRect.width / 2.0, boundingRect.y + boundingRect.height / 2.0);
            double distancia = Math.sqrt(Math.pow(centroContorno.x - centroImagen.x, 2) + Math.pow(centroContorno.y - centroImagen.y, 2));
            if (distancia < distanciaMinimaCentro) {
                distanciaMinimaCentro = distancia;
                contornoCuerpo = contour;
            }
        }
        if (contornoCuerpo != null) {
            Rect boundingRectCuerpo = Imgproc.boundingRect(contornoCuerpo);
            Point centroCuerpo = new Point(boundingRectCuerpo.x + boundingRectCuerpo.width / 2.0, boundingRectCuerpo.y + boundingRectCuerpo.height / 2.0);
            Point esquina1Cuerpo = new Point(centroCuerpo.x - 600 / 2.0, centroCuerpo.y - 800 / 2.0);
            Point esquina2Cuerpo = new Point(centroCuerpo.x + 600 / 2.0, centroCuerpo.y + 800 / 2.0);
            Imgproc.rectangle(imagenParaDibujar, esquina1Cuerpo, esquina2Cuerpo, new Scalar(255, 0, 0), 2);
            Imgproc.putText(imagenParaDibujar, "cuerpo", new Point(esquina2Cuerpo.x + 5, esquina2Cuerpo.y), Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 0, 0), 2);
        }

        // Guardar solo la imagen sin fondo y con los rectángulos
        String rutaConRectangulos = name.replace(".jpg", "_sin_fondo_rectangulos.jpg");
        boolean guardadoConRectangulos = Imgcodecs.imwrite(rutaConRectangulos, imagenParaDibujar);
        if (!guardadoConRectangulos) {
            System.err.println("Error: No se pudo guardar la imagen con rectángulos en: " + rutaConRectangulos);
        } else {
            System.out.println("Imagen con rectángulos guardada en: " + rutaConRectangulos);
        }
    }


// Método para quitar el fondo de la imagen
    public Mat quitBackground(Mat img) {
        // Convertir la imagen a escala de grises
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // Aplicar un umbral adaptativo para segmentar el fondo
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // Aplicar operaciones morfológicas para limpiar el ruido
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel);

        // Crear una máscara inversa
        Mat mask = new Mat();
        Core.bitwise_not(binary, mask);

        // Aplicar la máscara a la imagen original
        Mat resultado = new Mat();
        img.copyTo(resultado, mask);

        return resultado;
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