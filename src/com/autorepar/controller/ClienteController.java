package com.autorepar.controller;

import com.autorepar.dao.ClienteDAO;
import com.autorepar.model.Cliente;
import java.util.List;

public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController() {
        clienteDAO = new ClienteDAO();
    }

    public boolean guardar(Cliente cliente) {
        return clienteDAO.insertar(cliente);
    }

    public boolean actualizar(Cliente cliente) {
        return clienteDAO.actualizar(cliente);
    }

    public boolean eliminar(int id) {
        return clienteDAO.eliminar(id);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public List<Cliente> buscar(String texto) {
        return clienteDAO.buscar(texto);
    }

    public Cliente obtenerPorId(int id) {
        return clienteDAO.obtenerPorId(id);
    }
}
