package com.example.balanzapp.controllers;

import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class BaseController {
    @FXML
    protected Label lblUs;
    @FXML
    protected Label lblad;

    @FXML
    protected Button btnusuario;
    @FXML
    protected Button btnbitacora;
    @FXML
    protected Button btndoc;
    @FXML
    protected Button btnlibrodiario;
    @FXML
    protected Button btnlibromayor;
    @FXML
    protected Button btncatalogo;

    public void cargarDatosUsuario() {
        Usuario usuario = sessionUsu.getUsuarioActivo();

        if (usuario == null) return;

        lblUs.setText(usuario.getNombre());
        lblad.setText(usuario.getRol().getNombre_rol());

        int nivel = usuario.getRol().getNivel_acceso();

        // Permisos
        btnusuario.setVisible(nivel == 1);
        btnbitacora.setVisible(nivel == 1 || nivel == 3);
        btndoc.setVisible(nivel <= 2);
        btnlibrodiario.setVisible(nivel <= 2);
        btnlibromayor.setVisible(nivel <= 2);
        btncatalogo.setVisible(nivel <= 2);
    }
}