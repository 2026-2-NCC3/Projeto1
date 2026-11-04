package com.example.app_proxima_etapa.model;

public class Atividade {

    private String id;
    private String titulo;
    private String descricao;
    private String dataHora;
    private String local;
    private TipoAtividade tipo;
    private Curso curso;

    public Atividade(
            String id,
            String titulo,
            String descricao,
            String dataHora,
            String local,
            TipoAtividade tipo,
            Curso curso
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.local = local;
        this.tipo = tipo;
        this.curso = curso;
    }

    public void alterarDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String consultarInformacoes() {
        return titulo + " - " + dataHora + " - " + local;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDataHora() {
        return dataHora;
    }

    public String getLocal() {
        return local;
    }

    public TipoAtividade getTipo() {
        return tipo;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setTipo(TipoAtividade tipo) {
        this.tipo = tipo;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}