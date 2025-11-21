package com.example.balanzapp.controllers;

import com.example.balanzapp.dao.BitacoraDAO;
import com.example.balanzapp.models.Bitacora;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BitacoraController extends BaseController {

    // Combo de balances del sidebar
    @FXML private ComboBox<String> cmbbalances;

    // Filtros bitácora
    @FXML private ComboBox<String> cmbusu;
    @FXML private DatePicker dtdesde;
    @FXML private DatePicker dthasta;
    @FXML private ComboBox<String> cmbmodul;
    @FXML private Button btnfiltrar;
    @FXML private Button btndescargarPdf;
    @FXML private Button btndescargarExcel;

    // Tabla
    @FXML private TableView<Bitacora> tblbitacora;
    @FXML private TableColumn<Bitacora, String> colUsuario;
    @FXML private TableColumn<Bitacora, String> colAccion;
    @FXML private TableColumn<Bitacora, String> colRol;
    @FXML private TableColumn<Bitacora, String> colModulo;
    @FXML private TableColumn<Bitacora, String> colFecha;
    @FXML private TableColumn<Bitacora, String> colHora;

    @FXML
    private void initialize() {
        cargarDatosUsuario();

        // ====== COMBO BALANCES SIDEBAR ======
        if (cmbbalances != null) {
            cmbbalances.getItems().addAll(
                    "Balance de comprobación de saldos",
                    "Balance general"
            );
            cmbbalances.setOnAction(e -> balanceSelec());
        }

        // ====== COMBOS DE FILTRO ======
        cargarCombosFiltros();

        // ====== CONFIGURAR TABLA ======
        configurarTabla();

        // ====== BOTONES ======
        btnfiltrar.setOnAction(e -> cargarBitacora());
        btndescargarPdf.setOnAction(e -> descargarPDF());
        btndescargarExcel.setOnAction(e -> descargarExcel());

        cargarBitacora();
    }

    // -------- CONFIGURAR TABLA ----------------------------------------------
    private void configurarTabla() {
        colUsuario.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getUsuario())
        );

        colRol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getRol())
        );

        colAccion.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAccion())
        );

        colModulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getModulo())
        );

        colFecha.setCellValueFactory(cell -> {
            String txt = "";
            if (cell.getValue().getFecha() != null) {
                txt = cell.getValue().getFecha().toString();
            }
            return new SimpleStringProperty(txt);
        });

        colHora.setCellValueFactory(cell -> {
            String txt = "";
            if (cell.getValue().getHora() != null) {
                txt = cell.getValue().getHora().toString();
            }
            return new SimpleStringProperty(txt);
        });
    }

    // -------- CARGAR COMBOS FILTROS ----------------------------------------
    private void cargarCombosFiltros() {
        // Usuarios
        cmbusu.getItems().clear();
        cmbusu.getItems().add("Todos");
        cmbusu.getItems().addAll(BitacoraDAO.obtenerUsuariosFiltro());
        cmbusu.setValue("Todos");

        // Módulos
        cmbmodul.getItems().clear();
        cmbmodul.getItems().add("Todos");
        cmbmodul.getItems().addAll(BitacoraDAO.obtenerModulosFiltro());
        cmbmodul.setValue("Todos");
    }

    // -------- BOTÓN FILTRAR (desde FXML: onAction="#filtrar") --------------
    @FXML
    private void filtrar(ActionEvent event) {
        cargarBitacora();
    }

    // -------- CARGAR BITÁCORA CON FILTROS ----------------------------------
    private void cargarBitacora() {
        // Usuario (nombre o null)
        String usuario = cmbusu.getValue();
        if ("Todos".equals(usuario)) {
            usuario = null;
        }

        // Módulo (texto o null)
        String modulo = cmbmodul.getValue();
        if ("Todos".equals(modulo)) {
            modulo = null;
        }

        // Fechas (pueden ser null)
        LocalDate desde = dtdesde.getValue();
        LocalDate hasta = dthasta.getValue();

        if (desde != null && hasta != null && hasta.isBefore(desde)) {
            mostrarAlerta("Error", "La fecha final no puede ser menor que la inicial.");
            return;
        }

        List<Bitacora> registros = BitacoraDAO.filtrarBitacora(usuario, desde, hasta, modulo);
        tblbitacora.setItems(FXCollections.observableArrayList(registros));
    }

    // ================== PDF ==================

    private void descargarPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Bitácora como PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf")
        );
        fileChooser.setInitialFileName("Bitacora_Auditor.pdf");

        Stage stage = (Stage) btndescargarPdf.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            Document documento = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaGen = LocalDateTime.now().format(formatter);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("BITÁCORA DEL AUDITOR", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fechaGen, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(15);

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(6);
            tablaPDF.setWidthPercentage(100);

            String[] headers = {"Usuario", "Rol", "Acción", "Módulo", "Fecha", "Hora"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                tablaPDF.addCell(cell);
            }

            for (Bitacora reg : tblbitacora.getItems()) {
                tablaPDF.addCell(reg.getUsuario());
                tablaPDF.addCell(reg.getRol());
                tablaPDF.addCell(reg.getAccion());
                tablaPDF.addCell(reg.getModulo());
                tablaPDF.addCell(reg.getFecha() != null ? reg.getFecha().toString() : "");
                tablaPDF.addCell(reg.getHora() != null ? reg.getHora().toString() : "");
            }

            documento.add(tablaPDF);
            documento.close();

            mostrarInfo("Éxito", "El archivo PDF se generó correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el PDF: " + e.getMessage());
        }
    }

    // ================== EXCEL ==================

    private void descargarExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Bitácora en Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx")
        );
        fileChooser.setInitialFileName("Bitacora_Auditor.xlsx");

        Stage stage = (Stage) btndescargarExcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet hoja = workbook.createSheet("Bitácora");
            int filaIndex = 0;

            // Cabecera
            Row filaCabecera = hoja.createRow(filaIndex++);
            filaCabecera.createCell(0).setCellValue("Usuario");
            filaCabecera.createCell(1).setCellValue("Rol");
            filaCabecera.createCell(2).setCellValue("Acción");
            filaCabecera.createCell(3).setCellValue("Módulo");
            filaCabecera.createCell(4).setCellValue("Fecha");
            filaCabecera.createCell(5).setCellValue("Hora");

            // Datos
            for (Bitacora reg : tblbitacora.getItems()) {
                Row fila = hoja.createRow(filaIndex++);
                fila.createCell(0).setCellValue(reg.getUsuario());
                fila.createCell(1).setCellValue(reg.getRol());
                fila.createCell(2).setCellValue(reg.getAccion());
                fila.createCell(3).setCellValue(reg.getModulo());
                fila.createCell(4).setCellValue(reg.getFecha() != null ? reg.getFecha().toString() : "");
                fila.createCell(5).setCellValue(reg.getHora() != null ? reg.getHora().toString() : "");
            }

            for (int i = 0; i < 6; i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                workbook.write(fileOut);
            }

            mostrarInfo("Éxito", "El archivo Excel se generó correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el Excel: " + e.getMessage());
        }
    }

    // ================== ALERTAS LOCALES ==================

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ================== NAVEGACIÓN (igual que otros controladores) ==========
    @FXML
    void Close(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToBitacoraAuditor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/bitacora.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToCatalogoCuentas(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/catalogo_cuenta.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToDoc(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/documentos.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToEstadoResultados(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/estadosResultados.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToHome(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/inicio.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToLibroDiario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroDiario.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToLibroMayor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroMayor.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToUsuario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/usuarios.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
