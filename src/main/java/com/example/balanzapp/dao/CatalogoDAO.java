package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.Cuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {

    public static List<Cuenta> obtenerCuentas() {
        List<Cuenta> cuentas = new ArrayList<>();

        String sql = "SELECT * FROM tbl_cntaContables ORDER BY codigo ASC";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cuenta c = new Cuenta();
                c.setIdCuenta(rs.getInt("id_cuenta"));
                c.setCodigo(rs.getString("codigo"));
                c.setNombre(rs.getString("nombre"));
                c.setTipo(rs.getString("tipo"));
                c.setNaturaleza(rs.getString("naturaleza"));
                c.setGrupo(rs.getString("grupo"));
                cuentas.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cuentas;
    }

    public static boolean insertarCuenta(Cuenta cuenta) {
        String sql = "INSERT INTO tbl_cntaContables (codigo, nombre, tipo, naturaleza, grupo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cuenta.getCodigo());
            ps.setString(2, cuenta.getNombre());
            ps.setString(3, cuenta.getTipo());
            ps.setString(4, cuenta.getNaturaleza());
            ps.setString(5, cuenta.getGrupo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void eliminarCuenta(int id) {
        String sql = "DELETE FROM tbl_cntaContables WHERE id_cuenta = ?";
        try (Connection conn = ConexionDB.connection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminando cuenta: " + e.getMessage());
        }
    }
    public static boolean actualizarCuenta(Cuenta cuenta) {
        String sql = "UPDATE tbl_cntaContables SET nombre=?, tipo=?, naturaleza=?, grupo=? WHERE id_cuenta=?";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cuenta.getNombre());
            ps.setString(2, cuenta.getTipo());
            ps.setString(3, cuenta.getNaturaleza());
            ps.setString(4, cuenta.getGrupo());
            ps.setInt(5, cuenta.getIdCuenta());



            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}