package com.example.balanzapp.models;

import java.time.LocalDate;

public class Partida {

    private int idPartida;
    private int numeroPartida;
    private LocalDate fecha;
    private String concepto;
    private String cuenta;
    private double debe;
    private double haber;
    private String documento;
    private String tipoPartida;

    public Partida() {
    }

    public Partida(int idPartida, int numeroPartida, LocalDate fecha,
                   String concepto, String cuenta, double debe, double haber,
                   String documento, String tipoPartida) {
        this.idPartida = idPartida;
        this.numeroPartida = numeroPartida;
        this.fecha = fecha;
        this.concepto = concepto;
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
        this.documento = documento;
        this.tipoPartida = tipoPartida;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public int getNumeroPartida() {
        return numeroPartida;
    }

    public void setNumeroPartida(int numeroPartida) {
        this.numeroPartida = numeroPartida;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipoPartida() { return tipoPartida; }

    public void setTipoPartida(String tipoPartida) { this.tipoPartida = tipoPartida; }
}