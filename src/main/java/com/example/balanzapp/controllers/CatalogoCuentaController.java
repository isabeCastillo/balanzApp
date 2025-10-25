package com.example.balanzapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class CatalogoCuentaController {

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

    // Campos del formulario principal
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> cmbCuenta;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TableView<?> tblCatalogo;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setUsuario(String usuario, String rol) {
        lblUs.setText(usuario);
        lblad.setText(rol);
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

            // Pasar usuario/rol a la siguiente vista (si tiene los labels)
            Object controller = loader.getController();
            if (controller instanceof CatalogoCuentaController nextController) {
                nextController.setUsuario(lblUs.getText(), lblad.getText());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cmbCuenta.getItems().addAll("Activo", "Pasivo", "Capital", "Ingresos", "Gastos");
        cmbTipo.getItems().addAll("Corriente", "No corriente");
        cmbbalances.getItems().addAll("Balance de comprobación de saldos", "Balance general");
    }
}
