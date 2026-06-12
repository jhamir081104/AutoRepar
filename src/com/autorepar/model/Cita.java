package com.autorepar.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado; // PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
    private String descripcion;
    private int clienteId;
    private int vehiculoId;
    private int mecanicoId; // ID del usuario con rol MECANICO

    public Cita() {}

    public Cita(int id, LocalDate fecha, LocalTime hora, String estado, String descripcion, 
                int clienteId, int vehiculoId, int mecanicoId) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.descripcion = descripcion;
        this.clienteId = clienteId;
        this.vehiculoId = vehiculoId;
        this.mecanicoId = mecanicoId;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public int getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(int vehiculoId) { this.vehiculoId = vehiculoId; }
    public int getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(int mecanicoId) { this.mecanicoId = mecanicoId; }
}