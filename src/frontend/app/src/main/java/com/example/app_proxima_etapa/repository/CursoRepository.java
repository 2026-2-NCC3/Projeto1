package com.example.app_proxima_etapa.repository;

import com.example.app_proxima_etapa.model.Curso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CursoRepository {

    private final List<Curso> cursos;

    public CursoRepository() {
        cursos = new ArrayList<>();
    }

    public boolean cadastrar(Curso curso) {
        if (curso == null) {
            return false;
        }

        Curso cursoExistente = buscarPorId(curso.getId());

        if (cursoExistente != null) {
            return false;
        }

        cursos.add(curso);
        return true;
    }

    public List<Curso> listarTodos() {
        return new ArrayList<>(cursos);
    }

    public Curso buscarPorId(String id) {
        if (id == null) {
            return null;
        }

        for (Curso curso : cursos) {
            if (id.equals(curso.getId())) {
                return curso;
            }
        }

        return null;
    }

    public List<Curso> buscarPorTitulo(String titulo) {
        List<Curso> encontrados = new ArrayList<>();

        if (titulo == null) {
            return encontrados;
        }

        for (Curso curso : cursos) {
            String tituloDoCurso = curso.getTitulo();

            if (tituloDoCurso != null
                    && tituloDoCurso.toLowerCase()
                    .contains(titulo.toLowerCase())) {

                encontrados.add(curso);
            }
        }

        return encontrados;
    }

    public boolean editar(
            String id,
            String novoTitulo,
            String novaDescricao,
            int novaCargaHoraria,
            String novoLocal,
            String novaCategoria
    ) {
        Curso curso = buscarPorId(id);

        if (curso == null) {
            return false;
        }

        curso.setTitulo(novoTitulo);
        curso.setDescricao(novaDescricao);
        curso.setCargaHoraria(novaCargaHoraria);
        curso.setLocal(novoLocal);
        curso.setCategoria(novaCategoria);

        return true;
    }

    public boolean remover(String id) {
        Curso curso = buscarPorId(id);

        if (curso == null) {
            return false;
        }

        cursos.remove(curso);
        return true;
    }

    public List<Curso> listarOrdenadosPorTitulo() {
        List<Curso> cursosOrdenados =
                new ArrayList<>(cursos);

        Collections.sort(
                cursosOrdenados,
                new Comparator<Curso>() {
                    @Override
                    public int compare(
                            Curso primeiroCurso,
                            Curso segundoCurso
                    ) {
                        return primeiroCurso.getTitulo()
                                .compareToIgnoreCase(
                                        segundoCurso.getTitulo()
                                );
                    }
                }
        );

        return cursosOrdenados;
    }

    public int quantidadeDeCursos() {
        return cursos.size();
    }

    public boolean estaVazio() {
        return cursos.isEmpty();
    }
}