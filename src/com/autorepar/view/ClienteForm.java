package com.autorepar.view;

import com.autorepar.dao.ClienteDAO;
import com.autorepar.model.Cliente;
import com.autorepar.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class ClienteForm extends JFrame {
    private Usuario usuarioActual;
    private ClienteDAO clienteDAO;
    private JTable tblClientes;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JTextField txtNombre, txtApellido, txtTelefono, txtEmail, txtDireccion;
    private int selectedId = -1;

    public ClienteForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.clienteDAO = new ClienteDAO();
        initComponents();
        cargarClientes();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Clientes");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Sin margen superior para acoplar la cabecera
        mainPanel.setBackground(new Color(242, 244, 247)); 

        // Panel superior (Cabecera Azul Ejecutivo con Buscar)
        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Contenedor intermedio para mantener los márgenes de la tabla y formulario
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        contentPanel.setOpaque(false);

        // Panel central (tabla)
        JPanel centerPanel = crearCenterPanel();
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel de formulario
        JPanel formPanel = crearFormPanel();
        contentPanel.add(formPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel crearTopPanel() {
        // Cabecera Azul Ejecutivo (#0b2240)
        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(new Color(11, 34, 64)); 
        top.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); 

        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Título principal (Izquierda)
        JLabel lblTitle = new JLabel("Gestión de Clientes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.anchor = GridBagConstraints.WEST;
        top.add(lblTitle, gbc);

        // 2. Panel de Búsqueda y Botón Volver (Derecha)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        // Componentes de búsqueda
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBuscar.setForeground(Color.WHITE); 
        actionsPanel.add(lblBuscar);
        
        txtBuscar = new JTextField(20); // Tamaño 20 como tu código original
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarClientes();
            }
        });
        actionsPanel.add(txtBuscar);

        // Botón Volver al Dashboard
        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(new Color(50, 60, 75));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 215, 225), 1),
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
        actionsPanel.add(btnVolver);

        // Agregar el contenedor de acciones a la derecha de la cabecera
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        top.add(actionsPanel, gbc);

        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(242, 244, 247)); 
        
        String[] columnas = {"ID", "Nombre", "Apellido", "Teléfono", "Email", "Dirección"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblClientes = new JTable(tableModel);
        tblClientes.setRowHeight(35); 
        tblClientes.getTableHeader().setOpaque(true); 
        tblClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblClientes.getTableHeader().setBackground(new Color(11, 34, 64)); 
        tblClientes.getTableHeader().setForeground(Color.DARK_GRAY); 
    
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.setShowGrid(true);
        tblClientes.setShowHorizontalLines(true);
        tblClientes.setShowVerticalLines(true);
        tblClientes.setGridColor(new Color(205, 215, 225));
   
        tblClientes.setSelectionBackground(new Color(18, 38, 68, 40));
        tblClientes.setSelectionForeground(Color.BLACK);
        
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarClienteSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(11, 34, 64), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }
   
    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(11, 34, 64)); 
        Border lineaBlanca = BorderFactory.createLineBorder(Color.WHITE, 1);
        TitledBorder tituloBorde = BorderFactory.createTitledBorder(
                lineaBlanca, 
                "Registro de Cliente", 
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

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(labelFont);
        lblNombre.setForeground(labelColor);
        form.add(lblNombre, gbc);
        
        txtNombre = new JTextField(15);
        gbc.gridx = 1;
        form.add(txtNombre, gbc);

        gbc.gridx = 2;
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setFont(labelFont);
        lblApellido.setForeground(labelColor);
        form.add(lblApellido, gbc);
        
        txtApellido = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(labelFont);
        lblTelefono.setForeground(labelColor);
        form.add(lblTelefono, gbc);
        
        txtTelefono = new JTextField(15);
        gbc.gridx = 1;
        form.add(txtTelefono, gbc);

        gbc.gridx = 2;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(labelColor);
        form.add(lblEmail, gbc);
        
        txtEmail = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setFont(labelFont);
        lblDireccion.setForeground(labelColor);
        form.add(lblDireccion, gbc);
        
        txtDireccion = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        form.add(txtDireccion, gbc);

        // PANEL DE BOTONES ACCIONES
      
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false); 
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        // Color de fuente: Azul Ejecutivo para todos los botones
        Color azulEjecutivo = new Color(11, 34, 64);
        Font botonFont = new Font("Segoe UI", Font.BOLD, 13);

        // CONFIGURACIÓN DE FONDOS INTENSOS CON LETRA OSCURA
        // Guardar: Verde intenso/brillante (garantiza lectura del texto oscuro)
        btnGuardar.setBackground(Color.GREEN);
        btnGuardar.setForeground(azulEjecutivo);

        // Actualizar: Azul vivo / Cyan brillante
        btnActualizar.setBackground(Color.BLUE);
        btnActualizar.setForeground(azulEjecutivo);

        // Eliminar: Rojo coral encendido / fuerte
        btnEliminar.setBackground(Color.RED);
        btnEliminar.setForeground(azulEjecutivo);

        // Limpiar: Blanco puro
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setForeground(azulEjecutivo);

        // Aplicar propiedades estructurales y bordes en el bucle
        JButton[] botones = {btnGuardar, btnActualizar, btnEliminar, btnLimpiar};
        for (JButton btn : botones) {
            btn.setFont(botonFont);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(205, 215, 225), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
            buttonPanel.add(btn);
        }

        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarFormulario()); 
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        form.add(buttonPanel, gbc);

        return form;
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        tableModel.setRowCount(0);
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNombre(), c.getApellido(),
                c.getTelefono(), c.getEmail(), c.getDireccion()
            });
        }
    }

    private void buscarClientes() {
        String texto = txtBuscar.getText().trim();
        List<Cliente> clientes;
        if (texto.isEmpty()) {
            clientes = clienteDAO.listarTodos();
        } else {
            clientes = clienteDAO.buscar(texto);
        }
        tableModel.setRowCount(0);
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNombre(), c.getApellido(),
                c.getTelefono(), c.getEmail(), c.getDireccion()
            });
        }
    }

    private void cargarClienteSeleccionado() {
        int row = tblClientes.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) tableModel.getValueAt(row, 0);
            txtNombre.setText((String) tableModel.getValueAt(row, 1));
            txtApellido.setText((String) tableModel.getValueAt(row, 2));
            txtTelefono.setText((String) tableModel.getValueAt(row, 3));
            txtEmail.setText((String) tableModel.getValueAt(row, 4));
            txtDireccion.setText((String) tableModel.getValueAt(row, 5));
        }
    }

    private void guardarCliente() {
        if (validarCampos()) {
            Cliente cliente = new Cliente();
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setApellido(txtApellido.getText().trim());
            cliente.setTelefono(txtTelefono.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setDireccion(txtDireccion.getText().trim());

            if (clienteDAO.insertar(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente registrado con éxito");
                limpiarFormulario();
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarCliente() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para actualizar");
            return;
        }
        if (validarCampos()) {
            Cliente cliente = new Cliente();
            cliente.setId(selectedId);
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setApellido(txtApellido.getText().trim());
            cliente.setTelefono(txtTelefono.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setDireccion(txtDireccion.getText().trim());

            if (clienteDAO.actualizar(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado con éxito");
                limpiarFormulario();
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCliente() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(selectedId)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado");
                limpiarFormulario();
                cargarClientes();
                selectedId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre");
            return false;
        }
        if (txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el apellido");
            return false;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el teléfono");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        selectedId = -1;
        tblClientes.clearSelection();
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}