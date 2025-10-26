package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.Partida;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    public static List<Partida> obtenerPartidasPorMesYAnio(int mes, int anio) {
        List<Partida> lista = new ArrayList<>();

        String sql = """
                SELECT p.id_partida, p.fecha, p.concepto, c.nombre,
                       d.debe, d.haber
                FROM tbl_partidas p
                JOIN tbl_detallePartida d ON p.id_partida = d.id_partida
                JOIN tbl_cntaContables c ON d.id_cuenta = c.id_cuenta
                WHERE EXTRACT(MONTH FROM p.fecha) = ? AND EXTRACT(YEAR FROM p.fecha) = ?
                ORDER BY p.numero_partida ASC;
                """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Partida(
                        rs.getInt(1),
                        rs.getDate(2).toLocalDate(),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDouble(5),
                        rs.getDouble(6)
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error cargando partidas: " + e.getMessage());
        }

        return lista;
    }

    public static void insertarPartida(LocalDate fecha, String concepto, int idCuenta, boolean esDebe, double monto, int idUsuario) {
        String sqlPartida = "INSERT INTO tbl_partidas(fecha, concepto, id_usuario) VALUES (?, ?, ?) RETURNING id_partida";
        String sqlDetalle = "INSERT INTO tbl_detallePartida(id_partida, id_cuenta, debe, haber) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.connection()) {
            conn.setAutoCommit(false);

            PreparedStatement psPartida = conn.prepareStatement(sqlPartida);
            psPartida.setDate(1, Date.valueOf(fecha));
            psPartida.setString(2, concepto);
            psPartida.setInt(3, idUsuario);

            ResultSet rs = psPartida.executeQuery();
            rs.next();
            int idPartida = rs.getInt(1);

            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
            psDetalle.setInt(1, idPartida);
            psDetalle.setInt(2, idCuenta);

            if (esDebe) {
                psDetalle.setDouble(3, monto);
                psDetalle.setDouble(4, 0);
            } else {
                psDetalle.setDouble(3, 0);
                psDetalle.setDouble(4, monto);
            }

            psDetalle.execute();

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error insertando partida: " + e.getMessage());
        }
    }
}
