package com.example.balanzapp.models;

public class BalanceComprobacion {

    private String codigo;
    private String cuenta;
    private double debe;
    private double haber;

    public BalanceComprobacion() {
    }

    public BalanceComprobacion(String codigo, String cuenta, double debe, double haber) {
        this.codigo = codigo;
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
