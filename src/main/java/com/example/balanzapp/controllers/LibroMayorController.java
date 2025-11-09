package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.dao.MayorDAO;
import com.example.balanzapp.models.MovimientoMayor;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LibroMayorController extends BaseController {

    @FXML private ComboBox<String> cmbbalances;

    @FXML private Button btnbitacora;
    @FXML private Button btncatalogo;
    @FXML private Button btncerrar;
    @FXML private Button btndoc;
    @FXML private Button btnestadoderesultados;
    @FXML private Button btninicio;
    @FXML private Button btnlibrodiario;
    @FXML private Button btnlibromayor;
    @FXML private Button btnusuario;

    @FXML private Label lblUs;
    @FXML private Label lblad;

    // Filtros propio del mayor
    @FXML private ComboBox<String> comboCuenta;
    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;
    @FXML private Button bntbuscar;

    // Esquema de mayor
    @FXML private ListView<String> listDebe;
    @FXML private ListView<String> listHaber;
    @FXML private Label lblNombreYTipoCuenta;
    @FXML private Label lblSaldo;

    @FXML private Button btndescargarpdf;
    @FXML private Button btndescargarexcel;

    private int idCuentaSeleccionada = -1;

    @FXML
    private void initialize() {
        cargarDatosUsuario();

        // combos de balances
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(e -> balanceSelec());

        // filtros
        cargarCuentasDesdeBD();
        dateDesde.setValue(LocalDate.now().withDayOfMonth(1)); // 1er día del mes
        dateHasta.setValue(LocalDate.now());

        bntbuscar.setOnAction(e -> buscarMayor());
        btndescargarpdf.setOnAction(e -> descargarpdf());
        btndescargarexcel.setOnAction(e -> descargarexcel());

        // si quieres que cargue algo al entrar, descomenta:
        // buscarMayor();
    }

    private void cargarCuentasDesdeBD() {
        String sql = "SELECT id_cuenta, codigo, nombre, tipo FROM tbl_cntaContables ORDER BY codigo";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_cuenta");
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");

                // muestra "1101 - Caja" en el combo
                comboCuenta.getItems().add(id + " - " + codigo + " - " + nombre + " - " + tipo);
            }

        } catch (SQLException e) {
            System.out.println("Error cargando cuentas: " + e.getMessage());
        }

        if (!comboCuenta.getItems().isEmpty()) {
            comboCuenta.getSelectionModel().selectFirst();
        }
    }

    private void buscarMayor() {
        if (comboCuenta.getValue() == null) {
            mostrarAlerta("Error", "Selecciona una cuenta.");
            return;
        }
        if (dateDesde.getValue() == null || dateHasta.getValue() == null) {
            mostrarAlerta("Error", "Selecciona el rango de fechas.");
            return;
        }

        String valorCuenta = comboCuenta.getValue();
        String[] partes = valorCuenta.split(" - ");
        idCuentaSeleccionada = Integer.parseInt(partes[0]);
        String codigo = partes.length > 1 ? partes[1] : "";
        String nombre = partes.length > 2 ? partes[2] : "";
        String tipo = partes.length > 3 ? partes[3] : "";

        lblNombreYTipoCuenta.setText(codigo + " " + nombre + " - " + tipo);

        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();

        if (hasta.isBefore(desde)) {
            mostrarAlerta("Error", "La fecha final no puede ser menor que la inicial.");
            return;
        }

        List<MovimientoMayor> lista = MayorDAO.obtenerMayorPorCuentaYRango(
                idCuentaSeleccionada, desde, hasta
        );

        listDebe.getItems().clear();
        listHaber.getItems().clear();

        if (lista.isEmpty()) {
            lblSaldo.setText("SALDO: $0.00");
            mostrarInfo("Información", "No hay movimientos para esa cuenta en el rango seleccionado.");
            return;
        }

        double saldoFinal = 0.0;

        for (MovimientoMayor mov : lista) {
            String fechaStr = mov.getFecha() != null ? mov.getFecha().toString() : "";
            String textoBase = fechaStr + " | " + mov.getConcepto();

            if (mov.getDescripcion() != null && !mov.getDescripcion().isBlank()) {
                textoBase += " - " + mov.getDescripcion();
            }

            if (mov.getDebe() > 0) {
                listDebe.getItems().add(textoBase + " | " + String.format("%.2f", mov.getDebe()));
            } else {
                listDebe.getItems().add("");
            }

            if (mov.getHaber() > 0) {
                listHaber.getItems().add(textoBase + " | " + String.format("%.2f", mov.getHaber()));
            } else {
                listHaber.getItems().add("");
            }

            saldoFinal = mov.getSaldo();
        }

        lblSaldo.setText(String.format("SALDO: $%.2f", saldoFinal));
    }

    // ==== PDF & EXCEL ========================================================

    @FXML
    private void descargarpdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Libro Mayor como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Libro_Mayor.pdf");

        Stage stage = (Stage) btndescargarpdf.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            Document documento = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fecha = LocalDateTime.now().format(formatter);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("LIBRO MAYOR", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fecha, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(20);

            Paragraph cuentaParrafo = new Paragraph(lblNombreYTipoCuenta.getText());
            cuentaParrafo.setAlignment(Element.ALIGN_CENTER);
            cuentaParrafo.setSpacingAfter(10);

            documento.add(titulo);
            documento.add(fechaParrafo);
            documento.add(cuentaParrafo);

            PdfPTable tablaPDF = new PdfPTable(2);
            tablaPDF.setWidthPercentage(100);

            PdfPCell cDebe = new PdfPCell(new Phrase("DEBE"));
            cDebe.setHorizontalAlignment(Element.ALIGN_CENTER);
            cDebe.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell cHaber = new PdfPCell(new Phrase("HABER"));
            cHaber.setHorizontalAlignment(Element.ALIGN_CENTER);
            cHaber.setBackgroundColor(BaseColor.LIGHT_GRAY);

            tablaPDF.addCell(cDebe);
            tablaPDF.addCell(cHaber);

            int filas = Math.max(listDebe.getItems().size(), listHaber.getItems().size());

            for (int i = 0; i < filas; i++) {
                String d = i < listDebe.getItems().size() ? listDebe.getItems().get(i) : "";
                String h = i < listHaber.getItems().size() ? listHaber.getItems().get(i) : "";
                tablaPDF.addCell(d);
                tablaPDF.addCell(h);
            }

            documento.add(tablaPDF);

            Paragraph saldo = new Paragraph(lblSaldo.getText(), fontFecha);
            saldo.setAlignment(Element.ALIGN_RIGHT);
            saldo.setSpacingBefore(15);
            documento.add(saldo);

            documento.close();

            mostrarInfo("Éxito", "El archivo PDF se generó correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Error al generar el PDF: " + ex.getMessage());
        }
    }

    @FXML
    private void descargarexcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Libro Mayor en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Libro_Mayor.xlsx");

        Stage stage = (Stage) btndescargarexcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet hoja = workbook.createSheet("Libro Mayor");
            int filaIndex = 0;

            Row filaCabecera = hoja.createRow(filaIndex++);
            filaCabecera.createCell(0).setCellValue("DEBE");
            filaCabecera.createCell(1).setCellValue("HABER");

            int filas = Math.max(listDebe.getItems().size(), listHaber.getItems().size());
            for (int i = 0; i < filas; i++) {
                Row fila = hoja.createRow(filaIndex++);
                String d = i < listDebe.getItems().size() ? listDebe.getItems().get(i) : "";
                String h = i < listHaber.getItems().size() ? listHaber.getItems().get(i) : "";
                fila.createCell(0).setCellValue(d);
                fila.createCell(1).setCellValue(h);
            }

            Row filaSaldo = hoja.createRow(filaIndex + 1);
            filaSaldo.createCell(0).setCellValue(lblSaldo.getText());

            hoja.autoSizeColumn(0);
            hoja.autoSizeColumn(1);

            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                workbook.write(fileOut);
            }

            mostrarInfo("Éxito", "El archivo Excel se generó correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar Excel: " + e.getMessage());
        }
    }

    // ==== NAVEGACIÓN Y UTILIDADES ============================================

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

    // ==== LOS MISMOS MÉTODOS DE NAVEGACIÓN QUE YA TENÍAS =====================

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
        // ya estás en Libro Mayor; opcionalmente recargar:
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
}
