package com.autorepar.controller;

import com.autorepar.dao.ClienteDAO;
import com.autorepar.dao.VehiculoDAO;
import com.autorepar.model.Cliente;
import com.autorepar.model.Vehiculo;
import java.util.List;

public class VehiculoController {

    private final VehiculoDAO vehiculoDAO;
    private final ClienteDAO clienteDAO;

    public VehiculoController() {
        vehiculoDAO = new VehiculoDAO();
        clienteDAO = new ClienteDAO();
    }

    // Vehículos

    public List<Vehiculo> listarVehiculos() {
        return vehiculoDAO.listarTodos();
    }

    public boolean guardarVehiculo(Vehiculo vehiculo) {
        return vehiculoDAO.insertar(vehiculo);
    }

    public boolean actualizarVehiculo(Vehiculo vehiculo) {
        return vehiculoDAO.actualizar(vehiculo);
    }

    public boolean eliminarVehiculo(int id) {
        return vehiculoDAO.eliminar(id);
    }

    public Vehiculo obtenerVehiculo(int id) {
        return vehiculoDAO.obtenerPorId(id);
    }

    // Clientes

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente obtenerCliente(int id) {
        return clienteDAO.obtenerPorId(id);
    }
}
