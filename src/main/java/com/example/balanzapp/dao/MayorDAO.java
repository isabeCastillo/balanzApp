package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.MovimientoMayor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MayorDAO {

    public static List<MovimientoMayor> obtenerMayorPorCuentaYRango(
            int idCuenta, LocalDate desde, LocalDate hasta) {

        List<MovimientoMayor> lista = new ArrayList<>();

        String sql = """
                SELECT 
                    p.fecha,
                    p.concepto,
                    d.descripcion,
                    d.debe,
                    d.haber,
                    COALESCE(df.archivo_pdf, '') AS documento
                FROM tbl_detallePartida d
                JOIN tbl_partidas p ON p.id_partida = d.id_partida
                LEFT JOIN tbl_docFuente df ON df.id_partida = p.id_partida
                WHERE d.id_cuenta = ?
                  AND p.fecha BETWEEN ? AND ?
                ORDER BY p.fecha, p.numero_partida, d.id_detalle
                """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCuenta);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));

            ResultSet rs = ps.executeQuery();

            double saldoAcumulado = 0.0;

            while (rs.next()) {
                MovimientoMayor mov = new MovimientoMayor();
                mov.setFecha(rs.getDate("fecha").toLocalDate());
                mov.setConcepto(rs.getString("concepto"));
                mov.setDescripcion(rs.getString("descripcion"));
                mov.setDebe(rs.getDouble("debe"));
                mov.setHaber(rs.getDouble("haber"));
                mov.setDocumento(rs.getString("documento"));

                saldoAcumulado += mov.getDebe() - mov.getHaber();
                mov.setSaldo(saldoAcumulado);

                lista.add(mov);
            }

        } catch (SQLException e) {
            System.out.println("Error cargando mayor: " + e.getMessage());
        }

        return lista;
    }
}
