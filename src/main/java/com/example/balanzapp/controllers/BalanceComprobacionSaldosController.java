package com.example.balanzapp.controllers;

import com.example.balanzapp.dao.BalanceComprobacionDAO;
import com.example.balanzapp.models.BalanceComprobacion;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BalanceComprobacionSaldosController extends BaseController {

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
    @FXML private Label lblTotalDebe;
    @FXML private Label lblTotalHaber;


    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;

    @FXML private TableView<BalanceComprobacion> tblComprobacionSaldos;
    @FXML private TableColumn<BalanceComprobacion, String> colCodigo;
    @FXML private TableColumn<BalanceComprobacion, String> colCuenta;
    @FXML private TableColumn<BalanceComprobacion, Double> colDebe;
    @FXML private TableColumn<BalanceComprobacion, Double> colHaber;

    @FXML
    private void initialize() {
        cargarDatosUsuario();

        // Combo de balances (para navegar entre vistas)
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());

        // Configurar columnas de la tabla
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCuenta.setCellValueFactory(new PropertyValueFactory<>("cuenta"));
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));

        // Rango por defecto = mes actual
        YearMonth ym = YearMonth.now();
        dateDesde.setValue(ym.atDay(1));
        dateHasta.setValue(ym.atEndOfMonth());

        // Acciones
        btnDecargarPdf.setOnAction(e -> descargarpdf());
        btnDescargarExcel.setOnAction(e -> descargarexcel());
        btnbuscar.setOnAction(this::Buscar);
    }

    // ====== NAVEGACIÓN BALANCES ======

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

    // ====== BOTÓN BUSCAR ======

    @FXML
    void Buscar(ActionEvent event) {
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();

        if (desde == null || hasta == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Selecciona fecha Desde y Hasta.");
            return;
        }

        if (hasta.isBefore(desde)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La fecha Hasta no puede ser menor que Desde.");
            return;
        }

        var lista = BalanceComprobacionDAO.obtenerBalancePorRango(desde, hasta);
        tblComprobacionSaldos.setItems(FXCollections.observableArrayList(lista));

        if (lista.isEmpty()) {
            lblTotalDebe.setText("$0.00");
            lblTotalHaber.setText("$0.00");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Información",
                    "No hay movimientos en ese rango de fechas.");
            return;
        }

        // Calcular totales
        double totalDebe = lista.stream().mapToDouble(BalanceComprobacion::getDebe).sum();
        double totalHaber = lista.stream().mapToDouble(BalanceComprobacion::getHaber).sum();

        lblTotalDebe.setText(String.format("$%.2f", totalDebe));
        lblTotalHaber.setText(String.format("$%.2f", totalHaber));
    }


    // ====== NAVEGACIÓN GENERAL ======

    @FXML
    void goToBitacoraAuditor(ActionEvent actionEvent) {
        cambiarVista("/views/bitacora.fxml", actionEvent);
    }

    @FXML
    void goToCatalogoCuentas(ActionEvent actionEvent) {
        cambiarVista("/views/catalogo_cuenta.fxml", actionEvent);
    }

    @FXML
    void goToDoc(ActionEvent actionEvent) {
        cambiarVista("/views/documentos.fxml", actionEvent);
    }

    @FXML
    void goToEstadoResultados(ActionEvent actionEvent) {
        cambiarVista("/views/estadosResultados.fxml", actionEvent);
    }

    @FXML
    void goToHome(ActionEvent actionEvent) {
        cambiarVista("/views/inicio.fxml", actionEvent);
    }

    @FXML
    void goToLibroDiario(ActionEvent actionEvent) {
        cambiarVista("/views/libroDiario.fxml", actionEvent);
    }

    @FXML
    void goToLibroMayor(ActionEvent actionEvent) {
        cambiarVista("/views/libroMayor.fxml", actionEvent);
    }

    @FXML
    void goToUsuario(ActionEvent actionEvent) {
        cambiarVista("/views/usuarios.fxml", actionEvent);
    }

    @FXML
    void Close(ActionEvent actionEvent) {
        cambiarVista("/views/login.fxml", actionEvent);
    }

    private void cambiarVista(String ruta, ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ====== EXPORTAR PDF ======

    @FXML
    private void descargarpdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Balance De Comprobación como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Comprobacion_Saldos.pdf");

        Stage stage = (Stage) btnDecargarPdf.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            Document documento = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaGen = LocalDateTime.now().format(formatter);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("COMPROBACION DE SALDOS", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            String rango = "";
            if (dateDesde.getValue() != null && dateHasta.getValue() != null) {
                rango = "  (Desde " + dateDesde.getValue() + " Hasta " + dateHasta.getValue() + ")";
            }
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fechaGen + rango, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(20);

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(4); // Codigo, Cuenta, Debe, Haber
            tablaPDF.setWidthPercentage(100);

            String[] headers = {"Codigo", "Cuenta", "Debe", "Haber"};
            for (String h : headers) {
                PdfPCell celda = new PdfPCell(new Phrase(h));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }

            if (tblComprobacionSaldos.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(4);
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
                for (BalanceComprobacion b : tblComprobacionSaldos.getItems()) {
                    tablaPDF.addCell(b.getCodigo());
                    tablaPDF.addCell(b.getCuenta());
                    tablaPDF.addCell(String.format("%.2f", b.getDebe()));
                    tablaPDF.addCell(String.format("%.2f", b.getHaber()));
                }
            }

            documento.add(tablaPDF);
            documento.close();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El archivo PDF se generó correctamente.");
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al generar el PDF: " + ex.getMessage());
        }
    }

    // ====== EXPORTAR EXCEL ======

    @FXML
    private void descargarexcel() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Balance Comprobacion De Saldos en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Comprobacion_de_Saldos.xlsx");

        Stage stage = (Stage) btnDescargarExcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet hoja = workbook.createSheet("Balance Comprobacion");
            int filaIndex = 0;

            Row filaCabecera = hoja.createRow(filaIndex++);
            filaCabecera.createCell(0).setCellValue("Codigo");
            filaCabecera.createCell(1).setCellValue("Cuenta");
            filaCabecera.createCell(2).setCellValue("Debe");
            filaCabecera.createCell(3).setCellValue("Haber");

            for (BalanceComprobacion b : tblComprobacionSaldos.getItems()) {
                Row fila = hoja.createRow(filaIndex++);
                fila.createCell(0).setCellValue(b.getCodigo());
                fila.createCell(1).setCellValue(b.getCuenta());
                fila.createCell(2).setCellValue(b.getDebe());
                fila.createCell(3).setCellValue(b.getHaber());
            }

            for (int i = 0; i < 4; i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                workbook.write(fileOut);
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "El archivo Excel se generó correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al generar Excel: " + e.getMessage());
        }
    }

    // ====== ALERTAS ======

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
