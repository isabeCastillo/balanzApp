package com.example.balanzapp.models;

import com.example.balanzapp.Conexion.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String genero;
    private Date fecha_nacimiento;
    private String dui;
    private String telefono;
    private String direccion;
    private String correo;
    private String usuario;
    private String contraseña;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(int id_usuario, String nombre, String genero, Date fecha_nacimiento, String dui, String telefono, String direccion, String correo, String usuario, String contraseña, Rol rol) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.genero = genero;
        this.fecha_nacimiento = fecha_nacimiento;
        this.dui = dui;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public ObservableList<Usuario> getUsuarios() {
        ObservableList<Usuario> dataUsuario = FXCollections.observableArrayList();

        String sql = "SELECT u.*, r.id_rol, r.nombre_rol, r.nivel_acceso " +
                "FROM tbl_usuarios u " +
                "JOIN tbl_roles r ON u.id_rol = r.id_rol";

        try (Connection connection = ConexionDB.connection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Usuario model = new Usuario();
                model.setId_usuario(resultSet.getInt("id_usuario"));
                model.setNombre(resultSet.getString("nombre"));
                model.setGenero(resultSet.getString("genero"));
                model.setFecha_nacimiento(resultSet.getDate("fecha_nacimiento"));
                model.setDui(resultSet.getString("dui"));
                model.setTelefono(resultSet.getString("telefono"));
                model.setDireccion(resultSet.getString("direccion"));
                model.setCorreo(resultSet.getString("correo"));
                model.setUsuario(resultSet.getString("usuario"));
                model.setContraseña(resultSet.getString("contraseña"));

                Rol rol = new Rol();
                rol.setId_rol(resultSet.getInt("id_rol"));
                rol.setNombre_rol(resultSet.getString("nombre_rol"));
                rol.setNivel_acceso(resultSet.getInt("nivel_acceso"));
                model.setRol(rol);

                dataUsuario.add(model);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataUsuario;
    }

}
