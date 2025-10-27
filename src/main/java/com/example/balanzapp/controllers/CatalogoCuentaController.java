package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.dao.CatalogoDAO;
import com.example.balanzapp.models.Cuenta;
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

    // Campos del formulario principal
    @FXML
    private TextField txtCodigo;

    @FXML
    private ComboBox<String> cmbCuenta;

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private TableView<Cuenta> tblCatalogo;

    @FXML private TableColumn<Cuenta, String> colCodigo;
    @FXML private TableColumn<Cuenta, String> colNombre;
    @FXML private TableColumn<Cuenta, String> colTipo;
    @FXML private TableColumn<Cuenta, String> colGrupo;


    private Stage stage;
    private Scene scene;
    private Parent root;

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

    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cargarCuentasDesdeBD();
        cargarTiposDesdeBD();
        cargarTabla();
        cmbbalances.getItems().addAll("Balance de comprobación de saldos", "Balance general");
        cmbbalances.setOnAction(event -> balanceSelec());
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("grupo"));
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

    private void cargarCuentasDesdeBD() {
        String sql = "SELECT id_cuenta, nombre FROM tbl_cntaContables ORDER BY codigo ASC";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Guarda ID y nombre juntos para usarlos en Libro Diario
                cmbCuenta.getItems().add(rs.getInt("id_cuenta") + " - " + rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar cuentas: " + e.getMessage());
        }
    }
    private void cargarTiposDesdeBD() {
        String sql = "SELECT DISTINCT tipo FROM tbl_cntaContables ORDER BY tipo ASC";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cmbTipo.getItems().add(rs.getString("tipo"));
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar tipos: " + e.getMessage());
        }
    }
    @FXML
    private void agregarCuenta(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombreDocumento.getText();
        String tipo = cmbCuenta.getValue();
        String grupo = cmbTipo.getValue();

        if (codigo.isEmpty() || nombre.isEmpty() || tipo == null || grupo == null) {
            mostrarAlerta("Complete todos los campos.");
            return;
        }

        Cuenta cuenta = new Cuenta(codigo, nombre, tipo, grupo);

        if (!CatalogoDAO.insertarCuenta(cuenta)) {
            mostrarAlerta("existe una cuenta con este código.");
            return;
        }

        cargarTabla();
        limpiarCampos();
    }
    private void cargarTabla() {
        tblCatalogo.getItems().setAll(CatalogoDAO.obtenerCuentas());
    }
    @FXML
    private void eliminarCuenta() {
        Cuenta seleccionada = tblCatalogo.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        CatalogoDAO.eliminarCuenta(seleccionada.getIdCuenta());
        cargarTabla();
    }
    @FXML
    private void editarCuenta(ActionEvent event) {
        Cuenta seleccionada = tblCatalogo.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Seleccione una cuenta de la tabla.");
            return;
        }

        seleccionada.setNombre(txtNombreDocumento.getText());
        seleccionada.setTipo(cmbCuenta.getValue());
        seleccionada.setGrupo(cmbTipo.getValue());

        if (CatalogoDAO.actualizarCuenta(seleccionada)) {
            cargarTabla();
            limpiarCampos();
        } else {
            mostrarAlerta("No se pudo actualizar la cuenta.");
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombreDocumento.clear();
        cmbCuenta.setValue(null);
        cmbTipo.setValue(null);
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
        fileChooser.setTitle("Guardar Catalogo de Cuentas como PDF");
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
            Paragraph titulo = new Paragraph("CATALOGO DE CUENTAS", fontTitulo);
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

}