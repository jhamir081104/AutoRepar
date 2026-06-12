package com.autorepar.view;

import com.autorepar.controller.UsuarioController;
import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioForm extends JFrame {
    private Usuario usuarioActual;
    private UsuarioController usuarioController;
    private JTable tblUsuarios;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtEmail, txtPassword;
    private JComboBox<String> cbRol;
    private int selectedId = -1;
    
    private JButton btnGuardar, btnActualizar, btnEliminar;

    public UsuarioForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.usuarioController= new UsuarioController();
        initComponents();
        cargarUsuarios();
        aplicarPermisosPorRol();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Usuarios");
        setSize(1100, 700);
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
        
        JLabel lblTitle = new JLabel("Gestión de Usuarios del Sistema");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        top.add(lblTitle, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblRol = new JLabel("Tu rol: " + usuarioActual.getRol());
        lblRol.setFont(new Font("Arial", Font.BOLD, 12));
        lblRol.setForeground(usuarioActual.getRol().equals("ADMIN") ? new Color(0, 150, 0) : new Color(255, 100, 0));
        infoPanel.add(lblRol);
        
        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.addActionListener(e -> volverDashboard());
        infoPanel.add(btnVolver);
        
        top.add(infoPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        
        String[] columnas = {"ID", "Nombre", "Email", "Rol"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblUsuarios = new JTable(tableModel);
        tblUsuarios.setRowHeight(30);
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarUsuarioSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblUsuarios);
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registro de Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nombre completo:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtNombre, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 3;
        form.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtPassword, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Rol:"), gbc);
        cbRol = new JComboBox<>();
        cbRol.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 3;
        form.add(cbRol, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnGuardar = new JButton("💾 Guardar");
        btnActualizar = new JButton("🔄 Actualizar");
        btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnLimpiar = new JButton("🧹 Limpiar");

        btnGuardar.addActionListener(e -> guardarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        form.add(buttonPanel, gbc);

        return form;
    }

    private void aplicarPermisosPorRol() {
        String rol = usuarioActual.getRol();
        
        if (rol.equals("ADMIN")) {
            cbRol.removeAllItems();
            cbRol.addItem("ADMIN");
            cbRol.addItem("MECANICO");
            cbRol.addItem("RECEPCION");
            btnGuardar.setEnabled(true);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            setTitle("AutoRepar - Gestión de Usuarios [ADMIN - Acceso Total]");
            
        } else if (rol.equals("RECEPCION")) {
            cbRol.removeAllItems();
            cbRol.addItem("MECANICO");
            btnGuardar.setEnabled(true);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(true);
            setTitle("AutoRepar - Gestión de Usuarios [RECEPCION - Solo Mecánicos]");
            
            JOptionPane.showMessageDialog(this, 
                "⚠️ Como RECEPCIONISTA, solo puedes:\n" +
                "- Crear nuevos usuarios con rol MECANICO\n" +
                "- Eliminar usuarios con rol MECANICO\n" +
                "- No puedes editar ni modificar otros roles",
                "Permisos Limitados", JOptionPane.INFORMATION_MESSAGE);
                
        } else {
            btnGuardar.setEnabled(false);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            cbRol.setEnabled(false);
            txtNombre.setEnabled(false);
            txtEmail.setEnabled(false);
            txtPassword.setEnabled(false);
            setTitle("AutoRepar - Gestión de Usuarios [SIN ACCESO - Solo lectura]");
            
            JOptionPane.showMessageDialog(this, 
                "❌ No tienes permisos para gestionar usuarios.\n" +
                "Solo los ADMINISTRADORES y RECEPCIONISTAS pueden acceder a este módulo.",
                "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioController.listarTodos();
        tableModel.setRowCount(0);
        
        String rolActual = usuarioActual.getRol();
        
        for (Usuario u : usuarios) {
            if (rolActual.equals("RECEPCION") && !u.getRol().equals("MECANICO")) {
                continue;
            }
            tableModel.addRow(new Object[]{
                u.getId(), u.getNombre(), u.getEmail(), u.getRol()
            });
        }
    }

    private void cargarUsuarioSeleccionado() {
        int row = tblUsuarios.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) tableModel.getValueAt(row, 0);
            String rolSeleccionado = (String) tableModel.getValueAt(row, 3);
            
            txtNombre.setText((String) tableModel.getValueAt(row, 1));
            txtEmail.setText((String) tableModel.getValueAt(row, 2));
            txtPassword.setText("");
            
            if (usuarioActual.getRol().equals("RECEPCION")) {
                if (!rolSeleccionado.equals("MECANICO")) {
                    JOptionPane.showMessageDialog(this, 
                        "No puedes seleccionar usuarios que no sean MECANICOS",
                        "Permiso Denegado", JOptionPane.WARNING_MESSAGE);
                    tblUsuarios.clearSelection();
                    selectedId = -1;
                    limpiarFormulario();
                    return;
                }
                cbRol.removeAllItems();
                cbRol.addItem("MECANICO");
                cbRol.setSelectedItem("MECANICO");
                cbRol.setEnabled(false);
            } else {
                cbRol.setSelectedItem(rolSeleccionado);
                cbRol.setEnabled(true);
            }
        }
    }

    private void guardarUsuario() {
        if (validarCampos()) {
            String email = txtEmail.getText().trim();
            String rolSeleccionado = (String) cbRol.getSelectedItem();
            
            if (usuarioActual.getRol().equals("RECEPCION") && !rolSeleccionado.equals("MECANICO")) {
                JOptionPane.showMessageDialog(this, 
                    "Como RECEPCIONISTA, solo puedes crear usuarios con rol MECANICO",
                    "Permiso Denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (usuarioController.emailExiste(email)) {
                JOptionPane.showMessageDialog(this, "Este email ya está registrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setEmail(email);
            usuario.setPassword(txtPassword.getText().trim());
            usuario.setRol(rolSeleccionado);

            if (usuarioController.guardar(usuario)) {
                JOptionPane.showMessageDialog(this, "Usuario creado con éxito. ¡Ya puede iniciar sesión!");
                limpiarFormulario();
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarUsuario() {
        if (usuarioActual.getRol().equals("RECEPCION")) {
            JOptionPane.showMessageDialog(this, 
                "No tienes permiso para editar usuarios",
                "Permiso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar");
            return;
        }
        
        String nuevoEmail = txtEmail.getText().trim();
        String emailOriginal = (String) tableModel.getValueAt(tblUsuarios.getSelectedRow(), 2);
        
        if (!nuevoEmail.equals(emailOriginal) && usuarioController.emailExiste(nuevoEmail)) {
            JOptionPane.showMessageDialog(this, 
                "El email '" + nuevoEmail + "' ya está registrado por otro usuario",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (validarCampos()) {
            Usuario usuario = new Usuario();
            usuario.setId(selectedId);
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setEmail(nuevoEmail);
            usuario.setRol((String) cbRol.getSelectedItem());
            
            if (!txtPassword.getText().trim().isEmpty()) {
                usuario.setPassword(txtPassword.getText().trim());
            }

            if (usuarioController.actualizar(usuario)) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito");
                limpiarFormulario();
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar usuario. Verifique que el email no esté duplicado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarUsuario() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar");
            return;
        }
        
        if (selectedId == usuarioActual.getId()) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propio usuario", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String rolAEliminar = (String) tableModel.getValueAt(tblUsuarios.getSelectedRow(), 3);
        
        if (usuarioActual.getRol().equals("RECEPCION") && !rolAEliminar.equals("MECANICO")) {
            JOptionPane.showMessageDialog(this, 
                "Como RECEPCIONISTA, solo puedes eliminar usuarios con rol MECANICO",
                "Permiso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar al usuario: " + txtNombre.getText() + "?\nEsta acción es irreversible.", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioController.eliminar(selectedId)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado");
                limpiarFormulario();
                cargarUsuarios();
                selectedId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del usuario");
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el email");
            return false;
        }
        if (txtPassword.getText().trim().isEmpty() && selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Ingrese una contraseña para el nuevo usuario");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        if (usuarioActual.getRol().equals("ADMIN")) {
            cbRol.removeAllItems();
            cbRol.addItem("ADMIN");
            cbRol.addItem("MECANICO");
            cbRol.addItem("RECEPCION");
            cbRol.setSelectedIndex(0);
            cbRol.setEnabled(true);
        } else if (usuarioActual.getRol().equals("RECEPCION")) {
            cbRol.removeAllItems();
            cbRol.addItem("MECANICO");
            cbRol.setSelectedItem("MECANICO");
            cbRol.setEnabled(false);
        }
        selectedId = -1;
        tblUsuarios.clearSelection();
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}