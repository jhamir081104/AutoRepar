package com.autorepar.view;

import com.autorepar.dao.ServicioDAO;
import com.autorepar.model.Servicio;
import com.autorepar.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteForm extends JFrame {
    private Usuario usuarioActual;
    private ServicioDAO servicioDAO;
    private JTable tblReporte;
    private DefaultTableModel tableModel;
    private JTextField txtFechaInicio, txtFechaFin;
    private JLabel lblTotal;

    public ReporteForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.servicioDAO = new ServicioDAO();
        initComponents();
        cargarReportePorDefecto();
    }

    private void initComponents() {
        setTitle("AutoRepar - Generación de Reportes");
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
        
        JLabel lblTitle = new JLabel("Reportes de Servicios");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        top.add(lblTitle, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtrar por Rango de Fechas"));
        
        filterPanel.add(new JLabel("Fecha Inicio:"));
        txtFechaInicio = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 12);
        filterPanel.add(txtFechaInicio);
        
        filterPanel.add(new JLabel("Fecha Fin:"));
        txtFechaFin = new JTextField(LocalDate.now().toString(), 12);
        filterPanel.add(txtFechaFin);
        
        JButton btnGenerar = new JButton("📊 Generar Reporte");
        btnGenerar.setBackground(new Color(0, 102, 204));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.addActionListener(e -> generarReporte());
        filterPanel.add(btnGenerar);
        
        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.addActionListener(e -> volverDashboard());
        filterPanel.add(btnVolver);
        
        top.add(filterPanel, BorderLayout.CENTER);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        
        String[] columnas = {"ID", "Fecha", "Tipo de Servicio", "Descripción", "Costo (S/)", "ID Vehículo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblReporte = new JTable(tableModel);
        tblReporte.setRowHeight(30);
        tblReporte.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tblReporte);
        center.add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 102, 204));
        bottomPanel.add(lblTotal);
        center.add(bottomPanel, BorderLayout.SOUTH);
        
        return center;
    }

    private void cargarReportePorDefecto() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        txtFechaInicio.setText(inicio.toString());
        txtFechaFin.setText(fin.toString());
        generarReporte();
    }

    private void generarReporte() {
        try {
            LocalDate inicio = LocalDate.parse(txtFechaInicio.getText().trim());
            LocalDate fin = LocalDate.parse(txtFechaFin.getText().trim());
            
            if (inicio.isAfter(fin)) {
                JOptionPane.showMessageDialog(this, "La fecha de inicio no puede ser mayor a la fecha fin");
                return;
            }
            
            List<Servicio> servicios = servicioDAO.listarPorRangoFechas(inicio, fin);
            double total = servicioDAO.obtenerTotalServiciosPorRango(inicio, fin);
            
            tableModel.setRowCount(0);
            for (Servicio s : servicios) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getFecha().toString(), s.getTipo(),
                    s.getDescripcion(), String.format("%.2f", s.getCosto()),
                    s.getVehiculoId()
                });
            }
            
            lblTotal.setText(String.format("Total de ingresos: S/ %.2f", total));
            
            if (servicios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay servicios en el rango de fechas seleccionado");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use YYYY-MM-DD");
        }
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}