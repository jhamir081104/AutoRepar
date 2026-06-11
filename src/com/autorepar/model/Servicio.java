package com.autorepar.model;

import java.time.LocalDate;

public class Servicio {
    private int id;
    private String tipo;
    private String descripcion;
    private double costo;
    private LocalDate fecha;
    private int vehiculoId;
    private int mecanicoId;

    public Servicio() {}

    public Servicio(int id, String tipo, String descripcion, double costo, LocalDate fecha, int vehiculoId, int mecanicoId) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.fecha = fecha;
        this.vehiculoId = vehiculoId;
        this.mecanicoId = mecanicoId;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public int getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(int vehiculoId) { this.vehiculoId = vehiculoId; }
    public int getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(int mecanicoId) { this.mecanicoId = mecanicoId; }
}