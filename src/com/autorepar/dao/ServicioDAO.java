package com.autorepar.dao;

import com.autorepar.model.Servicio;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {
    
    public boolean insertar(Servicio servicio) {
        String sql = "INSERT INTO Servicio (tipo, descripcion, costo, fecha, vehiculo_id, mecanico_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, servicio.getTipo());
            ps.setString(2, servicio.getDescripcion());
            ps.setDouble(3, servicio.getCosto());
            ps.setDate(4, Date.valueOf(servicio.getFecha()));
            ps.setInt(5, servicio.getVehiculoId());
            ps.setInt(6, servicio.getMecanicoId());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    servicio.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Servicio> listarPorVehiculo(int vehiculoId) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicio WHERE vehiculo_id=? ORDER BY fecha DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, vehiculoId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                servicios.add(extraerServicio(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }
    
    public List<Servicio> listarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicio WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fin));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                servicios.add(extraerServicio(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }
    
    public List<Servicio> listarTodos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicio ORDER BY fecha DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                servicios.add(extraerServicio(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }
    
    public double obtenerTotalServiciosPorRango(LocalDate inicio, LocalDate fin) {
        String sql = "SELECT SUM(costo) as total FROM Servicio WHERE fecha BETWEEN ? AND ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fin));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Servicio extraerServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id_servicio"));
        s.setTipo(rs.getString("tipo"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setCosto(rs.getDouble("costo"));
        s.setFecha(rs.getDate("fecha").toLocalDate());
        s.setVehiculoId(rs.getInt("vehiculo_id"));
        s.setMecanicoId(rs.getInt("mecanico_id"));
        return s;
    }
}