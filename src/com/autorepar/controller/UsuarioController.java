package com.autorepar.controller;

import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import java.util.List;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public List<Usuario> listarMecanicos() {
        return usuarioDAO.listarMecanicos();
    }

    public Usuario obtenerPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public boolean emailExiste(String email) {
        return usuarioDAO.emailExiste(email);
    }

    public boolean guardar(Usuario usuario) {
        return usuarioDAO.insertar(usuario);
    }

    public boolean actualizar(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminar(int id) {
        return usuarioDAO.eliminar(id);
    }
}