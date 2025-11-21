package com.example.balanzapp.service;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.Usuario;
import com.example.balanzapp.utils.sessionUsu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AuditoriaService {

    public static void registrarAccion(String modulo, String accion, String detalles) {
        Usuario usuario = sessionUsu.getUsuarioActivo();
        if (usuario == null) {
            System.out.println("No hay usuario en sesión para registrar bitácora.");
            return;
        }

        String sql = """
            INSERT INTO tbl_bitacAud (id_usuario, accion, modulo, detalles, fecha, hora)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuario.getId_usuario());
            ps.setString(2, accion);
            ps.setString(3, modulo);
            ps.setString(4, detalles);
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            ps.setTime(6, java.sql.Time.valueOf(LocalTime.now()));
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error registrando bitácora: " + e.getMessage());
        }
    }
}