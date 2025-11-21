package com.example.balanzapp.controllers;

import com.example.balanzapp.models.Partida;
import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;

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
    @FXML
    protected TitledPane partidaactual;
    @FXML
    protected TitledPane historial;

    public void cargarDatosUsuario() {
        Usuario usuario = sessionUsu.getUsuarioActivo();
        if (usuario == null) return;

        if (lblUs != null) {
            lblUs.setText(usuario.getNombre());
        }
        if (lblad != null && usuario.getRol() != null) {
            lblad.setText(usuario.getRol().getNombre_rol());
        }

        int nivel = usuario.getRol() != null ? usuario.getRol().getNivel_acceso() : 3;

        if (btnusuario != null)   btnusuario.setVisible(nivel == 1);
        if (btnbitacora != null)  btnbitacora.setVisible(nivel == 1 || nivel == 3);
        if (btndoc != null)       btndoc.setVisible(nivel <= 2);
        if (btnlibrodiario != null) btnlibrodiario.setVisible(nivel <= 3);
        if (btnlibromayor != null)  btnlibromayor.setVisible(nivel <= 3);
        if (btncatalogo != null)    btncatalogo.setVisible(nivel <= 3);
        if (partidaactual != null) {
            partidaactual.setExpanded(false);
            historial.setExpanded(true);
            partidaactual.setDisable(nivel <= 3);
        }
    }

}