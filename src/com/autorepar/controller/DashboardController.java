package com.autorepar.controller;

import com.autorepar.dao.CitaDAO;
import com.autorepar.model.Cita;
import com.autorepar.model.Usuario;
import com.autorepar.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    private DashboardForm view;
    private Usuario usuarioActual;
    private CitaDAO citaDAO;

    public DashboardController(DashboardForm view, Usuario usuarioActual) {
        this.view = view;
        this.usuarioActual = usuarioActual;
        this.citaDAO = new CitaDAO();
    }

    public void cargarCitasDelDia() {
        try {

            List<Cita> citas = citaDAO.listarPorFecha(LocalDate.now());

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            for (Cita cita : citas) {

                model.addRow(new Object[]{
                        cita.getId(),
                        cita.getHora().toString(),
                        cita.getClienteId(),
                        cita.getVehiculoId(),
                        cita.getMecanicoId(),
                        cita.getEstado(),
                        cita.getDescripcion() != null
                                ? cita.getDescripcion()
                                : ""
                });
            }

            view.getLblCitasCount()
                    .setText("Total de citas hoy: " + citas.size());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    view,
                    "Error al cargar citas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            view.getLblCitasCount()
                    .setText("Error al cargar citas");
        }
    }

    public void abrirClientes() {
        new ClienteForm(usuarioActual).setVisible(true);
        view.dispose();
    }

    public void abrirVehiculos() {
        new VehiculoForm(usuarioActual).setVisible(true);
        view.dispose();
    }

    public void abrirCitas() {
        new CitaForm(usuarioActual).setVisible(true);
        view.dispose();
    }

    public void abrirHistorial() {
        new HistorialForm(usuarioActual).setVisible(true);
        view.dispose();
    }

    public void abrirReportes() {
        new ReporteForm(usuarioActual).setVisible(true);
        view.dispose();
    }

    public void abrirUsuarios() {
        new UsuarioForm(usuarioActual).setVisible(true);
        view.dispose();
    }

    public void cerrarSesion() {

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            view.dispose();
        }
    }
}