package com.example.app_proxima_etapa;

import java.io.Serializable;
// Se implementa o "Serializable" para poder passar o objeto inteiro através dos Intents

public class Curso implements Serializable {
    private String nome;
    private int totalVagas;
    private int imagemResId;

    public Curso(String nome, int totalVagas, int imagemResId) {
        this.nome = nome;
        this.totalVagas = totalVagas;
        this.imagemResId = imagemResId;
    }

    public String getNome() {
        return nome;
    }

    public int getTotalVagas() {
        return totalVagas;
    }

    public int getImagemResId() {
        return imagemResId;
    }
}