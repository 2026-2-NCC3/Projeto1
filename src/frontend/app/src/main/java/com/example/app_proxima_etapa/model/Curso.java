package com.example.app_proxima_etapa.model;

public class Curso {

    private String id;
    private String titulo;
    private String descricao;
    private int cargaHoraria;
    private String local;
    private String bannerUrl;
    private String universidadeId;
    private int totalVagas;
    private int vagasDisponiveis;
    private int pontosConcedidos;
    private boolean possuiCertificado;
    private String categoria;
    private boolean ativo;

    public Curso(
            String id,
            String titulo,
            String descricao,
            int cargaHoraria,
            String local,
            String bannerUrl,
            String universidadeId,
            int totalVagas,
            int pontosConcedidos,
            boolean possuiCertificado,
            String categoria
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
        this.local = local;
        this.bannerUrl = bannerUrl;
        this.universidadeId = universidadeId;
        this.totalVagas = totalVagas;
        this.vagasDisponiveis = totalVagas;
        this.pontosConcedidos = pontosConcedidos;
        this.possuiCertificado = possuiCertificado;
        this.categoria = categoria;
        this.ativo = true;
    }

    public void atualizarDados(
            String titulo,
            String descricao,
            int cargaHoraria,
            String local,
            String categoria
    ) {
        setTitulo(titulo);
        setDescricao(descricao);
        setCargaHoraria(cargaHoraria);
        setLocal(local);
        setCategoria(categoria);
    }

    public boolean possuiVagas() {
        return ativo && vagasDisponiveis > 0;
    }

    public void ocuparVaga() {
        if (!ativo) {
            throw new IllegalStateException(
                    "O curso esta inativo."
            );
        }

        if (!possuiVagas()) {
            throw new IllegalStateException(
                    "O curso nao possui vagas disponiveis."
            );
        }

        vagasDisponiveis--;
    }

    public void liberarVaga() {
        if (vagasDisponiveis < totalVagas) {
            vagasDisponiveis++;
        }
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

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public String getLocal() {
        return local;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getUniversidadeId() {
        return universidadeId;
    }

    public int getTotalVagas() {
        return totalVagas;
    }

    public int getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public int getPontosConcedidos() {
        return pontosConcedidos;
    }

    public boolean isPossuiCertificado() {
        return possuiCertificado;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O titulo do curso e obrigatorio."
            );
        }

        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCargaHoraria(int cargaHoraria) {
        if (cargaHoraria <= 0) {
            throw new IllegalArgumentException(
                    "A carga horaria deve ser maior que zero."
            );
        }

        this.cargaHoraria = cargaHoraria;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public void setUniversidadeId(String universidadeId) {
        this.universidadeId = universidadeId;
    }

    public void setPontosConcedidos(int pontosConcedidos) {
        if (pontosConcedidos < 0) {
            throw new IllegalArgumentException(
                    "Os pontos nao podem ser negativos."
            );
        }

        this.pontosConcedidos = pontosConcedidos;
    }

    public void setPossuiCertificado(boolean possuiCertificado) {
        this.possuiCertificado = possuiCertificado;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", local='" + local + '\'' +
                ", vagasDisponiveis=" + vagasDisponiveis +
                ", ativo=" + ativo +
                '}';
    }
}