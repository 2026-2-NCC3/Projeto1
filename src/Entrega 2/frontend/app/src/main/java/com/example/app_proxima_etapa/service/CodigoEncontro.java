package com.example.app_proxima_etapa.service;

public final class CodigoEncontro {
    private static final String UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private CodigoEncontro() {}
    public static String encontro(String codigo, String selecionado) {
        if (codigo == null) throw new IllegalArgumentException("Informe um QR válido.");
        if (codigo.matches("ENCONTRO-" + UUID)) return codigo.substring(9);
        if (codigo.matches("ALUNO-" + UUID) && selecionado != null && selecionado.matches(UUID)) return selecionado;
        throw new IllegalArgumentException("Leia o QR do encontro ou selecione um encontro e leia o QR do aluno.");
    }
}
