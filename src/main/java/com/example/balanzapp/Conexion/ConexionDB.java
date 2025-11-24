package com.example.balanzapp.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static String url = "jdbc:postgresql://localhost:5432/db_contables";

    private static  final String USER = "postgres"; //ingrese el nombre de su usuario de postgres

    private static final String PASS = "ingrese_su_contraseña_de_postgres";

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
