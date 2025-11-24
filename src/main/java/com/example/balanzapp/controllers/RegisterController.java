package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtDui;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnLogin;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPasswordCheckBox;

    @FXML
    private void initialize() {
        cmbGenero.getItems().addAll("Masculino", "Femenino", "Otro");
        passwordVisibleField.textProperty().bindBidirectional(txtContrasena.textProperty());
    }

    @FXML
    private void irALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrar("Error", "No se pudo cargar el login.");
        }
    }

    @FXML
    private void registrar() {

        if (validarCampos() == false) return;

        try (Connection conn = ConexionDB.connection()) {

            String sql = """
                INSERT INTO tbl_usuarios
                (nombre, genero, fecha_nacimiento, dui, telefono, direccion, correo, usuario, contraseña, id_rol)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtNombre.getText());
            ps.setString(2, cmbGenero.getValue());
            ps.setDate(3, java.sql.Date.valueOf(dateNacimiento.getValue()));
            ps.setString(4, txtDui.getText());
            ps.setString(5, txtTelefono.getText());
            ps.setString(6, txtDireccion.getText());
            ps.setString(7, txtCorreo.getText());
            ps.setString(8, txtUsuario.getText());
            ps.setString(9, txtContrasena.getText());
            ps.setInt(10, 3); // Rol por defecto = 3 → Usuario normal

            ps.executeUpdate();

            mostrar("Registro exitoso", "Tu cuenta ha sido creada.");
            irALogin();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error", "No se pudo registrar el usuario.");
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() ||
                cmbGenero.getValue() == null ||
                dateNacimiento.getValue() == null ||
                txtDui.getText().isEmpty() ||
                txtTelefono.getText().isEmpty() ||
                txtDireccion.getText().isEmpty() ||
                txtCorreo.getText().isEmpty() ||
                txtUsuario.getText().isEmpty() ||
                txtContrasena.getText().isEmpty()) {

            mostrar("Campos incompletos", "Por favor, llena todos los campos.");
            return false;
        }

        return true;
    }

    private void mostrar(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void handleShowPassword(ActionEvent event) {
        if (showPasswordCheckBox.isSelected()) {
            passwordVisibleField.setVisible(true);
            txtContrasena.setVisible(false);
        } else {
            passwordVisibleField.setVisible(false);
            txtContrasena.setVisible(true);
        }
        passwordVisibleField.requestFocus();
        passwordVisibleField.positionCaret(txtContrasena.getCaretPosition());
    }

    public String getPasswordValue() {
        return txtContrasena.getText();
    }
}