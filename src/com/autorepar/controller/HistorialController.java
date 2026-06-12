package com.autorepar.controller;

import com.autorepar.dao.ServicioDAO;
import com.autorepar.dao.UsuarioDAO;
import com.autorepar.dao.VehiculoDAO;
import com.autorepar.model.Servicio;
import com.autorepar.model.Usuario;
import com.autorepar.model.Vehiculo;
import com.autorepar.view.DashboardForm;
import com.autorepar.view.HistorialForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class HistorialController {

    private HistorialForm view;
    private Usuario usuarioActual;

    private ServicioDAO servicioDAO;
    private VehiculoDAO vehiculoDAO;
    private UsuarioDAO usuarioDAO;

    public HistorialController(HistorialForm view, Usuario usuarioActual) {
        this.view = view;
        this.usuarioActual = usuarioActual;

        servicioDAO = new ServicioDAO();
        vehiculoDAO = new VehiculoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    public void cargarVehiculos() {

        List<Vehiculo> vehiculos = vehiculoDAO.listarTodos();

        JComboBox<Vehiculo> cbVehiculo = view.getCbVehiculo();

        cbVehiculo.removeAllItems();

        for (Vehiculo v : vehiculos) {
            cbVehiculo.addItem(v);
        }

        if (!vehiculos.isEmpty()) {
            cargarHistorial();
        }
    }

    public void cargarHistorial() {

        Vehiculo vehiculo =
                (Vehiculo) view.getCbVehiculo().getSelectedItem();

        if (vehiculo == null) {
            return;
        }

        List<Servicio> servicios =
                servicioDAO.listarPorVehiculo(vehiculo.getId());

        DefaultTableModel model = view.getTableModel();

        model.setRowCount(0);

        for (Servicio s : servicios) {

            Usuario mecanico =
                    usuarioDAO.obtenerPorId(s.getMecanicoId());

            model.addRow(new Object[]{
                    s.getId(),
                    s.getFecha().toString(),
                    s.getTipo(),
                    s.getDescripcion(),
                    String.format("%.2f", s.getCosto()),
                    mecanico != null ? mecanico.getNombre() : "N/A"
            });
        }

        if (servicios.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Este vehículo no tiene servicios registrados"
            );
        }
    }

    public void buscarPorPlaca() {

        String placa = view.getTxtBuscarPlaca()
                .getText()
                .trim()
                .toUpperCase();

        if (placa.isEmpty()) {
            cargarVehiculos();
            return;
        }

        Vehiculo vehiculo =
                vehiculoDAO.buscarPorPlaca(placa);

        if (vehiculo != null) {

            JComboBox<Vehiculo> combo =
                    view.getCbVehiculo();

            for (int i = 0; i < combo.getItemCount(); i++) {

                Vehiculo v = combo.getItemAt(i);

                if (v.getPlaca().equalsIgnoreCase(placa)) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }

        } else {

            JOptionPane.showMessageDialog(
                    view,
                    "No se encontró vehículo con placa: " + placa
            );
        }
    }

    public void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        view.dispose();
    }
}
