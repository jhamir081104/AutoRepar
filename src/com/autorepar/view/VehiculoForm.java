package com.autorepar.view;

import com.autorepar.dao.ClienteDAO;
import com.autorepar.dao.VehiculoDAO;
import com.autorepar.model.Cliente;
import com.autorepar.model.Usuario;
import com.autorepar.model.Vehiculo;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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

    // Colores corporativos de AutoRepar (Consistentes)
    private final Color AZUL_EJECUTIVO = new Color(11, 34, 64); // Actualizado al azul noche profundo de las cabeceras
    private final Color GRIS_PLATINO = new Color(242, 244, 247);
    private final Color GRIS_BORDE = new Color(205, 215, 225);

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
        mainPanel.setBackground(GRIS_PLATINO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Eliminado margen superior para acoplar la cabecera

        // Cabecera Corporativa Azul de borde a borde
        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Contenedor intermedio para mantener los márgenes de las tablas y el formulario
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        contentPanel.setOpaque(false);

        JPanel centerPanel = crearCenterPanel();
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel formPanel = crearFormPanel();
        contentPanel.add(formPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel crearTopPanel() {
        // Rediseño de la cabecera usando GridBagLayout para máxima respuesta y alineación precisa
        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(AZUL_EJECUTIVO);
        top.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); // Padding elegante

        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Título principal (Izquierda)
        JLabel lblTitle = new JLabel("Gestión de Vehículos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.anchor = GridBagConstraints.WEST;
        top.add(lblTitle, gbc);

        // 2. Botón Volver al Dashboard (Derecha)
        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(new Color(50, 60, 75));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        
        btnVolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVolver.setBackground(new Color(210, 228, 252));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnVolver.setBackground(Color.WHITE);
            }
        });
        btnVolver.addActionListener(e -> volverDashboard());
        
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        top.add(btnVolver, gbc);

        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(GRIS_PLATINO);
        
        String[] columnas = {"ID", "Placa", "Marca", "Modelo", "Año", "Color", "Cliente"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblVehiculos = new JTable(tableModel);
        tblVehiculos.setRowHeight(35); 
        
        // Cabecera de la tabla corregida a texto blanco
        tblVehiculos.getTableHeader().setOpaque(true);
        tblVehiculos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblVehiculos.getTableHeader().setBackground(AZUL_EJECUTIVO);
        tblVehiculos.getTableHeader().setForeground(Color.DARK_GRAY); // Cambiado a Blanco
        tblVehiculos.getTableHeader().setReorderingAllowed(false);
        
        tblVehiculos.setShowGrid(true);
        tblVehiculos.setGridColor(GRIS_BORDE);
        tblVehiculos.setSelectionBackground(new Color(11, 34, 64, 40));
        tblVehiculos.setSelectionForeground(Color.BLACK);

        tblVehiculos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarVehiculoSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblVehiculos);
        scrollPane.setBorder(BorderFactory.createLineBorder(AZUL_EJECUTIVO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AZUL_EJECUTIVO);
        
        Border lineaBlanca = BorderFactory.createLineBorder(Color.WHITE, 1);
        TitledBorder tituloBorde = BorderFactory.createTitledBorder(
                lineaBlanca, 
                "Registro de Vehículo", 
                TitledBorder.LEFT, 
                TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 13), 
                Color.WHITE
        );
        form.setBorder(tituloBorde);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        Color labelColor = Color.WHITE;

        // Fila 0: Cliente y Placa
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(labelFont);
        lblCliente.setForeground(labelColor);
        form.add(lblCliente, gbc);

        cbCliente = new JComboBox<>();
        cbCliente.setPreferredSize(new Dimension(250, 25));
        gbc.gridx = 1;
        form.add(cbCliente, gbc);

        gbc.gridx = 2;
        JLabel lblPlaca = new JLabel("Placa:");
        lblPlaca.setFont(labelFont);
        lblPlaca.setForeground(labelColor);
        form.add(lblPlaca, gbc);

        txtPlaca = new JTextField(12);
        gbc.gridx = 3;
        form.add(txtPlaca, gbc);

        // Fila 1: Marca y Modelo
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblMarca = new JLabel("Marca:");
        lblMarca.setFont(labelFont);
        lblMarca.setForeground(labelColor);
        form.add(lblMarca, gbc);

        txtMarca = new JTextField(15);
        gbc.gridx = 1;
        form.add(txtMarca, gbc);

        gbc.gridx = 2;
        JLabel lblModelo = new JLabel("Modelo:");
        lblModelo.setFont(labelFont);
        lblModelo.setForeground(labelColor);
        form.add(lblModelo, gbc);

        txtModelo = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtModelo, gbc);

        // Fila 2: Año y Color
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblAnio = new JLabel("Año:");
        lblAnio.setFont(labelFont);
        lblAnio.setForeground(labelColor);
        form.add(lblAnio, gbc);

        txtAnio = new JTextField(12);
        gbc.gridx = 1;
        form.add(txtAnio, gbc);

        gbc.gridx = 2;
        JLabel lblColor = new JLabel("Color:");
        lblColor.setFont(labelFont);
        lblColor.setForeground(labelColor);
        form.add(lblColor, gbc);

        txtColor = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtColor, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false); 
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        // --- ESTILIZADO INDIVIDUAL SEGÚN TU IMAGEN DE REFERENCIA ---
        Font botonFont = new Font("Segoe UI", Font.BOLD, 13);
        Border paddingInterno = BorderFactory.createEmptyBorder(8, 16, 8, 16);

        // 1. Guardar (Fondo Blanco, Texto Azul Ejecutivo, Borde Verde Línea)
        btnGuardar.setFont(botonFont);
        btnGuardar.setBackground(Color.WHITE);
        btnGuardar.setForeground(AZUL_EJECUTIVO);
        btnGuardar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                paddingInterno
        ));

        // 2. Actualizar (Fondo Blanco, Texto Azul Ejecutivo, Borde Azul Línea)
        btnActualizar.setFont(botonFont);
        btnActualizar.setBackground(Color.WHITE);
        btnActualizar.setForeground(AZUL_EJECUTIVO);
        btnActualizar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                paddingInterno
        ));

        // 3. Eliminar (Fondo Blanco, Texto Azul Ejecutivo, Borde Rojo Línea)
        btnEliminar.setFont(botonFont);
        btnEliminar.setBackground(Color.WHITE);
        btnEliminar.setForeground(AZUL_EJECUTIVO);
        btnEliminar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                paddingInterno
        ));

        // 4. Limpiar (Fondo Blanco, Texto Azul Ejecutivo, Borde Gris Estándar)
        btnLimpiar.setFont(botonFont);
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setForeground(AZUL_EJECUTIVO);
        btnLimpiar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                paddingInterno
        ));

        // Propiedades estructurales comunes aplicadas en lote
        JButton[] botones = {btnGuardar, btnActualizar, btnEliminar, btnLimpiar};
        for (JButton btn : botones) {
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Reestablecemos el comportamiento del MouseListener original para mantener tu fondo blanco
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(245, 248, 255)); // Cambio sutil al pasar el mouse
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(Color.WHITE);
                }
            });
            buttonPanel.add(btn);
        }

        btnGuardar.addActionListener(e -> guardarVehiculo());
        btnActualizar.addActionListener(e -> actualizarVehiculo());
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        form.add(buttonPanel, gbc);

        return form;
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        cbCliente.removeAllItems();
        for (Cliente c : clientes) {
            cbCliente.addItem(c);  
        }
        
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