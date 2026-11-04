package com.example.app_proxima_etapa.repository;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.model.Atividade;
import com.example.app_proxima_etapa.model.Presenca;

import java.util.ArrayList;
import java.util.List;

public class PresencaRepository {

    private final List<Presenca> presencas;

    public PresencaRepository() {
        presencas = new ArrayList<>();
    }

    public boolean cadastrar(Presenca presenca) {
        if (presenca == null) {
            return false;
        }

        if (buscarPorId(presenca.getId()) != null) {
            return false;
        }

        if (presencaJaRegistrada(
                presenca.getAluno(),
                presenca.getAtividade()
        )) {
            return false;
        }

        presencas.add(presenca);
        return true;
    }

    public List<Presenca> listarTodas() {
        return new ArrayList<>(presencas);
    }

    public Presenca buscarPorId(String id) {
        if (id == null) {
            return null;
        }

        for (Presenca presenca : presencas) {
            if (id.equals(presenca.getId())) {
                return presenca;
            }
        }

        return null;
    }

    public List<Presenca> buscarPorAluno(String alunoId) {
        List<Presenca> encontradas = new ArrayList<>();

        if (alunoId == null) {
            return encontradas;
        }

        for (Presenca presenca : presencas) {
            Aluno aluno = presenca.getAluno();

            if (aluno != null
                    && alunoId.equals(aluno.getId())) {

                encontradas.add(presenca);
            }
        }

        return encontradas;
    }

    public List<Presenca> buscarPorAtividade(
            String atividadeId
    ) {
        List<Presenca> encontradas = new ArrayList<>();

        if (atividadeId == null) {
            return encontradas;
        }

        for (Presenca presenca : presencas) {
            Atividade atividade = presenca.getAtividade();

            if (atividade != null
                    && atividadeId.equals(atividade.getId())) {

                encontradas.add(presenca);
            }
        }

        return encontradas;
    }

    public boolean presencaJaRegistrada(
            Aluno aluno,
            Atividade atividade
    ) {
        if (aluno == null || atividade == null) {
            return false;
        }

        for (Presenca presenca : presencas) {
            Aluno alunoRegistrado = presenca.getAluno();
            Atividade atividadeRegistrada =
                    presenca.getAtividade();

            if (alunoRegistrado != null
                    && atividadeRegistrada != null
                    && aluno.getId().equals(
                    alunoRegistrado.getId()
            )
                    && atividade.getId().equals(
                    atividadeRegistrada.getId()
            )) {

                return true;
            }
        }

        return false;
    }

    public boolean editarCodigoValidacao(
            String id,
            String novoCodigo
    ) {
        Presenca presenca = buscarPorId(id);

        if (presenca == null) {
            return false;
        }

        presenca.setCodigoValidacao(novoCodigo);
        return true;
    }

    public boolean remover(String id) {
        Presenca presenca = buscarPorId(id);

        if (presenca == null) {
            return false;
        }

        presencas.remove(presenca);
        return true;
    }

    public int quantidadeDePresencas() {
        return presencas.size();
    }

    public boolean estaVazio() {
        return presencas.isEmpty();
    }
}