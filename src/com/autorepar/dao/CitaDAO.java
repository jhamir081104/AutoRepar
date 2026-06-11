package com.autorepar.dao;

import com.autorepar.model.Cita;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {
    
    public boolean insertar(Cita cita) {
        String sql = "INSERT INTO Cita (fecha, hora, estado, descripcion, cliente_id, vehiculo_id, mecanico_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDate(1, Date.valueOf(cita.getFecha()));
            ps.setTime(2, Time.valueOf(cita.getHora()));
            ps.setString(3, cita.getEstado());
            ps.setString(4, cita.getDescripcion());
            ps.setInt(5, cita.getClienteId());
            ps.setInt(6, cita.getVehiculoId());
            ps.setInt(7, cita.getMecanicoId());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    cita.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean actualizar(Cita cita) {
        String sql = "UPDATE Cita SET fecha=?, hora=?, estado=?, descripcion=?, cliente_id=?, vehiculo_id=?, mecanico_id=? WHERE id_cita=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(cita.getFecha()));
            ps.setTime(2, Time.valueOf(cita.getHora()));
            ps.setString(3, cita.getEstado());
            ps.setString(4, cita.getDescripcion());
            ps.setInt(5, cita.getClienteId());
            ps.setInt(6, cita.getVehiculoId());
            ps.setInt(7, cita.getMecanicoId());
            ps.setInt(8, cita.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean cancelar(int id) {
        String sql = "UPDATE Cita SET estado='CANCELADA' WHERE id_cita=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Cita WHERE id_cita=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora, int mecanicoId) {
        String sql = "SELECT COUNT(*) FROM Cita WHERE fecha=? AND hora=? AND mecanico_id=? AND estado != 'CANCELADA'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            ps.setInt(3, mecanicoId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Cita> listarPorFecha(LocalDate fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM Cita WHERE fecha=? ORDER BY hora";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                citas.add(extraerCita(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
    
    public List<Cita> listarPorCliente(int clienteId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM Cita WHERE cliente_id=? ORDER BY fecha DESC, hora DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                citas.add(extraerCita(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
    
    public List<Cita> listarTodas() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM Cita ORDER BY fecha, hora";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                citas.add(extraerCita(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
    
    private Cita extraerCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setId(rs.getInt("id_cita"));
        c.setFecha(rs.getDate("fecha").toLocalDate());
        c.setHora(rs.getTime("hora").toLocalTime());
        c.setEstado(rs.getString("estado"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setClienteId(rs.getInt("cliente_id"));
        c.setVehiculoId(rs.getInt("vehiculo_id"));
        c.setMecanicoId(rs.getInt("mecanico_id"));
        return c;
    }
}