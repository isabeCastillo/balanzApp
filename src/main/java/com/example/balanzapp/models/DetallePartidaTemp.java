package com.example.balanzapp.models;

public class DetallePartidaTemp {
    private int idCuenta;
    private String nombreCuenta;
    private String descripcion;
    private double debe;
    private double haber;

    public DetallePartidaTemp(int idCuenta, String nombreCuenta,
                              String descripcion, double debe, double haber) {
        this.idCuenta = idCuenta;
        this.nombreCuenta = nombreCuenta;
        this.descripcion = descripcion;
        this.debe = debe;
        this.haber = haber;
    }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }

    public String getNombreCuenta() { return nombreCuenta; }
    public void setNombreCuenta(String nombreCuenta) { this.nombreCuenta = nombreCuenta; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getDebe() { return debe; }
    public void setDebe(double debe) { this.debe = debe; }

    public double getHaber() { return haber; }
    public void setHaber(double haber) { this.haber = haber; }

    // Para mostrar en la tabla de detalle:
    public String getCuenta() {
        return idCuenta + " - " + nombreCuenta;
    }
}
