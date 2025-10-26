package com.example.balanzapp.models;

import java.time.LocalDate;

public class Partida {
    private int idPartida;
    private LocalDate fecha;
    private String concepto;
    private String cuenta;
    private double debe;
    private double haber;

    public Partida() {
    }

    public Partida(int idPartida, LocalDate fecha, String concepto, String cuenta, double debe, double haber) {
        this.idPartida = idPartida;
        this.fecha = fecha;
        this.concepto = concepto;
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getDebe() {
        return debe;
    }

    public void setDebe(double debe) {
        this.debe = debe;
    }

    public double getHaber() {
        return haber;
    }

    public void setHaber(double haber) {
        this.haber = haber;
    }
}
