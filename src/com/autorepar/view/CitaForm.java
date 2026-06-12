package com.autorepar.view;

import com.autorepar.dao.*;
import com.autorepar.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaForm extends JFrame {
    private Usuario usuarioActual;
    private CitaDAO citaDAO;
    private ClienteDAO clienteDAO;
    private VehiculoDAO vehiculoDAO;
    private UsuarioDAO usuarioDAO;
    private ServicioDAO servicioDAO;
    private JTable tblCitas;
    private DefaultTableModel tableModel;
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Vehiculo> cbVehiculo;
    private JComboBox<Usuario> cbMecanico;
    private JTextField txtFecha, txtHora;
    private JTextArea txtDescripcion;
    private JComboBox<String> cbEstado;
    private int selectedId = -1;
    private JCheckBox chkMostrarPagadas;

    public CitaForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.citaDAO = new CitaDAO();
        this.clienteDAO = new ClienteDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.servicioDAO = new ServicioDAO();
        initComponents();
        cargarCitas();
        cargarCombos();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Citas");
        setSize(1300, 800);
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
        
        // Panel superior con checkbox
        JPanel topCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkMostrarPagadas = new JCheckBox("Mostrar citas pagadas");
        chkMostrarPagadas.addActionListener(e -> cargarCitas());
        topCenter.add(chkMostrarPagadas);
        center.add(topCenter, BorderLayout.NORTH);
        
        // Tabla
        String[] columnas = {"ID", "Fecha", "Hora", "Cliente", "Vehículo", "Mecánico", "Estado", "Descripción"};
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
        cbCliente.setPreferredSize(new Dimension(250, 25));
        cbCliente.addActionListener(e -> actualizarVehiculosPorCliente());
        gbc.gridx = 1;
        form.add(cbCliente, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Vehículo:"), gbc);
        cbVehiculo = new JComboBox<>();
        cbVehiculo.setPreferredSize(new Dimension(250, 25));
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
        cbEstado = new JComboBox<>(new String[]{"PENDIENTE", "CONFIRMADA", "COMPLETADA"});
        cbEstado.setEnabled(false);
        gbc.gridx = 3;
        form.add(cbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextArea(3, 40);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(400, 60));
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        form.add(scrollDesc, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("💾 Guardar");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnPagarCita = new JButton("💰 Pagar Cita");
        JButton btnEliminar = new JButton("🗑️ Eliminar"); // NUEVO BOTÓN
        JButton btnLimpiar = new JButton("🧹 Limpiar");

        btnGuardar.addActionListener(e -> guardarCita());
        btnActualizar.addActionListener(e -> actualizarCita());
        btnPagarCita.addActionListener(e -> pagarCita());
        btnEliminar.addActionListener(e -> eliminarCita());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnPagarCita);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.CENTER;
        form.add(buttonPanel, gbc);

        return form;
    }

    private void cargarCombos() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        cbCliente.removeAllItems();
        for (Cliente c : clientes) {
            cbCliente.addItem(c);
        }
        
        List<Usuario> mecanicos = usuarioDAO.listarMecanicos();
        cbMecanico.removeAllItems();
        for (Usuario u : mecanicos) {
            cbMecanico.addItem(u);
        }
        
        if (mecanicos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ No hay mecánicos registrados.\nDebe crear usuarios con rol MECANICO en el módulo de Usuarios.",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarVehiculosPorCliente() {
        Cliente cliente = (Cliente) cbCliente.getSelectedItem();
        if (cliente != null) {
            List<Vehiculo> vehiculos = vehiculoDAO.listarPorCliente(cliente.getId());
            cbVehiculo.removeAllItems();
            for (Vehiculo v : vehiculos) {
                cbVehiculo.addItem(v);
            }
            
            if (vehiculos.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Este cliente no tiene vehículos registrados.\nDebe registrar un vehículo primero.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void cargarCitas() {
        List<Cita> todas = citaDAO.listarTodas();
        List<Cita> citasAMostrar = new ArrayList<>();
        
        for (Cita c : todas) {
            if (chkMostrarPagadas.isSelected()) {
                citasAMostrar.add(c);
            } else {
                if (!c.getEstado().equals("CANCELADA")) {
                    citasAMostrar.add(c);
                }
            }
        }
        
        tableModel.setRowCount(0);
        for (Cita c : citasAMostrar) {
            Cliente cliente = clienteDAO.obtenerPorId(c.getClienteId());
            Vehiculo vehiculo = vehiculoDAO.obtenerPorId(c.getVehiculoId());
            Usuario mecanico = usuarioDAO.obtenerPorId(c.getMecanicoId());
            
            String estadoMostrar = c.getEstado();
            if (estadoMostrar.equals("CANCELADA")) {
                estadoMostrar = "PAGADA";
            }
            
            tableModel.addRow(new Object[]{
                c.getId(), 
                c.getFecha().toString(), 
                c.getHora().toString(),
                cliente != null ? cliente.getNombreCompleto() : "N/A",
                vehiculo != null ? vehiculo.getDescripcion() : "N/A",
                mecanico != null ? mecanico.getNombre() : "N/A",
                estadoMostrar,
                c.getDescripcion() != null ? c.getDescripcion() : ""
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
            String estado = (String) tableModel.getValueAt(row, 6);
            String descripcion = (String) tableModel.getValueAt(row, 7);

            txtFecha.setText(fechaStr);
            txtHora.setText(horaStr);
            txtDescripcion.setText(descripcion);
            
            if (estado.equals("PAGADA")) {
                cbEstado.setSelectedItem("COMPLETADA");
                cbEstado.setEnabled(false);
                cbCliente.setEnabled(false);
                cbVehiculo.setEnabled(false);
                cbMecanico.setEnabled(false);
                txtFecha.setEnabled(false);
                txtHora.setEnabled(false);
                txtDescripcion.setEnabled(false);
            } else {
                cbEstado.setSelectedItem(estado);
                cbEstado.setEnabled(true);
                cbCliente.setEnabled(true);
                cbVehiculo.setEnabled(true);
                cbMecanico.setEnabled(true);
                txtFecha.setEnabled(true);
                txtHora.setEnabled(true);
                txtDescripcion.setEnabled(true);
            }

            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                Cliente c = cbCliente.getItemAt(i);
                if (c != null && c.getNombreCompleto().equals(clienteNombre)) {
                    cbCliente.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardarCita() {
        if (validarCampos()) {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
            Usuario mecanico = (Usuario) cbMecanico.getSelectedItem();
            
            if (cliente == null || vehiculo == null || mecanico == null) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Cita cita = new Cita();
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado((String) cbEstado.getSelectedItem());
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(cliente.getId());
            cita.setVehiculoId(vehiculo.getId());
            cita.setMecanicoId(mecanico.getId());

            if (citaDAO.verificarDisponibilidad(cita.getFecha(), cita.getHora(), cita.getMecanicoId())) {
                if (citaDAO.insertar(cita)) {
                    JOptionPane.showMessageDialog(this, "Cita registrada con éxito");
                    limpiarFormulario();
                    cargarCitas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar cita", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Horario no disponible para este mecánico", "Conflicto", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void actualizarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para actualizar");
            return;
        }
        
        String estadoActual = (String) tableModel.getValueAt(tblCitas.getSelectedRow(), 6);
        if (estadoActual.equals("PAGADA")) {
            JOptionPane.showMessageDialog(this, "No se puede modificar una cita ya pagada", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (validarCampos()) {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
            Usuario mecanico = (Usuario) cbMecanico.getSelectedItem();
            
            Cita cita = new Cita();
            cita.setId(selectedId);
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado((String) cbEstado.getSelectedItem());
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(cliente.getId());
            cita.setVehiculoId(vehiculo.getId());
            cita.setMecanicoId(mecanico.getId());

            if (citaDAO.actualizar(cita)) {
                JOptionPane.showMessageDialog(this, "Cita actualizada con éxito");
                limpiarFormulario();
                cargarCitas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // NUEVO MÉTODO: Eliminar cita permanentemente
    private void eliminarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para eliminar");
            return;
        }
        
        String estadoActual = (String) tableModel.getValueAt(tblCitas.getSelectedRow(), 6);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "⚠️ ELIMINAR CITA PERMANENTEMENTE\n\n" +
            "ID: " + selectedId + "\n" +
            "Cliente: " + tableModel.getValueAt(tblCitas.getSelectedRow(), 3) + "\n" +
            "Fecha: " + txtFecha.getText() + "\n" +
            "Hora: " + txtHora.getText() + "\n" +
            "Estado: " + estadoActual + "\n\n" +
            "Esta acción eliminará la cita de la base de datos.\n" +
            "¿Está seguro?",
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (citaDAO.eliminar(selectedId)) {
                JOptionPane.showMessageDialog(this, "✅ Cita eliminada permanentemente");
                limpiarFormulario();
                cargarCitas();
                selectedId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la cita", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pagarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para pagar");
            return;
        }
        
        String estadoActual = (String) tableModel.getValueAt(tblCitas.getSelectedRow(), 6);
        if (estadoActual.equals("PAGADA")) {
            JOptionPane.showMessageDialog(this, "Esta cita ya está pagada", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Registrar pago de esta cita?\n\n" +
            "Cliente: " + tableModel.getValueAt(tblCitas.getSelectedRow(), 3) + "\n" +
            "Vehículo: " + tableModel.getValueAt(tblCitas.getSelectedRow(), 4) + "\n" +
            "Descripción: " + txtDescripcion.getText() + "\n\n" +
            "La cita se marcará como PAGADA y se registrará en el historial.", 
            "Confirmar Pago", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
            Usuario mecanico = (Usuario) cbMecanico.getSelectedItem();
            
            Cita cita = new Cita();
            cita.setId(selectedId);
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado("CANCELADA");
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(cliente.getId());
            cita.setVehiculoId(vehiculo.getId());
            cita.setMecanicoId(mecanico.getId());
            
            if (citaDAO.actualizar(cita)) {
                registrarServicioConCostoObligatorio(cita);
                limpiarFormulario();
                cargarCitas();
                JOptionPane.showMessageDialog(this, "✅ Cita marcada como PAGADA", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registrarServicioConCostoObligatorio(Cita cita) {
        Cliente cliente = clienteDAO.obtenerPorId(cita.getClienteId());
        Vehiculo vehiculo = vehiculoDAO.obtenerPorId(cita.getVehiculoId());
        Usuario mecanico = usuarioDAO.obtenerPorId(cita.getMecanicoId());
        
        String clienteNombre = cliente != null ? cliente.getNombreCompleto() : "N/A";
        String vehiculoInfo = vehiculo != null ? vehiculo.getDescripcion() : "N/A";
        String mecanicoNombre = mecanico != null ? mecanico.getNombre() : "N/A";
        
        double costo = 0;
        boolean costoValido = false;
        
        while (!costoValido) {
            String costoStr = JOptionPane.showInputDialog(this,
                "═══════════════════════════════════════════════════════\n" +
                "        REGISTRO DE SERVICIO - CITA PAGADA\n" +
                "═══════════════════════════════════════════════════════\n\n" +
                "📋 DATOS DEL SERVICIO:\n" +
                "───────────────────────────────────────────────────────\n" +
                "  • Cliente:      " + clienteNombre + "\n" +
                "  • Vehículo:     " + vehiculoInfo + "\n" +
                "  • Mecánico:     " + mecanicoNombre + "\n" +
                "  • Fecha:        " + cita.getFecha().toString() + "\n" +
                "  • Descripción:  " + cita.getDescripcion() + "\n" +
                "───────────────────────────────────────────────────────\n\n" +
                "💰 Ingrese el COSTO del servicio (S/):\n" +
                "(Ejemplo: 150.00)\n\n" +
                "⚠️ Este campo es OBLIGATORIO.",
                "Registrar Servicio - AutoRepar",
                JOptionPane.QUESTION_MESSAGE);
            
            if (costoStr == null || costoStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "❌ El costo es OBLIGATORIO.\n" +
                    "Debe ingresar un monto válido para registrar el servicio.",
                    "Costo Obligatorio", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            try {
                costo = Double.parseDouble(costoStr.trim());
                if (costo < 0) {
                    JOptionPane.showMessageDialog(this,
                        "El costo no puede ser negativo.\nIngrese un valor válido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                costoValido = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "❌ Costo no válido.\n" +
                    "Ingrese un número válido (ejemplo: 150.00 o 150).\n" +
                    "No use letras ni caracteres especiales.",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        Servicio servicio = new Servicio();
        servicio.setFecha(cita.getFecha());
        servicio.setVehiculoId(cita.getVehiculoId());
        servicio.setMecanicoId(cita.getMecanicoId());
        servicio.setTipo("Servicio - Cita Pagada #" + cita.getId());
        servicio.setDescripcion(cita.getDescripcion());
        servicio.setCosto(costo);
        
        if (servicioDAO.insertar(servicio)) {
            JOptionPane.showMessageDialog(this,
                "✅ SERVICIO REGISTRADO EN EL HISTORIAL\n\n" +
                "   • Vehículo: " + vehiculoInfo + "\n" +
                "   • Costo: S/ " + String.format("%.2f", costo) + "\n" +
                "   • Estado: Cita Pagada\n\n" +
                "El servicio ha sido guardado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Error al registrar el servicio.\n" +
                "El pago se registró pero el servicio no se guardó.\n" +
                "Por favor, regístrelo manualmente en el historial.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (cbCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente");
            return false;
        }
        if (cbVehiculo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo");
            return false;
        }
        if (cbMecanico.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un mecánico");
            return false;
        }
        if (txtFecha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la fecha");
            return false;
        }
        if (txtHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la hora");
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
        cbEstado.setEnabled(true);
        cbCliente.setEnabled(true);
        cbVehiculo.setEnabled(true);
        cbMecanico.setEnabled(true);
        txtFecha.setEnabled(true);
        txtHora.setEnabled(true);
        txtDescripcion.setEnabled(true);
        if (cbCliente.getItemCount() > 0) {
            cbCliente.setSelectedIndex(0);
        }
        selectedId = -1;
        tblCitas.clearSelection();
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}