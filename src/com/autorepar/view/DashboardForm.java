package com.autorepar.view;

import com.autorepar.controller.DashboardController;
import com.autorepar.dao.CitaDAO;
import com.autorepar.model.Usuario;
import com.autorepar.model.Cita;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardForm extends JFrame {

    private DashboardController controller;
    private Usuario usuarioActual;
    private JTable tblCitasHoy;
    private DefaultTableModel tableModel;
    private JLabel lblBienvenida;
    private JLabel lblFechaActual;
    private JLabel lblCitasCount;

    public DashboardForm(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
        controller = new DashboardController(this, usuarioActual);
        controller.cargarCitasDelDia();
    }

    private void initComponents() {
        setTitle("AutoRepar - Dashboard");
        setSize(1100, 700);
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

        JLabel lblTituloSeccion = new JLabel("📅 Citas Programadas para Hoy");
        lblTituloSeccion.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloSeccion.setForeground(new Color(0, 102, 204));
        borderPanel.add(lblTituloSeccion, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        lblCitasCount = new JLabel("Cargando citas...");
        lblCitasCount.setFont(new Font("Arial", Font.BOLD, 12));
        summaryPanel.add(lblCitasCount);
        borderPanel.add(summaryPanel, BorderLayout.CENTER);

        center.add(borderPanel, BorderLayout.NORTH);

        String[] columnas = {"ID", "Hora", "Cliente ID", "Vehículo ID", "Mecánico ID", "Estado", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCitasHoy = new JTable(tableModel);
        tblCitasHoy.setRowHeight(30);
        tblCitasHoy.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblCitasHoy.setFont(new Font("Arial", Font.PLAIN, 12));
        tblCitasHoy.setSelectionBackground(new Color(0, 102, 204, 80));

        JScrollPane scrollPane = new JScrollPane(tblCitasHoy);
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

        // Lista de botones base
        java.util.ArrayList<String[]> botonesList = new java.util.ArrayList<>();
        botonesList.add(new String[]{"🏠", "Dashboard", "dashboard"});
        botonesList.add(new String[]{"👤", "Clientes", "clientes"});
        botonesList.add(new String[]{"🚗", "Vehículos", "vehiculos"});
        botonesList.add(new String[]{"📅", "Citas", "citas"});
        botonesList.add(new String[]{"🔧", "Historial", "historial"});
        botonesList.add(new String[]{"📊", "Reportes", "reportes"});

        // Solo mostrar el botón de Usuarios si es ADMIN o RECEPCION
        if (usuarioActual.getRol().equals("ADMIN") || usuarioActual.getRol().equals("RECEPCION")) {
            botonesList.add(new String[]{"👥", "Usuarios", "usuarios"});
        }

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
                    button.addActionListener(e -> controller.cargarCitasDelDia());
                    break;
                case "clientes":
                    button.addActionListener(e -> controller.abrirCitas());
                    break;
                case "vehiculos":
                    button.addActionListener(e -> controller.abrirVehiculos());
                    break;
                case "citas":
                    button.addActionListener(e -> controller.abrirVehiculos());
                    break;
                case "historial":
                    button.addActionListener(e -> controller.abrirHistorial());
                    break;
                case "reportes":
                    button.addActionListener(e -> controller.abrirReportes());
                    break;
                case "usuarios":
                    button.addActionListener(e -> controller.abrirUsuarios());
                    break;
                case "logout":
                    button.addActionListener(e -> controller.cerrarSesion());
                    break;
            }

            nav.add(button);
            nav.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return nav;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JLabel getLblCitasCount() {
        return lblCitasCount;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
