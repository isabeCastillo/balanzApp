package com.example.balanzapp.controllers;

import com.example.balanzapp.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class InicioController {
    @FXML
    private Button btnbalances;

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
    private Label lblUs;

    @FXML
    private Label lblad;

    @FXML
    private void initialize(){

    }

    public void goToHome(ActionEvent actionEvent) throws IOException {
        try {
            MainApp.setRoot("inicio");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void goToDoc(ActionEvent actionEvent) throws IOException {
        try {
            MainApp.setRoot("documentos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToLibroDiario(ActionEvent actionEvent) {
        try {
            MainApp.setRoot("libroDiario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToLibroMayor(ActionEvent actionEvent) {
        try {
            MainApp.setRoot("libroMayor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToBlances(ActionEvent actionEvent) {
    }

    public void goToEstadoResultados(ActionEvent actionEvent) {
    }

    public void goToCatalogoCuentas(ActionEvent actionEvent) {
    }

    public void goToUsuario(ActionEvent actionEvent) {
    }

    public void goToBitacoraAuditor(ActionEvent actionEvent) {
    }

    public void Close(ActionEvent actionEvent) {

    }
}
