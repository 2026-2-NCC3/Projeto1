package com.example.app_proxima_etapa.model;

import java.io.Serializable;

public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private int imagemResId;

    /*
     * Construtor completo.
     * Usado para representar todos os dados do curso.
     */
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
        this.imagemResId = 0;
    }

    /*
     * Construtor simplificado.
     * Usado pela CursoApi para criar os cursos recebidos da API.
     */
    public Curso(
            String titulo,
            int totalVagas,
            int imagemResId
    ) {
        this.id = "";
        this.titulo = titulo;
        this.descricao = "";
        this.cargaHoraria = 0;
        this.local = "";
        this.bannerUrl = "";
        this.universidadeId = "";
        this.totalVagas = totalVagas;
        this.vagasDisponiveis = totalVagas;
        this.pontosConcedidos = 0;
        this.possuiCertificado = false;
        this.categoria = "";
        this.ativo = true;
        this.imagemResId = imagemResId;
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

    /*
     * Mantido para compatibilidade com o CursoAdapter
     * e com a DetalhesActivity criados pelo grupo.
     */
    public String getNome() {
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

    public int getImagemResId() {
        return imagemResId;
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
        if (cargaHoraria < 0) {
            throw new IllegalArgumentException(
                    "A carga horaria nao pode ser negativa."
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

    public void setTotalVagas(int totalVagas) {
        if (totalVagas < 0) {
            throw new IllegalArgumentException(
                    "O total de vagas nao pode ser negativo."
            );
        }

        this.totalVagas = totalVagas;

        if (vagasDisponiveis > totalVagas) {
            vagasDisponiveis = totalVagas;
        }
    }

    public void setVagasDisponiveis(int vagasDisponiveis) {
        if (vagasDisponiveis < 0) {
            throw new IllegalArgumentException(
                    "As vagas disponiveis nao podem ser negativas."
            );
        }

        if (vagasDisponiveis > totalVagas) {
            throw new IllegalArgumentException(
                    "As vagas disponiveis nao podem superar o total."
            );
        }

        this.vagasDisponiveis = vagasDisponiveis;
    }

    public void setPontosConcedidos(int pontosConcedidos) {
        if (pontosConcedidos < 0) {
            throw new IllegalArgumentException(
                    "Os pontos nao podem ser negativos."
            );
        }

        this.pontosConcedidos = pontosConcedidos;
    }

    public void setPossuiCertificado(
            boolean possuiCertificado
    ) {
        this.possuiCertificado = possuiCertificado;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setImagemResId(int imagemResId) {
        this.imagemResId = imagemResId;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", local='" + local + '\'' +
                ", totalVagas=" + totalVagas +
                ", vagasDisponiveis=" + vagasDisponiveis +
                ", ativo=" + ativo +
                '}';
    }
}