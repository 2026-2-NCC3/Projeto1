package com.example.app_proxima_etapa.service;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.model.Inscricao;
import com.example.app_proxima_etapa.repository.InscricaoRepository;

public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;

    public InscricaoService(
            InscricaoRepository inscricaoRepository
    ) {
        this.inscricaoRepository = inscricaoRepository;
    }

    public boolean realizarInscricao(
            Inscricao inscricao
    ) {
        if (inscricao == null) {
            return false;
        }

        Aluno aluno = inscricao.getAluno();
        Curso curso = inscricao.getCurso();

        if (aluno == null || curso == null) {
            return false;
        }

        if (!aluno.podeAcessar()) {
            return false;
        }

        if (!curso.isAtivo()) {
            return false;
        }

        if (!curso.possuiVagas()) {
            return false;
        }

        if (inscricaoRepository.alunoJaInscrito(
                aluno,
                curso
        )) {
            return false;
        }

        boolean cadastrada =
                inscricaoRepository.cadastrar(inscricao);

        if (!cadastrada) {
            return false;
        }

        curso.ocuparVaga();
        inscricao.confirmar();

        return true;
    }

    public boolean cancelarInscricao(String inscricaoId) {
        Inscricao inscricao =
                inscricaoRepository.buscarPorId(inscricaoId);

        if (inscricao == null) {
            return false;
        }

        if (!inscricao.estaAtiva()) {
            return false;
        }

        inscricao.cancelar();

        Curso curso = inscricao.getCurso();

        if (curso != null) {
            curso.liberarVaga();
        }

        return true;
    }

    public boolean alunoPodeSeInscrever(
            Aluno aluno,
            Curso curso
    ) {
        if (aluno == null || curso == null) {
            return false;
        }

        if (!aluno.podeAcessar()) {
            return false;
        }

        if (!curso.isAtivo()) {
            return false;
        }

        if (!curso.possuiVagas()) {
            return false;
        }

        return !inscricaoRepository.alunoJaInscrito(
                aluno,
                curso
        );
    }
}