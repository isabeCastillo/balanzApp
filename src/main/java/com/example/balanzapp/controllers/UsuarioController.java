package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.dao.UsuarioDAO;
import com.example.balanzapp.models.Rol;
import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;           // 👈 importante para convertir a LocalDate

public class UsuarioController extends BaseController {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtDUI;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCorreo;

    @FXML private TextField txtNombreUsuario;
    @FXML private PasswordField txtContraseña;
    @FXML private ComboBox<Rol> cmbRoles;

    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colGenero;
    @FXML private TableColumn<Usuario, String> colFechaNacimiento;
    @FXML private TableColumn<Usuario, String> colDUI;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, String> colDireccion;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;

    @FXML private Button btnAgregarUsuario;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private ObservableList<Usuario> listaUsuarios;
    private ObservableList<Rol> listaRoles;

    @FXML
    public void initialize() {
        Usuario usuarioActivo = sessionUsu.getUsuarioActivo();
        cmbGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));
        if (usuarioActivo == null || usuarioActivo.getRol().getNivel_acceso() != 1) {
            bloquearFormulario();
            mostrarAlerta("Acceso denegado", "Solo el Administrador puede gestionar usuarios.");
            return;
        }

        cargarDatosUsuario();
        configurarTabla();
        cargarRoles();
        cargarUsuarios();

        tblUsuarios.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevo) -> {
            if (nuevo != null) {
                llenarFormularioDesdeUsuario(nuevo);
            }
        });
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));

        colFechaNacimiento.setCellValueFactory(data -> {
            java.util.Date f = data.getValue().getFecha_nacimiento();
            return new SimpleStringProperty(f != null ? f.toString() : "");
        });

        colDUI.setCellValueFactory(new PropertyValueFactory<>("dui"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        colRol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getRol() != null
                                ? data.getValue().getRol().getNombre_rol()
                                : ""
                ));
    }

    private void cargarUsuarios() {
        listaUsuarios = UsuarioDAO.obtenerUsuarios();
        tblUsuarios.setItems(listaUsuarios);
    }

    private void cargarRoles() {
        listaRoles = FXCollections.observableArrayList();
        String sql = "SELECT * FROM tbl_roles";

        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId_rol(rs.getInt("id_rol"));
                rol.setNombre_rol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setNivel_acceso(rs.getInt("nivel_acceso"));
                listaRoles.add(rol);
            }
            cmbRoles.setItems(listaRoles);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void llenarFormularioDesdeUsuario(Usuario u) {
        txtNombre.setText(u.getNombre());
        cmbGenero.setValue(u.getGenero());

        // --- FECHA (arreglado) ---
        java.util.Date fecha = u.getFecha_nacimiento();
        if (fecha != null) {
            if (fecha instanceof java.sql.Date) {
                // Viene de la BD como java.sql.Date -> usamos toLocalDate()
                dpFechaNacimiento.setValue(((java.sql.Date) fecha).toLocalDate());
            } else {
                // Si alguna vez usas java.util.Date "pura", aquí sí se puede usar toInstant()
                dpFechaNacimiento.setValue(
                        fecha.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                );
            }
        } else {
            dpFechaNacimiento.setValue(null);
        }

        // --- RESTO DE CAMPOS ---
        txtDUI.setText(u.getDui());
        txtTelefono.setText(u.getTelefono());
        txtDireccion.setText(u.getDireccion());
        txtCorreo.setText(u.getCorreo());
        txtNombreUsuario.setText(u.getUsuario());
        txtContraseña.setText(u.getContraseña());

        if (u.getRol() != null && listaRoles != null) {
            for (Rol r : listaRoles) {
                if (r.getId_rol() == u.getRol().getId_rol()) {
                    cmbRoles.setValue(r);
                    break;
                }
            }
        } else {
            cmbRoles.setValue(null);
        }
    }


    private Usuario construirDesdeFormulario() {
        Usuario u = new Usuario();

        u.setNombre(txtNombre.getText());
        u.setGenero(cmbGenero.getValue());
        LocalDate fecha = dpFechaNacimiento.getValue();
        u.setFecha_nacimiento(fecha != null ? java.sql.Date.valueOf(fecha) : null);
        u.setDui(txtDUI.getText());
        u.setTelefono(txtTelefono.getText());
        u.setDireccion(txtDireccion.getText());
        u.setCorreo(txtCorreo.getText());
        u.setUsuario(txtNombreUsuario.getText());
        u.setContraseña(txtContraseña.getText());
        u.setRol(cmbRoles.getValue());

        return u;
    }

    private boolean hayCamposVacios() {
        return txtNombre.getText().isEmpty()
                || cmbGenero.getValue() == null
                || dpFechaNacimiento.getValue() == null
                || txtDUI.getText().isEmpty()
                || txtTelefono.getText().isEmpty()
                || txtDireccion.getText().isEmpty()
                || txtCorreo.getText().isEmpty()
                || txtNombreUsuario.getText().isEmpty()
                || txtContraseña.getText().isEmpty()
                || cmbRoles.getValue() == null;
    }

    private void limpiarCampos() {
        txtNombre.clear();
        cmbGenero.setValue(null);
        dpFechaNacimiento.setValue(null);
        txtDUI.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtCorreo.clear();
        txtNombreUsuario.clear();
        txtContraseña.clear();
        cmbRoles.setValue(null);
        tblUsuarios.getSelectionModel().clearSelection();
    }

    private void bloquearFormulario() {
        txtNombre.setDisable(true);
        cmbGenero.setDisable(true);
        dpFechaNacimiento.setDisable(true);
        txtDUI.setDisable(true);
        txtTelefono.setDisable(true);
        txtDireccion.setDisable(true);
        txtCorreo.setDisable(true);
        txtNombreUsuario.setDisable(true);
        txtContraseña.setDisable(true);
        cmbRoles.setDisable(true);
        btnAgregarUsuario.setDisable(true);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        tblUsuarios.setDisable(true);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void agregarUsuario() {
        if (hayCamposVacios()) {
            mostrarAlerta("Campos vacíos", "Completa todos los campos antes de agregar un usuario.");
            return;
        }

        try {
            Usuario u = construirDesdeFormulario();
            UsuarioDAO.insertarUsuario(u);

            mostrarAlerta("Éxito", "Usuario agregado correctamente.");
            limpiarCampos();
            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo agregar el usuario.");
        }
    }

    @FXML
    private void editarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un usuario para editar.");
            return;
        }

        if (hayCamposVacios()) {
            mostrarAlerta("Campos vacíos", "Completa todos los campos antes de actualizar.");
            return;
        }

        try {
            Usuario u = construirDesdeFormulario();
            u.setId_usuario(seleccionado.getId_usuario());

            UsuarioDAO.actualizarUsuario(u);

            mostrarAlerta("Éxito", "Usuario actualizado correctamente.");
            limpiarCampos();
            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar el usuario.");
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un usuario para eliminar.");
            return;
        }

        try {
            UsuarioDAO.eliminarUsuario(seleccionado.getId_usuario());
            mostrarAlerta("Éxito", "Usuario eliminado correctamente.");
            limpiarCampos();
            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar el usuario.");
        }
    }

    @FXML
    void Close(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToBitacoraAuditor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/bitacora.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToCatalogoCuentas(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/catalogo_cuenta.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToDoc(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/documentos.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToEstadoResultados(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/estadosResultados.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToHome(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/inicio.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToLibroDiario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroDiario.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToLibroMayor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroMayor.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void goToUsuario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/usuarios.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}