package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.dao.CatalogoDAO;
import com.example.balanzapp.models.Cuenta;
import com.example.balanzapp.service.AuditoriaService;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CatalogoCuentaController extends BaseController{

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
    private Button btndescargarExcel;

    @FXML
    private Button btndescargarPdf;

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtCuenta;

    @FXML
    private TextField txtTipo;

    @FXML
    private TextField txtGrupo;

    @FXML
    private TextField txtNaturaleza;
    @FXML
    private TableView<Cuenta> tblCatalogo;

    @FXML private TableColumn<Cuenta, String> colCodigo;
    @FXML private TableColumn<Cuenta, String> colNombre;
    @FXML private TableColumn<Cuenta, String> colTipo;
    @FXML private TableColumn<Cuenta, String> colNaturaleza;
    @FXML private TableColumn<Cuenta, String> colGrupo;


    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cargarTabla();
        cmbbalances.getItems().addAll("Balance de comprobación de saldos", "Balance general");
        cmbbalances.setOnAction(event -> balanceSelec());

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colNaturaleza.setCellValueFactory(new PropertyValueFactory<>("naturaleza"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("grupo"));

        btndescargarPdf.setOnAction(e -> descargarpdf());
        btndescargarExcel.setOnAction(e -> descargarexcel());

        tblCatalogo.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtCodigo.setText(newSel.getCodigo());
                txtCuenta.setText(newSel.getNombre());
                txtTipo.setText(newSel.getTipo());
                txtNaturaleza.setText(newSel.getNaturaleza());
                txtGrupo.setText(newSel.getGrupo());
            }
        });
    }
    @FXML
    void Close(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToBitacoraAuditor(ActionEvent actionEvent) { cambiarVista("/views/bitacora.fxml", actionEvent); }

    @FXML
    void goToCatalogoCuentas(ActionEvent actionEvent) { cambiarVista("/views/catalogo_cuenta.fxml", actionEvent); }

    @FXML
    void goToDoc(ActionEvent actionEvent) { cambiarVista("/views/documentos.fxml", actionEvent); }

    @FXML
    void goToEstadoResultados(ActionEvent actionEvent) { cambiarVista("/views/estadosResultados.fxml", actionEvent); }

    @FXML
    void goToHome(ActionEvent actionEvent) { cambiarVista("/views/inicio.fxml", actionEvent); }

    @FXML
    void goToLibroDiario(ActionEvent actionEvent) { cambiarVista("/views/libroDiario.fxml", actionEvent); }

    @FXML
    void goToLibroMayor(ActionEvent actionEvent) { cambiarVista("/views/libroMayor.fxml", actionEvent); }

    @FXML
    void goToUsuario(ActionEvent actionEvent) { cambiarVista("/views/usuarios.fxml", actionEvent); }


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
    private void agregarCuenta(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtCuenta.getText();
        String tipo = txtTipo.getText();
        String naturaleza = txtNaturaleza.getText();
        String grupo = txtGrupo.getText();

        if (codigo.isEmpty() || nombre.isEmpty() || tipo.isEmpty() || grupo.isEmpty()) {
            mostrarAlerta("Complete todos los campos.");
            return;
        }

        Cuenta cuenta = new Cuenta(codigo, nombre, tipo,naturaleza, grupo);

        if (!CatalogoDAO.insertarCuenta(cuenta)) {
            mostrarAlerta("existe una cuenta con este código.");
            return;
        }

        cargarTabla();
        limpiarCampos();
        AuditoriaService.registrarAccion(
                "Catálogo de Cuentas",
                "Registró una cuenta contable",
                "Código: " + txtCodigo.getText()
                        + " | Nombre: " + txtCuenta.getText()
                        + " | Tipo: " + txtTipo.getText()
                        + " | Naturaleza: " + txtNaturaleza.getText()
        );

    }
    private void cargarTabla() {
        var cuentas = CatalogoDAO.obtenerCuentas();
        System.out.println("Cuentas cargadas: " + cuentas.size());
        cuentas.forEach(c -> System.out.println(c.getCodigo() + " - " + c.getNombre()));
        tblCatalogo.getItems().setAll(cuentas);
    }

    @FXML
    private void eliminarCuenta() {
        Cuenta seleccionada = tblCatalogo.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        CatalogoDAO.eliminarCuenta(seleccionada.getIdCuenta());
        cargarTabla();
        AuditoriaService.registrarAccion(
                "Catálogo de Cuentas",
                "Eliminó una cuenta contable",
                "Código: " + txtCodigo.getText()
                        + " | Nombre: " + txtCuenta.getText()
                        + " | Tipo: " + txtTipo.getText()
                        + " | Naturaleza: " + txtNaturaleza.getText()
        );
    }
    @FXML
    private void editarCuenta(ActionEvent event) {
        Cuenta seleccionada = tblCatalogo.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Seleccione una cuenta de la tabla.");
            return;
        }
        seleccionada.setCodigo(txtCodigo.getText());
        seleccionada.setNombre(txtCuenta.getText());
        seleccionada.setTipo(txtTipo.getText());
        seleccionada.setNaturaleza(txtNaturaleza.getText());
        seleccionada.setGrupo(txtGrupo.getText());

        if (CatalogoDAO.actualizarCuenta(seleccionada)) {
            cargarTabla();
            limpiarCampos();
            AuditoriaService.registrarAccion(
                    "Catálogo de Cuentas",
                    "Modificó una cuenta contable",
                    "Código: " + txtCodigo.getText()
                            + " | Nombre: " + txtCuenta.getText()
                            + " | Tipo: " + txtTipo.getText()
                            + " | Naturaleza: " + txtNaturaleza.getText()
            );

        } else {
            mostrarAlerta("No se pudo actualizar la cuenta.");
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtCuenta.clear();
        txtGrupo.clear();
        txtTipo.clear();
        txtNaturaleza.clear();
    }

    private void mostrarAlerta(String mensaje){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }
    @FXML
    private void descargarpdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Catalogo de cuentas como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Catalogo_Cuentas.pdf");

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
            Paragraph titulo = new Paragraph("CATALOGO_CUENTA", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fecha, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(20);

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(tblCatalogo.getColumns().size());
            tablaPDF.setWidthPercentage(100);

            for (TableColumn<?, ?> col : tblCatalogo.getColumns()) {
                PdfPCell celda = new PdfPCell(new Phrase(col.getText()));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }
            if (tblCatalogo.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(tblCatalogo.getColumns().size());
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
                tblCatalogo.getItems().forEach(item -> {
                    for (TableColumn<?, ?> col : tblCatalogo.getColumns()) {
                        Object valor = col.getCellData(item.getIdCuenta());
                        tablaPDF.addCell(valor == null ? "" : valor.toString());
                    }
                });
            }

            documento.add(tablaPDF);
            documento.close();
            // ===== AUDITORÍA: descarga de catálogo de cuentas en PDF =====
            int totalCuentas = tblCatalogo.getItems() != null ? tblCatalogo.getItems().size() : 0;

            AuditoriaService.registrarAccion(
                    "Catálogo de Cuentas",
                    "Descargó el Catálogo de Cuentas en PDF",
                    "Total de cuentas en el reporte: " + totalCuentas
            );


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
        fileChooser.setTitle("Guardar Catalogo de Cuentas en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Catalogo_de_Cuentas.xlsx");

        Stage stage = (Stage) btndescargarExcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet hoja = workbook.createSheet("Catalogo De Cuentas");
            int filaIndex = 0;

            Row filaCabecera = hoja.createRow(filaIndex++);
            int colIndex = 0;
            for (TableColumn<?, ?> col : tblCatalogo.getColumns()) {
                org.apache.poi.ss.usermodel.Cell cell = filaCabecera.createCell(colIndex++);
                cell.setCellValue(col.getText());
            }

            for (Object item : tblCatalogo.getItems()) {
                Row fila = hoja.createRow(filaIndex++);
                colIndex = 0;
                for (TableColumn<?, ?> col : tblCatalogo.getColumns()) {
                    Object valor = col.getCellObservableValue((Integer) item).getValue();
                    fila.createCell(colIndex++).setCellValue(valor == null ? "" : valor.toString());
                }
            }

            for (int i = 0; i < tblCatalogo.getColumns().size(); i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                workbook.write(fileOut);
            }
            int totalCuentas = tblCatalogo.getItems() != null ? tblCatalogo.getItems().size() : 0;

            AuditoriaService.registrarAccion(
                    "Catálogo de Cuentas",
                    "Descargó el Catálogo de Cuentas en Excel",
                    "Total de cuentas en el reporte: " + totalCuentas
            );

            Alerta("Éxito","El archivo Excel se generó correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al generar Excel: " + e.getMessage());
        }
    }
}