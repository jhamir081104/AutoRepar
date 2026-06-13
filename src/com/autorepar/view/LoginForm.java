package com.autorepar.view;

import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import com.google.common.base.Strings;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("AutoRepar - Inicio de Sesión");
        setSize(450, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);

        // Título
        JLabel lblTitulo = new JLabel("AutoRepar");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Gestión de Citas y Servicios");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(lblSubtitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 5, 15);

        // Email
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(lblEmail, gbc);

        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        // ELIMINADO: txtEmail.setText("admin@autorepar.com");
        gbc.gridx = 1;
        mainPanel.add(txtEmail, gbc);

        // Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        // ELIMINADO: txtPassword.setText("admin123");
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Botón Ingresar - MEJORADO
        JButton btnLogin = new JButton("INGRESAR");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 150, 0));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Efecto hover
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(0, 180, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(0, 150, 0));
            }
        });
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 15, 10, 15);
        mainPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> realizarLogin());

        // Enter key listener
        getRootPane().setDefaultButton(btnLogin);

        add(mainPanel);
    }

    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        //validacion con Guava
        if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor complete todos los campos",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }
        
        //validacion basica de formato email
        if(!email.contains("@")){
            JOptionPane.showMessageDialog(this, "ingrese un email valido", "Email invalido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.login(email, password);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this,
                "¡Bienvenido " + usuario.getNombre() + "!",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            new DashboardForm(usuario).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Email o contraseña incorrectos",
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }
}