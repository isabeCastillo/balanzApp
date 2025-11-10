package com.example.balanzapp.controllers;

import com.example.balanzapp.dao.DocumentoDAO;
import com.example.balanzapp.models.DocumentoTabla;
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
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class DocumentosController extends BaseController {

    @FXML private Button btnagregar;
    @FXML private ComboBox<String> cmbbalances;
    @FXML private Button btnbitacora;
    @FXML private Button btncatalogo;
    @FXML private Button btncerrar;
    @FXML private Button btndoc;
    @FXML private Button btneditar;
    @FXML private Button btneliminar;
    @FXML private Button btnestadoderesultados;
    @FXML private Button btninicio;
    @FXML private Button btnlibrodiario;
    @FXML private Button btnlibromayor;
    @FXML private Button btnusuario;
    @FXML private ComboBox<?> cmbElegirDoc;
    @FXML private Label lblUs;
    @FXML private Label lblad;
    @FXML private TextField txtNombreDocumento;

    // Tabla de documentos
    @FXML private TableView<DocumentoTabla> tablaDocumentos;
    @FXML private TableColumn<DocumentoTabla, String> colNombreArchivo;
    @FXML private TableColumn<DocumentoTabla, String> colClasificacion;
    @FXML private TableColumn<DocumentoTabla, String> colFecha;
    @FXML private TableColumn<DocumentoTabla, String> colUsuario;
    @FXML private TableColumn<DocumentoTabla, Void>   colVer;

    private ObservableList<DocumentoTabla> documentos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cargarDatosUsuario();

        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());
        configurarTabla();
        cargarDocumentos();
    }

    private void configurarTabla() {
        colNombreArchivo.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colClasificacion.setCellValueFactory(new PropertyValueFactory<>("clasificacion"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        // Columna con botón "Ver"
        colVer.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Ver");

            {
                btn.setOnAction(e -> {
                    DocumentoTabla doc = getTableView().getItems().get(getIndex());
                    abrirDocumento(doc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tablaDocumentos.setItems(documentos);

        // Doble clic en la fila también abre el documento
        tablaDocumentos.setRowFactory(tv -> {
            TableRow<DocumentoTabla> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    abrirDocumento(row.getItem());
                }
            });
            return row;
        });
    }

    private void cargarDocumentos() {
        documentos.setAll(DocumentoDAO.obtenerTodosLosDocumentos());
    }

    private void abrirDocumento(DocumentoTabla doc) {
        if (doc.getRutaArchivo() == null || doc.getRutaArchivo().isBlank()) {
            mostrarError("El documento no tiene una ruta registrada.");
            return;
        }

        File archivo = new File(doc.getRutaArchivo());
        if (!archivo.exists()) {
            mostrarError("El archivo no existe en la ruta:\n" + doc.getRutaArchivo());
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            } else {
                mostrarError("La apertura de archivos no es soportada en este sistema.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el documento: " + e.getMessage());
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

    // ==== Navegación ====

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

    @FXML void goToBitacoraAuditor(ActionEvent e) { cambiarVista("/views/bitacora.fxml", e); }
    @FXML void goToCatalogoCuentas(ActionEvent e) { cambiarVista("/views/catalogo_cuenta.fxml", e); }
    @FXML void goToDoc(ActionEvent e) { cambiarVista("/views/documentos.fxml", e); }
    @FXML void goToEstadoResultados(ActionEvent e) { cambiarVista("/views/estadosResultados.fxml", e); }
    @FXML void goToHome(ActionEvent e) { cambiarVista("/views/inicio.fxml", e); }
    @FXML void goToLibroDiario(ActionEvent e) { cambiarVista("/views/libroDiario.fxml", e); }
    @FXML void goToLibroMayor(ActionEvent e) { cambiarVista("/views/libroMayor.fxml", e); }
    @FXML void goToUsuario(ActionEvent e) { cambiarVista("/views/usuarios.fxml", e); }

    private void cambiarVista(String fxml, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==== Alertas ====

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
