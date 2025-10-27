package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.MainApp;
import com.example.balanzapp.dao.PartidaDAO;
import com.example.balanzapp.models.Partida;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LibroDiarioController extends BaseController{

    @FXML
    private Button btnbitacora;

    @FXML
    private Button btncatalogo;

    @FXML
    private Button btncerrar;

    @FXML
    private Button btndoc;

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
    private ComboBox<String> cmbbalances;

    @FXML
    private ComboBox<String> comboAnio;

    @FXML
    private ComboBox<String> comboCuenta;

    @FXML
    private ComboBox<String> comboMes;

    @FXML
    private DatePicker dateFecha;

    @FXML
    private Label lblUs;

    @FXML
    private Label lblad;

    @FXML
    private RadioButton radioDebe;

    @FXML
    private RadioButton radioHaber;

    @FXML
    private TableView<Partida> tablaDiario;

    @FXML
    private TextField txtConcepto;

    @FXML
    private TextField txtMonto;

    @FXML
    private Button btndescargarpdf;


    @FXML
    private void initialize(){
        cargarDatosUsuario();
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());
        btndescargarpdf.setOnAction(e -> descargarpdf());
        comboMes.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12");
        cargarAniosDesdeBD();
        cargarCuentasDesdeBD();
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
        fileChooser.setTitle("Guardar Libro Diario como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Libro_Diario.pdf");

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
            Paragraph titulo = new Paragraph("LIBRO DIARIO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph fechaParrafo = new Paragraph("Generado el: " + fecha, fontFecha);
            fechaParrafo.setAlignment(Element.ALIGN_CENTER);
            fechaParrafo.setSpacingAfter(20);

            documento.add(titulo);
            documento.add(fechaParrafo);

            PdfPTable tablaPDF = new PdfPTable(tablaDiario.getColumns().size());
            tablaPDF.setWidthPercentage(100);

            for (TableColumn<?, ?> col : tablaDiario.getColumns()) {
                PdfPCell celda = new PdfPCell(new Phrase(col.getText()));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celda);
            }
            if (tablaDiario.getItems().isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("Tabla sin contenido"));
                celdaVacia.setColspan(tablaDiario.getColumns().size());
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPDF.addCell(celdaVacia);
            } else {
                tablaDiario.getItems().forEach(item -> {
                    for (TableColumn<?, ?> col : tablaDiario.getColumns()) {
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

    private void cargarAniosDesdeBD() {
        String sql = "SELECT DISTINCT EXTRACT(YEAR FROM fecha) FROM tbl_partidas ORDER BY 1 DESC";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboAnio.getItems().add(String.valueOf((int) rs.getDouble(1)));
            }
        } catch (SQLException e) { System.out.println(e); }
    }

    private void cargarCuentasDesdeBD() {
        String sql = "SELECT id_cuenta, nombre FROM tbl_cntaContables";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboCuenta.getItems().add(rs.getInt(1) + " - " + rs.getString(2));
            }
        } catch (SQLException e) { System.out.println(e); }
    }

    @FXML
    private void agregarPartida() {
        String cuentaStr = comboCuenta.getValue();
        int idCuenta = Integer.parseInt(cuentaStr.split(" - ")[0]);

        boolean esDebe = radioDebe.isSelected();
        double monto = Double.parseDouble(txtMonto.getText());

        PartidaDAO.insertarPartida(dateFecha.getValue(), txtConcepto.getText(), idCuenta, esDebe, monto, 2); // id_usuario = 2 (Cambiar dinámico)
        cargarTabla();
    }

    private void cargarTabla() {
        int mes = Integer.parseInt(comboMes.getValue());
        int anio = Integer.parseInt(comboAnio.getValue());

        tablaDiario.getItems().setAll(PartidaDAO.obtenerPartidasPorMesYAnio(mes, anio));
    }
}