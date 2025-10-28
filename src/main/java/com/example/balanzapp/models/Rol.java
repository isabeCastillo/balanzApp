package com.example.balanzapp.models;

public class Rol {
    private int id_rol;
    private String nombre_rol;
    private String descripcion;
    private int nivel_acceso;

    public Rol() {
    }

    public Rol(int id_rol, String nombre_rol, String descripcion, int nivel_acceso) {
        this.id_rol = id_rol;
        this.nombre_rol = nombre_rol;
        this.descripcion = descripcion;
        this.nivel_acceso = nivel_acceso;
    }
    @Override
    public String toString() {
        return nombre_rol;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre_rol() {
        return nombre_rol;
    }

    public void setNombre_rol(String nombre_rol) {
        this.nombre_rol = nombre_rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNivel_acceso() {
        return nivel_acceso;
    }

    public void setNivel_acceso(int nivel_acceso) {
        this.nivel_acceso = nivel_acceso;
    }
}
