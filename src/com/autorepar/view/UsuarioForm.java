package com.autorepar.view;

import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class UsuarioForm extends JFrame {
    private Usuario usuarioActual;
    private UsuarioDAO usuarioDAO;
    private JTable tblUsuarios;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private int selectedId = -1;
    
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    // PALETA DE COLORES (Consistente con Gestión de Vehículos)
    private final Color AZUL_EJECUTIVO = new Color(16, 44, 87);
    private final Color GRIS_FONDO = new Color(245, 247, 248);
    private final Color GRIS_BORDE = new Color(200, 200, 200);

    public UsuarioForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarUsuarios();
        aplicarPermisosPorRol();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Usuarios");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con el color gris claro de fondo general
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(GRIS_FONDO);
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
        JPanel top = new JPanel(new BorderLayout(15, 10));
        top.setBackground(AZUL_EJECUTIVO);
        top.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // TÍTULO EN BLANCO
        JLabel lblTitle = new JLabel("Gestión de Usuarios del Sistema");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        top.add(lblTitle, BorderLayout.WEST);

        // PANEL DE INFORMACIÓN Y RETORNO
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        infoPanel.setBackground(AZUL_EJECUTIVO);
        
        JLabel lblRol = new JLabel("Tu rol: " + usuarioActual.getRol());
        lblRol.setFont(new Font("Arial", Font.BOLD, 14));
        lblRol.setForeground(usuarioActual.getRol().equals("ADMIN") ? new Color(40, 167, 69) : new Color(255, 193, 7));
        infoPanel.add(lblRol);
        
        // BOTÓN VOLVER ESTILIZADO
        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 13));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(AZUL_EJECUTIVO);
        btnVolver.setOpaque(true);
        btnVolver.setContentAreaFilled(true);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnVolver.addActionListener(e -> volverDashboard());
        infoPanel.add(btnVolver);
        
        top.add(infoPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(GRIS_FONDO);
        
        String[] columnas = {"ID", "Nombre", "Email", "Rol"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblUsuarios = new JTable(tableModel);
        tblUsuarios.setRowHeight(32);
        tblUsuarios.setFont(new Font("Arial", Font.PLAIN, 14));
        tblUsuarios.setSelectionBackground(new Color(232, 240, 254));
        tblUsuarios.setSelectionForeground(Color.BLACK);
        tblUsuarios.setShowGrid(true);
        tblUsuarios.setGridColor(new Color(230, 230, 230));

        // Personalización de Cabecera de Tabla
        JTableHeader header = tblUsuarios.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(AZUL_EJECUTIVO);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AZUL_EJECUTIVO));
        
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarUsuarioSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRIS_BORDE, 1));
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel crearFormPanel() {
        // Contenedor principal del formulario con fondo oscuro uniforme
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AZUL_EJECUTIVO);
        
        var titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 130, 180), 1), 
                " Registro de Usuario "
        );
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        titledBorder.setTitleColor(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fuentes y colores de etiquetas
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font inputFont = new Font("Arial", Font.PLAIN, 14);

        // Fila 1: Nombre Completo
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre completo:");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(labelFont);
        form.add(lblNombre, gbc);
        
        txtNombre = new JTextField(20);
        txtNombre.setFont(inputFont);
        gbc.gridx = 1;
        form.add(txtNombre, gbc);

        // Fila 1: Email
        gbc.gridx = 2;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setFont(labelFont);
        form.add(lblEmail, gbc);
        
        txtEmail = new JTextField(20);
        txtEmail.setFont(inputFont);
        gbc.gridx = 3;
        form.add(txtEmail, gbc);

        // Fila 2: Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(labelFont);
        form.add(lblPass, gbc);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setEchoChar('●');
        txtPassword.setFont(inputFont);
        gbc.gridx = 1;
        form.add(txtPassword, gbc);

        // Fila 2: Rol
        gbc.gridx = 2;
        JLabel lblRolLabel = new JLabel("Rol:");
        lblRolLabel.setForeground(Color.WHITE);
        lblRolLabel.setFont(labelFont);
        form.add(lblRolLabel, gbc);
        
        cbRol = new JComboBox<>();
        cbRol.setFont(inputFont);
        cbRol.setPreferredSize(new Dimension(150, 28));
        gbc.gridx = 3;
        form.add(cbRol, gbc);

        // PANEL DE BOTONES ACCIONES
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(AZUL_EJECUTIVO);
        
        // MODIFICACIÓN: Ajuste de colores para legibilidad óptima
        btnGuardar = new JButton("Guardar");
        estilizarBotonFormulario(btnGuardar, Color.GREEN); 
        btnGuardar.setForeground(AZUL_EJECUTIVO);; // Letras negras sobre fondo verde brillante

        btnActualizar = new JButton("Actualizar");
        estilizarBotonFormulario(btnActualizar, new Color(51, 153, 255)); // Azul más claro y moderno
        btnActualizar.setForeground(AZUL_EJECUTIVO); // Letras negras para mejor contraste

        btnEliminar = new JButton("Eliminar");
        estilizarBotonFormulario(btnEliminar, new Color(204, 0, 0)); // Rojo más oscuro y elegante
        btnEliminar.setForeground(AZUL_EJECUTIVO); // El texto blanco ahora sí resalta

        btnLimpiar = new JButton("Limpiar");
        estilizarBotonFormulario(btnLimpiar, Color.WHITE);
        btnLimpiar.setForeground(AZUL_EJECUTIVO); 

        btnGuardar.addActionListener(e -> guardarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);

        // Agregar panel de botones en la parte inferior del GridBagLayout
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 0, 5, 0);
        form.add(buttonPanel, gbc);

        return form;
    }

    // Método asistente para estandarizar el diseño plano moderno en los botones
    private void estilizarBotonFormulario(JButton boton, Color bg) {
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setBackground(bg);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
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
        List<Usuario> usuarios = usuarioDAO.listarTodos();
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
            String nombre = (String) tableModel.getValueAt(row, 1);
            String email = (String) tableModel.getValueAt(row, 2);
            String rolSeleccionado = (String) tableModel.getValueAt(row, 3);
            
            txtNombre.setText(nombre);
            txtEmail.setText(email);
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
            String password = new String(txtPassword.getPassword());
            
            if (usuarioActual.getRol().equals("RECEPCION") && !rolSeleccionado.equals("MECANICO")) {
                JOptionPane.showMessageDialog(this, 
                    "Como RECEPCIONISTA, solo puedes crear usuarios con rol MECANICO",
                    "Permiso Denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (usuarioDAO.emailExiste(email)) {
                JOptionPane.showMessageDialog(this, "Este email ya está registrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setEmail(email);
            usuario.setPassword(password);
            usuario.setRol(rolSeleccionado);

            if (usuarioDAO.insertar(usuario)) {
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
        
        boolean esMismoEmail = nuevoEmail.equals(emailOriginal);
        boolean emailExisteEnOtro = !esMismoEmail && usuarioDAO.emailExiste(nuevoEmail);
        
        if (emailExisteEnOtro) {
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
            
            String nuevaPassword = new String(txtPassword.getPassword());
            if (!nuevaPassword.isEmpty()) {
                usuario.setPassword(nuevaPassword);
            }

            if (usuarioDAO.actualizar(usuario)) {
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
            if (usuarioDAO.eliminar(selectedId)) {
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
        if (selectedId == -1) {
            String password = new String(txtPassword.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese una contraseña para el nuevo usuario");
                return false;
            }
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