package com.autorepar.view;

import com.autorepar.dao.*;
import com.autorepar.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistorialForm extends JFrame {

    private Usuario usuarioActual;
    private ServicioDAO servicioDAO;
    private VehiculoDAO vehiculoDAO;
    private UsuarioDAO usuarioDAO;
    private JTable tblHistorial;
    private DefaultTableModel tableModel;
    private JComboBox<Vehiculo> cbVehiculo;
    private JTextField txtBuscarPlaca;

    // Colores corporativos de AutoRepar (Fondo Azul Ejecutivo solicitado)
    private final Color AZUL_EJECUTIVO = new Color(18, 38, 68);
    private final Color GRIS_PLATINO = new Color(242, 244, 247);
    private final Color GRIS_BORDE = new Color(205, 215, 225);

    public HistorialForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.servicioDAO = new ServicioDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarVehiculos();
    }

    private void initComponents() {
        setTitle("AutoRepar - Historial de Servicios");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // TODO EL FONDO AZUL EJECUTIVO: Configuración del panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(AZUL_EJECUTIVO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = crearCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(AZUL_EJECUTIVO); // Fondo Azul Ejecutivo

        // TITULO EN COLOR BLANCO
        JLabel lblTitle = new JLabel("Historial de Servicios por Vehículo");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        top.add(lblTitle, BorderLayout.WEST);

        // Subpanel de búsqueda y acciones a la derecha
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchPanel.setBackground(AZUL_EJECUTIVO); // Fondo Azul Ejecutivo

        JLabel lblBuscar = new JLabel("Buscar por Placa:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        lblBuscar.setForeground(Color.WHITE); // Texto Blanco
        searchPanel.add(lblBuscar);

        txtBuscarPlaca = new JTextField(10);
        txtBuscarPlaca.setFont(new Font("Arial", Font.PLAIN, 13));
        searchPanel.add(txtBuscarPlaca);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(Color.MAGENTA);
        btnBuscar.setForeground(Color.MAGENTA);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnBuscar.addActionListener(e -> buscarPorPlaca());
        searchPanel.add(btnBuscar);

        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 12));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(AZUL_EJECUTIVO);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnVolver.addActionListener(e -> volverDashboard());
        searchPanel.add(btnVolver);

        top.add(searchPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AZUL_EJECUTIVO); // Fondo Azul Ejecutivo

        // Panel del filtro del ComboBox
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(AZUL_EJECUTIVO); // Fondo Azul Ejecutivo

        JLabel lblSeleccionar = new JLabel("Seleccionar Vehículo:");
        lblSeleccionar.setFont(new Font("Arial", Font.BOLD, 13));
        lblSeleccionar.setForeground(Color.WHITE); // Texto Blanco
        filterPanel.add(lblSeleccionar);

        cbVehiculo = new JComboBox<>();
        cbVehiculo.setPreferredSize(new Dimension(400, 28));
        cbVehiculo.setFont(new Font("Arial", Font.PLAIN, 13));
        cbVehiculo.addActionListener(e -> {
            if (cbVehiculo.getSelectedItem() instanceof Vehiculo) {
                cargarHistorial();
            }
        });
        filterPanel.add(cbVehiculo);
        center.add(filterPanel, BorderLayout.NORTH);

        // Configuración de la Tabla
        String[] columnas = {"ID", "Fecha", "Tipo", "Descripción", "Costo (S/)", "Mecánico"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHistorial = new JTable(tableModel);
        tblHistorial.setRowHeight(35); // Filas más amplias y profesionales
        tblHistorial.setFont(new Font("Arial", Font.PLAIN, 13));

        // Estilo de la cabecera (Invertido a Gris Platino/Azul Ejecutivo para resaltar)
        tblHistorial.getTableHeader().setOpaque(true);
        tblHistorial.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblHistorial.getTableHeader().setBackground(GRIS_PLATINO);
        tblHistorial.getTableHeader().setForeground(AZUL_EJECUTIVO);
        tblHistorial.getTableHeader().setReorderingAllowed(false);

        // Estilos de la grilla interna
        tblHistorial.setShowGrid(true);
        tblHistorial.setGridColor(GRIS_BORDE);
        tblHistorial.setSelectionBackground(new Color(18, 38, 68, 40));
        tblHistorial.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tblHistorial);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private void cargarVehiculos() {
        List<Vehiculo> vehiculos = vehiculoDAO.listarTodos();
        cbVehiculo.removeAllItems();

        if (vehiculos.isEmpty()) {
            cbVehiculo.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "No hay vehículos registrados en el sistema.\nDebe registrar vehículos primero.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
        } else {
            for (Vehiculo v : vehiculos) {
                cbVehiculo.addItem(v);
            }
            cbVehiculo.setSelectedIndex(0);
            cbVehiculo.setEnabled(true);
            cargarHistorial();
        }
    }

    private void cargarHistorial() {
        Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
        if (vehiculo == null) {
            tableModel.setRowCount(0);
            return;
        }

        List<Servicio> servicios = servicioDAO.listarPorVehiculo(vehiculo.getId());
        tableModel.setRowCount(0);

        for (Servicio s : servicios) {
            Usuario mecanico = usuarioDAO.obtenerPorId(s.getMecanicoId());
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getFecha().toString(),
                s.getTipo(),
                s.getDescripcion(),
                String.format("%.2f", s.getCosto()),
                mecanico != null ? mecanico.getNombre() : "N/A"
            });
        }
    }

    private void buscarPorPlaca() {
        String placa = txtBuscarPlaca.getText().trim().toUpperCase();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una placa para buscar", "Buscar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Vehiculo vehiculo = vehiculoDAO.buscarPorPlaca(placa);
        if (vehiculo != null) {
            for (int i = 0; i < cbVehiculo.getItemCount(); i++) {
                Vehiculo v = cbVehiculo.getItemAt(i);
                if (v.getPlaca().equals(placa)) {
                    cbVehiculo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró vehículo con placa: " + placa, "No encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }

    public JComboBox<Vehiculo> getCbVehiculo() {
        return cbVehiculo;
    }

    public JTextField getTxtBuscarPlaca() {
        return txtBuscarPlaca;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
