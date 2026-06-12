package com.autorepar.controller;

import com.autorepar.dao.ServicioDAO;
import com.autorepar.model.Servicio;
import java.time.LocalDate;
import java.util.List;

public class ReportController {

    private final ServicioDAO servicioDAO;

    public ReportController() {
        servicioDAO = new ServicioDAO();
    }

    public List<Servicio> obtenerServiciosPorRango(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        return servicioDAO.listarPorRangoFechas(
                fechaInicio,
                fechaFin);
    }

    public double obtenerTotalPorRango(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        return servicioDAO.obtenerTotalServiciosPorRango(
                fechaInicio,
                fechaFin);
    }
}
