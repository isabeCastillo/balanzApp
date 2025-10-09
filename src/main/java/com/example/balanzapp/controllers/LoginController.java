package com.example.balanzapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Hyperlink linkRegistro;

    @FXML
    private Button btnIrRegistro;

    @FXML
    private Button btnIngresar;

    @FXML
    private void initialize() {
        // Puedes agregar lógica de inicialización aquí
    }

    @FXML
    private void irARegistro() {
        try {
            MainApp.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ingresar() {
        // Aquí puedes validar usuario/contraseña
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Debes llenar todos los campos.");
        } else {
            mostrarAlerta("Bienvenido", "Inicio de sesión exitoso.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}