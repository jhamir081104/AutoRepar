package com.autorepar.dao;

import com.autorepar.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ============ MÉTODOS EXISTENTES ============
    
    public Usuario login(String email, String password) {
        String sql = "SELECT * FROM Usuario WHERE email = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listarMecanicos() {
        List<Usuario> mecanicos = new ArrayList<>();
        String sql = "SELECT * FROM Usuario WHERE rol = 'MECANICO'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                mecanicos.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mecanicos;
    }

    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM Usuario WHERE id_usuario = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============ MÉTODOS CRUD CORREGIDOS ============
    
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Usuario (nombre, email, password, rol) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean actualizar(Usuario usuario) {
        String sql;
        PreparedStatement ps = null;
        
        try (Connection conn = DBConnection.getConnection()) {
            // Verificar si el email ya existe en OTRO usuario (no el mismo)
            if (emailExisteEnOtroUsuario(usuario.getEmail(), usuario.getId())) {
                System.out.println("❌ El email " + usuario.getEmail() + " ya está usado por otro usuario");
                return false;
            }
            
            // Si tiene nueva contraseña, actualizarla también
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                sql = "UPDATE Usuario SET nombre=?, email=?, password=?, rol=? WHERE id_usuario=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getPassword());
                ps.setString(4, usuario.getRol());
                ps.setInt(5, usuario.getId());
            } else {
                sql = "UPDATE Usuario SET nombre=?, email=?, rol=? WHERE id_usuario=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getRol());
                ps.setInt(4, usuario.getId());
            }
            
            int resultado = ps.executeUpdate();
            System.out.println("✅ Usuario actualizado: " + resultado + " fila(s) afectada(s)");
            return resultado > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error en actualización: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método auxiliar para verificar si un email existe en otro usuario
    private boolean emailExisteEnOtroUsuario(String email, int idUsuarioActual) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE email = ? AND id_usuario != ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setInt(2, idUsuarioActual);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Usuario WHERE id_usuario=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuario ORDER BY id_usuario";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                // No devolvemos la contraseña por seguridad
                usuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
    
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}