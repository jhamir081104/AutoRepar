package com.autorepar.view;

import com.autorepar.dao.ServicioDAO;
import com.autorepar.model.Servicio;
import com.autorepar.model.Usuario;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteForm extends JFrame {
    private Usuario usuarioActual;
    private ServicioDAO servicioDAO;
    private JTable tblReporte;
    private DefaultTableModel tableModel;
    private JTextField txtFechaInicio, txtFechaFin;
    private JLabel lblTotal;

    // Paleta de colores corporativos (Fondo Azul Ejecutivo e interfaces limpias)
    private final Color AZUL_EJECUTIVO = new Color(18, 38, 68);
    private final Color GRIS_PLATINO = new Color(242, 244, 247);
    private final Color GRIS_BORDE = new Color(205, 215, 225);

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

        // PANEL PRINCIPAL CON FONDO AZUL EJECUTIVO
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
        JPanel top = new JPanel(new BorderLayout(15, 10));
        top.setBackground(AZUL_EJECUTIVO);
        
        // TÍTULO EN BLANCO
        JLabel lblTitle = new JLabel("Reportes de Servicios");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        top.add(lblTitle, BorderLayout.WEST);

        // PANEL DE FILTROS Y ACCIONES
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        filterPanel.setBackground(AZUL_EJECUTIVO);
        
        // TitledBorder adaptado con letras blancas
        var titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1), 
                "Filtrar por Rango de Fechas"
        );
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 12));
        titledBorder.setTitleColor(Color.WHITE);
        filterPanel.setBorder(titledBorder);
        
        JLabel lblInicio = new JLabel("Fecha Inicio:");
        lblInicio.setForeground(Color.WHITE);
        lblInicio.setFont(new Font("Arial", Font.BOLD, 12));
        filterPanel.add(lblInicio);
        
        txtFechaInicio = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 10);
        txtFechaInicio.setFont(new Font("Arial", Font.PLAIN, 13));
        filterPanel.add(txtFechaInicio);
        
        JLabel lblFin = new JLabel("Fecha Fin:");
        lblFin.setForeground(Color.WHITE);
        lblFin.setFont(new Font("Arial", Font.BOLD, 12));
        filterPanel.add(lblFin);
        
        txtFechaFin = new JTextField(LocalDate.now().toString(), 10);
        txtFechaFin.setFont(new Font("Arial", Font.PLAIN, 13));
        filterPanel.add(txtFechaFin);
        
        // BOTÓN BUSCAR - FONDO VERDE CON LETRAS BLANCAS PARA ALTO CONTRASTE
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        btnBuscar.setBackground(new Color(40, 167, 69)); // Verde profesional
        btnBuscar.setForeground(Color.GREEN);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 134, 55), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnBuscar.addActionListener(e -> generarReporte());
        filterPanel.add(btnBuscar);
        
        // BOTÓN GENERAR PDF - FONDO ROJO CON LETRAS BLANCAS PARA ALTO CONTRASTE
        JButton btnGenerarPDF = new JButton("Generar PDF");
        btnGenerarPDF.setFont(new Font("Arial", Font.BOLD, 13));
        btnGenerarPDF.setBackground(new Color(220, 53, 69)); // Rojo plano corporativo
        btnGenerarPDF.setForeground(Color.RED);
        btnGenerarPDF.setFocusPainted(false);
        btnGenerarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerarPDF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 39, 53), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnGenerarPDF.addActionListener(e -> generarPDF());
        filterPanel.add(btnGenerarPDF);
        
        // BOTÓN VOLVER - ESTILO LIMPIO EN BLANCO
        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 12));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(AZUL_EJECUTIVO);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnVolver.addActionListener(e -> volverDashboard());
        filterPanel.add(btnVolver);
        
        top.add(filterPanel, BorderLayout.CENTER);
        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AZUL_EJECUTIVO);
        
        String[] columnas = {"ID", "Fecha", "Tipo de Servicio", "Descripción", "Costo (S/)", "ID Vehículo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblReporte = new JTable(tableModel);
        tblReporte.setRowHeight(35);
        tblReporte.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Estilo de la Cabecera de la Tabla
        tblReporte.getTableHeader().setOpaque(true);
        tblReporte.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblReporte.getTableHeader().setBackground(GRIS_PLATINO);
        tblReporte.getTableHeader().setForeground(AZUL_EJECUTIVO);
        tblReporte.getTableHeader().setReorderingAllowed(false);
        
        // Grilla interna
        tblReporte.setShowGrid(true);
        tblReporte.setGridColor(GRIS_BORDE);
        tblReporte.setSelectionBackground(new Color(18, 38, 68, 40));
        tblReporte.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tblReporte);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        center.add(scrollPane, BorderLayout.CENTER);
        
        // Panel Inferior para el Total
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(AZUL_EJECUTIVO);
        
        lblTotal = new JLabel("Total de ingresos: S/ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Color.WHITE); // Cambiado a Blanco para resaltar sobre el fondo azul
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

    private void generarPDF() {
        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, 
                "No hay datos para generar el PDF.\nPrimero realice una búsqueda.", 
                "Sin datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new File("reporte_servicios_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File archivo = fileChooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getAbsolutePath() + ".pdf");
        }
        
        try {
            PdfWriter writer = new PdfWriter(archivo);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            Paragraph titulo = new Paragraph("REPORTE DE SERVICIOS")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(titulo);
            
            String fechaInicio = txtFechaInicio.getText().trim();
            String fechaFin = txtFechaFin.getText().trim();
            Paragraph subtitulo = new Paragraph("Período: " + fechaInicio + " al " + fechaFin)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitulo);
            
            Paragraph fechaGen = new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20);
            document.add(fechaGen);
            
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{5, 10, 20, 25, 10, 10}));
            tabla.setWidth(UnitValue.createPercentValue(100));
            
            String[] headers = {"ID", "Fecha", "Tipo", "Descripción", "Costo (S/)", "Vehículo ID"};
            for (String header : headers) {
                Cell celda = new Cell().add(new Paragraph(header).setBold());
                celda.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                celda.setTextAlignment(TextAlignment.CENTER);
                tabla.addCell(celda);
            }
            
            double totalGeneral = 0;
            for (int i = 0; i < rowCount; i++) {
                String id = String.valueOf(tableModel.getValueAt(i, 0));
                String fecha = (String) tableModel.getValueAt(i, 1);
                String tipo = (String) tableModel.getValueAt(i, 2);
                String descripcion = (String) tableModel.getValueAt(i, 3);
                String costo = (String) tableModel.getValueAt(i, 4);
                String vehiculoId = String.valueOf(tableModel.getValueAt(i, 5));
                
                tabla.addCell(new Cell().add(new Paragraph(id)).setTextAlignment(TextAlignment.CENTER));
                tabla.addCell(new Cell().add(new Paragraph(fecha)).setTextAlignment(TextAlignment.CENTER));
                tabla.addCell(new Cell().add(new Paragraph(tipo)));
                tabla.addCell(new Cell().add(new Paragraph(descripcion)));
                tabla.addCell(new Cell().add(new Paragraph(costo)).setTextAlignment(TextAlignment.RIGHT));
                tabla.addCell(new Cell().add(new Paragraph(vehiculoId)).setTextAlignment(TextAlignment.CENTER));
                
                try {
                    totalGeneral += Double.parseDouble(costo.replace(",", "."));
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
            
            document.add(tabla);
            
            Paragraph total = new Paragraph("TOTAL GENERAL: S/ " + String.format("%.2f", totalGeneral))
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(20);
            document.add(total);
            
            Paragraph footer = new Paragraph("AutoRepar - Sistema de Gestión de Citas y Servicios")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);
            
            document.close();
            
            JOptionPane.showMessageDialog(this, 
                "✅ PDF generado exitosamente!\n" +
                "Archivo guardado en:\n" + archivo.getAbsolutePath(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            try {
                Desktop.getDesktop().open(archivo);
            } catch (Exception ex) {
                // No se pudo abrir automáticamente
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al generar el PDF:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}