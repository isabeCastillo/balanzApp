package com.example.balanzapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnIrLogin;

    @FXML
    private Hyperlink linkLogin;

    @FXML
    private void initialize() {
        // Puedes agregar lógica al cargar la vista
    }

    @FXML
    private void irALogin() {
        try {
            MainApp.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registrar() {
        String usuario = txtUsuario.getText();
        String correo = txtCorreo.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos.");
        } else {
            mostrarAlerta("Registro exitoso", "Tu cuenta ha sido creada correctamente.");
            irALogin(); // vuelve automáticamente al login
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