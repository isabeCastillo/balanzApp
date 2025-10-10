package com.example.balanzapp.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static String url = "jdbc:postgresql://localhost:5432/db_contables";

    private static  final String USER = "postgres";

    private static final String PASS = "progra2023";

    //crear objeto de tipo conect
    public static Connection connection()
    {
        try {
            Connection conectar = DriverManager.getConnection(url, USER, PASS);
            System.out.println("Conectado a la base");
            return conectar;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
