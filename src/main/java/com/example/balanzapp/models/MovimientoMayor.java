package com.example.balanzapp.models;

import java.time.LocalDate;

public class MovimientoMayor {
    private LocalDate fecha;
    private String concepto;
    private String descripcion;
    private double debe;
    private double haber;
    private double saldo;
    private String documento;

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getDebe() { return debe; }
    public void setDebe(double debe) { this.debe = debe; }

    public double getHaber() { return haber; }
    public void setHaber(double haber) { this.haber = haber; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
}
