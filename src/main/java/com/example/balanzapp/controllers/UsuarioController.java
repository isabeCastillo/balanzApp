package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.Rol;
import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.time.ZoneId;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;


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
        // Verificación de permisos
        if (usuarioActivo == null || usuarioActivo.getRol().getNivel_acceso() != 1) {
            bloquearFormulario();
            mostrarAlerta("Acceso denegado", "Solo el Administrador puede gestionar usuarios.");
            return;
        }
        cargarDatosUsuario();
        configurarTabla();
        cargarRoles();
        cargarUsuarios();

        tblUsuarios.setOnMouseClicked(event -> {
            Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                txtNombre.setText(seleccionado.getNombre());
                cmbGenero.setValue(seleccionado.getGenero());
                if (seleccionado.getFecha_nacimiento() != null) {
                    dpFechaNacimiento.setValue(
                            seleccionado.getFecha_nacimiento().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                    );
                } else {
                    dpFechaNacimiento.setValue(null);
                }
                txtDUI.setText(seleccionado.getDui());
                txtTelefono.setText(seleccionado.getTelefono());
                txtDireccion.setText(seleccionado.getDireccion());
                txtCorreo.setText(seleccionado.getCorreo());
                txtNombreUsuario.setText(seleccionado.getUsuario());
                txtContraseña.setText(seleccionado.getContraseña());
                cmbRoles.setValue(seleccionado.getRol());
            }
        });




    }


    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colFechaNacimiento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha_nacimiento().toString()));
        colDUI.setCellValueFactory(new PropertyValueFactory<>("dui"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRol().getNombre_rol()));
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

    private void cargarUsuarios() {
        Usuario usuario = new Usuario();
        listaUsuarios = usuario.getUsuarios();
        tblUsuarios.setItems(listaUsuarios);
    }

    @FXML
    private void agregarUsuario() {
        if (txtNombre.getText().isEmpty() || cmbGenero.getValue() == null || dpFechaNacimiento.getValue() == null ||
                txtDUI.getText().isEmpty() || txtTelefono.getText().isEmpty() || txtDireccion.getText().isEmpty() ||
                txtCorreo.getText().isEmpty() || txtNombreUsuario.getText().isEmpty() || txtContraseña.getText().isEmpty() ||
                cmbRoles.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Completa todos los campos antes de agregar un usuario.");
            return;
        }

        String sql = "INSERT INTO tbl_usuarios (nombre, genero, fecha_nacimiento, dui, telefono, direccion, correo, usuario, contraseña, id_rol) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, cmbGenero.getValue());
            ps.setDate(3, Date.valueOf(dpFechaNacimiento.getValue()));
            ps.setString(4, txtDUI.getText());
            ps.setString(5, txtTelefono.getText());
            ps.setString(6, txtDireccion.getText());
            ps.setString(7, txtCorreo.getText());
            ps.setString(8, txtNombreUsuario.getText());
            ps.setString(9, txtContraseña.getText());
            ps.setInt(10, cmbRoles.getValue().getId_rol());

            ps.executeUpdate();
            mostrarAlerta("Éxito", "Usuario agregado correctamente.");
            limpiarCampos();
            cargarUsuarios();

        } catch (SQLException e) {
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

        String sql = "UPDATE tbl_usuarios SET nombre=?, genero=?, fecha_nacimiento=?, dui=?, telefono=?, direccion=?, correo=?, usuario=?, contraseña=?, id_rol=? " +
                "WHERE id_usuario=?";

        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, cmbGenero.getValue());
            ps.setDate(3, Date.valueOf(dpFechaNacimiento.getValue()));
            ps.setString(4, txtDUI.getText());
            ps.setString(5, txtTelefono.getText());
            ps.setString(6, txtDireccion.getText());
            ps.setString(7, txtCorreo.getText());
            ps.setString(8, txtNombreUsuario.getText());
            ps.setString(9, txtContraseña.getText());
            ps.setInt(10, cmbRoles.getValue().getId_rol());
            ps.setInt(11, seleccionado.getId_usuario());

            ps.executeUpdate();
            mostrarAlerta("Éxito", "Usuario actualizado correctamente.");
            limpiarCampos();
            cargarUsuarios();

        } catch (SQLException e) {
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

        String sql = "DELETE FROM tbl_usuarios WHERE id_usuario=?";
        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seleccionado.getId_usuario());
            ps.executeUpdate();

            mostrarAlerta("Éxito", "Usuario eliminado correctamente.");
            cargarUsuarios();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar el usuario.");
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    void goToCatalogoCuentas(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/catalogo_cuenta.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void goToDoc(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/documentos.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToEstadoResultados(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/estadosResultados.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void goToHome(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/inicio.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToLibroDiario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroDiario.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToLibroMayor(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/libroMayor.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToUsuario(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/usuarios.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}