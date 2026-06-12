package com.autorepar.view;

import com.autorepar.dao.CitaDAO;
import com.autorepar.dao.ClienteDAO;
import com.autorepar.dao.VehiculoDAO;
import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import com.autorepar.model.Cita;
import com.autorepar.model.Cliente;
import com.autorepar.model.Vehiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardForm extends JFrame {
    private Usuario usuarioActual;
    private JTable tblCitas;
    private DefaultTableModel tableModel;
    private JLabel lblBienvenida;
    private JLabel lblFechaActual;
    private JLabel lblCitasCount;
    
    private ClienteDAO clienteDAO;
    private VehiculoDAO vehiculoDAO;
    private UsuarioDAO usuarioDAO;

    public DashboardForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.clienteDAO = new ClienteDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarCitasActivas();
    }

    private void initComponents() {
        setTitle("AutoRepar - Dashboard");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = crearHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = crearCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel navPanel = crearNavPanel();
        mainPanel.add(navPanel, BorderLayout.WEST);

        add(mainPanel);
    }

    private JPanel crearHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 102, 204));
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("AutoRepar");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        header.add(lblTitle, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);

        lblBienvenida = new JLabel("Bienvenido, " + usuarioActual.getNombre() + " (" + usuarioActual.getRol() + ")");
        lblBienvenida.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBienvenida.setForeground(Color.BLACK);

        lblFechaActual = new JLabel(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy").format(LocalDate.now()));
        lblFechaActual.setFont(new Font("Arial", Font.PLAIN, 12));
        lblFechaActual.setForeground(Color.DARK_GRAY);

        infoPanel.add(lblBienvenida);
        infoPanel.add(lblFechaActual);
        header.add(infoPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(Color.WHITE);
        
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTituloSeccion = new JLabel("📋 Citas Activas (no pagadas)");
        lblTituloSeccion.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloSeccion.setForeground(new Color(0, 102, 204));
        borderPanel.add(lblTituloSeccion, BorderLayout.NORTH);
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        lblCitasCount = new JLabel("Cargando...");
        lblCitasCount.setFont(new Font("Arial", Font.BOLD, 12));
        summaryPanel.add(lblCitasCount);
        borderPanel.add(summaryPanel, BorderLayout.CENTER);
        
        center.add(borderPanel, BorderLayout.NORTH);

        String[] columnas = {"ID", "Fecha", "Hora", "Cliente", "Vehículo", "Mecánico", "Estado", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCitas = new JTable(tableModel);
        tblCitas.setRowHeight(35);
        tblCitas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblCitas.setFont(new Font("Arial", Font.PLAIN, 12));
        tblCitas.setSelectionBackground(new Color(0, 102, 204, 80));

        JScrollPane scrollPane = new JScrollPane(tblCitas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        center.add(scrollPane, BorderLayout.CENTER);

        return center;
    }

    private JPanel crearNavPanel() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(220, 220, 220));
        nav.setPreferredSize(new Dimension(220, 0));
        nav.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        java.util.ArrayList<String[]> botonesList = new java.util.ArrayList<>();
        
        // Dashboard - visible para todos
        botonesList.add(new String[]{"🏠", "Dashboard", "dashboard"});
        
        // Clientes - visible para todos
        botonesList.add(new String[]{"👤", "Clientes", "clientes"});
        
        // Vehículos - visible para todos
        botonesList.add(new String[]{"🚗", "Vehículos", "vehiculos"});
        
        // Citas - visible solo para ADMIN y RECEPCION (no para MECANICO)
        if (!usuarioActual.getRol().equals("MECANICO")) {
            botonesList.add(new String[]{"📅", "Citas", "citas"});
        }
        
        // Historial - visible solo para ADMIN y RECEPCION (no para MECANICO)
        if (!usuarioActual.getRol().equals("MECANICO")) {
            botonesList.add(new String[]{"🔧", "Historial", "historial"});
        }
        
        // Reportes - visible solo para ADMIN
        if (usuarioActual.getRol().equals("ADMIN")) {
            botonesList.add(new String[]{"📊", "Reportes", "reportes"});
        }
        
        // Usuarios - visible solo para ADMIN y RECEPCION (no para MECANICO)
        if (!usuarioActual.getRol().equals("MECANICO")) {
            botonesList.add(new String[]{"👥", "Usuarios", "usuarios"});
        }
        
        // Cerrar Sesión - visible para todos
        botonesList.add(new String[]{"🚪", "Cerrar Sesión", "logout"});

        for (String[] btn : botonesList) {
            JButton button = new JButton(btn[0] + "  " + btn[1]);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 45));
            button.setMinimumSize(new Dimension(200, 45));
            button.setPreferredSize(new Dimension(200, 45));
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(new Color(240, 240, 240));
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(200, 220, 240));
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                        BorderFactory.createEmptyBorder(7, 14, 7, 14)
                    ));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(240, 240, 240));
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            });

            switch (btn[2]) {
                case "dashboard":
                    button.addActionListener(e -> cargarCitasActivas());
                    break;
                case "clientes":
                    button.addActionListener(e -> abrirClientes());
                    break;
                case "vehiculos":
                    button.addActionListener(e -> abrirVehiculos());
                    break;
                case "citas":
                    button.addActionListener(e -> abrirCitas());
                    break;
                case "historial":
                    button.addActionListener(e -> abrirHistorial());
                    break;
                case "reportes":
                    button.addActionListener(e -> abrirReportes());
                    break;
                case "usuarios":
                    button.addActionListener(e -> abrirUsuarios());
                    break;
                case "logout":
                    button.addActionListener(e -> cerrarSesion());
                    break;
            }

            nav.add(button);
            nav.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return nav;
    }

    private void cargarCitasActivas() {
        try {
            CitaDAO citaDAO = new CitaDAO();
            List<Cita> todas = citaDAO.listarTodas();
            List<Cita> activas = new ArrayList<>();
            for (Cita c : todas) {
                if (!c.getEstado().equals("CANCELADA")) {
                    activas.add(c);
                }
            }

            tableModel.setRowCount(0);
            for (Cita cita : activas) {
                Cliente cliente = clienteDAO.obtenerPorId(cita.getClienteId());
                Vehiculo vehiculo = vehiculoDAO.obtenerPorId(cita.getVehiculoId());
                Usuario mecanico = usuarioDAO.obtenerPorId(cita.getMecanicoId());
                
                String nombreCliente = (cliente != null) ? cliente.getNombreCompleto() : "N/A";
                String infoVehiculo = (vehiculo != null) ? vehiculo.getDescripcion() : "N/A";
                String nombreMecanico = (mecanico != null) ? mecanico.getNombre() + " (" + mecanico.getRol() + ")" : "N/A";
                
                tableModel.addRow(new Object[]{
                    cita.getId(),
                    cita.getFecha().toString(),
                    cita.getHora().toString(),
                    nombreCliente,
                    infoVehiculo,
                    nombreMecanico,
                    cita.getEstado(),
                    cita.getDescripcion() != null && !cita.getDescripcion().isEmpty() ? cita.getDescripcion() : "Sin descripción"
                });
            }

            lblCitasCount.setText("Total de citas activas: " + activas.size());
        } catch (Exception e) {
            System.out.println("Error al cargar citas: " + e.getMessage());
            lblCitasCount.setText("Error al cargar citas");
        }
    }

    private void abrirClientes() {
        new ClienteForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    private void abrirVehiculos() {
        new VehiculoForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    private void abrirCitas() {
        new CitaForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    private void abrirHistorial() {
        new HistorialForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    private void abrirReportes() {
        new ReporteForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    private void abrirUsuarios() {
        new UsuarioForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }
}