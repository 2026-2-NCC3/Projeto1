package com.example.app_proxima_etapa.model;

public class Aluno {

    private String id;
    private String ra;
    private String nome;
    private String email;
    private String faculdade;
    private String cursoFaculdade;
    private StatusAluno status;
    private String codigoQrCode;
    private String resultadoDisc;
    private double progressoGeral;
    private int quantidadeFaltas;
    private boolean bloqueado;

    public Aluno(
            String id,
            String ra,
            String nome,
            String email,
            String faculdade,
            String cursoFaculdade
    ) {
        this.id = id;
        this.ra = ra;
        this.nome = nome;
        this.email = email;
        this.faculdade = faculdade;
        this.cursoFaculdade = cursoFaculdade;
        this.status = StatusAluno.ATIVO;
        this.codigoQrCode = "ALUNO-" + ra;
        this.resultadoDisc = "Nao realizado";
        this.progressoGeral = 0.0;
        this.quantidadeFaltas = 0;
        this.bloqueado = false;
    }

    public boolean podeAcessar() {
        return !bloqueado
                && status == StatusAluno.ATIVO;
    }

    public boolean estaSuspenso() {
        return bloqueado
                || status == StatusAluno.SUSPENSO
                || status == StatusAluno.BLOQUEADO;
    }

    public void atualizarPerfil() {
        // Será implementado quando criarmos a tela de perfil.
    }

    public double consultarProgresso() {
        return progressoGeral;
    }

    public String consultarResultadoDisc() {
        return resultadoDisc;
    }

    public void adicionarFalta() {
        quantidadeFaltas++;

        if (quantidadeFaltas >= 2) {
            bloqueado = true;
            status = StatusAluno.BLOQUEADO;
        }
    }

    public void desbloquear() {
        quantidadeFaltas = 0;
        bloqueado = false;
        status = StatusAluno.ATIVO;
    }

    public String getId() {
        return id;
    }

    public String getRa() {
        return ra;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getFaculdade() {
        return faculdade;
    }

    public String getCursoFaculdade() {
        return cursoFaculdade;
    }

    public StatusAluno getStatus() {
        return status;
    }

    public String getCodigoQrCode() {
        return codigoQrCode;
    }

    public String getResultadoDisc() {
        return resultadoDisc;
    }

    public double getProgressoGeral() {
        return progressoGeral;
    }

    public int getQuantidadeFaltas() {
        return quantidadeFaltas;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O nome do aluno e obrigatorio."
            );
        }

        this.nome = nome;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException(
                    "O e-mail informado e invalido."
            );
        }

        this.email = email;
    }

    public void setStatus(StatusAluno status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "O status do aluno e obrigatorio."
            );
        }

        this.status = status;
    }

    public void setResultadoDisc(String resultadoDisc) {
        this.resultadoDisc = resultadoDisc;
    }

    public void setProgressoGeral(double progressoGeral) {
        if (progressoGeral < 0 || progressoGeral > 100) {
            throw new IllegalArgumentException(
                    "O progresso deve estar entre 0 e 100."
            );
        }

        this.progressoGeral = progressoGeral;
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id='" + id + '\'' +
                ", ra='" + ra + '\'' +
                ", nome='" + nome + '\'' +
                ", status=" + status +
                ", bloqueado=" + bloqueado +
                '}';
    }
}