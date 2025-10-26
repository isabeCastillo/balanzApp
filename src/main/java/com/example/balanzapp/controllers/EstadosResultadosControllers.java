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
    private TableView<?> tblResultados;

    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cmbPeriodo.getItems().addAll("Mensual", "Trimestral", "Anual");
        cmbAnio.getItems().addAll("2023", "2024", "2025");
        cmbMes.getItems().addAll("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
        cmbbalances.getItems().addAll("Balance de comprobación de saldos", "Balance general");
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
}
