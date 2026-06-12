package com.autorepar.dao;

import com.autorepar.conexion.Conexion;
import com.autorepar.model.Vehiculo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    public boolean insertar(Vehiculo vehiculo) {

        String sql = """
        INSERT INTO vehiculo
        (placa, marca, modelo, anio, color, cliente_id)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING id_vehiculo
        """;

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehiculo.getPlaca().toUpperCase());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setString(5, vehiculo.getColor());
            ps.setInt(6, vehiculo.getClienteId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                vehiculo.setId(rs.getInt("id_vehiculo"));
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizar(Vehiculo vehiculo) {

        String sql = """
        UPDATE vehiculo
        SET placa = ?,
            marca = ?,
            modelo = ?,
            anio = ?,
            color = ?,
            cliente_id = ?
        WHERE id_vehiculo = ?
        """;

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehiculo.getPlaca().toUpperCase());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setString(5, vehiculo.getColor());
            ps.setInt(6, vehiculo.getClienteId());
            ps.setInt(7, vehiculo.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean eliminar(int id) {

        String sql = """
        DELETE FROM vehiculo
        WHERE id_vehiculo = ?
        """;

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Vehiculo obtenerPorId(int id) {
        String sql = "SELECT * FROM vehiculo WHERE id_vehiculo=?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extraerVehiculo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Vehiculo> listarPorCliente(int clienteId) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE cliente_id=? ORDER BY marca, modelo";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                vehiculos.add(extraerVehiculo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    public List<Vehiculo> listarTodos() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo ORDER BY marca, modelo";

        try (Connection conn = Conexion.getConexion(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(extraerVehiculo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    public Vehiculo buscarPorPlaca(String placa) {
        String sql = "SELECT * FROM Vehiculo WHERE placa=?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, placa.toUpperCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extraerVehiculo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Vehiculo extraerVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id_vehiculo"));
        v.setPlaca(rs.getString("placa"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnio(rs.getInt("anio"));
        v.setColor(rs.getString("color"));
        v.setClienteId(rs.getInt("cliente_id"));
        return v;
    }
}
