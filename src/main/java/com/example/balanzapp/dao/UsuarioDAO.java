package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.Rol;
import com.example.balanzapp.models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UsuarioDAO {
    public static ObservableList<Usuario> obtenerUsuarios() {
        ObservableList<Usuario> lista = FXCollections.observableArrayList();
        String sql = """
                SELECT u.*, r.id_rol, r.nombre_rol, r.descripcion, r.nivel_acceso
                FROM tbl_usuarios u
                JOIN tbl_roles r ON u.id_rol = r.id_rol
                ORDER BY u.id_usuario
                """;
        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setGenero(rs.getString("genero"));
                u.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                u.setDui(rs.getString("dui"));
                u.setTelefono(rs.getString("telefono"));
                u.setDireccion(rs.getString("direccion"));
                u.setCorreo(rs.getString("correo"));
                u.setUsuario(rs.getString("usuario"));
                u.setContraseña(rs.getString("contraseña"));

                Rol rol = new Rol();
                rol.setId_rol(rs.getInt("id_rol"));
                rol.setNombre_rol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setNivel_acceso(rs.getInt("nivel_acceso"));

                u.setRol(rol);
                lista.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static void insertarUsuario(Usuario u) throws SQLException {
        String sql = """
            INSERT INTO tbl_usuarios
            (nombre, genero, fecha_nacimiento, dui, telefono, direccion, correo, usuario, contraseña, id_rol)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getGenero());
            if (u.getFecha_nacimiento() != null) {
                ps.setDate(3, new java.sql.Date(u.getFecha_nacimiento().getTime()));
            } else {
                ps.setDate(3, null);
            }
            ps.setString(4, u.getDui());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getDireccion());
            ps.setString(7, u.getCorreo());
            ps.setString(8, u.getUsuario());
            ps.setString(9, u.getContraseña());
            ps.setInt(10, u.getRol().getId_rol());

            ps.executeUpdate();
        }
    }

    public static void actualizarUsuario(Usuario u) throws SQLException {
        String sql = """
            UPDATE tbl_usuarios
            SET nombre=?, genero=?, fecha_nacimiento=?, dui=?, telefono=?, direccion=?, correo=?, usuario=?, contraseña=?, id_rol=?
            WHERE id_usuario=?
            """;

        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getGenero());

            if (u.getFecha_nacimiento() != null) {
                ps.setDate(3, new java.sql.Date(u.getFecha_nacimiento().getTime()));
            } else {
                ps.setDate(3, null);
            }

            ps.setString(4, u.getDui());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getDireccion());
            ps.setString(7, u.getCorreo());
            ps.setString(8, u.getUsuario());
            ps.setString(9, u.getContraseña());
            ps.setInt(10, u.getRol().getId_rol());
            ps.setInt(11, u.getId_usuario());

            ps.executeUpdate();
        }
    }

    public static void eliminarUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM tbl_usuarios WHERE id_usuario=?";
        try (Connection con = ConexionDB.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }
}
