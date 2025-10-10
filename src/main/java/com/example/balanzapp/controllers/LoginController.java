package com.example.balanzapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.example.balanzapp.MainApp;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.equals("admin") && contrasena.equals("1234")) {
            try {
                MainApp.setRoot("inicio"); // Cambia a la vista de inicio
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("No se pudo cargar la vista de inicio.");
            }
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void irARegistro() {
        try {
            MainApp.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista de registro.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText("Error");
        alerta.setContentText(mensaje);
        alerta.show();
    }
}