package com.autorepar.view;

import com.autorepar.controller.HistorialController;
import com.autorepar.dao.*;
import com.autorepar.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistorialForm extends JFrame {

    private Usuario usuarioActual;
    private HistorialController controller;
    private JTable tblHistorial;
    private DefaultTableModel tableModel;
    private JComboBox<Vehiculo> cbVehiculo;
    private JTextField txtBuscarPlaca;

    public HistorialForm(Usuario usuario) {
        this.usuarioActual = usuario;
        controller = new HistorialController(this, usuarioActual);
        initComponents();
        controller.cargarVehiculos();
    }

    private void initComponents() {
        setTitle("AutoRepar - Historial de Servicios");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = crearCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Historial de Servicios por Vehículo");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        top.add(lblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Buscar por Placa:"));
        txtBuscarPlaca = new JTextField(10);
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.addActionListener(e -> controller.buscarPorPlaca());
        searchPanel.add(txtBuscarPlaca);
        searchPanel.add(btnBuscar);

        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.addActionListener(e -> controller.volverDashboard());
        searchPanel.add(btnVolver);

        top.add(searchPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Seleccionar Vehículo:"));
        cbVehiculo = new JComboBox<>();
        cbVehiculo.setPreferredSize(new Dimension(300, 25));
        cbVehiculo.addActionListener(e -> controller.cargarHistorial());
        filterPanel.add(cbVehiculo);
        center.add(filterPanel, BorderLayout.NORTH);

        String[] columnas = {"ID", "Fecha", "Tipo", "Descripción", "Costo (S/)", "Mecánico"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHistorial = new JTable(tableModel);
        tblHistorial.setRowHeight(30);
        tblHistorial.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tblHistorial);
        center.add(scrollPane, BorderLayout.CENTER);

        return center;
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
