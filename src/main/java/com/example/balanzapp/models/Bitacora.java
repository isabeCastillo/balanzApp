package com.example.balanzapp.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Bitacora {

    private String usuario;
    private String rol;
    private String accion;
    private String modulo;
    private LocalDate fecha;
    private LocalTime hora;

    public Bitacora() {
    }

    public Bitacora(String usuario,
                    String rol,
                    String accion,
                    String modulo,
                    LocalDate fecha,
                    LocalTime hora) {
        this.usuario = usuario;
        this.rol = rol;
        this.accion = accion;
        this.modulo = modulo;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
}
