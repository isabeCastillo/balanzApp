package com.example.balanzapp.controllers;

import com.example.balanzapp.dao.PartidaDAO;
import com.example.balanzapp.models.EstadoResultadoFila;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EstadosResultadosControllers extends BaseController {

    // Navbar
    @FXML private ComboBox<String> cmbbalances;
    @FXML private Button btninicio;
    @FXML private Button btndoc;
    @FXML private Button btnlibrodiario;
    @FXML private Button btnlibromayor;
    @FXML private Button btnestadoderesultados;
    @FXML private Button btncatalogo;
    @FXML private Button btnusuario;
    @FXML private Button btnbitacora;
    @FXML private Button btncerrar;
    @FXML private Label lblUs;
    @FXML private Label lblad;

    // Filtros
    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;

    // Tabla estado de resultados
    @FXML private TableView<EstadoResultadoFila> tblResultados;
    @FXML private TableColumn<EstadoResultadoFila, String>  colCuenta;
    @FXML private TableColumn<EstadoResultadoFila, Double> colDebe;
    @FXML private TableColumn<EstadoResultadoFila, Double> colHaber;
    @FXML private TableColumn<EstadoResultadoFila, Double> colSaldo;

    // Botones
    @FXML private Button btndescargarExcel;
    @FXML private Button btndescargarPdf;
    @FXML private Button btnGenerar;

    // Utilidad neta
    @FXML private Label lblUtilidadNeta;

    @FXML
    public void initialize() {
        cargarDatosUsuario();

        // Combo de balances en navbar
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());

        // Configurar columnas de la tabla
        colCuenta.setCellValueFactory(new PropertyValueFactory<>("cuenta"));
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));

        // Fechas por defecto: desde inicio de mes hasta hoy
        LocalDate hoy = LocalDate.now();
        dateHasta.setValue(hoy);
        dateDesde.setValue(hoy.withDayOfMonth(1));

        // Acciones
        btnGenerar.setOnAction(e -> generarEstado());
        btndescargarPdf.setOnAction(e -> descargarpdf());
        btndescargarExcel.setOnAction(e -> descargarexcel());
    }

    private void balanceSelec() {
        String seleccion = cmbbalances.getValue();
        String rutaFXML = null;

        if ("Balance de comprobación de saldos".equals(seleccion)) {
            rutaFXML = "/views/balanceSaldos.fxml";
        } else if ("Balance general".equals(seleccion)) {
            rutaFXML = "/views/balanceGeneral.fxml";
        }

        if (rutaFXML != null) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
                Stage stage = (Stage) cmbbalances.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ================== GENERAR ESTADO ==================
    @FXML
    public void generarEstado() {
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();

        if (desde == null || hasta == null) {
            mostrarError("Selecciona las fechas Desde y Hasta.");
            return;
        }
        if (hasta.isBefore(desde)) {
            mostrarError("La fecha 'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        var lista = PartidaDAO.obtenerEstadoResultados(desde, hasta);
        tblResultados.setItems(FXCollections.observableArrayList(lista));

        double utilidad = PartidaDAO.calcularUtilidadNeta(desde, hasta);
        lblUtilidadNeta.setText(String.format("Utilidad neta: %.2f", utilidad));
    }

    // ================== NAVEGACIÓN ==================

    @FXML
    void Close(ActionEvent event) { cambiarVista("/views/login.fxml", event); }

    @FXML void goToHome(ActionEvent e) { cambiarVista("/views/inicio.fxml", e); }
    @FXML void goToDoc(ActionEvent e) { cambiarVista("/views/documentos.fxml", e); }
    @FXML void goToLibroDiario(ActionEvent e) { cambiarVista("/views/libroDiario.fxml", e); }
    @FXML void goToLibroMayor(ActionEvent e) { cambiarVista("/views/libroMayor.fxml", e); }
    @FXML void goToCatalogoCuentas(ActionEvent e) { cambiarVista("/views/catalogo_cuenta.fxml", e); }
    @FXML void goToUsuario(ActionEvent e) { cambiarVista("/views/usuarios.fxml", e); }
    @FXML void goToBitacoraAuditor(ActionEvent e) { cambiarVista("/views/bitacora.fxml", e); }
    @FXML void goToEstadoResultados(ActionEvent e) { cambiarVista("/views/estadosResultados.fxml", e); }

    private void cambiarVista(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================== PDF ==================
    @FXML
    private void descargarpdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Estado De Resultados como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Estado_De_Resultados.pdf");

        Stage stage = (Stage) btndescargarPdf.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            Document documento = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fecha = LocalDateTime.now().format(formatter);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("ESTADO DE RESULTADOS", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fecha, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(20);

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(tblResultados.getColumns().size());
            tablaPDF.setWidthPercentage(100);

            // Encabezados
            for (TableColumn<?, ?> col : tblResultados.getColumns()) {
                PdfPCell celda = new PdfPCell(new Phrase(col.getText()));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }

            // Datos
            if (tblResultados.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(tblResultados.getColumns().size());
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
                for (EstadoResultadoFila fila : tblResultados.getItems()) {
                    tablaPDF.addCell(fila.getCuenta());
                    tablaPDF.addCell(String.format("%.2f", fila.getDebe()));
                    tablaPDF.addCell(String.format("%.2f", fila.getHaber()));
                    tablaPDF.addCell(String.format("%.2f", fila.getSaldo()));
                }
            }

            documento.add(tablaPDF);

            Paragraph util = new Paragraph(
                    lblUtilidadNeta.getText(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)
            );
            util.setSpacingBefore(10);
            documento.add(util);

            documento.close();

            Alerta("Éxito", "El archivo PDF se generó correctamente.");
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al generar el PDF: " + ex.getMessage());
        }
    }

    // ================== EXCEL ==================
    @FXML
    private void descargarexcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Estados de Resultados en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Estados_Resultados.xlsx");

        Stage stage = (Stage) btndescargarExcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet hoja = workbook.createSheet("Estado de Resultados");
            int filaIndex = 0;

            // Encabezados
            Row filaCabecera = hoja.createRow(filaIndex++);
            int colIndex = 0;
            for (TableColumn<?, ?> col : tblResultados.getColumns()) {
                org.apache.poi.ss.usermodel.Cell cell = filaCabecera.createCell(colIndex++);
                cell.setCellValue(col.getText());
            }

            // Datos
            for (EstadoResultadoFila fila : tblResultados.getItems()) {
                Row filaExcel = hoja.createRow(filaIndex++);
                int c = 0;
                filaExcel.createCell(c++).setCellValue(fila.getCuenta());
                filaExcel.createCell(c++).setCellValue(fila.getDebe());
                filaExcel.createCell(c++).setCellValue(fila.getHaber());
                filaExcel.createCell(c++).setCellValue(fila.getSaldo());
            }

            for (int i = 0; i < tblResultados.getColumns().size(); i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                workbook.write(fileOut);
            }

            Alerta("Éxito","El archivo Excel se generó correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al generar Excel: " + e.getMessage());
        }
    }

    // ================== ALERTAS ==================
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void Alerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
