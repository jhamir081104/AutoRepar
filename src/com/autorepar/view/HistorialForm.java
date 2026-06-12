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
        btnBuscar.addActionListener(e -> buscarPorPlaca());
        searchPanel.add(txtBuscarPlaca);
        searchPanel.add(btnBuscar);

        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.addActionListener(e -> volverDashboard());
        searchPanel.add(btnVolver);

        top.add(searchPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Seleccionar Vehículo:"));
        cbVehiculo = new JComboBox<>();
        cbVehiculo.setPreferredSize(new Dimension(400, 25));
        cbVehiculo.addActionListener(e -> {
            // Solo cargar historial si el elemento seleccionado es un Vehiculo
            if (cbVehiculo.getSelectedItem() instanceof Vehiculo) {
                cargarHistorial();
            }
        });
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
            cargarHistorial(); // Cargar historial del primer vehículo
        }
    }

    private void cargarHistorial() {
        // Obtener el vehículo seleccionado con casting explícito
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
        
        if (servicios.isEmpty()) {
            // Opcional: mostrar un mensaje en la tabla o en un label, pero no un popup molesto
            // Aquí puedes dejarlo vacío o añadir una fila con un mensaje
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
            // Buscar el vehículo en el combo y seleccionarlo
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
}