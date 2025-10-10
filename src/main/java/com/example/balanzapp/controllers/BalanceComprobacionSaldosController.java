package com.example.balanzapp.controllers;

import com.example.balanzapp.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class BalanceComprobacionSaldosController {

    @FXML
    private Button btnDecargarPdf;

    @FXML
    private Button btnDescargarExcel;

    @FXML
    private Button btnbitacora;

    @FXML
    private Button btnbuscar;

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
    private ComboBox<?> cmbAño;

    @FXML
    private ComboBox<?> cmbPeriodo;

    @FXML
    private ComboBox<String> cmbbalances;

    @FXML
    private Label lblUs;

    @FXML
    private Label lblad;

    @FXML
    private TableView<?> tblComprobacionSaldos;

    @FXML
    private void initialize(){
        cmbbalances.getItems().addAll(
                "Balance de comprobación de saldos",
                "Balance general"
        );
        cmbbalances.setOnAction(event -> balanceSelec());

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
    void Buscar(ActionEvent event) {

    }

    @FXML
    void Close(ActionEvent event) {

    }

    @FXML
    void DecargarPdf(ActionEvent event) {

    }

    @FXML
    void DescargarExcel(ActionEvent event) {

    }

    @FXML
    void goToBitacoraAuditor(ActionEvent event) {

    }

    @FXML
    void goToCatalogoCuentas(ActionEvent event) {

    }

    @FXML
    void goToDoc(ActionEvent event) {
        try {
            MainApp.setRoot("documentos");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void goToEstadoResultados(ActionEvent event) {

    }

    @FXML
    void goToHome(ActionEvent event) {

        try {
            MainApp.setRoot("inicio");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToLibroDiario(ActionEvent event) {
        try {
            MainApp.setRoot("libroDiario");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void goToLibroMayor(ActionEvent event) {
        try {
            MainApp.setRoot("libroMayor");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void goToUsuario(ActionEvent event) {

    }

}
