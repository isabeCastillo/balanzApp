package com.example.balanzapp.models;

import java.time.LocalDate;

public class MovimientoMayor {

    private LocalDate fecha;
    private String concepto;
    private String descripcion;
    private double debe;
    private double haber;
    private double saldo;

    public MovimientoMayor(
            LocalDate fecha,
            String concepto,
            String descripcion,
            double debe,
            double haber,
            double saldo
    ) {
        this.fecha = fecha;
        this.concepto = concepto;
        this.descripcion = descripcion;
        this.debe = debe;
        this.haber = haber;
        this.saldo = saldo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getDebe() {
        return debe;
    }

    public double getHaber() {
        return haber;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDebe(double debe) {
        this.debe = debe;
    }

    public void setHaber(double haber) {
        this.haber = haber;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
