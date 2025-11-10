package com.example.balanzapp.models;

import java.time.LocalDate;

public class DocumentoTabla {

    private int idDocumento;
    private String nombreArchivo;
    private String clasificacion;
    private LocalDate fecha;
    private String usuario;
    private String rutaArchivo;

    public DocumentoTabla(int idDocumento,
                          String nombreArchivo,
                          String clasificacion,
                          LocalDate fecha,
                          String usuario,
                          String rutaArchivo) {
        this.idDocumento = idDocumento;
        this.nombreArchivo = nombreArchivo;
        this.clasificacion = clasificacion;
        this.fecha = fecha;
        this.usuario = usuario;
        this.rutaArchivo = rutaArchivo;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
