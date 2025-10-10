package com.example.balanzapp.controllers;

import com.example.balanzapp.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class DocumentosController {
    @FXML
    private Button btnagregar;

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
    private void initialize(){

    }

    @FXML
    void AggDoc(ActionEvent event) {


    }

    @FXML
    void Close(ActionEvent event) {

    }

    @FXML
    void EditDoc(ActionEvent event) {

    }

    @FXML
    void EliminarDoc(ActionEvent event) {

    }

    @FXML
    void goToBitacoraAuditor(ActionEvent event) {

    }

    @FXML
    void goToBlances(ActionEvent event) {

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

    }

    @FXML
    void goToLibroMayor(ActionEvent event) {

    }

    @FXML
    void goToUsuario(ActionEvent event) {

    }

}
