package com.autorepar.view;

import com.autorepar.dao.ClienteDAO;
import com.autorepar.dao.VehiculoDAO;
import com.autorepar.model.Cliente;
import com.autorepar.model.Usuario;
import com.autorepar.model.Vehiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VehiculoForm extends JFrame {
    private Usuario usuarioActual;
    private VehiculoDAO vehiculoDAO;
    private ClienteDAO clienteDAO;
    private JTable tblVehiculos;
    private DefaultTableModel tableModel;
    private JComboBox<Cliente> cbCliente;
    private JTextField txtPlaca, txtMarca, txtModelo, txtAnio, txtColor;
    private int selectedId = -1;

    public VehiculoForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.vehiculoDAO = new VehiculoDAO();
        this.clienteDAO = new ClienteDAO();
        initComponents();
        cargarVehiculos();
        cargarClientes();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Vehículos");
        setSize(1200, 700);
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
        
        JLabel lblTitle = new JLabel("Gestión de Vehículos");
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
        
        String[] columnas = {"ID", "Placa", "Marca", "Modelo", "Año", "Color", "Cliente"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblVehiculos = new JTable(tableModel);
        tblVehiculos.setRowHeight(30);
        tblVehiculos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarVehiculoSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblVehiculos);
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registro de Vehículo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Cliente:"), gbc);
        cbCliente = new JComboBox<>();
        cbCliente.setPreferredSize(new Dimension(250, 25));
        gbc.gridx = 1;
        form.add(cbCliente, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Placa:"), gbc);
        txtPlaca = new JTextField(10);
        gbc.gridx = 3;
        form.add(txtPlaca, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Marca:"), gbc);
        txtMarca = new JTextField(15);
        gbc.gridx = 1;
        form.add(txtMarca, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Modelo:"), gbc);
        txtModelo = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtModelo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Año:"), gbc);
        txtAnio = new JTextField(6);
        gbc.gridx = 1;
        form.add(txtAnio, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Color:"), gbc);
        txtColor = new JTextField(10);
        gbc.gridx = 3;
        form.add(txtColor, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("💾 Guardar");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnLimpiar = new JButton("🧹 Limpiar");

        btnGuardar.addActionListener(e -> guardarVehiculo());
        btnActualizar.addActionListener(e -> actualizarVehiculo());
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        form.add(buttonPanel, gbc);

        return form;
    }

    // ============ MÉTODO CORREGIDO ============
    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        cbCliente.removeAllItems();
        for (Cliente c : clientes) {
            cbCliente.addItem(c);  // Ahora mostrará el nombre gracias a toString()
        }
        
        // Si no hay clientes, mostrar mensaje
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay clientes registrados. Debe registrar un cliente primero.",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarVehiculos() {
        List<Vehiculo> vehiculos = vehiculoDAO.listarTodos();
        tableModel.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            Cliente c = clienteDAO.obtenerPorId(v.getClienteId());
            String clienteNombre = c != null ? c.getNombreCompleto() : "N/A";
            tableModel.addRow(new Object[]{
                v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                v.getAnio(), v.getColor(), clienteNombre
            });
        }
    }

    private void cargarVehiculoSeleccionado() {
        int row = tblVehiculos.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) tableModel.getValueAt(row, 0);
            txtPlaca.setText((String) tableModel.getValueAt(row, 1));
            txtMarca.setText((String) tableModel.getValueAt(row, 2));
            txtModelo.setText((String) tableModel.getValueAt(row, 3));
            txtAnio.setText(String.valueOf(tableModel.getValueAt(row, 4)));
            txtColor.setText((String) tableModel.getValueAt(row, 5));
            
            String clienteNombre = (String) tableModel.getValueAt(row, 6);
            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                Cliente cliente = cbCliente.getItemAt(i);
                if (cliente != null && cliente.getNombreCompleto().equals(clienteNombre)) {
                    cbCliente.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardarVehiculo() {
        if (validarCampos()) {
            Cliente clienteSeleccionado = (Cliente) cbCliente.getSelectedItem();
            if (clienteSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Vehiculo v = new Vehiculo();
            v.setPlaca(txtPlaca.getText().trim().toUpperCase());
            v.setMarca(txtMarca.getText().trim());
            v.setModelo(txtModelo.getText().trim());
            v.setAnio(Integer.parseInt(txtAnio.getText().trim()));
            v.setColor(txtColor.getText().trim());
            v.setClienteId(clienteSeleccionado.getId());

            if (vehiculoDAO.insertar(v)) {
                JOptionPane.showMessageDialog(this, "Vehículo registrado con éxito");
                limpiarFormulario();
                cargarVehiculos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar vehículo (verifique placa única)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarVehiculo() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo");
            return;
        }
        if (validarCampos()) {
            Cliente clienteSeleccionado = (Cliente) cbCliente.getSelectedItem();
            if (clienteSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Vehiculo v = new Vehiculo();
            v.setId(selectedId);
            v.setPlaca(txtPlaca.getText().trim().toUpperCase());
            v.setMarca(txtMarca.getText().trim());
            v.setModelo(txtModelo.getText().trim());
            v.setAnio(Integer.parseInt(txtAnio.getText().trim()));
            v.setColor(txtColor.getText().trim());
            v.setClienteId(clienteSeleccionado.getId());

            if (vehiculoDAO.actualizar(v)) {
                JOptionPane.showMessageDialog(this, "Vehículo actualizado");
                limpiarFormulario();
                cargarVehiculos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarVehiculo() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar vehículo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (vehiculoDAO.eliminar(selectedId)) {
                JOptionPane.showMessageDialog(this, "Vehículo eliminado");
                limpiarFormulario();
                cargarVehiculos();
                selectedId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarCampos() {
        if (cbCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente");
            return false;
        }
        if (txtPlaca.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la placa");
            return false;
        }
        if (txtMarca.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la marca");
            return false;
        }
        if (txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el modelo");
            return false;
        }
        if (txtAnio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el año");
            return false;
        }
        try {
            Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un año válido");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtAnio.setText("");
        txtColor.setText("");
        if (cbCliente.getItemCount() > 0) {
            cbCliente.setSelectedIndex(0);
        }
        selectedId = -1;
        tblVehiculos.clearSelection();
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}