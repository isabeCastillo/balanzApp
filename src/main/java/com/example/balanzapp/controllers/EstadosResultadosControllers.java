package com.example.balanzapp.controllers;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EstadosResultadosControllers extends BaseController{

    @FXML
    private ComboBox<String> cmbbalances;

    @FXML
    private ComboBox<String> cmbAnio;

    @FXML
    private ComboBox<String> cmbMes;

    @FXML
    private ComboBox<String> cmbPeriodo;

    @FXML
    private Button btninicio;

    @FXML
    private Button btndoc;

    @FXML
    private Button btnlibrodiario;

    @FXML
    private Button btnlibromayor;

    @FXML
    private Button btnestadoderesultados;

    @FXML
    private Button btncatalogo;

    @FXML
    private Button btnusuario;

    @FXML
    private Button btnbitacora;

    @FXML
    private Button btncerrar;

    @FXML
    private Label lblUs;

    @FXML
    private Label lblad;

    @FXML
    private TableView<String> tblResultados;

    @FXML
    private Button btndescargarExcel;

    @FXML
    private Button btndescargarPdf;

    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cmbPeriodo.getItems().addAll("Mensual", "Trimestral", "Anual");
        cmbAnio.getItems().addAll("2023", "2024", "2025");
        cmbMes.getItems().addAll("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
        cmbbalances.getItems().addAll("Balance de comprobación de saldos", "Balance general");
        cmbbalances.setOnAction(event -> balanceSelec());
        btndescargarPdf.setOnAction(e -> descargarpdf());

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

    @FXML
    void Close(ActionEvent event) {
        cambiarVista("/views/login.fxml", event);
    }

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

            PdfPTable tablaPDF = new PdfPTable(tblResultados.getColumns().size());
            tablaPDF.setWidthPercentage(100);

            for (TableColumn<?, ?> col : tblResultados.getColumns()) {
                PdfPCell celda = new PdfPCell(new Phrase(col.getText()));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }
            if (tblResultados.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(tblResultados.getColumns().size());
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
               tblResultados.getItems().forEach(item -> {
                    for (TableColumn<?, ?> col : tblResultados.getColumns()) {
                        Object valor = col.getCellData(Integer.parseInt(item));
                        tablaPDF.addCell(valor == null ? "" : valor.toString());
                    }
                });
            }

            documento.add(tablaPDF);
            documento.close();

            Alerta("Éxito", "El archivo PDF se generó correctamente.");
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al generar el PDF: " + ex.getMessage());
        }
    }
    private void Alerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
