package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.MainApp;
import com.example.balanzapp.models.Rol;
import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;

    @FXML
    private void iniciarSesion() {
        String usuarioTxt = txtUsuario.getText().trim();
        String contrasenaTxt = txtContrasena.getText().trim();

        if (usuarioTxt.isEmpty() || contrasenaTxt.isEmpty()) {
            mostrarError("Por favor ingresa usuario y contraseña.");
            return;
        }

        Usuario usuarioLogueado = autenticarUsuario(usuarioTxt, contrasenaTxt);

        if (usuarioLogueado != null) {
            try {
                // Cargar la vista de inicio
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/inicio.fxml"));
                Parent root = loader.load();

                // Pasar los datos del usuario al InicioController
                sessionUsu.setUsuarioActivo(usuarioLogueado);


                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("No se pudo cargar la vista de inicio.");
            }
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }

    private Usuario autenticarUsuario(String usuarioTxt, String contrasenaTxt) {
        Usuario usuario = null;
        String sql = """
                     SELECT u.*, r.id_rol, r.nombre_rol, r.nivel_acceso
                     FROM tbl_usuarios u
                     JOIN tbl_roles r ON u.id_rol = r.id_rol
                     WHERE u.usuario = ? AND u.contraseña = ?
                     """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioTxt);
            ps.setString(2, contrasenaTxt);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId_usuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setCorreo(rs.getString("correo"));

                Rol rol = new Rol();
                rol.setId_rol(rs.getInt("id_rol"));
                rol.setNombre_rol(rs.getString("nombre_rol"));
                rol.setNivel_acceso(rs.getInt("nivel_acceso"));
                usuario.setRol(rol);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    @FXML
    private void irARegistro(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/register.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista de registro.");
        }

    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}