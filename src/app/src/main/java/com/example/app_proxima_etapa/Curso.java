package com.example.app_proxima_etapa;

import java.io.Serializable;
// Se implementa o "Serializable" para poder passar o objeto inteiro através dos Intents

public class Curso implements Serializable {
    private String nome;
    private int cargaHoraria;
    private int imagemResId;

    public Curso(String nome, int cargaHoraria, int imagemResId) {
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.imagemResId = imagemResId;
    }

    public String getNome() {
        return nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public int getImagemResId() {
        return imagemResId;
    }
}