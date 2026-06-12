package com.autorepar.view;

import com.autorepar.controller.CitaController;
import com.autorepar.dao.*;
import com.autorepar.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CitaForm extends JFrame {
    private Usuario usuarioActual;
    private CitaController citaController;
    private JTable tblCitas;
    private DefaultTableModel tableModel;
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Vehiculo> cbVehiculo;
    private JComboBox<Usuario> cbMecanico;
    private JTextField txtFecha, txtHora, txtDescripcion;
    private JComboBox<String> cbEstado;
    private int selectedId = -1;

    public CitaForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.citaController = new CitaController();
        initComponents();
        cargarCitas();
        cargarCombos();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Citas");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = crearCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel formPanel = crearFormPanel();
        mainPanel.add(formPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel crearTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Gestión de Citas");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        top.add(lblTitle, BorderLayout.WEST);

        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.addActionListener(e -> volverDashboard());
        top.add(btnVolver, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        
        String[] columnas = {"ID", "Fecha", "Hora", "Cliente", "Vehículo", "Mecánico", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblCitas = new JTable(tableModel);
        tblCitas.setRowHeight(30);
        tblCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCitaSeleccionada();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblCitas);
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Programar Cita"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Cliente:"), gbc);
        cbCliente = new JComboBox<>();
        cbCliente.setPreferredSize(new Dimension(200, 25));
        cbCliente.addActionListener(e -> actualizarVehiculosPorCliente());
        gbc.gridx = 1;
        form.add(cbCliente, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Vehículo:"), gbc);
        cbVehiculo = new JComboBox<>();
        cbVehiculo.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 3;
        form.add(cbVehiculo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Mecánico:"), gbc);
        cbMecanico = new JComboBox<>();
        cbMecanico.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        form.add(cbMecanico, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
        txtFecha = new JTextField(LocalDate.now().toString(), 12);
        gbc.gridx = 3;
        form.add(txtFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Hora (HH:MM):"), gbc);
        txtHora = new JTextField("09:00", 8);
        gbc.gridx = 1;
        form.add(txtHora, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Estado:"), gbc);
        cbEstado = new JComboBox<>(new String[]{"PENDIENTE", "CONFIRMADA", "COMPLETADA", "CANCELADA"});
        gbc.gridx = 3;
        form.add(cbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        form.add(txtDescripcion, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("💾 Guardar");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnCancelar = new JButton("❌ Cancelar Cita");
        JButton btnLimpiar = new JButton("🧹 Limpiar");

        btnGuardar.addActionListener(e -> guardarCita());
        btnActualizar.addActionListener(e -> actualizarCita());
        btnCancelar.addActionListener(e -> cancelarCita());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnLimpiar);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        form.add(buttonPanel, gbc);

        return form;
    }

    private void cargarCombos() {
        List<Cliente> clientes = citaController.listarClientes();
        cbCliente.removeAllItems();
        for (Cliente c : clientes) {
            cbCliente.addItem(c);
        }

        List<Usuario> mecanicos = citaController.listarMecanicos();
        cbMecanico.removeAllItems();
        for (Usuario u : mecanicos) {
            cbMecanico.addItem(u);
        }
        if (mecanicos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay mecánicos registrados. Agregue mecánicos en la BD.");
        }
    }

    private void actualizarVehiculosPorCliente() {
        Cliente cliente = (Cliente) cbCliente.getSelectedItem();
        if (cliente != null) {
            List<Vehiculo> vehiculos = citaController.listarVehiculosPorCliente(cliente.getId());
            cbVehiculo.removeAllItems();
            for (Vehiculo v : vehiculos) {
                cbVehiculo.addItem(v);
            }
        }
    }

    private void cargarCitas() {
        List<Cita> citas = citaController.listarCitas();
        tableModel.setRowCount(0);
        for (Cita c : citas) {
            Cliente cliente = citaController.obtenerCliente(c.getClienteId());
            Vehiculo vehiculo = citaController.obtenerVehiculo(c.getVehiculoId());
            Usuario mecanico = citaController.obtenerUsuario(c.getMecanicoId());
            
            tableModel.addRow(new Object[]{
                c.getId(), c.getFecha().toString(), c.getHora().toString(),
                cliente != null ? cliente.getNombreCompleto() : "N/A",
                vehiculo != null ? vehiculo.getDescripcion() : "N/A",
                mecanico != null ? mecanico.getNombre() : "N/A",
                c.getEstado()
            });
        }
    }

    private void cargarCitaSeleccionada() {
        int row = tblCitas.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) tableModel.getValueAt(row, 0);
            String fechaStr = (String) tableModel.getValueAt(row, 1);
            String horaStr = (String) tableModel.getValueAt(row, 2);
            String clienteNombre = (String) tableModel.getValueAt(row, 3);
            String mecanicoNombre = (String) tableModel.getValueAt(row, 5);
            String estado = (String) tableModel.getValueAt(row, 6);

            txtFecha.setText(fechaStr);
            txtHora.setText(horaStr);
            cbEstado.setSelectedItem(estado);

            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                if (cbCliente.getItemAt(i).getNombreCompleto().equals(clienteNombre)) {
                    cbCliente.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < cbMecanico.getItemCount(); i++) {
                if (cbMecanico.getItemAt(i).getNombre().equals(mecanicoNombre)) {
                    cbMecanico.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardarCita() {
        if (validarCampos()) {
            Cita cita = new Cita();
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado((String) cbEstado.getSelectedItem());
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(((Cliente) cbCliente.getSelectedItem()).getId());
            cita.setVehiculoId(((Vehiculo) cbVehiculo.getSelectedItem()).getId());
            cita.setMecanicoId(((Usuario) cbMecanico.getSelectedItem()).getId());

            if (citaController.guardar(cita)) {
                if (citaController.guardar(cita)) {
                    JOptionPane.showMessageDialog(this, "Cita registrada");
                    limpiarFormulario();
                    cargarCitas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Horario no disponible para este mecánico", "Conflicto", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void actualizarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita");
            return;
        }
        if (validarCampos()) {
            Cita cita = new Cita();
            cita.setId(selectedId);
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado((String) cbEstado.getSelectedItem());
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(((Cliente) cbCliente.getSelectedItem()).getId());
            cita.setVehiculoId(((Vehiculo) cbVehiculo.getSelectedItem()).getId());
            cita.setMecanicoId(((Usuario) cbMecanico.getSelectedItem()).getId());

            if (citaController.actualizar(cita)) {
                JOptionPane.showMessageDialog(this, "Cita actualizada");
                limpiarFormulario();
                cargarCitas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para cancelar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Cancelar esta cita?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (citaController.cancelar(selectedId)) {
                JOptionPane.showMessageDialog(this, "Cita cancelada");
                limpiarFormulario();
                cargarCitas();
                selectedId = -1;
            }
            
        }
    }

    private boolean validarCampos() {
        if (cbCliente.getSelectedItem() == null || cbVehiculo.getSelectedItem() == null || cbMecanico.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return false;
        }
        try {
            LocalDate.parse(txtFecha.getText().trim());
            LocalTime.parse(txtHora.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha (YYYY-MM-DD) u hora (HH:MM) incorrecto");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText("09:00");
        txtDescripcion.setText("");
        cbEstado.setSelectedIndex(0);
        selectedId = -1;
        tblCitas.clearSelection();
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}