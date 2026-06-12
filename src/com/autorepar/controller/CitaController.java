package com.autorepar.controller;

import com.autorepar.dao.*;
import com.autorepar.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CitaController {

    private final CitaDAO citaDAO;
    private final ClienteDAO clienteDAO;
    private final VehiculoDAO vehiculoDAO;
    private final UsuarioDAO usuarioDAO;

    public CitaController() {
        citaDAO = new CitaDAO();
        clienteDAO = new ClienteDAO();
        vehiculoDAO = new VehiculoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    public List<Cita> listarCitas() {
        return citaDAO.listarTodas();
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public List<Vehiculo> listarVehiculosPorCliente(int clienteId) {
        return vehiculoDAO.listarPorCliente(clienteId);
    }

    public List<Usuario> listarMecanicos() {
        return usuarioDAO.listarMecanicos();
    }

    public Cliente obtenerCliente(int id) {
        return clienteDAO.obtenerPorId(id);
    }

    public Vehiculo obtenerVehiculo(int id) {
        return vehiculoDAO.obtenerPorId(id);
    }

    public Usuario obtenerUsuario(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public boolean guardar(Cita cita) {
        if (!citaDAO.verificarDisponibilidad(
                cita.getFecha(),
                cita.getHora(),
                cita.getMecanicoId())) {
            return false;
        }

        return citaDAO.insertar(cita);
    }

    public boolean actualizar(Cita cita) {
        return citaDAO.actualizar(cita);
    }

    public boolean cancelar(int id) {
        return citaDAO.cancelar(id);
    }
}