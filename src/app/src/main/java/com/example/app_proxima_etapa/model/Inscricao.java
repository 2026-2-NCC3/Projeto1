package com.example.app_proxima_etapa.model;

public class Inscricao {

    private String id;
    private Aluno aluno;
    private Curso curso;
    private String dataInscricao;
    private String status;
    private String dataPresenca;
    private int pontosGanhos;

    public Inscricao(
            String id,
            Aluno aluno,
            Curso curso,
            String dataInscricao
    ) {
        this.id = id;
        this.aluno = aluno;
        this.curso = curso;
        this.dataInscricao = dataInscricao;
        this.status = "INSCRITO";
        this.dataPresenca = null;
        this.pontosGanhos = 0;
    }

    public void confirmar() {
        status = "CONFIRMADA";
    }

    public void cancelar() {
        status = "CANCELADA";
    }

    public boolean estaAtiva() {
        return status.equals("INSCRITO")
                || status.equals("CONFIRMADA");
    }

    public String getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public String getDataInscricao() {
        return dataInscricao;
    }

    public String getStatus() {
        return status;
    }

    public String getDataPresenca() {
        return dataPresenca;
    }

    public int getPontosGanhos() {
        return pontosGanhos;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDataPresenca(String dataPresenca) {
        this.dataPresenca = dataPresenca;
    }

    public void setPontosGanhos(int pontosGanhos) {
        this.pontosGanhos = pontosGanhos;
    }
}