/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.autorepar.conexion;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author salaz
 */
public class Conexion {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/autoReparDB";
    private static final String USER = "postgres";
    private static final String PASS = "Las=09.123";
    
    public static Connection getConexion() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return null;
    }
}
