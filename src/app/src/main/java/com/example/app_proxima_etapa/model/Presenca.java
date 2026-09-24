package com.example.app_proxima_etapa.model;

public class Presenca {

    private String id;
    private Aluno aluno;
    private Atividade atividade;
    private String dataHoraRegistro;
    private String codigoValidacao;

    public Presenca(
            String id,
            Aluno aluno,
            Atividade atividade,
            String dataHoraRegistro,
            String codigoValidacao
    ) {
        this.id = id;
        this.aluno = aluno;
        this.atividade = atividade;
        this.dataHoraRegistro = dataHoraRegistro;
        this.codigoValidacao = codigoValidacao;
    }

    public boolean validarEntrada() {
        return aluno != null
                && atividade != null
                && codigoValidacao != null
                && !codigoValidacao.trim().isEmpty()
                && aluno.podeAcessar();
    }

    public String consultarRegistro() {
        return "Aluno: " + aluno.getNome()
                + " | Atividade: " + atividade.getTitulo()
                + " | Registro: " + dataHoraRegistro;
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Atividade getAtividade() {
        return atividade;
    }

    public String getDataHoraRegistro() {
        return dataHoraRegistro;
    }

    public String getCodigoValidacao() {
        return codigoValidacao;
    }

    public void setDataHoraRegistro(String dataHoraRegistro) {
        this.dataHoraRegistro = dataHoraRegistro;
    }

    public void setCodigoValidacao(String codigoValidacao) {
        this.codigoValidacao = codigoValidacao;
    }
}