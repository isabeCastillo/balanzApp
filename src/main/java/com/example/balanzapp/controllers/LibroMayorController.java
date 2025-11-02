package com.example.balanzapp.controllers;

import com.example.balanzapp.MainApp;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LibroMayorController extends BaseController {

    @FXML
    private Button btnagregar;

    @FXML
    private ComboBox<String> cmbbalances;

    @FXML
    private Button btnbitacora;

    @FXML
    private Button btncatalogo;

    @FXML
    private Button btncerrar;

    @FXML
    private Button btndoc;

    @FXML
    private Button btneditar;

    @FXML
    private Button btneliminar;

    @FXML
    private Button btnestadoderesultados;

    @FXML
    private Button btninicio;

    @FXML
    private Button btnlibrodiario;

    @FXML
    private Button btnlibromayor;

    @FXML
    private Button btnusuario;

    @FXML
    private ComboBox<?> cmbElegirDoc;

    @FXML
    private Label lblUs;

    @FXML
    private Label lblad;

    @FXML
    private TextField txtNombreDocumento;

    @FXML
    private Button btndescargarexcel;

    @FXML
    private Button btndescargarpdf;

    @FXML
    private TableView<String> tablaMayor;


    @FXML
    private void initialize(){
        cargarDatosUsuario();
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());
        btndescargarpdf.setOnAction(e -> descargarpdf());
        btndescargarexcel.setOnAction(e -> descargarpdf());
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
    void AggDoc(ActionEvent event) {


    }

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
    void EditDoc(ActionEvent event) {

    }

    @FXML
    void EliminarDoc(ActionEvent event) {

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

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(tablaMayor.getColumns().size());
            tablaPDF.setWidthPercentage(100);

            for (TableColumn<?, ?> col : tablaMayor.getColumns()) {
                PdfPCell celda = new PdfPCell(new Phrase(col.getText()));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }
            if (tablaMayor.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(tablaMayor.getColumns().size());
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
                tablaMayor.getItems().forEach(item -> {
                    for (TableColumn<?, ?> col : tablaMayor.getColumns()) {
                        Object valor = col.getCellData(Integer.parseInt(String.valueOf(item)));
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
    @FXML
    private void descargarexcel() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Libro Mayor Del Auditor en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Libro_Mayor.xlsx");

        Stage stage = (Stage) btndescargarexcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet hoja = workbook.createSheet("Libro Mayor");
            int filaIndex = 0;

            Row filaCabecera = hoja.createRow(filaIndex++);
            int colIndex = 0;
            for (TableColumn<?, ?> col : tablaMayor.getColumns()) {
                org.apache.poi.ss.usermodel.Cell cell = filaCabecera.createCell(colIndex++);
                cell.setCellValue(col.getText());
            }

            for (Object item : tablaMayor.getItems()) {
                Row fila = hoja.createRow(filaIndex++);
                colIndex = 0;
                for (TableColumn<?, ?> col : tablaMayor.getColumns()) {
                    Object valor = col.getCellObservableValue((Integer) item).getValue();
                    fila.createCell(colIndex++).setCellValue(valor == null ? "" : valor.toString());
                }
            }

            for (int i = 0; i < tablaMayor.getColumns().size(); i++) {
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
}


