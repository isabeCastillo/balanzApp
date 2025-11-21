package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.Bitacora;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {
    public static List<String> obtenerUsuariosFiltro() {
        List<String> usuarios = new ArrayList<>();

        String sql = "SELECT DISTINCT nombre FROM tbl_usuarios ORDER BY nombre";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error cargando usuarios filtro bitácora: " + e.getMessage());
        }

        return usuarios;
    }

    // ==== PARA LLENAR COMBO DE MÓDULOS
    public static List<String> obtenerModulosFiltro() {
        List<String> modulos = new ArrayList<>();

        String sql = "SELECT DISTINCT modulo FROM tbl_bitacaud ORDER BY modulo";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modulos.add(rs.getString("modulo"));
            }

        } catch (SQLException e) {
            System.out.println("Error cargando módulos filtro bitácora: " + e.getMessage());
        }

        return modulos;
    }

    // ==== FILTRAR BITÁCORA
    public static List<Bitacora> filtrarBitacora(
            String usuario,
            LocalDate desde,
            LocalDate hasta,
            String modulo
    ) {
        List<Bitacora> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT 
                u.nombre AS usuario,
                r.nombre_rol AS rol,
                b.accion,
                b.modulo,
                b.fecha,
                b.hora
            FROM tbl_bitacaud b
            JOIN tbl_usuarios u ON b.id_usuario = u.id_usuario
            JOIN tbl_roles r ON u.id_rol = r.id_rol
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (usuario != null && !usuario.isBlank()) {
            sql.append(" AND u.nombre = ? ");
            params.add(usuario);
        }

        if (desde != null) {
            sql.append(" AND b.fecha >= ? ");
            params.add(Date.valueOf(desde));
        }

        if (hasta != null) {
            sql.append(" AND b.fecha <= ? ");
            params.add(Date.valueOf(hasta));
        }

        if (modulo != null && !modulo.isBlank()) {
            sql.append(" AND b.modulo = ? ");
            params.add(modulo);
        }

        sql.append(" ORDER BY b.fecha DESC, b.hora DESC ");

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Bitacora reg = new Bitacora(
                        rs.getString("usuario"),
                        rs.getString("rol"),
                        rs.getString("accion"),
                        rs.getString("modulo"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime()
                );
                lista.add(reg);
            }
        } catch (SQLException e) {
            System.out.println("Error filtrando bitácora: " + e.getMessage());
        }
        return lista;
    }
}
