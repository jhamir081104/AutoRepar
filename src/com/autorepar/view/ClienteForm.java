package com.autorepar.view;

import com.autorepar.dao.ClienteDAO;
import com.autorepar.model.Cliente;
import com.autorepar.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior
        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel central (tabla)
        JPanel centerPanel = crearCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel de formulario
        JPanel formPanel = crearFormPanel();
        mainPanel.add(formPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel crearTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("Gestión de Clientes");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        top.add(lblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarClientes();
            }
        });
        searchPanel.add(txtBuscar);

        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.addActionListener(e -> volverDashboard());
        searchPanel.add(btnVolver);

        top.add(searchPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        
        String[] columnas = {"ID", "Nombre", "Apellido", "Teléfono", "Email", "Dirección"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblClientes = new JTable(tableModel);
        tblClientes.setRowHeight(30);
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarClienteSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblClientes);
        center.add(scrollPane, BorderLayout.CENTER);
        
        return center;
    }

    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registro de Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(15);
        gbc.gridx = 1;
        form.add(txtNombre, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Apellido:"), gbc);
        txtApellido = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Teléfono:"), gbc);
        txtTelefono = new JTextField(15);
        gbc.gridx = 1;
        form.add(txtTelefono, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 3;
        form.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Dirección:"), gbc);
        txtDireccion = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        form.add(txtDireccion, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("💾 Guardar");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnLimpiar = new JButton("🧹 Limpiar");

        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
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