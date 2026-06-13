package com.autorepar.view;

import com.autorepar.dao.*;
import com.autorepar.model.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CitaForm extends JFrame {

    private Usuario usuarioActual;
    private CitaDAO citaDAO;
    private ClienteDAO clienteDAO;
    private VehiculoDAO vehiculoDAO;
    private UsuarioDAO usuarioDAO;
    private ServicioDAO servicioDAO;
    private JTable tblCitas;
    private DefaultTableModel tableModel;
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Vehiculo> cbVehiculo;
    private JComboBox<Usuario> cbMecanico;
    private JTextField txtFecha, txtHora;
    private JTextArea txtDescripcion;
    private JComboBox<String> cbEstado;
    private int selectedId = -1;
    private JCheckBox chkMostrarPagadas;

    // Colores corporativos de AutoRepar
    private final Color AZUL_EJECUTIVO = new Color(18, 38, 68);
    private final Color GRIS_PLATINO = new Color(242, 244, 247);
    private final Color GRIS_BORDE = new Color(205, 215, 225);

    public CitaForm(Usuario usuario) {
        this.usuarioActual = usuario;
        this.citaDAO = new CitaDAO();
        this.clienteDAO = new ClienteDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.servicioDAO = new ServicioDAO();
        initComponents();
        cargarCitas();
        cargarCombos();
    }

    private void initComponents() {
        setTitle("AutoRepar - Gestión de Citas");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con el fondo Gris Platino solicitado
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(GRIS_PLATINO);
        // Quitamos los márgenes laterales del panel exterior para que la cabecera azul pegue a los bordes
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel topPanel = crearTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Contenedor interno para mantener un espacio limpio en la tabla y formulario
        JPanel contenedorContenido = new JPanel(new BorderLayout(10, 10));
        contenedorContenido.setBackground(GRIS_PLATINO);
        contenedorContenido.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel centerPanel = crearCenterPanel();
        contenedorContenido.add(centerPanel, BorderLayout.CENTER);

        JPanel formPanel = crearFormPanel();
        contenedorContenido.add(formPanel, BorderLayout.SOUTH);

        mainPanel.add(contenedorContenido, BorderLayout.CENTER);

        add(mainPanel);
    }

    // MODIFICADO: Cabecera con diseño institucional full-width, fondo azul y texto blanco
    private JPanel crearTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AZUL_EJECUTIVO);
        // Relleno interno para que se vea robusto y espacioso de forma simétrica
        top.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("Gestión de Citas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Cambiado a Segoe UI para alineación ejecutiva
        lblTitle.setForeground(Color.WHITE); // Texto Blanco solicitado
        top.add(lblTitle, BorderLayout.WEST);

        JButton btnVolver = new JButton("← Volver al Dashboard");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(AZUL_EJECUTIVO);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnVolver.addActionListener(e -> volverDashboard());
        top.add(btnVolver, BorderLayout.EAST);

        return top;
    }

    private JPanel crearCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(GRIS_PLATINO);

        // Panel superior con checkbox adaptado al fondo Gris Platino
        JPanel topCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topCenter.setBackground(GRIS_PLATINO);
        chkMostrarPagadas = new JCheckBox("Mostrar citas pagadas");
        chkMostrarPagadas.setFont(new Font("Arial", Font.BOLD, 12));
        chkMostrarPagadas.setBackground(GRIS_PLATINO);
        chkMostrarPagadas.setForeground(AZUL_EJECUTIVO);
        chkMostrarPagadas.addActionListener(e -> cargarCitas());
        topCenter.add(chkMostrarPagadas);
        center.add(topCenter, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Fecha", "Hora", "Cliente", "Vehículo", "Mecánico", "Estado", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCitas = new JTable(tableModel);
        tblCitas.setRowHeight(35); // Altura de fila más espaciosa y moderna

        // CORREGIDO: Cabecera con fondo Azul Ejecutivo y Letras Blancas legibles
        tblCitas.getTableHeader().setOpaque(true);
        tblCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblCitas.getTableHeader().setBackground(AZUL_EJECUTIVO);
        tblCitas.getTableHeader().setForeground(AZUL_EJECUTIVO); // Cambiado de DARK_GRAY a WHITE para contraste
        tblCitas.getTableHeader().setReorderingAllowed(false);

        // Estilos de grillas
        tblCitas.setShowGrid(true);
        tblCitas.setGridColor(GRIS_BORDE);
        tblCitas.setSelectionBackground(new Color(18, 38, 68, 40));
        tblCitas.setSelectionForeground(Color.BLACK);

        tblCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCitaSeleccionada();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblCitas);
        scrollPane.setBorder(BorderFactory.createLineBorder(AZUL_EJECUTIVO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel crearFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());

        // Fondo Azul Ejecutivo para la sección de programación de citas
        form.setBackground(AZUL_EJECUTIVO);

        // Borde elegante con títulos y líneas blancas
        Border lineaBlanca = BorderFactory.createLineBorder(Color.WHITE, 1);
        TitledBorder tituloBorde = BorderFactory.createTitledBorder(
                lineaBlanca,
                "Programar Cita",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13),
                Color.WHITE
        );
        form.setBorder(tituloBorde);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Estilo unificado para los JLabels internos (Blanco y Negrita)
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Color labelColor = Color.WHITE;

        // Fila 0: Cliente y Vehículo
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(labelFont);
        lblCliente.setForeground(labelColor);
        form.add(lblCliente, gbc);

        cbCliente = new JComboBox<>();
        cbCliente.setPreferredSize(new Dimension(250, 25));
        cbCliente.addActionListener(e -> actualizarVehiculosPorCliente());
        gbc.gridx = 1;
        form.add(cbCliente, gbc);

        gbc.gridx = 2;
        JLabel lblVehiculo = new JLabel("Vehículo:");
        lblVehiculo.setFont(labelFont);
        lblVehiculo.setForeground(labelColor);
        form.add(lblVehiculo, gbc);

        cbVehiculo = new JComboBox<>();
        cbVehiculo.setPreferredSize(new Dimension(250, 25));
        gbc.gridx = 3;
        form.add(cbVehiculo, gbc);

        // Fila 1: Mecánico y Fecha
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblMecanico = new JLabel("Mecánico:");
        lblMecanico.setFont(labelFont);
        lblMecanico.setForeground(labelColor);
        form.add(lblMecanico, gbc);

        cbMecanico = new JComboBox<>();
        cbMecanico.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        form.add(cbMecanico, gbc);

        gbc.gridx = 2;
        JLabel lblFecha = new JLabel("Fecha (YYYY-MM-DD):");
        lblFecha.setFont(labelFont);
        lblFecha.setForeground(labelColor);
        form.add(lblFecha, gbc);

        txtFecha = new JTextField(LocalDate.now().toString(), 12);
        gbc.gridx = 3;
        form.add(txtFecha, gbc);

        // Fila 2: Hora y Estado
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblHora = new JLabel("Hora (HH:MM):");
        lblHora.setFont(labelFont);
        lblHora.setForeground(labelColor);
        form.add(lblHora, gbc);

        txtHora = new JTextField("09:00", 8);
        gbc.gridx = 1;
        form.add(txtHora, gbc);

        gbc.gridx = 2;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(labelFont);
        lblEstado.setForeground(labelColor);
        form.add(lblEstado, gbc);

        cbEstado = new JComboBox<>(new String[]{"PENDIENTE", "CONFIRMADA", "COMPLETADA"});
        cbEstado.setEnabled(false);
        gbc.gridx = 3;
        form.add(cbEstado, gbc);

        // Fila 3: Descripción
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(labelFont);
        lblDescripcion.setForeground(labelColor);
        form.add(lblDescripcion, gbc);

        txtDescripcion = new JTextArea(3, 40);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(400, 60));
        scrollDesc.setBorder(BorderFactory.createLineBorder(GRIS_BORDE, 1));
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        form.add(scrollDesc, gbc);

        // Panel de Botones con diseño ejecutivo interactivo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton(" Actualizar");
        JButton btnPagarCita = new JButton(" Pagar Cita");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton(" Limpiar");

        // --- ESTILIZADO INDIVIDUAL CON BORDES DE COLORES INTENSOS ---
        Font botonFont = new Font("Segoe UI", Font.BOLD, 13);
        Border paddingInterno = BorderFactory.createEmptyBorder(8, 16, 8, 16);

        // 1. Guardar: Borde Verde Línea (2px)
        btnGuardar.setFont(botonFont);
        btnGuardar.setBackground(Color.WHITE);
        btnGuardar.setForeground(AZUL_EJECUTIVO);
        btnGuardar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                paddingInterno
        ));

        // 2. Actualizar: Borde Azul Línea (2px)
        btnActualizar.setFont(botonFont);
        btnActualizar.setBackground(Color.WHITE);
        btnActualizar.setForeground(AZUL_EJECUTIVO);
        btnActualizar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                paddingInterno
        ));

        // 3. Pagar Cita: Borde Naranja Línea (2px)
        btnPagarCita.setFont(botonFont);
        btnPagarCita.setBackground(Color.WHITE);
        btnPagarCita.setForeground(AZUL_EJECUTIVO);
        btnPagarCita.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),
                paddingInterno
        ));

        // 4. Eliminar: Borde Rojo Línea (2px)
        btnEliminar.setFont(botonFont);
        btnEliminar.setBackground(Color.WHITE);
        btnEliminar.setForeground(AZUL_EJECUTIVO);
        btnEliminar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                paddingInterno
        ));

        // 5. Limpiar: Borde Gris Estándar (1px)
        btnLimpiar.setFont(botonFont);
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setForeground(AZUL_EJECUTIVO);
        btnLimpiar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                paddingInterno
        ));

        // Propiedades estructurales y listeners comunes asignados en lote
        JButton[] botones = {btnGuardar, btnActualizar, btnPagarCita, btnEliminar, btnLimpiar};
        for (JButton btn : botones) {
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(245, 248, 255)); // Iluminación sutil al hacer hover
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(Color.WHITE);
                }
            });
            buttonPanel.add(btn);
        }

        btnGuardar.addActionListener(e -> guardarCita());
        btnActualizar.addActionListener(e -> actualizarCita());
        btnPagarCita.addActionListener(e -> pagarCita());
        btnEliminar.addActionListener(e -> eliminarCita());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.CENTER;
        form.add(buttonPanel, gbc);

        return form;
    }

    private void cargarCombos() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        cbCliente.removeAllItems();
        for (Cliente c : clientes) {
            cbCliente.addItem(c);
        }

        List<Usuario> mecanicos = usuarioDAO.listarMecanicos();
        cbMecanico.removeAllItems();
        for (Usuario u : mecanicos) {
            cbMecanico.addItem(u);
        }

        if (mecanicos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ No hay mecánicos registrados.\nDebe crear usuarios con rol MECANICO en el módulo de Usuarios.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarVehiculosPorCliente() {
        Cliente cliente = (Cliente) cbCliente.getSelectedItem();
        if (cliente != null) {
            List<Vehiculo> vehiculos = vehiculoDAO.listarPorCliente(cliente.getId());
            cbVehiculo.removeAllItems();
            for (Vehiculo v : vehiculos) {
                cbVehiculo.addItem(v);
            }

            if (vehiculos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Este cliente no tiene vehículos registrados.\nDebe registrar un vehículo primero.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void cargarCitas() {
        List<Cita> todas = citaDAO.listarTodas();
        List<Cita> citasAMostrar = new ArrayList<>();

        for (Cita c : todas) {
            if (chkMostrarPagadas.isSelected()) {
                citasAMostrar.add(c);
            } else {
                if (!c.getEstado().equals("CANCELADA")) {
                    citasAMostrar.add(c);
                }
            }
        }

        tableModel.setRowCount(0);
        for (Cita c : citasAMostrar) {
            Cliente cliente = clienteDAO.obtenerPorId(c.getClienteId());
            Vehiculo vehiculo = vehiculoDAO.obtenerPorId(c.getVehiculoId());
            Usuario mecanico = usuarioDAO.obtenerPorId(c.getMecanicoId());

            String estadoMostrar = c.getEstado();
            if (estadoMostrar.equals("CANCELADA")) {
                estadoMostrar = "PAGADA";
            }

            tableModel.addRow(new Object[]{
                c.getId(),
                c.getFecha().toString(),
                c.getHora().toString(),
                cliente != null ? cliente.getNombreCompleto() : "N/A",
                vehiculo != null ? vehiculo.getDescripcion() : "N/A",
                mecanico != null ? mecanico.getNombre() : "N/A",
                estadoMostrar,
                c.getDescripcion() != null ? c.getDescripcion() : ""
            });
        }
    }

    private void cargarCitaSeleccionada() {
        int row = tblCitas.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) tableModel.getValueAt(row, 0);
            String fechaStr = (String) tableModel.getValueAt(row, 1);
            String horaStr = (String) tableModel.getValueAt(row, 2);
            String clienteNombre = (String) tableModel.getValueAt(row, 3);
            String estado = (String) tableModel.getValueAt(row, 6);
            String descripcion = (String) tableModel.getValueAt(row, 7);

            txtFecha.setText(fechaStr);
            txtHora.setText(horaStr);
            txtDescripcion.setText(descripcion);

            if (estado.equals("PAGADA")) {
                cbEstado.setSelectedItem("COMPLETADA");
                cbEstado.setEnabled(false);
                cbCliente.setEnabled(false);
                cbVehiculo.setEnabled(false);
                cbMecanico.setEnabled(false);
                txtFecha.setEnabled(false);
                txtHora.setEnabled(false);
                txtDescripcion.setEnabled(false);
            } else {
                cbEstado.setSelectedItem(estado);
                cbEstado.setEnabled(true);
                cbCliente.setEnabled(true);
                cbVehiculo.setEnabled(true);
                cbMecanico.setEnabled(true);
                txtFecha.setEnabled(true);
                txtHora.setEnabled(true);
                txtDescripcion.setEnabled(true);
            }

            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                Cliente c = cbCliente.getItemAt(i);
                if (c != null && c.getNombreCompleto().equals(clienteNombre)) {
                    cbCliente.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardarCita() {
        if (validarCampos()) {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
            Usuario mecanico = (Usuario) cbMecanico.getSelectedItem();

            if (cliente == null || vehiculo == null || mecanico == null) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cita cita = new Cita();
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado((String) cbEstado.getSelectedItem());
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(cliente.getId());
            cita.setVehiculoId(vehiculo.getId());
            cita.setMecanicoId(mecanico.getId());

            if (citaDAO.verificarDisponibilidad(cita.getFecha(), cita.getHora(), cita.getMecanicoId())) {
                if (citaDAO.insertar(cita)) {
                    JOptionPane.showMessageDialog(this, "Cita registrada con éxito");
                    limpiarFormulario();
                    cargarCitas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar cita", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Horario no disponible para este mecánico", "Conflicto", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void actualizarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para actualizar");
            return;
        }

        String estadoActual = (String) tableModel.getValueAt(tblCitas.getSelectedRow(), 6);
        if (estadoActual.equals("PAGADA")) {
            JOptionPane.showMessageDialog(this, "No se puede modificar una cita ya pagada", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validarCampos()) {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
            Usuario mecanico = (Usuario) cbMecanico.getSelectedItem();

            Cita cita = new Cita();
            cita.setId(selectedId);
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado((String) cbEstado.getSelectedItem());
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(cliente.getId());
            cita.setVehiculoId(vehiculo.getId());
            cita.setMecanicoId(mecanico.getId());

            if (citaDAO.actualizar(cita)) {
                JOptionPane.showMessageDialog(this, "Cita actualizada con éxito");
                limpiarFormulario();
                cargarCitas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para eliminar");
            return;
        }

        String estadoActual = (String) tableModel.getValueAt(tblCitas.getSelectedRow(), 6);

        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ ELIMINAR CITA PERMANENTEMENTE\n\n"
                + "ID: " + selectedId + "\n"
                + "Cliente: " + tableModel.getValueAt(tblCitas.getSelectedRow(), 3) + "\n"
                + "Fecha: " + txtFecha.getText() + "\n"
                + "Hora: " + txtHora.getText() + "\n"
                + "Estado: " + estadoActual + "\n\n"
                + "Esta acción eliminará la cita de la base de datos.\n"
                + "¿Está seguro?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (citaDAO.eliminar(selectedId)) {
                JOptionPane.showMessageDialog(this, "✅ Cita eliminada permanentemente");
                limpiarFormulario();
                cargarCitas();
                selectedId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la cita", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   

    private void generarExcelPago(Cita cita, Cliente cliente, Vehiculo vehiculo) {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pago Cita");

        // Encabezados
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Cliente");
        header.createCell(1).setCellValue("Vehículo");
        header.createCell(2).setCellValue("Fecha");
        header.createCell(3).setCellValue("Hora");
        header.createCell(4).setCellValue("Descripción");
        header.createCell(5).setCellValue("Estado");

        // Datos
        Row row = sheet.createRow(1);

        row.createCell(0).setCellValue(cliente.getNombre());
        row.createCell(1).setCellValue(vehiculo.getPlaca());
        row.createCell(2).setCellValue(cita.getFecha().toString());
        row.createCell(3).setCellValue(cita.getHora().toString());
        row.createCell(4).setCellValue(cita.getDescripcion());
        row.createCell(5).setCellValue("PAGADA");

        // Ajustar tamaño columnas
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut
                = new FileOutputStream("Pago_Cita_" + cita.getId() + ".xlsx")) {

            workbook.write(fileOut);
            workbook.close();

            JOptionPane.showMessageDialog(null,
                    "Excel generado correctamente");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(null,
                    "Error al generar Excel");

            e.printStackTrace();
        }
    }

    private void pagarCita() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para pagar");
            return;
        }

        String estadoActual = (String) tableModel.getValueAt(tblCitas.getSelectedRow(), 6);
        if (estadoActual.equals("PAGADA")) {
            JOptionPane.showMessageDialog(this, "Esta cita ya está pagada", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Registrar pago de esta cita?\n\n"
                + "Cliente: " + tableModel.getValueAt(tblCitas.getSelectedRow(), 3) + "\n"
                + "Vehículo: " + tableModel.getValueAt(tblCitas.getSelectedRow(), 4) + "\n"
                + "Descripción: " + txtDescripcion.getText() + "\n\n"
                + "La cita se marcará como PAGADA y se registrará en el historial.",
                "Confirmar Pago", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculo.getSelectedItem();
            Usuario mecanico = (Usuario) cbMecanico.getSelectedItem();

            Cita cita = new Cita();
            cita.setId(selectedId);
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado("CANCELADA");
            cita.setDescripcion(txtDescripcion.getText().trim());
            cita.setClienteId(cliente.getId());
            cita.setVehiculoId(vehiculo.getId());
            cita.setMecanicoId(mecanico.getId());

            if (citaDAO.actualizar(cita)) {

                registrarServicioConCostoObligatorio(cita);

                // GENERAR EXCEL
                generarExcelPago(cita, cliente, vehiculo);

                limpiarFormulario();
                cargarCitas();

                JOptionPane.showMessageDialog(this,
                        "✅ Cita marcada como PAGADA",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        }
    }

    private void registrarServicioConCostoObligatorio(Cita cita) {
        Cliente cliente = clienteDAO.obtenerPorId(cita.getClienteId());
        Vehiculo vehiculo = vehiculoDAO.obtenerPorId(cita.getVehiculoId());
        Usuario mecanico = usuarioDAO.obtenerPorId(cita.getMecanicoId());

        String clienteNombre = cliente != null ? cliente.getNombreCompleto() : "N/A";
        String vehiculoInfo = vehiculo != null ? vehiculo.getDescripcion() : "N/A";
        String mecanicoNombre = mecanico != null ? mecanico.getNombre() : "N/A";

        double costo = 0;
        boolean costoValido = false;

        while (!costoValido) {
            String costoStr = JOptionPane.showInputDialog(this,
                    "═══════════════════════════════════════════════════════\n"
                    + "        REGISTRO DE SERVICIO - CITA PAGADA\n"
                    + "═══════════════════════════════════════════════════════\n\n"
                    + "📋 DATOS DEL SERVICIO:\n"
                    + "───────────────────────────────────────────────────────\n"
                    + "  • Cliente:      " + clienteNombre + "\n"
                    + "  • Vehículo:     " + vehiculoInfo + "\n"
                    + "  • Mecánico:     " + mecanicoNombre + "\n"
                    + "  • Fecha:        " + cita.getFecha().toString() + "\n"
                    + "  • Descripción:  " + cita.getDescripcion() + "\n"
                    + "───────────────────────────────────────────────────────\n\n"
                    + "💰 Ingrese el COSTO del servicio (S/):\n"
                    + "(Ejemplo: 150.00)\n\n"
                    + "⚠️ Este campo es OBLIGATORIO.",
                    "Registrar Servicio - AutoRepar",
                    JOptionPane.QUESTION_MESSAGE);

            if (costoStr == null || costoStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "❌ El costo es OBLIGATORIO.\n"
                        + "Debe ingresar un monto válido para registrar el servicio.",
                        "Costo Obligatorio", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            try {
                costo = Double.parseDouble(costoStr.trim());
                if (costo < 0) {
                    JOptionPane.showMessageDialog(this,
                            "El costo no puede ser negativo.\nIngrese un valor válido.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                costoValido = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "❌ Costo no válido.\n"
                        + "Ingrese un número válido (ejemplo: 150.00 o 150).\n"
                        + "No use letras ni caracteres especiales.",
                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }

        Servicio servicio = new Servicio();
        servicio.setFecha(cita.getFecha());
        servicio.setVehiculoId(cita.getVehiculoId());
        servicio.setMecanicoId(cita.getMecanicoId());
        servicio.setTipo("Servicio - Cita Pagada #" + cita.getId());
        servicio.setDescripcion(cita.getDescripcion());
        servicio.setCosto(costo);

        if (servicioDAO.insertar(servicio)) {
            JOptionPane.showMessageDialog(this,
                    "✅ SERVICIO REGISTRADO EN EL HISTORIAL\n\n"
                    + "   • Vehículo: " + vehiculoInfo + "\n"
                    + "   • Costo: S/ " + String.format("%.2f", costo) + "\n"
                    + "   • Estado: Cita Pagada\n\n"
                    + "El servicio ha sido guardado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Error al registrar el servicio.\n"
                    + "El pago se registró pero el servicio no se guardó.\n"
                    + "Por favor, regístrelo manualmente en el historial.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (cbCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente");
            return false;
        }
        if (cbVehiculo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo");
            return false;
        }
        if (cbMecanico.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un mecánico");
            return false;
        }
        if (txtFecha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la fecha");
            return false;
        }
        if (txtHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la hora");
            return false;
        }
        try {
            LocalDate.parse(txtFecha.getText().trim());
            LocalTime.parse(txtHora.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha (YYYY-MM-DD) u hora (HH:MM) incorrecto");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText("09:00");
        txtDescripcion.setText("");
        cbEstado.setSelectedIndex(0);
        cbEstado.setEnabled(true);
        cbCliente.setEnabled(true);
        cbVehiculo.setEnabled(true);
        cbMecanico.setEnabled(true);
        txtFecha.setEnabled(true);
        txtHora.setEnabled(true);
        txtDescripcion.setEnabled(true);
        if (cbCliente.getItemCount() > 0) {
            cbCliente.setSelectedIndex(0);
        }
        selectedId = -1;
        tblCitas.clearSelection();
    }

    private void volverDashboard() {
        new DashboardForm(usuarioActual).setVisible(true);
        this.dispose();
    }
}
