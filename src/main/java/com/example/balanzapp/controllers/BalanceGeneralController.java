package com.example.balanzapp.controllers;

import com.example.balanzapp.dao.PartidaDAO;
import com.example.balanzapp.models.BalanceGeneralFila;
import com.example.balanzapp.service.AuditoriaService;
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
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BalanceGeneralController extends BaseController {

    @FXML private Button btnDecargarPdf;
    @FXML private Button btnDescargarExcel;
    @FXML private Button btnbitacora;
    @FXML private Button btnbuscar;
    @FXML private Button btncatalogo;
    @FXML private Button btncerrar;
    @FXML private Button btndoc;
    @FXML private Button btnestadoderesultados;
    @FXML private Button btninicio;
    @FXML private Button btnlibrodiario;
    @FXML private Button btnlibromayor;
    @FXML private Button btnusuario;
    @FXML private ComboBox<String> cmbbalances;
    @FXML private Label lblUs;
    @FXML private Label lblad;

    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;

    @FXML private TableView<BalanceGeneralFila> tblBalanceGeneral;
    @FXML private TableColumn<BalanceGeneralFila, String>  colActivo;
    @FXML private TableColumn<BalanceGeneralFila, Double> colSaldoActivo;
    @FXML private TableColumn<BalanceGeneralFila, String>  colPasivo;
    @FXML private TableColumn<BalanceGeneralFila, Double> colSaldoPasivo;
    @FXML private TableColumn<BalanceGeneralFila, String>  colPatrimonio;
    @FXML private TableColumn<BalanceGeneralFila, Double> colSaldoPatrimonio;

    @FXML private Label lblTotalActivo;
    @FXML private Label lblTotalPasivo;
    @FXML private Label lblTotalPatrimonio;
    @FXML private Label lblUtilidadNeta;

    @FXML
    private void initialize() {
        cargarDatosUsuario();

        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());

        // Configurar columnas de la tabla
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colSaldoActivo.setCellValueFactory(new PropertyValueFactory<>("saldoActivo"));
        colPasivo.setCellValueFactory(new PropertyValueFactory<>("pasivo"));
        colSaldoPasivo.setCellValueFactory(new PropertyValueFactory<>("saldoPasivo"));
        colPatrimonio.setCellValueFactory(new PropertyValueFactory<>("patrimonio"));
        colSaldoPatrimonio.setCellValueFactory(new PropertyValueFactory<>("saldoPatrimonio"));

        // Fechas por defecto: último mes
        LocalDate hoy = LocalDate.now();
        dateHasta.setValue(hoy);
        dateDesde.setValue(hoy.minusMonths(1));

        btnDecargarPdf.setOnAction(e -> descargarpdf());
        btnDescargarExcel.setOnAction(e -> descargarexcel());
    }

    private void balanceSelec() {
        String seleccion = cmbbalances.getValue();
        String rutaFXML = null;

        if (seleccion.equals("Balance de comprobación de saldos")) {
            rutaFXML = "/views/balanceSaldos.fxml";
        } else if (seleccion.equals("Balance general")) {
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

    // ================== BUSCAR ==================

    @FXML
    void Buscar(ActionEvent event) {
        if (dateDesde.getValue() == null || dateHasta.getValue() == null) {
            mostrarError("Selecciona la fecha 'Desde' y 'Hasta'.");
            return;
        }

        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();

        if (hasta.isBefore(desde)) {
            mostrarError("'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        var filas = PartidaDAO.obtenerBalanceGeneral(desde, hasta);
        tblBalanceGeneral.getItems().setAll(filas);

        double totalActivo = filas.stream().mapToDouble(BalanceGeneralFila::getSaldoActivo).sum();
        double totalPasivo = filas.stream().mapToDouble(BalanceGeneralFila::getSaldoPasivo).sum();
        double totalPatrimonio = filas.stream().mapToDouble(BalanceGeneralFila::getSaldoPatrimonio).sum();

        double totalPasivoMasPatrimonio = totalPasivo + totalPatrimonio;

        lblTotalActivo.setText(String.format("Total Activo: %.2f", totalActivo));
        lblTotalPatrimonio.setText(String.format("Total Pasivo: %.2f", totalPasivo));
        lblTotalPasivo.setText(String.format("Total Pasivo + Capital: %.2f", totalPasivoMasPatrimonio));

        double utilidadNeta = PartidaDAO.calcularUtilidadNeta(desde, hasta);
        lblUtilidadNeta.setText(String.format("Utilidad Neta del Ejercicio: %.2f", utilidadNeta));
        AuditoriaService.registrarAccion(
                "Balance General",
                "Consultó el Balance General",
                "Desde: " + dateDesde.getValue()
                        + " | Hasta: " + dateHasta.getValue()
        );

    }

    // ================== NAVEGACIÓN ==================

    @FXML
    void Close(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToBitacoraAuditor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/bitacora.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToCatalogoCuentas(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/catalogo_cuenta.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToDoc(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/documentos.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToEstadoResultados(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/estadosResultados.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToHome(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/inicio.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToLibroDiario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroDiario.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToLibroMayor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroMayor.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToUsuario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/usuarios.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ================== EXPORTAR PDF / EXCEL ==================

    @FXML
    private void descargarpdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Balance General como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Balance_General.pdf");

        Stage stage = (Stage) btnDecargarPdf.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            Document documento = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fecha = LocalDateTime.now().format(formatter);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("BALANCE GENERAL", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fecha, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(20);

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(6);
            tablaPDF.setWidthPercentage(100);

            String[] headers = {"Activo", "Saldo Activo", "Pasivo", "Saldo Pasivo", "Patrimonio", "Saldo Patrimonio"};
            for (String h : headers) {
                PdfPCell celda = new PdfPCell(new Phrase(h));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }

            if (tblBalanceGeneral.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(6);
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
                double totalActivo = 0, totalPasivo = 0, totalPatrimonio = 0;

                for (BalanceGeneralFila fila : tblBalanceGeneral.getItems()) {
                    tablaPDF.addCell(fila.getActivo() == null ? "" : fila.getActivo());
                    tablaPDF.addCell(String.format("%.2f", fila.getSaldoActivo()));
                    tablaPDF.addCell(fila.getPasivo() == null ? "" : fila.getPasivo());
                    tablaPDF.addCell(String.format("%.2f", fila.getSaldoPasivo()));
                    tablaPDF.addCell(fila.getPatrimonio() == null ? "" : fila.getPatrimonio());
                    tablaPDF.addCell(String.format("%.2f", fila.getSaldoPatrimonio()));

                    totalActivo += fila.getSaldoActivo();
                    totalPasivo += fila.getSaldoPasivo();
                    totalPatrimonio += fila.getSaldoPatrimonio();
                }

                // Fila de totales
                PdfPCell celdaTot = new PdfPCell(new Phrase("TOTALES:"));
                celdaTot.setColspan(1);
                celdaTot.setBackgroundColor(BaseColor.YELLOW);
                tablaPDF.addCell(celdaTot);
                tablaPDF.addCell(new PdfPCell(new Phrase(String.format("%.2f", totalActivo))));

                tablaPDF.addCell(new PdfPCell()); // vacío bajo "Pasivo"
                tablaPDF.addCell(new PdfPCell(new Phrase(String.format("%.2f", totalPasivo))));

                tablaPDF.addCell(new PdfPCell()); // vacío bajo "Patrimonio"
                tablaPDF.addCell(new PdfPCell(new Phrase(String.format("%.2f", totalPatrimonio))));
            }

            documento.add(tablaPDF);
            documento.close();
            AuditoriaService.registrarAccion(
                    "Balance General",
                    "Descargó el Balance General en PDF",
                    "Desde: " + dateDesde.getValue()
                            + " | Hasta: " + dateHasta.getValue()
            );

            Alerta("Éxito", "El archivo PDF se generó correctamente.");
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al generar el PDF: " + ex.getMessage());
        }
    }

    @FXML
    private void descargarexcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Balance General en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Balance_General.xlsx");

        Stage stage = (Stage) btnDescargarExcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet hoja = workbook.createSheet("Balance General");
            int filaIndex = 0;

            Row filaCabecera = hoja.createRow(filaIndex++);
            filaCabecera.createCell(0).setCellValue("Activo");
            filaCabecera.createCell(1).setCellValue("Saldo Activo");
            filaCabecera.createCell(2).setCellValue("Pasivo");
            filaCabecera.createCell(3).setCellValue("Saldo Pasivo");
            filaCabecera.createCell(4).setCellValue("Patrimonio");
            filaCabecera.createCell(5).setCellValue("Saldo Patrimonio");

            double totalActivo = 0, totalPasivo = 0, totalPatrimonio = 0;

            for (BalanceGeneralFila fila : tblBalanceGeneral.getItems()) {
                Row r = hoja.createRow(filaIndex++);
                r.createCell(0).setCellValue(fila.getActivo() == null ? "" : fila.getActivo());
                r.createCell(1).setCellValue(fila.getSaldoActivo());
                r.createCell(2).setCellValue(fila.getPasivo() == null ? "" : fila.getPasivo());
                r.createCell(3).setCellValue(fila.getSaldoPasivo());
                r.createCell(4).setCellValue(fila.getPatrimonio() == null ? "" : fila.getPatrimonio());
                r.createCell(5).setCellValue(fila.getSaldoPatrimonio());

                totalActivo += fila.getSaldoActivo();
                totalPasivo += fila.getSaldoPasivo();
                totalPatrimonio += fila.getSaldoPatrimonio();
            }

            // Fila de totales
            Row filaTotales = hoja.createRow(filaIndex++);
            filaTotales.createCell(0).setCellValue("TOTALES:");
            filaTotales.createCell(1).setCellValue(totalActivo);
            filaTotales.createCell(3).setCellValue(totalPasivo);
            filaTotales.createCell(5).setCellValue(totalPatrimonio);

            for (int i = 0; i < 6; i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                workbook.write(fileOut);
            }
            AuditoriaService.registrarAccion(
                    "Balance General",
                    "Descargó el Balance General en Excel",
                    "Desde: " + dateDesde.getValue()
                            + " | Hasta: " + dateHasta.getValue()
            );

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