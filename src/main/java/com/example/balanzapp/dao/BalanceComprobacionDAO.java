package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.BalanceComprobacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BalanceComprobacionDAO {

    /**
     * Obtiene la balanza de comprobación para un mes y año.
     * mes: 1-12, anio: 2024, 2025, etc.
     */
    public static List<BalanceComprobacion> obtenerBalanceMensual(int mes, int anio) {
        List<BalanceComprobacion> lista = new ArrayList<>();

        String sql = """
                SELECT c.codigo,
                       c.nombre AS cuenta,
                       COALESCE(SUM(d.debe), 0) AS debe,
                       COALESCE(SUM(d.haber), 0) AS haber
                FROM tbl_cntaContables c
                JOIN tbl_detallePartida d ON c.id_cuenta = d.id_cuenta
                JOIN tbl_partidas p ON d.id_partida = p.id_partida
                WHERE EXTRACT(MONTH FROM p.fecha) = ? 
                  AND EXTRACT(YEAR  FROM p.fecha) = ?
                GROUP BY c.codigo, c.nombre
                HAVING COALESCE(SUM(d.debe),0) <> 0 OR COALESCE(SUM(d.haber),0) <> 0
                ORDER BY c.codigo;
                """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mes);
            ps.setInt(2, anio);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BalanceComprobacion b = new BalanceComprobacion(
                        rs.getString("codigo"),
                        rs.getString("cuenta"),
                        rs.getDouble("debe"),
                        rs.getDouble("haber")
                );
                lista.add(b);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo balanza: " + e.getMessage());
        }

        return lista;
    }
}