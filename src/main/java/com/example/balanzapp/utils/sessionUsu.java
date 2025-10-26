package com.example.balanzapp.utils;

import com.example.balanzapp.models.Usuario;

public class sessionUsu {
    private static Usuario usuarioActivo;

    public static void setUsuarioActivo(Usuario usuario) {
        usuarioActivo = usuario;
    }

    public static Usuario getUsuarioActivo() {
        return usuarioActivo;
    }
}
