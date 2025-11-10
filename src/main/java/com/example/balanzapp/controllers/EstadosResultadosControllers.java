package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.dao.PartidaDAO;
import com.example.balanzapp.models.EstadoResultadoFila;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class EstadosResultadosControllers extends BaseController {

    @FXML private ComboBox<String> cmbbalances;
    @FXML private ComboBox<String> cmbAnio;
    @FXML private ComboBox<String> cmbMes;
    @FXML private ComboBox<String> cmbPeriodo;

    @FXML private Button btninicio;
    @FXML private Button btndoc;
    @FXML private Button btnlibrodiario;
    @FXML private Button btnlibromayor;
    @FXML private Button btnestadoderesultados;
    @FXML private Button btncatalogo;
    @FXML private Button btnusuario;
    @FXML private Button btnbitacora;
    @FXML private Button btncerrar;

    @FXML private Button btnGenerar;
    @FXML private Button btndescargarExcel;
    @FXML private Button btndescargarPdf;

    @FXML private Label lblUs;
    @FXML private Label lblad;
    @FXML private Label lblUtilidadNeta;

    @FXML private TableView<EstadoResultadoFila> tblResultados;
    @FXML private TableColumn<EstadoResultadoFila, String>  colCuenta;
    @FXML private TableColumn<EstadoResultadoFila, Double> colDebe;
    @FXML private TableColumn<EstadoResultadoFila, Double> colHaber;
    @FXML private TableColumn<EstadoResultadoFila, Double> colSaldo;

    @FXML
    public void initialize() {
        cargarDatosUsuario();

        cmbPeriodo.getItems().addAll("Mensual", "Trimestral", "Anual");
        cargarAniosDesdeBD();
        cmbMes.getItems().addAll(
                "Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        );

        cmbbalances.getItems().addAll("Balance de comprobación de saldos", "Balance general");
        cmbbalances.setOnAction(event -> balanceSelec());

        // Configurar columnas de la tabla
        colCuenta.setCellValueFactory(new PropertyValueFactory<>("cuenta"));
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));

        btndescargarPdf.setOnAction(e -> descargarpdf());
        btndescargarExcel.setOnAction(e -> descargarexcel());
    }

    private void cargarAniosDesdeBD() {
        String sql = "SELECT DISTINCT EXTRACT(YEAR FROM fecha) FROM tbl_partidas ORDER BY 1 DESC";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbAnio.getItems().add(String.valueOf((int) rs.getDouble(1)));
            }
        } catch (SQLException e) {
            System.out.println("Error cargando años: " + e.getMessage());
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

    // ================== GENERAR ESTADO ==================

    @FXML
    void generarEstado(ActionEvent event) {
        String periodo = cmbPeriodo.getValue();
        String anioStr = cmbAnio.getValue();
        String mesStr  = cmbMes.getValue();

        if (periodo == null || anioStr == null) {
            mostrarError("Selecciona periodo y año.");
            return;
        }

        int anio = Integer.parseInt(anioStr);
        LocalDate desde;
        LocalDate hasta;

        switch (periodo) {
            case "Mensual" -> {
                if (mesStr == null) {
                    mostrarError("Selecciona el mes para el periodo mensual.");
                    return;
                }
                int mes = getNumeroMes(mesStr);
                YearMonth ym = YearMonth.of(anio, mes);
                desde = ym.atDay(1);
                hasta = ym.atEndOfMonth();
            }
            case "Trimestral" -> {
                if (mesStr == null) {
                    mostrarError("Selecciona un mes dentro del trimestre.");
                    return;
                }
                int mesRef = getNumeroMes(mesStr);
                int mesInicioTrimestre = ((mesRef - 1) / 3) * 3 + 1;
                int mesFinTrimestre = mesInicioTrimestre + 2;

                YearMonth ymInicio = YearMonth.of(anio, mesInicioTrimestre);
                YearMonth ymFin = YearMonth.of(anio, mesFinTrimestre);

                desde = ymInicio.atDay(1);
                hasta = ymFin.atEndOfMonth();
            }
            case "Anual" -> {
                desde = LocalDate.of(anio, 1, 1);
                hasta = LocalDate.of(anio, 12, 31);
            }
            default -> {
                mostrarError("Periodo no válido.");
                return;
            }
        }

        var filas = PartidaDAO.obtenerEstadoResultados(desde, hasta);
        tblResultados.getItems().setAll(filas);

        double utilidad = PartidaDAO.calcularUtilidadNeta(desde, hasta);
        lblUtilidadNeta.setText(String.format("Utilidad neta: %.2f", utilidad));
    }

    private int getNumeroMes(String nombreMes) {
        return switch (nombreMes) {
            case "Enero" -> 1;
            case "Febrero" -> 2;
            case "Marzo" -> 3;
            case "Abril" -> 4;
            case "Mayo" -> 5;
            case "Junio" -> 6;
            case "Julio" -> 7;
            case "Agosto" -> 8;
            case "Septiembre" -> 9;
            case "Octubre" -> 10;
            case "Noviembre" -> 11;
            case "Diciembre" -> 12;
            default -> 1;
        };
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

    // ================== EXPORTAR PDF ==================

    @FXML
    private void descargarpdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Estado De Resultados como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("EstadoDe_Resultados.pdf");

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

            PdfPTable tablaPDF = new PdfPTable(4);
            tablaPDF.setWidthPercentage(100);

            String[] headers = {"Cuenta", "Debe", "Haber", "Saldo"};
            for (String h : headers) {
                PdfPCell celda = new PdfPCell(new Phrase(h));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }

            if (tblResultados.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(4);
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

            // Agregar utilidad neta al final
            Paragraph utilidadParrafo = new Paragraph(lblUtilidadNeta.getText());
            utilidadParrafo.setSpacingBefore(10);
            documento.add(utilidadParrafo);

            documento.close();

            Alerta("Éxito", "El archivo PDF se generó correctamente.");
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al generar el PDF: " + ex.getMessage());
        }
    }

    // ================== EXPORTAR EXCEL ==================

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

            Row filaCabecera = hoja.createRow(filaIndex++);
            filaCabecera.createCell(0).setCellValue("Cuenta");
            filaCabecera.createCell(1).setCellValue("Debe");
            filaCabecera.createCell(2).setCellValue("Haber");
            filaCabecera.createCell(3).setCellValue("Saldo");

            for (EstadoResultadoFila fila : tblResultados.getItems()) {
                Row r = hoja.createRow(filaIndex++);
                r.createCell(0).setCellValue(fila.getCuenta());
                r.createCell(1).setCellValue(fila.getDebe());
                r.createCell(2).setCellValue(fila.getHaber());
                r.createCell(3).setCellValue(fila.getSaldo());
            }

            // Fila con utilidad neta
            Row rUtil = hoja.createRow(filaIndex++);
            rUtil.createCell(0).setCellValue("Utilidad neta");
            rUtil.createCell(1).setCellValue(lblUtilidadNeta.getText());

            for (int i = 0; i < 4; i++) {
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