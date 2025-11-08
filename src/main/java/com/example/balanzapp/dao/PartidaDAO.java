package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.DetallePartidaTemp;
import com.example.balanzapp.models.Partida;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    public static List<Partida> obtenerPartidasPorMesYAnio(int mes, int anio) {
        List<Partida> lista = new ArrayList<>();

        String sql = """
        SELECT 
            p.id_partida,
            p.numero_partida,
            p.fecha,
            p.concepto,
            c.nombre AS cuenta,
            d.debe,
            d.haber,
            df.archivo_pdf AS documento
        FROM tbl_partidas p
        JOIN tbl_detallePartida d ON p.id_partida = d.id_partida
        JOIN tbl_cntaContables c ON d.id_cuenta = c.id_cuenta
        LEFT JOIN tbl_docFuente df ON p.id_partida = df.id_partida
        WHERE EXTRACT(MONTH FROM p.fecha) = ? 
          AND EXTRACT(YEAR FROM p.fecha) = ?
        ORDER BY p.numero_partida ASC;
        """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Partida p = new Partida();
                p.setIdPartida(rs.getInt("id_partida"));
                p.setNumeroPartida(rs.getInt("numero_partida"));
                p.setFecha(rs.getDate("fecha").toLocalDate());
                p.setConcepto(rs.getString("concepto"));
                p.setCuenta(rs.getString("cuenta"));
                p.setDebe(rs.getDouble("debe"));
                p.setHaber(rs.getDouble("haber"));
                p.setDocumento(rs.getString("documento"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error cargando partidas: " + e.getMessage());
        }

        return lista;
    }

    public static List<DetallePartidaTemp> obtenerDetallePorPartida(int idPartida) {
        List<DetallePartidaTemp> detalles = new ArrayList<>();

        String sql = """
        SELECT 
            c.id_cuenta,
            c.nombre AS cuenta,
            d.descripcion,
            d.debe,
            d.haber
        FROM tbl_detallePartida d
        JOIN tbl_cntaContables c ON d.id_cuenta = c.id_cuenta
        WHERE d.id_partida = ?
        ORDER BY d.id_detalle;
        """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartida);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetallePartidaTemp det = new DetallePartidaTemp(
                        rs.getInt("id_cuenta"),
                        rs.getString("cuenta"),
                        rs.getString("descripcion"),
                        rs.getDouble("debe"),
                        rs.getDouble("haber")
                );
                detalles.add(det);
            }

        } catch (SQLException e) {
            System.out.println("Error cargando detalle: " + e.getMessage());
        }

        return detalles;
    }


    public static void insertarPartidaConDetalles(
            LocalDate fecha,
            String concepto,
            String tipoPartida,
            int idUsuario,
            List<DetallePartidaTemp> detalles,
            String rutaDocumentoPdf // puede ser null
    ) throws SQLException {

        String sqlNumPartida = """
                SELECT COALESCE(MAX(numero_partida), 0) + 1 AS siguiente
                FROM tbl_partidas
                WHERE EXTRACT(YEAR FROM fecha) = ?
                """;

        String sqlInsertPartida = """
                INSERT INTO tbl_partidas (numero_partida, fecha, concepto, tipo_partida, id_usuario)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id_partida
                """;

        String sqlInsertDetalle = """
                INSERT INTO tbl_detallePartida (id_partida, id_cuenta, descripcion, debe, haber)
                VALUES (?, ?, ?, ?, ?)
                """;

        String sqlInsertDoc = """
                INSERT INTO tbl_docFuente (archivo_pdf, id_partida)
                VALUES (?, ?)
                """;

        Connection conn = null;

        try {
            conn = ConexionDB.connection();
            conn.setAutoCommit(false);

            int numeroPartida;
            try (PreparedStatement psNum = conn.prepareStatement(sqlNumPartida)) {
                psNum.setInt(1, fecha.getYear());
                ResultSet rs = psNum.executeQuery();
                rs.next();
                numeroPartida = rs.getInt("siguiente");
            }

            int idPartida;
            try (PreparedStatement psPartida = conn.prepareStatement(sqlInsertPartida)) {
                psPartida.setInt(1, numeroPartida);
                psPartida.setDate(2, Date.valueOf(fecha));
                psPartida.setString(3, concepto);
                psPartida.setString(4, tipoPartida);
                psPartida.setInt(5, idUsuario);
                ResultSet rs = psPartida.executeQuery();
                rs.next();
                idPartida = rs.getInt("id_partida");
            }

            try (PreparedStatement psDet = conn.prepareStatement(sqlInsertDetalle)) {
                for (DetallePartidaTemp d : detalles) {
                    psDet.setInt(1, idPartida);
                    psDet.setInt(2, d.getIdCuenta());
                    psDet.setString(3, d.getDescripcion());
                    psDet.setDouble(4, d.getDebe());
                    psDet.setDouble(5, d.getHaber());
                    psDet.addBatch();
                }
                psDet.executeBatch();
            }

            if (rutaDocumentoPdf != null && !rutaDocumentoPdf.isBlank()) {
                try (PreparedStatement psDoc = conn.prepareStatement(sqlInsertDoc)) {
                    psDoc.setString(1, rutaDocumentoPdf);
                    psDoc.setInt(2, idPartida);
                    psDoc.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
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
