package com.practicas;

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
            Mat pezAislado = quitBackground(imagenOriginal);
            Imgcodecs.imwrite(name, pezAislado);
           /* // 2. Continuar con segmentación de ojos/piel...
            segmentos = new ArrayList<>();
            Mat edges = new Mat();
            Imgproc.Canny(pezAislado, edges, 50, 150);

            segmentos = new ArrayList<>();

            // 1. Preprocesamiento (escala de grises + desenfoque)
            Mat gray = new Mat();
            Imgproc.cvtColor(imagenOriginal, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

            // 2. Detección de bordes (Canny)
            Mat edges = new Mat();
            Imgproc.Canny(gray, edges, 50, 150);

            // 3. Encontrar contornos
            Mat hierarchy = new Mat();
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // 4. Guardar cada contorno como un Segmento
            for (MatOfPoint contour : contours) {
                Segmento segmento = new Segmento();
                segmento.setContorno(contour); // Guardar puntos del contorno
                segmento.setArea(Imgproc.contourArea(contour)); // Calcular área
                segmentos.add(segmento);
            }

            // Opcional: Calcular brillo y contraste promedio
            this.brillo = (float) Core.mean(imagenOriginal).val[0];
            this.contraste = calcularContraste(imagenOriginal);*/
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


        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public float getBrillo() { return brillo; }
        public void setBrillo(float brillo) { this.brillo = brillo; }

        public float getContraste() { return contraste; }
        public void setContraste(float contraste) { this.contraste = contraste; }

        public List<Segmento> getSegmentos() { return segmentos; }
        public void setSegmentos(List<Segmento> segmentos) { this.segmentos = segmentos; }

    public void setValoracion(Valoracion valoracion) {
            this.valoracion = valoracion;
    }
}