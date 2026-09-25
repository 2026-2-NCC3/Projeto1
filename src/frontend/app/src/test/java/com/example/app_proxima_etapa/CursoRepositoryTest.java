package com.example.app_proxima_etapa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.repository.CursoRepository;

import org.junit.Before;
import org.junit.Test;

public class CursoRepositoryTest {

    private CursoRepository repository;
    private Curso cursoProgramacao;
    private Curso cursoCurriculo;

    @Before
    public void prepararTeste() {
        repository = new CursoRepository();

        cursoProgramacao = new Curso(
                "curso-1",
                "Introducao a Programacao",
                "Curso para iniciantes",
                20,
                "Sala 1",
                "",
                "universidade-1",
                30,
                100,
                true,
                "Tecnologia"
        );

        cursoCurriculo = new Curso(
                "curso-2",
                "Oficina de Curriculo",
                "Oficina de preparacao profissional",
                8,
                "Sala 2",
                "",
                "universidade-1",
                20,
                50,
                true,
                "Carreira"
        );
    }

    @Test
    public void deveCadastrarCurso() {
        boolean cadastrado =
                repository.cadastrar(cursoProgramacao);

        assertTrue(cadastrado);
        assertEquals(1, repository.quantidadeDeCursos());
    }

    @Test
    public void naoDeveCadastrarIdDuplicado() {
        Curso cursoDuplicado = new Curso(
                "curso-1",
                "Outro Curso",
                "Curso com ID repetido",
                10,
                "Sala 3",
                "",
                "universidade-2",
                15,
                20,
                false,
                "Outros"
        );

        assertTrue(repository.cadastrar(cursoProgramacao));
        assertFalse(repository.cadastrar(cursoDuplicado));
        assertEquals(1, repository.quantidadeDeCursos());
    }

    @Test
    public void deveBuscarCursoPorId() {
        repository.cadastrar(cursoProgramacao);

        Curso encontrado =
                repository.buscarPorId("curso-1");

        assertNotNull(encontrado);
        assertEquals(
                "Introducao a Programacao",
                encontrado.getTitulo()
        );
    }

    @Test
    public void deveRetornarNullParaCursoInexistente() {
        Curso encontrado =
                repository.buscarPorId("curso-999");

        assertNull(encontrado);
    }

    @Test
    public void deveBuscarCursoPorTitulo() {
        repository.cadastrar(cursoProgramacao);
        repository.cadastrar(cursoCurriculo);

        assertEquals(
                1,
                repository
                        .buscarPorTitulo("programacao")
                        .size()
        );

        assertEquals(
                "Introducao a Programacao",
                repository
                        .buscarPorTitulo("programacao")
                        .get(0)
                        .getTitulo()
        );
    }

    @Test
    public void deveEditarCurso() {
        repository.cadastrar(cursoProgramacao);

        boolean editado = repository.editar(
                "curso-1",
                "Programacao Java",
                "Curso atualizado",
                30,
                "Laboratorio 2",
                "Tecnologia"
        );

        Curso cursoEditado =
                repository.buscarPorId("curso-1");

        assertTrue(editado);
        assertNotNull(cursoEditado);
        assertEquals(
                "Programacao Java",
                cursoEditado.getTitulo()
        );
        assertEquals(
                "Laboratorio 2",
                cursoEditado.getLocal()
        );
        assertEquals(
                30,
                cursoEditado.getCargaHoraria()
        );
    }

    @Test
    public void deveRemoverCurso() {
        repository.cadastrar(cursoProgramacao);

        boolean removido =
                repository.remover("curso-1");

        assertTrue(removido);
        assertTrue(repository.estaVazio());
        assertNull(repository.buscarPorId("curso-1"));
    }

    @Test
    public void deveOrdenarCursosPorTitulo() {
        repository.cadastrar(cursoProgramacao);
        repository.cadastrar(cursoCurriculo);

        assertEquals(
                "Introducao a Programacao",
                repository
                        .listarOrdenadosPorTitulo()
                        .get(0)
                        .getTitulo()
        );

        assertEquals(
                "Oficina de Curriculo",
                repository
                        .listarOrdenadosPorTitulo()
                        .get(1)
                        .getTitulo()
        );
    }

    @Test
    public void deveOcuparUmaVaga() {
        int vagasAntes =
                cursoProgramacao.getVagasDisponiveis();

        cursoProgramacao.ocuparVaga();

        assertEquals(
                vagasAntes - 1,
                cursoProgramacao.getVagasDisponiveis()
        );
    }

    @Test
    public void deveLiberarUmaVaga() {
        cursoProgramacao.ocuparVaga();

        int vagasDepoisDeOcupar =
                cursoProgramacao.getVagasDisponiveis();

        cursoProgramacao.liberarVaga();

        assertEquals(
                vagasDepoisDeOcupar + 1,
                cursoProgramacao.getVagasDisponiveis()
        );
    }

    @Test
    public void deveInformarQueCursoPossuiVagas() {
        assertTrue(cursoProgramacao.possuiVagas());
    }
}