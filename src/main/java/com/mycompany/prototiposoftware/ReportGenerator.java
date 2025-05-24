package com.mycompany.prototiposoftware;



import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.databaseInteractions.DBConnect;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.processing.Lote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.mycompany.prototiposoftware.ResultAlgorithmController.determinarCalidad;

public class ReportGenerator {
    private static final Logger log = LoggerFactory.getLogger(ReportGenerator.class);
    // datos utilizables en la creacion del pdf

    String rutaCarpetaAnomalias = "src/main/imgAnomal/";
    String idLote;
    String fechaAnalisis = "0000";
    String cantidadMuestras = "null";
    String ciudadLote = "";
    String trazabilidadLote = "";
    String calidadPromedio = "null";
    String promedioOjos = "null";
    String promedioPiel = "nasdasd";
    String cantidadAnomalias = "0";

    String calificacionPromedioLote = determinarCalidad(calidadPromedio); //Baja, Media, Aceptable, Ideal. toca hacer la validación respecto al promedio con, por ejemplo, un if
    List<String[]> registrosAnalisisIndividual = new ArrayList<>(); //esta matriz debe almacenar las filas extraidas de la tabla de registro individual de cada pez perteneciente a una misma idLote
    String rutaGeneracionPDF = "";

    /**
     *
     * <p>El metodo <code>public void PDFgenerator()</code> construye el informe con los datos del lote y las muestras individuales.</p>
     *
     * <p><b>Nota:</b> La ruta de la carpeta desde donde se extraen las imágenes de anomalías se
     * toma por defecto como <code>src/main/imgAnomal/</code>. puede especificar una ruta
     * diferente añadiendo un parametro final de tipo <b>String</b> con la ruta deseada</p>
     *
     * @param idLote                ID único del lote analizado.
     * @param registrosAnalisisIndividual Lista de registros individuales por muestra, donde cada arreglo
     *                                    debe tener la siguiente estructura:
     * <ul>
     *   <li><b>[0]</b> id_imagen</li>
     *   <li><b>[1]</b> fecha_analisis</li>
     *   <li><b>[2]</b> ciudad</li>
     *   <li><b>[3]</b> trazabilidad</li>
     *   <li><b>[4]</b> calidad_piel</li>
     *   <li><b>[5]</b> calidad_ojo</li>
     *   <li><b>[6]</b> calidad_general</li>
     * </ul>
     *
     * @throws IOException Si ocurre un error al generar o guardar el PDF.
     */
    public void PDFgenerator(String idLote,  String[] params, String rutaGeneracionPDF, List<String[]> registrosAnalisisIndividual) throws IOException {
        PDFgenerator(idLote, params[0], params[1], params[3], params[2], params[4], params[6], params[7], params[5], rutaGeneracionPDF, registrosAnalisisIndividual, determinarCalidad(params[4]), rutaCarpetaAnomalias);
    }

    /**
     * Genera un informe PDF para un lote de análisis de calidad.
     *
     * <p>El constructor recibe los siguientes parámetros</p>
     *
     * @param idLote                ID único del lote analizado.
     * @param fechaAnalisis         Fecha en la que se realizó el análisis del lote.
     * @param cantidadMuestras      Número total de muestras analizadas dentro del lote.
     * @param ciudadLote            Ciudad o lugar de procedencia del lote.
     * @param trazabilidadLote      Información sobre la trazabilidad del lote (por ejemplo, origen, distribución).
     * @param calidadPromedio       Promedio general de calidad calculado para el lote (ej. "3.7/5").
     * @param promedioOjos          Promedio de calidad de los ojos de los peces del lote.
     * @param promedioPiel          Promedio de calidad de la piel de los peces del lote.
     * @param cantidadAnomalias     Número total de anomalías detectadas en las muestras.
     * @param registrosAnalisisIndividual Lista de registros individuales por muestra, donde cada arreglo
     *                                    debe tener la siguiente estructura:
     *
     * <ul>
     *   <li><b>[0]</b> id_imagen</li>
     *   <li><b>[1]</b> fecha_analisis</li>
     *   <li><b>[2]</b> ciudad</li>
     *   <li><b>[3]</b> trazabilidad</li>
     *   <li><b>[4]</b> calidad_piel</li>
     *   <li><b>[5]</b> calidad_ojo</li>
     *   <li><b>[6]</b> calidad_general</li>
     * </ul>
     * @param rutaCarpetaAnomalias  (Opcional) Especifica otra ruta de ubicación de la carpeta de imagenes de anomalias
     *                              si no agrega este parámetro al método, por defecto asigna la ruta <code>src/main/imgAnomal/</code>.
     *
     * @throws IOException Si ocurre un error al generar o guardar el PDF.
     */
    public void PDFgenerator(String idLote, String fechaAnalisis, String cantidadMuestras, String ciudadLote, String trazabilidadLote, String calidadPromedio, String promedioOjos, String promedioPiel, String cantidadAnomalias, String rutaGeneracionPDF,List<String[]> registrosAnalisisIndividual,String calidadFinalTextual , String rutaCarpetaAnomalias) throws IOException {

//############################################ ESTRUCTURA DEL PDF ##############################################
        //-----------------------------------------------------------------------------Variables ha extraer de la base de datos
        this.idLote = idLote; // toca pasarlo desde el resultado de analisis
        this.fechaAnalisis = fechaAnalisis;
        this.cantidadMuestras = cantidadMuestras;
        this.ciudadLote = ciudadLote;
        this.trazabilidadLote = trazabilidadLote;
        this.calidadPromedio = calidadPromedio;
        this.promedioOjos = promedioOjos;
        this.promedioPiel = promedioPiel;
        this.cantidadAnomalias = cantidadAnomalias;
        this.registrosAnalisisIndividual = registrosAnalisisIndividual;
        this.rutaCarpetaAnomalias = rutaCarpetaAnomalias;
        this.calificacionPromedioLote = calidadFinalTextual;

        //CREA EL PDF
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM_HH-mm-yyyy");
        String fechaSistema = now.format(formatter);
        String dest = rutaGeneracionPDF + "/reporte " +fechaSistema + ".pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdfDoc, PageSize.LETTER);
        document.setMargins(0, 0, 0, 0);

        //registra un evento (para que las paginas nuevas tengna un color de fondo)
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new BackgroundEventHandler());


        // DIV SUPERIOR (sección datos promedios de análisis)
        Div analisisReporteDiv = new Div()
                .setBackgroundColor(colorHex("#0e759e"))
                .setHeight((PageSize.LETTER.getHeight() / 2)+30)
                .setWidth(PageSize.LETTER.getWidth());

        // DIV INFERIOR (sección de tabla_imagenes_anomalias)
        Div feedbackDiv = new Div()
                //.setHeight((PageSize.LETTER.getHeight() / 2)-30)
                .setFontColor(colorHex("#111111"))
                .setPadding(10)
                .setWidth(PageSize.LETTER.getWidth());
        Div anomaliasDiv = new Div()
                //.setHeight((PageSize.LETTER.getHeight() / 2)-30)
                .setFontColor(colorHex("#111111"))
                .setPadding(10)
                .setWidth(PageSize.LETTER.getWidth());
        Div registroIndividualDiv = new Div()
                //.setHeight((PageSize.LETTER.getHeight() / 2)-30)
                .setFontColor(colorHex("#111111"))
                .setPadding(10)
                .setWidth(PageSize.LETTER.getWidth());

        // FUENTES VARIAS PARA IMPLEMENTAR
        PdfFont fontTimes = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont fontTimesB = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD); // Simula negrita por estilo, no cambia la fuente
        PdfFont fontHelvetica = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontHelveticaB = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fontCourier = PdfFontFactory.createFont(StandardFonts.COURIER);
        PdfFont fontCourierB = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
        PdfFont fontZAPFDINGBATS = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
        //-----------------------------------------------------------------------------Fin variables base de datos


        float[] columnas;  // variable para creación de tablas
        Image imageLoad;   // variable para cargar imagenes desde una ruta
//######################################### FIN ESTRUCTURA DEL PDF ##############################################



//###################################### DISEÑO ELEMENTOS CUERPO SUPERIOR ###################################
// -----------------------------------------Tabla 1-------------------------------------------------------//

// Tabla 2x2 para: fecha_creacion, cantidad_imagenes, ciudad, título estático
        columnas = new float[]{1, 1};
        Table tablaCalificacion = new Table(UnitValue.createPercentArray(columnas))
                .useAllAvailableWidth()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(DeviceRgb.WHITE)
                .setMarginBottom(10);

// Fila 1: Etiquetas
        tablaCalificacion.addCell(new Cell().add(new Paragraph("Fecha de análisis"))                    // (1)
                .setBackgroundColor(colorHex("#4289a5"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelveticaB)
                .setFontSize(8).setBorderRight(new SolidBorder(ColorConstants.WHITE, 0.5f)));

        tablaCalificacion.addCell(new Cell().add(new Paragraph("Cantidad de muestras"))                // (2)
                .setBackgroundColor(colorHex("#4289a5"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelveticaB)
                .setFontSize(8)
                .setFontSize(8).setBorderLeft(new SolidBorder(ColorConstants.WHITE, 0.5f)));

// Fila 2: Valores
        tablaCalificacion.addCell(new Cell().add(new Paragraph(fechaAnalisis))                 // (1)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(12)
                .setFontSize(8).setBorderRight(new SolidBorder(ColorConstants.WHITE, 0.5f)));

        tablaCalificacion.addCell(new Cell().add(new Paragraph(cantidadMuestras))                    // (2)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(12)
                .setFontSize(8).setBorderLeft(new SolidBorder(ColorConstants.WHITE, 0.5f)));

// Fila 3: Etiquetas
        tablaCalificacion.addCell(new Cell().add(new Paragraph("Ciudad del lote"))                  // (3)
                .setBackgroundColor(colorHex("4289a5"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelveticaB)
                .setFontSize(8)
                .setFontSize(8).setBorderRight(new SolidBorder(ColorConstants.WHITE, 0.5f)));

        tablaCalificacion.addCell(new Cell().add(new Paragraph("Trazabilidad del lote"))         // (4)
                .setBackgroundColor(colorHex("4289a5"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelveticaB)
                .setFontSize(8)
                .setFontSize(8).setBorderLeft(new SolidBorder(ColorConstants.WHITE, 0.5f)));

// Fila 4: Valores
        tablaCalificacion.addCell(new Cell().add(new Paragraph(ciudadLote))                    // (3)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(12)
                .setFontSize(8).setBorderRight(new SolidBorder(ColorConstants.WHITE, 0.5f)));

        tablaCalificacion.addCell(new Cell().add(new Paragraph(trazabilidadLote)) // (4)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(10)
                .setFontSize(8).setBorderLeft(new SolidBorder(ColorConstants.WHITE, 0.5f)));




// -----------------------------------------Tabla 2-------------------------------------------------------//


// Tabla con 4 columnas para: califinal, avg_ojos, avg_piel, cantidad_anomalias
        columnas = new float[]{1, 1, 1, 1};
        Table tablaStats = new Table(UnitValue.createPercentArray(columnas))
                .useAllAvailableWidth()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(DeviceRgb.WHITE);

// Fila 1: Valores
        tablaStats.addCell(new Cell().add(new Paragraph(calidadPromedio))             // (5)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(16));

        tablaStats.addCell(new Cell().add(new Paragraph(promedioOjos))              // (6)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(16));

        tablaStats.addCell(new Cell().add(new Paragraph(promedioPiel))              // (7)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(16));

        tablaStats.addCell(new Cell().add(new Paragraph(cantidadAnomalias))              // (9)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(16));

// Fila 2: Etiquetas
        tablaStats.addCell(new Cell().add(new Paragraph("Calificación Promedio"))   // (5)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(8));

        tablaStats.addCell(new Cell().add(new Paragraph("Promedio Ojos"))          // (6)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(8));

        tablaStats.addCell(new Cell().add(new Paragraph("Promedio Piel"))          // (7)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(8));

        tablaStats.addCell(new Cell().add(new Paragraph("Cantidad Anomalías"))     // (9)
                .setBackgroundColor(colorHex("#0e759e"))
                .setBorder(Border.NO_BORDER)
                .setFont(fontHelvetica)
                .setFontSize(8));


//############################### FIN DISEÑO ELEMENTOS CUERPO SUPERIOR ###################################

//################################# DISEÑO ELEMENTOS CUERPO INFERIOR #####################################
        columnas = new float[]{1, 1, 1, 1};
        Table tableAnomalies = new Table(UnitValue.createPercentArray(columnas))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(colorHex("#111111"));

        Path dir = Paths.get(rutaCarpetaAnomalias);

        try (Stream<Path> stream = Files.list(dir)) {
            for (Path path : (Iterable<Path>) stream::iterator) {
                if (Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".jpg")) {
                    System.out.println("Archivo JPG encontrado: " + path.getFileName());
                    imageLoad = new  Image(ImageDataFactory.create(path.toString())).setRotationAngle(Math.PI / 2).setHeight(120).setWidth(60);
                    tableAnomalies.addCell(new Cell().add(imageLoad.setHorizontalAlignment(HorizontalAlignment.CENTER)).setMargins(0, 5, 5, 0).setBorder(Border.NO_BORDER));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        // Tabla con 5 columnas: Nombre, Archivo, Calidad Piel, Calidad Ojos, Calidad General
        columnas = new float[]{2, 2, 1, 1, 1};
        Table tableRegistroIndividual = new Table(UnitValue.createPercentArray(columnas))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(colorHex("#111111"));

        // Encabezados
        String[] headers = {"Nombre", "Archivo", "Calidad Piel", "Calidad Ojos", "Calidad General"};
        for (String header : headers) {
            tableRegistroIndividual.addCell(new Cell().add(new Paragraph(header).setFontSize(10))
                    .setBackgroundColor(colorHex("#4295a5"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER));
        }

        // Filas de datos
        for (String[] fila : registrosAnalisisIndividual) {
            // Asume que fila[0]=Nombre, fila[1]=Archivo, fila[4]=Calidad Piel, fila[5]=Calidad Ojos, fila[6]=Calidad General

            // Nombre
            tableRegistroIndividual.addCell(new Cell()
                    .add(new Paragraph(fila[0]).setFontSize(8).setFontColor(colorHex("#414141")))
                    .setBackgroundColor(colorHex("#f1f1f1"))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Archivo como hipervínculo corto
            String fullPath = fila[1];
            String fileName = new java.io.File(fullPath).getName();
            Link link = new Link(fileName, PdfAction.createURI(fullPath));
            Paragraph linkParagraph = new Paragraph().add(link.setFontColor(ColorConstants.BLUE).setUnderline());
            tableRegistroIndividual.addCell(new Cell()
                    .add(linkParagraph.setFontSize(8))
                    .setBackgroundColor(colorHex("#cccece"))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Calidad Piel
            tableRegistroIndividual.addCell(new Cell()
                    .add(new Paragraph(fila[2]).setFontSize(8).setFontColor(colorHex("#414141")))
                    .setBackgroundColor(colorHex("#f1f1f1"))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Calidad Ojos
            tableRegistroIndividual.addCell(new Cell()
                    .add(new Paragraph(fila[3]).setFontSize(8).setFontColor(colorHex("#414141")))
                    .setBackgroundColor(colorHex("#cccece"))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Calidad General
            tableRegistroIndividual.addCell(new Cell()
                    .add(new Paragraph(fila[4]).setFontSize(8).setFontColor(colorHex("#414141")))
                    .setBackgroundColor(colorHex("#f1f1f1"))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
//################################# FIN DISEÑO ELEMENTOS CUERPO INFERIOR #####################################


//####################################### IMPLEMENTACIÓN DE ELEMENTOS ########################################
        //Logo y titulo
        //CARGA EL LOGO DEL PROGRAMA EN MEMORIA
        imageLoad = new Image(ImageDataFactory.create("src/main/resources/img/MainLogo.png"))
                .scaleAbsolute(150, 130)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        analisisReporteDiv.add(imageLoad);

        analisisReporteDiv.add(new Paragraph("Generador de reportes SACP")
                .setFont(fontHelveticaB)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(colorHex("#ffffff"))
                .setFontSize(20)
                .setMarginTop(0)
                .setMarginBottom(0));

        analisisReporteDiv.add(crearSeparador(colorHex("#ffffff")));
        // (8) Título de la sección de estadísticas
        analisisReporteDiv.add(tablaCalificacion.setMarginTop(10).setMarginLeft(30).setMarginRight(30));
        analisisReporteDiv.add(crearSeparador(colorHex("#ffffff")));
        analisisReporteDiv.add(new Paragraph("ESTADÍSTICAS PROMEDIO")
                .setFont(fontHelveticaB)
                .setFontSize(12)
                .setFontColor(DeviceRgb.WHITE)
                .setTextAlignment(TextAlignment.CENTER));
        analisisReporteDiv.add(tablaStats);
        analisisReporteDiv.add(crearSeparador(colorHex("#ffffff")));
        analisisReporteDiv.add(new Paragraph("CALIFICACIÓN PROMEDIO DEL LOTE")
                .setFont(fontHelveticaB)
                .setFontSize(12)
                .setFontColor(DeviceRgb.WHITE)
                .setTextAlignment(TextAlignment.CENTER));
        analisisReporteDiv.add(new Paragraph(calificacionPromedioLote)
                .setMarginTop(10)
                .setFont(fontHelveticaB)
                .setFontSize(10)
                .setFontColor(DeviceRgb.WHITE)
                .setTextAlignment(TextAlignment.CENTER));


        //-------------------------------------------------------- Mitad inferior -----------------------------------------------//

        feedbackDiv.add(new Paragraph("Feedback del Lote — Calificación: "+ calidadPromedio)
                .setFont(fontHelveticaB)
                .setFontSize(15)
                .setMarginLeft(30)
                .setMarginTop(0)
                .setFontColor(colorHex("#0e759e")));

        feedbackDiv.add(new Paragraph("El lote evaluado presenta una calidad general" + calificacionPromedioLote + ", aunque existen ciertos factores que podrían estar limitando un mejor desempeño en la calificación final. A continuación se detallan posibles aspectos a mejorar:")
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(10)
                .setFont(fontHelvetica));

        feedbackDiv.add(new Paragraph("• Condiciones Climáticas:")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelveticaB));
        feedbackDiv.add(new Paragraph("Variaciones en la temperatura o humedad ambiental podrían estar afectando el desarrollo óptimo de los especímenes. Es recomendable monitorear y controlar estos parámetros para evitar estrés en los peces.")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelvetica));

        feedbackDiv.add(new Paragraph("• Alimentación:")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelveticaB));
        feedbackDiv.add(new Paragraph("La calidad y frecuencia de la alimentación pueden influir directamente en la salud y crecimiento. Revisar la formulación del alimento y asegurar una dieta balanceada contribuirá a mejorar la condición general.")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelvetica));

        feedbackDiv.add(new Paragraph("• Calidad del agua:")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelveticaB));
        feedbackDiv.add(new Paragraph("Parámetros como el pH, oxígeno disuelto y niveles de contaminantes deben mantenerse en rangos ideales para la especie. Una gestión adecuada del sistema de agua puede prevenir enfermedades y mejorar la calidad del lote.")                .setMarginLeft(30)
                .setMarginLeft(50)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelvetica));

        feedbackDiv.add(new Paragraph("• Manejo y transporte: ")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelveticaB));
        feedbackDiv.add(new Paragraph("Procesos inadecuados durante el manejo o traslado pueden generar estrés o daño físico en los peces, lo cual impacta negativamente en la evaluación.")
                .setMarginLeft(50)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(9)
                .setFont(fontHelvetica));


        feedbackDiv.add(crearSeparador(colorHex("#bcbcbc")));
        feedbackDiv.add(new Paragraph("NOTA: Este feedback tiene como objetivo orientar acciones específicas para mejorar la calidad en futuras evaluaciones. Los consejos son generales y podría no aplicar en su contexto específico")
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(8)
                .setFont(fontHelveticaB));
        feedbackDiv.add(crearSeparador(colorHex("#bcbcbc")));


//-------------------------------------------------------------------------------------------------- SECCION DE ANOMALIAS
        anomaliasDiv.add(new Paragraph("Anomalías")
                .setFont(fontHelveticaB)
                .setFontSize(15)
                .setMarginLeft(30)
                .setMarginTop(10)
                .setFontColor(colorHex("#0e759e")));
        anomaliasDiv.add(new Paragraph("Durante el análisis automatizado del lote, el sistema puede llegar a detectar en algunos casos desviaciones significativas respecto a estos parámetros promedios. Estas desviaciones son clasificadas como anomalías.")
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(10)
                .setFont(fontHelvetica));

        anomaliasDiv.add(new Paragraph("Las imágenes mostradas a continuación corresponden a aquellas en las que se detectaron dichas anomalías. Esto puede incluir alteraciones físicas visibles, condiciones irregulares en la toma de la fotografía (como iluminación o ángulo), o especímenes que difieren considerablemente del comportamiento general del lote.")
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(10)
                .setFont(fontHelvetica));


        anomaliasDiv.add(crearSeparador(colorHex("#bcbcbc")));
        anomaliasDiv.add(new Paragraph("NOTA: Tenga en cuenta que la detección de una anomalía no implica necesariamente un defecto o rechazo del producto, sino que constituye un punto de atención para su revisión. La información presentada en esta sección permite al cliente tener mayor trazabilidad y control sobre los factores que podrían influir en la calidad del lote evaluado.")
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(8)
                .setFont(fontHelveticaB));
        anomaliasDiv.add(crearSeparador(colorHex("#bcbcbc")));
        anomaliasDiv.add(new Paragraph("Tabla de muestras con anomalías")
                .setFont(fontHelveticaB)
                .setFontSize(15)
                .setMarginLeft(30)
                .setMarginRight(30)
                .setMarginTop(10)
                .setPaddingLeft(10)
                .setBackgroundColor(colorHex("#4289a5"))
                .setFontColor(colorHex("#ffffff")));
        anomaliasDiv.add(tableAnomalies);

        registroIndividualDiv.add(new Paragraph("Registro individual por imagen")
                .setFont(fontHelveticaB)
                .setFontSize(15)
                .setMarginLeft(30)
                .setMarginTop(10)
                .setFontColor(colorHex("#0e759e")));
        registroIndividualDiv.add(new Paragraph("A continuación se mostrará el registro individual para cada imagen cargada perteneciente al lote analizado, con el fin de que identifique el comportamientdo del programa.")
                .setMarginLeft(30)
                .setMarginRight(30)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontColor(colorHex("#7e7e7e"))
                .setFontSize(10)
                .setFont(fontHelvetica));
        registroIndividualDiv.add(new Paragraph("Tabla de registros")
                .setFont(fontHelveticaB)
                .setFontSize(15)
                .setMarginLeft(30)
                .setMarginRight(30)
                .setMarginTop(10)
                .setPaddingLeft(10)
                .setBackgroundColor(colorHex("#4289a5"))
                .setFontColor(colorHex("#ffffff")));
        registroIndividualDiv.add(tableRegistroIndividual);
//#################################### FIN IMPLEMENTACIÓN DE ELEMENTOS ########################################



        // INCLUYE LOS DIVS EN EL PDF
        document.add(analisisReporteDiv);
        document.add(feedbackDiv);
        document.add(anomaliasDiv);
        Rectangle area = document.getRenderer().getCurrentArea().getBBox();

        // salto de pagina si ocupa mas del 70%
        float yActual = area.getY(); // Posición vertical actual (de abajo hacia arriba)
        float alturaPagina = document.getPdfDocument().getDefaultPageSize().getHeight();
        float alturaDisponible = yActual;

        float porcentajeOcupado = 1 - (alturaDisponible / alturaPagina);

        // Si se ha ocupado más del 60%
        if (porcentajeOcupado > 0.6) {
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        }


        document.add(registroIndividualDiv);

        // CIERRA EL DOCUMENTO
        document.close();
        System.out.println("PDF tamaño Carta generado: " + dest);
    }
//############################################## MÉTODO PARA CREAR EXCEL  ################################################
/**
 * Exporta los registros individuales a un archivo Excel (.xlsx).
 *
 * <p>
 * Crea un archivo Excel con las columnas: Nombre, Archivo, Calidad Piel, Calidad Ojos y Calidad General,
 * y escribe los datos de la lista proporcionada.
 * </p>
 *
 * @param rutaArchivo Ruta completa donde se guardará el archivo Excel generado (por ejemplo: "C:/reportes/tabla_individual.xlsx").
 * @param registrosAnalisisIndividual Lista de arreglos de String, donde cada arreglo representa una fila con los siguientes datos:
 *                                   <ul>
 *                                     <li>[0] Nombre</li>
 *                                     <li>[1] Archivo (ruta o nombre del archivo)</li>
 *                                     <li>[2] Calidad Piel</li>
 *                                     <li>[3] Calidad Ojos</li>
 *                                     <li>[4] Calidad General</li>
 *                                   </ul>
 * @throws IOException Si ocurre un error al crear o guardar el archivo Excel.
 *
 * <b>Uso:</b>
 * <pre>
 *   reportGenerator.exportarExcel("C:/reportes/tabla_individual.xlsx", registrosAnalisisIndividual);
 * </pre>
 */
public void exportarExcel(String rutaArchivo, List<String[]> registrosAnalisisIndividual) throws IOException {
    org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
    org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Registros Individuales");

    String[] headers = {"Nombre", "Archivo", "Calidad Piel", "Calidad Ojos", "Calidad General"};
    org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
        org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
    }

    int rowNum = 1;
    for (String[] fila : registrosAnalisisIndividual) {
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(fila[i] != null ? fila[i] : "");
        }
    }

    for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
    }

    try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
        workbook.write(fileOut);
    }
    workbook.close();
}


//############################################## MÉTODOS VARIOS ################################################

    //------------------------------------------------------------------------ METODO RECIBE HEX Y RETORNA RGB
    public static DeviceRgb colorHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() != 6) {
            throw new IllegalArgumentException("El color hexadecimal debe tener exactamente 6 caracteres.");
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new DeviceRgb(r, g, b);
    }
    //------------------------------------------------------------------------- LINEA SEPARADORA REUTILIZABLE (AJUSTABLE)
    public static LineSeparator crearSeparador(DeviceRgb color) {
        SolidLine solidLine = new SolidLine(1f); // Grosor fijo, o parametrizable si quieres
        solidLine.setColor(color);

        LineSeparator separator = new LineSeparator(solidLine);
        separator.setWidth(UnitValue.createPercentValue(90)); // Porcentaje del ancho
        separator.setHorizontalAlignment(HorizontalAlignment.CENTER);
        separator.setMarginTop(5);
        separator.setMarginBottom(5);

        return separator;
    }

    //----------------------------------------------------------------------------METODO EVENTO DE PAGINA NUEVA
    private static class BackgroundEventHandler implements IEventHandler {
        private static final DeviceRgb BACKGROUND_COLOR = colorHex("#ebebeb");

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            Rectangle rect = page.getPageSize();

            // Dibujar antes del contenido
            PdfCanvas canvas = new PdfCanvas(
                    page.newContentStreamBefore(),
                    page.getResources(),
                    docEvent.getDocument()
            );
            canvas
                    .saveState()
                    .setFillColor(BACKGROUND_COLOR)
                    .rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight())
                    .fill()
                    .restoreState();
        }
    }

//########################################## FIN MÉTODOS VARIOS ################################################
}