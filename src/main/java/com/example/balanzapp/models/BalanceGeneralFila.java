package com.example.balanzapp.models;

public class BalanceGeneralFila {

    private String activo;
    private double saldoActivo;

    private String pasivo;
    private double saldoPasivo;

    private String patrimonio;
    private double saldoPatrimonio;

    public BalanceGeneralFila() {}

    public BalanceGeneralFila(String activo, double saldoActivo,
                              String pasivo, double saldoPasivo,
                              String patrimonio, double saldoPatrimonio) {
        this.activo = activo;
        this.saldoActivo = saldoActivo;
        this.pasivo = pasivo;
        this.saldoPasivo = saldoPasivo;
        this.patrimonio = patrimonio;
        this.saldoPatrimonio = saldoPatrimonio;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public double getSaldoActivo() {
        return saldoActivo;
    }

    public void setSaldoActivo(double saldoActivo) {
        this.saldoActivo = saldoActivo;
    }

    public String getPasivo() {
        return pasivo;
    }

    public void setPasivo(String pasivo) {
        this.pasivo = pasivo;
    }

    public double getSaldoPasivo() {
        return saldoPasivo;
    }

    public void setSaldoPasivo(double saldoPasivo) {
        this.saldoPasivo = saldoPasivo;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public double getSaldoPatrimonio() {
        return saldoPatrimonio;
    }

    public void setSaldoPatrimonio(double saldoPatrimonio) {
        this.saldoPatrimonio = saldoPatrimonio;
    }
}
