package com.autorepar.controller;

import com.autorepar.dao.*;
import com.autorepar.model.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class CitaController {

    private final CitaDAO citaDAO;
    private final ClienteDAO clienteDAO;
    private final VehiculoDAO vehiculoDAO;
    private final UsuarioDAO usuarioDAO;

    public CitaController() {
        citaDAO = new CitaDAO();
        clienteDAO = new ClienteDAO();
        vehiculoDAO = new VehiculoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    public List<Cita> listarCitas() {
        return citaDAO.listarTodas();
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public List<Vehiculo> listarVehiculosPorCliente(int clienteId) {
        return vehiculoDAO.listarPorCliente(clienteId);
    }

    public List<Usuario> listarMecanicos() {
        return usuarioDAO.listarMecanicos();
    }

    public Cliente obtenerCliente(int id) {
        return clienteDAO.obtenerPorId(id);
    }

    public Vehiculo obtenerVehiculo(int id) {
        return vehiculoDAO.obtenerPorId(id);
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
    

    public Usuario obtenerUsuario(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public boolean guardar(Cita cita) {
        if (!citaDAO.verificarDisponibilidad(
                cita.getFecha(),
                cita.getHora(),
                cita.getMecanicoId())) {
            return false;
        }

        return citaDAO.insertar(cita);
    }

    public boolean actualizar(Cita cita) {
        return citaDAO.actualizar(cita);
    }

    public boolean cancelar(int id) {
        return citaDAO.cancelar(id);
    }
}
