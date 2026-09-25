package com.example.app_proxima_etapa.repository;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.model.Inscricao;

import java.util.ArrayList;
import java.util.List;

public class InscricaoRepository {

    private final List<Inscricao> inscricoes;

    public InscricaoRepository() {
        inscricoes = new ArrayList<>();
    }

    public boolean cadastrar(Inscricao inscricao) {
        if (inscricao == null) {
            return false;
        }

        if (buscarPorId(inscricao.getId()) != null) {
            return false;
        }

        if (alunoJaInscrito(
                inscricao.getAluno(),
                inscricao.getCurso()
        )) {
            return false;
        }

        inscricoes.add(inscricao);
        return true;
    }

    public List<Inscricao> listarTodas() {
        return new ArrayList<>(inscricoes);
    }

    public Inscricao buscarPorId(String id) {
        if (id == null) {
            return null;
        }

        for (Inscricao inscricao : inscricoes) {
            if (id.equals(inscricao.getId())) {
                return inscricao;
            }
        }

        return null;
    }

    public List<Inscricao> buscarPorAluno(String alunoId) {
        List<Inscricao> encontradas = new ArrayList<>();

        if (alunoId == null) {
            return encontradas;
        }

        for (Inscricao inscricao : inscricoes) {
            Aluno aluno = inscricao.getAluno();

            if (aluno != null
                    && alunoId.equals(aluno.getId())) {

                encontradas.add(inscricao);
            }
        }

        return encontradas;
    }

    public List<Inscricao> buscarPorCurso(String cursoId) {
        List<Inscricao> encontradas = new ArrayList<>();

        if (cursoId == null) {
            return encontradas;
        }

        for (Inscricao inscricao : inscricoes) {
            Curso curso = inscricao.getCurso();

            if (curso != null
                    && cursoId.equals(curso.getId())) {

                encontradas.add(inscricao);
            }
        }

        return encontradas;
    }

    public boolean alunoJaInscrito(
            Aluno aluno,
            Curso curso
    ) {
        if (aluno == null || curso == null) {
            return false;
        }

        for (Inscricao inscricao : inscricoes) {
            Aluno alunoCadastrado = inscricao.getAluno();
            Curso cursoCadastrado = inscricao.getCurso();

            if (alunoCadastrado != null
                    && cursoCadastrado != null
                    && aluno.getId().equals(
                    alunoCadastrado.getId()
            )
                    && curso.getId().equals(
                    cursoCadastrado.getId()
            )
                    && inscricao.estaAtiva()) {

                return true;
            }
        }

        return false;
    }

    public boolean cancelar(String id) {
        Inscricao inscricao = buscarPorId(id);

        if (inscricao == null) {
            return false;
        }

        inscricao.cancelar();
        return true;
    }

    public boolean confirmar(String id) {
        Inscricao inscricao = buscarPorId(id);

        if (inscricao == null) {
            return false;
        }

        inscricao.confirmar();
        return true;
    }

    public boolean remover(String id) {
        Inscricao inscricao = buscarPorId(id);

        if (inscricao == null) {
            return false;
        }

        inscricoes.remove(inscricao);
        return true;
    }

    public int quantidadeDeInscricoes() {
        return inscricoes.size();
    }

    public boolean estaVazio() {
        return inscricoes.isEmpty();
    }
}