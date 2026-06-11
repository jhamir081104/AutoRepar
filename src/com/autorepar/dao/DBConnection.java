package com.autorepar.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:derby:AutoReparDB;create=true");
                System.out.println("✅ Conexión a la base de datos exitosa");
                crearTablasSiNoExisten();
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static void crearTablasSiNoExisten() {
        String[] tablas = {
            "CREATE TABLE Usuario (id_usuario INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, nombre VARCHAR(100), email VARCHAR(100) UNIQUE, password VARCHAR(100), rol VARCHAR(30))",
            "CREATE TABLE Cliente (id_cliente INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, nombre VARCHAR(50), apellido VARCHAR(50), telefono VARCHAR(20), email VARCHAR(100), direccion VARCHAR(200))",
            "CREATE TABLE Vehiculo (id_vehiculo INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, placa VARCHAR(10) UNIQUE, marca VARCHAR(50), modelo VARCHAR(50), anio INT, color VARCHAR(30), cliente_id INT, FOREIGN KEY (cliente_id) REFERENCES Cliente(id_cliente))",
            "CREATE TABLE Cita (id_cita INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, fecha DATE, hora TIME, estado VARCHAR(20), descripcion VARCHAR(500), cliente_id INT, vehiculo_id INT, mecanico_id INT, FOREIGN KEY (cliente_id) REFERENCES Cliente(id_cliente), FOREIGN KEY (vehiculo_id) REFERENCES Vehiculo(id_vehiculo), FOREIGN KEY (mecanico_id) REFERENCES Usuario(id_usuario))",
            "CREATE TABLE Servicio (id_servicio INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, tipo VARCHAR(50), descripcion VARCHAR(500), costo DECIMAL(10,2), fecha DATE, vehiculo_id INT, mecanico_id INT, FOREIGN KEY (vehiculo_id) REFERENCES Vehiculo(id_vehiculo), FOREIGN KEY (mecanico_id) REFERENCES Usuario(id_usuario))"
        };

        try (Statement stmt = getConnection().createStatement()) {
            for (String tabla : tablas) {
                try {
                    stmt.execute(tabla);
                    System.out.println("✅ Tabla creada correctamente");
                } catch (SQLException e) {
                    if (!e.getSQLState().equals("X0Y32")) { // X0Y32 = tabla ya existe
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Insertar usuario admin por defecto si no existe
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("INSERT INTO Usuario (nombre, email, password, rol) VALUES ('Administrador', 'admin@autorepar.com', 'admin123', 'ADMIN')");
            System.out.println("✅ Usuario admin creado");
        } catch (SQLException e) {
            // Usuario ya existe, ignorar
        }
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("🔒 Conexión cerrada");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}