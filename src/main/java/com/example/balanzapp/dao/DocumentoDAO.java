package com.example.balanzapp.dao;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.models.DocumentoTabla;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    public static List<DocumentoTabla> obtenerTodosLosDocumentos() {
        List<DocumentoTabla> lista = new ArrayList<>();

        String sql = """
            SELECT 
                df.id_documento,
                df.archivo_pdf,
                df.fecha_subida::date AS fecha,
                COALESCE(cd.nombre_clasificacion, 'Sin clasificar') AS clasificacion,
                COALESCE(u.nombre, 'Desconocido') AS usuario
            FROM tbl_docFuente df
            LEFT JOIN tbl_clasDocumento cd ON df.id_clasificacion = cd.id_clasificacion
            LEFT JOIN tbl_partidas p ON df.id_partida = p.id_partida
            LEFT JOIN tbl_usuarios u ON p.id_usuario = u.id_usuario
            ORDER BY df.fecha_subida DESC, df.id_documento DESC
            """;

        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idDoc = rs.getInt("id_documento");
                String archivo = rs.getString("archivo_pdf");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String clasificacion = rs.getString("clasificacion");
                String usuario = rs.getString("usuario");

                // Aquí asumo que archivo_pdf guarda la ruta completa o el nombre del archivo
                // Si en tu app guardas ruta absoluta (como cuando subes desde Libro Diario),
                // rutaArchivo = archivo. Si solo es nombre, tal vez tengas que armar la ruta.
                String rutaArchivo = archivo;

                DocumentoTabla doc = new DocumentoTabla(
                        idDoc,
                        archivo,
                        clasificacion,
                        fecha,
                        usuario,
                        rutaArchivo
                );

                lista.add(doc);
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo documentos: " + e.getMessage());
        }

        return lista;
    }
}