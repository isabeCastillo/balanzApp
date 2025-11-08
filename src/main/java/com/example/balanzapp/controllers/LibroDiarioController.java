package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.MainApp;
import com.example.balanzapp.dao.PartidaDAO;
import com.example.balanzapp.models.DetallePartidaTemp;
import com.example.balanzapp.models.Partida;
import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LibroDiarioController extends BaseController{
    @FXML private Button btninicio;
    @FXML private Button btndoc;
    @FXML private Button btnlibrodiario;
    @FXML private Button btnlibromayor;
    @FXML private ComboBox<String> cmbbalances;
    @FXML private Button btnestadoderesultados;
    @FXML private Button btncatalogo;
    @FXML private Button btnusuario;
    @FXML private Button btnbitacora;
    @FXML private Button btncerrar;

    // ====== ENCABEZADO PARTIDA ======
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtConcepto;
    @FXML private ComboBox<String> comboTipoPartida;
    @FXML private Label lblNumeroPartida;

    // ====== DETALLE PARTIDA (formulario de línea) ======
    @FXML private ComboBox<String> comboCuenta;
    @FXML private RadioButton radioDebe;
    @FXML private RadioButton radioHaber;
    @FXML private TextField txtMonto;
    @FXML private TextField txtDescripcionLinea;
    @FXML private Button btnSubirdoc;
    @FXML private Button btnagregar;
    @FXML private Button btneditar;
    @FXML private Button btneliminar;
    @FXML private Button btnGuardarPartida;
    @FXML private Button btnNuevaPartida;

    // ====== TABLA DETALLE ======
    @FXML private TableView<DetallePartidaTemp> tablaDetalle;
    @FXML private TableColumn<DetallePartidaTemp, String> colCuentaDetalle;
    @FXML private TableColumn<DetallePartidaTemp, String> colDescripcionDetalle;
    @FXML private TableColumn<DetallePartidaTemp, Double> colDebeDetalle;
    @FXML private TableColumn<DetallePartidaTemp, Double> colHaberDetalle;

    @FXML private Label lblTotalDebe;
    @FXML private Label lblTotalHaber;

    // ====== HISTORIAL LIBRO DIARIO ======
    @FXML private ComboBox<String> comboAnio;
    @FXML private ComboBox<String> comboMes; // si creas un DTO específico puedes tiparla mejor
    @FXML private Button btndescargarpdf;
    @FXML private Button btndescargarexcel;
    @FXML private Button btnbuscar;
    @FXML private TableView<Partida> tablaDiario;
    @FXML private TableColumn<Partida, LocalDate> colFecha;
    @FXML private TableColumn<Partida, Integer> colNumeroPartida;
    @FXML private TableColumn<Partida, String> colConcepto;
    @FXML private TableColumn<Partida, String> colCuenta;
    @FXML private TableColumn<Partida, Double> colDebe;
    @FXML private TableColumn<Partida, Double> colHaber;
    @FXML private TableColumn<Partida, String> colDocumento;

    // ====== DATA EN MEMORIA ======
    private ObservableList<DetallePartidaTemp> detalles = FXCollections.observableArrayList();
    private File documentoSeleccionado; // PDF evidencial (opcional)

    @FXML
    private void initialize(){
        cargarDatosUsuario();
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(e -> balanceSelec());
        comboMes.getItems().addAll(
                "1","2","3","4","5","6","7","8","9","10","11","12"
        );
        cargarAniosDesdeBD();
        // Seleccionar por defecto mes actual y último año si existen
        if (!comboMes.getItems().isEmpty()) {
            String mesActual = String.valueOf(LocalDate.now().getMonthValue());
            if (comboMes.getItems().contains(mesActual)) {
                comboMes.setValue(mesActual);
            } else {
                comboMes.getSelectionModel().selectFirst();
            }
        }
        if (!comboAnio.getItems().isEmpty()) {
            comboAnio.getSelectionModel().selectFirst();
        }
        comboTipoPartida.getItems().addAll("Regular", "Ajuste", "Apertura", "Cierre");
        cargarCuentasDesdeBD();
        ToggleGroup grupo = new ToggleGroup();
        radioDebe.setToggleGroup(grupo);
        radioHaber.setToggleGroup(grupo);
        configurarTablaDetalle();
        configurarTablaHistorial();
        btnagregar.setOnAction(e -> agregarLinea());
        btneditar.setOnAction(e -> editarLinea());
        btneliminar.setOnAction(e -> eliminarLinea());
        btnSubirdoc.setOnAction(this::seleccionarDocumento);
        btnGuardarPartida.setOnAction(e -> guardarPartida());
        btnNuevaPartida.setOnAction(e -> limpiarFormulario());
        btnbuscar.setOnAction(e -> cargarTablaHistorial());
        btndescargarpdf.setOnAction(e -> descargarpdf());
        btndescargarexcel.setOnAction(e -> descargarexcel());
        // fecha por defecto hoy
        dateFecha.setValue(LocalDate.now());
        cargarTablaHistorial();
        tablaDiario.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevaPartida) -> {
            if (nuevaPartida != null) {
                int idPartida = nuevaPartida.getIdPartida();
                var listaDetalles = PartidaDAO.obtenerDetallePorPartida(idPartida);
                detalles.setAll(listaDetalles);
                actualizarTotales();
            }
        });
        // si el usuario es auditor (nivel 3), deshabilitar edición
        Usuario u = sessionUsu.getUsuarioActivo();
        if (u != null && u.getRol().getNivel_acceso() == 3) {
            deshabilitarEdicion();
        }
    }

    private void configurarTablaHistorial() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colNumeroPartida.setCellValueFactory(new PropertyValueFactory<>("numeroPartida"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        colCuenta.setCellValueFactory(new PropertyValueFactory<>("cuenta"));
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
    }

    // ================== HISTORIAL ==================

    private void cargarTablaHistorial() {
        if (comboMes.getValue() == null || comboAnio.getValue() == null) {
            // no mostrar error aquí al iniciar, solo si el usuario da clic en buscar sin elegir
            return;
        }

        int mes = Integer.parseInt(comboMes.getValue());
        int anio = Integer.parseInt(comboAnio.getValue());

        var lista = PartidaDAO.obtenerPartidasPorMesYAnio(mes, anio);
        tablaDiario.getItems().setAll(lista);
    }

    private void deshabilitarEdicion() {
        txtConcepto.setDisable(true);
        comboTipoPartida.setDisable(true);
        comboCuenta.setDisable(true);
        radioDebe.setDisable(true);
        radioHaber.setDisable(true);
        txtMonto.setDisable(true);
        txtDescripcionLinea.setDisable(true);
        btnagregar.setDisable(true);
        btneditar.setDisable(true);
        btneliminar.setDisable(true);
        btnSubirdoc.setDisable(true);
        btnGuardarPartida.setDisable(true);
        btnNuevaPartida.setDisable(true);
    }

    private void configurarTablaDetalle() {
        colCuentaDetalle.setCellValueFactory(new PropertyValueFactory<>("cuenta"));
        colDescripcionDetalle.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDebeDetalle.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaberDetalle.setCellValueFactory(new PropertyValueFactory<>("haber"));

        tablaDetalle.setItems(detalles);
    }

    // ================== DETALLE EN MEMORIA ==================

    private void agregarLinea() {
        String cuentaStr = comboCuenta.getValue();
        if (cuentaStr == null || cuentaStr.isBlank()) {
            mostrarError("Selecciona una cuenta.");
            return;
        }

        if (!radioDebe.isSelected() && !radioHaber.isSelected()) {
            mostrarError("Selecciona si el monto va al Debe o al Haber.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText());
            if (monto <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("Monto inválido. Debe ser un número mayor que cero.");
            return;
        }

        int idCuenta = Integer.parseInt(cuentaStr.split(" - ")[0]);
        String nombreCuenta = cuentaStr.split(" - ")[1];
        String descLinea = txtDescripcionLinea.getText();

        double debe = radioDebe.isSelected() ? monto : 0.0;
        double haber = radioHaber.isSelected() ? monto : 0.0;

        DetallePartidaTemp det = new DetallePartidaTemp(idCuenta, nombreCuenta, descLinea, debe, haber);
        detalles.add(det);

        actualizarTotales();
        limpiarLinea();
    }

    private void editarLinea() {
        DetallePartidaTemp seleccionado = tablaDetalle.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona una línea para editar.");
            return;
        }

        String cuentaStr = comboCuenta.getValue();
        if (cuentaStr != null && !cuentaStr.isBlank()) {
            int idCuenta = Integer.parseInt(cuentaStr.split(" - ")[0]);
            String nombreCuenta = cuentaStr.split(" - ")[1];
            seleccionado.setIdCuenta(idCuenta);
            seleccionado.setNombreCuenta(nombreCuenta);
        }

        if (radioDebe.isSelected() || radioHaber.isSelected()) {
            double monto;
            try {
                monto = Double.parseDouble(txtMonto.getText());
                if (monto <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarError("Monto inválido para editar.");
                return;
            }
            if (radioDebe.isSelected()) {
                seleccionado.setDebe(monto);
                seleccionado.setHaber(0.0);
            } else {
                seleccionado.setHaber(monto);
                seleccionado.setDebe(0.0);
            }
        }

        seleccionado.setDescripcion(txtDescripcionLinea.getText());

        tablaDetalle.refresh();
        actualizarTotales();
        limpiarLinea();
    }

    private void eliminarLinea() {
        DetallePartidaTemp seleccionado = tablaDetalle.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona una línea para eliminar.");
            return;
        }
        detalles.remove(seleccionado);
        actualizarTotales();
    }

    private void actualizarTotales() {
        double totalDebe = detalles.stream().mapToDouble(DetallePartidaTemp::getDebe).sum();
        double totalHaber = detalles.stream().mapToDouble(DetallePartidaTemp::getHaber).sum();

        lblTotalDebe.setText(String.format("%.2f", totalDebe));
        lblTotalHaber.setText(String.format("%.2f", totalHaber));
    }

    private void limpiarLinea() {
        comboCuenta.getSelectionModel().clearSelection();
        radioDebe.setSelected(false);
        radioHaber.setSelected(false);
        txtMonto.clear();
        txtDescripcionLinea.clear();
    }

    private void limpiarFormulario() {
        dateFecha.setValue(LocalDate.now());
        txtConcepto.clear();
        comboTipoPartida.getSelectionModel().clearSelection();
        lblNumeroPartida.setText("--");
        detalles.clear();
        actualizarTotales();
        documentoSeleccionado = null;
        limpiarLinea();
    }


    // ================== GUARDAR PARTIDA EN BD ==================

    private void guardarPartida() {
        if (dateFecha.getValue() == null) {
            mostrarError("Selecciona la fecha de la partida.");
            return;
        }
        if (txtConcepto.getText().isBlank()) {
            mostrarError("Ingresa el concepto de la partida.");
            return;
        }
        if (comboTipoPartida.getValue() == null) {
            mostrarError("Selecciona el tipo de partida.");
            return;
        }
        if (detalles.size() < 2) {
            mostrarError("Una partida debe tener al menos dos líneas (Debe y Haber).");
            return;
        }

        double totalDebe = detalles.stream().mapToDouble(DetallePartidaTemp::getDebe).sum();
        double totalHaber = detalles.stream().mapToDouble(DetallePartidaTemp::getHaber).sum();

        if (totalDebe <= 0 || totalHaber <= 0) {
            mostrarError("Debe existir al menos un Debe y un Haber mayor que cero.");
            return;
        }

        if (Math.abs(totalDebe - totalHaber) > 0.001) {
            mostrarError("La partida no cuadra. El total del Debe y el Haber deben ser iguales.");
            return;
        }

        Usuario usuario = sessionUsu.getUsuarioActivo();
        if (usuario == null) {
            mostrarError("No hay usuario en sesión.");
            return;
        }

        try {
            PartidaDAO.insertarPartidaConDetalles(
                    dateFecha.getValue(),
                    txtConcepto.getText(),
                    comboTipoPartida.getValue(),
                    usuario.getId_usuario(),
                    detalles,
                    documentoSeleccionado != null ? documentoSeleccionado.getAbsolutePath() : null
            );

            Alerta("Éxito", "Partida registrada correctamente.");
            limpiarFormulario();
            cargarTablaHistorial();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarError("Error al guardar la partida: " + e.getMessage());
        }
    }

    // ================== DOCUMENTO FUENTE ==================

    private void seleccionarDocumento(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento (PDF)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            documentoSeleccionado = archivo;
            Alerta("Documento seleccionado", archivo.getName());
        }
    }

    // ================== CARGAS AUXILIARES ==================

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
        String sql = "SELECT id_cuenta, nombre FROM tbl_cntaContables ORDER BY codigo";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboCuenta.getItems().add(rs.getInt(1) + " - " + rs.getString(2));
            }
        } catch (SQLException e) { System.out.println(e); }
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

    private void cargarTabla() {
        int mes = Integer.parseInt(comboMes.getValue());
        int anio = Integer.parseInt(comboAnio.getValue());

        tablaDiario.getItems().setAll(PartidaDAO.obtenerPartidasPorMesYAnio(mes, anio));
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

    @FXML
    private void agregarPartida() {
        String cuentaStr = comboCuenta.getValue();
        int idCuenta = Integer.parseInt(cuentaStr.split(" - ")[0]);

        boolean esDebe = radioDebe.isSelected();
        double monto = Double.parseDouble(txtMonto.getText());

        PartidaDAO.insertarPartida(dateFecha.getValue(), txtConcepto.getText(), idCuenta, esDebe, monto, 2); // id_usuario = 2 (Cambiar dinámico)
        cargarTabla();
    }

    @FXML
    private void descargarexcel() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Libro Diario en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Libro_Diario.xlsx");

        Stage stage = (Stage) btndescargarexcel.getScene().getWindow();
        java.io.File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet hoja = workbook.createSheet("Libro Diario");
            int filaIndex = 0;

            Row filaCabecera = hoja.createRow(filaIndex++);
            int colIndex = 0;
            for (TableColumn<?, ?> col : tablaDiario.getColumns()) {
                org.apache.poi.ss.usermodel.Cell cell = filaCabecera.createCell(colIndex++);
                cell.setCellValue(col.getText());
            }

            for (Object item : tablaDiario.getItems()) {
                Row fila = hoja.createRow(filaIndex++);
                colIndex = 0;
                for (TableColumn<?, ?> col : tablaDiario.getColumns()) {
                    Object valor = col.getCellObservableValue((Integer) item).getValue();
                    fila.createCell(colIndex++).setCellValue(valor == null ? "" : valor.toString());
                }
            }

            for (int i = 0; i < tablaDiario.getColumns().size(); i++) {
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