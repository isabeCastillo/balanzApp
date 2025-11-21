package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.MovimientoMayor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MayorDAO {
    public static List<MovimientoMayor> obtenerMayorPorCuentaYRango(
            String codigoCuenta,
            LocalDate desde,
            LocalDate hasta
    ) {
        List<MovimientoMayor> lista = new ArrayList<>();

        String sql = """
            SELECT 
                p.fecha,
                p.concepto,
                d.descripcion,
                d.debe,
                d.haber
            FROM tbl_cntaContables c
            JOIN tbl_detallePartida d ON c.id_cuenta = d.id_cuenta
            JOIN tbl_partidas p       ON p.id_partida = d.id_partida
            WHERE c.codigo = ?
              AND p.fecha BETWEEN ? AND ?
            ORDER BY p.fecha, p.id_partida, d.id_detalle
            """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigoCuenta);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));

            ResultSet rs = ps.executeQuery();

            double saldoAcumulado = 0.0;

            while (rs.next()) {
                LocalDate fecha       = rs.getDate("fecha").toLocalDate();
                String concepto       = rs.getString("concepto");
                String descripcion    = rs.getString("descripcion");
                double debe           = rs.getDouble("debe");
                double haber          = rs.getDouble("haber");

                // saldo acumulado = suma(debe - haber)
                saldoAcumulado += (debe - haber);

                MovimientoMayor mov = new MovimientoMayor(
                        fecha,
                        concepto,
                        descripcion,
                        debe,
                        haber,
                        saldoAcumulado
                );

                lista.add(mov);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo mayor por cuenta: " + e.getMessage());
        }

        return lista;
    }
}
