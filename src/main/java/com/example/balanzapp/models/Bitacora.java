package com.example.balanzapp.models;

public class Bitacora {
    private String usuario;
    private String accionRealizada;
    private String rol;
    private String modulo;
    private String fecha;
    private String hora;

    public Bitacora(String usuario, String accionRealizada, String rol, String modulo, String fecha, String hora) {
        this.usuario = usuario;
        this.accionRealizada = accionRealizada;
        this.rol = rol;
        this.modulo = modulo;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getUsuario() { return usuario; }
    public String getAccionRealizada() { return accionRealizada; }
    public String getRol() { return rol; }
    public String getModulo() { return modulo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }

    public void setAccionRealizada(String accionRealizada) {
        this.accionRealizada = accionRealizada;
    }
}
