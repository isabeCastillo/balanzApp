package com.example.balanzapp.models;

public class EstadoResultadoFila {

    private String cuenta;
    private double debe;
    private double haber;
    private double saldo;

    public EstadoResultadoFila() {}

    public EstadoResultadoFila(String cuenta, double debe, double haber, double saldo) {
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
        this.saldo = saldo;
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

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
