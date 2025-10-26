package com.example.balanzapp.models;

public class Cuenta {
    private int idCuenta;
    private String codigo;
    private String nombre;
    private String tipo;
    private String grupo;

    public Cuenta() {
    }

    public Cuenta(int idCuenta, String codigo, String nombre, String tipo, String grupo) {
        this.idCuenta = idCuenta;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.grupo = grupo;
    }
    public Cuenta(String codigo, String nombre, String tipo, String grupo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.grupo = grupo;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}