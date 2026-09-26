package com.example.app_proxima_etapa.repository;

import com.example.app_proxima_etapa.model.Aluno;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlunoRepository {

    private final List<Aluno> alunos;

    public AlunoRepository() {
        alunos = new ArrayList<>();
    }

    public boolean cadastrar(Aluno aluno) {
        if (aluno == null) {
            return false;
        }

        if (buscarPorId(aluno.getId()) != null) {
            return false;
        }

        if (buscarPorRa(aluno.getRa()) != null) {
            return false;
        }

        alunos.add(aluno);
        return true;
    }

    public List<Aluno> listarTodos() {
        return new ArrayList<>(alunos);
    }

    public Aluno buscarPorId(String id) {
        for (Aluno aluno : alunos) {
            if (aluno.getId().equals(id)) {
                return aluno;
            }
        }

        return null;
    }

    public Aluno buscarPorRa(String ra) {
        for (Aluno aluno : alunos) {
            if (aluno.getRa().equalsIgnoreCase(ra)) {
                return aluno;
            }
        }

        return null;
    }

    public List<Aluno> buscarPorNome(String nome) {
        List<Aluno> encontrados = new ArrayList<>();

        if (nome == null) {
            return encontrados;
        }

        for (Aluno aluno : alunos) {
            if (aluno.getNome()
                    .toLowerCase()
                    .contains(nome.toLowerCase())) {

                encontrados.add(aluno);
            }
        }

        return encontrados;
    }

    public boolean editar(
            String id,
            String novoNome,
            String novoEmail
    ) {
        Aluno aluno = buscarPorId(id);

        if (aluno == null) {
            return false;
        }

        aluno.setNome(novoNome);
        aluno.setEmail(novoEmail);

        return true;
    }

    public boolean remover(String id) {
        Aluno aluno = buscarPorId(id);

        if (aluno == null) {
            return false;
        }

        return alunos.remove(aluno);
    }

    public List<Aluno> listarOrdenadosPorNome() {
        List<Aluno> alunosOrdenados =
                new ArrayList<>(alunos);

        alunosOrdenados.sort(
                Comparator.comparing(
                        Aluno::getNome,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return alunosOrdenados;
    }

    public int quantidadeDeAlunos() {
        return alunos.size();
    }

    public boolean estaVazio() {
        return alunos.isEmpty();
    }
}