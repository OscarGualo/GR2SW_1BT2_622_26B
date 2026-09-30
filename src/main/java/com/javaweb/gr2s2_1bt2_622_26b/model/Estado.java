package com.javaweb.gr2s2_1bt2_622_26b.model;

public enum Estado {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}