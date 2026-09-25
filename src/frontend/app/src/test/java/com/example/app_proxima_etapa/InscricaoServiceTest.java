package com.example.app_proxima_etapa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.model.Inscricao;
import com.example.app_proxima_etapa.repository.InscricaoRepository;
import com.example.app_proxima_etapa.service.InscricaoService;

import org.junit.Before;
import org.junit.Test;

public class InscricaoServiceTest {

    private InscricaoRepository repository;
    private InscricaoService service;
    private Aluno aluno;
    private Curso curso;

    @Before
    public void prepararTeste() {
        repository = new InscricaoRepository();
        service = new InscricaoService(repository);

        aluno = new Aluno(
                "aluno-1",
                "25028270",
                "Arthur Ferreira",
                "arthur@email.com",
                "Faculdade Exemplo",
                "Ciencia da Computacao"
        );

        curso = new Curso(
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
    }

    @Test
    public void deveRealizarInscricao() {
        Inscricao inscricao = criarInscricao("inscricao-1");

        boolean resultado =
                service.realizarInscricao(inscricao);

        assertTrue(resultado);
        assertEquals(1, repository.quantidadeDeInscricoes());
        assertEquals("CONFIRMADA", inscricao.getStatus());
    }

    @Test
    public void deveDiminuirUmaVagaAoRealizarInscricao() {
        int vagasAntes = curso.getVagasDisponiveis();

        Inscricao inscricao = criarInscricao("inscricao-1");

        boolean resultado =
                service.realizarInscricao(inscricao);

        assertTrue(resultado);
        assertEquals(
                vagasAntes - 1,
                curso.getVagasDisponiveis()
        );
    }

    @Test
    public void naoDevePermitirInscricaoDuplicada() {
        Inscricao primeiraInscricao =
                criarInscricao("inscricao-1");

        Inscricao segundaInscricao =
                criarInscricao("inscricao-2");

        assertTrue(
                service.realizarInscricao(primeiraInscricao)
        );

        assertFalse(
                service.realizarInscricao(segundaInscricao)
        );

        assertEquals(1, repository.quantidadeDeInscricoes());
    }

    @Test
    public void naoDeveInscreverAlunoBloqueado() {
        aluno.adicionarFalta();
        aluno.adicionarFalta();

        Inscricao inscricao = criarInscricao("inscricao-1");

        boolean resultado =
                service.realizarInscricao(inscricao);

        assertFalse(resultado);
        assertEquals(0, repository.quantidadeDeInscricoes());
        assertEquals(30, curso.getVagasDisponiveis());
    }

    @Test
    public void naoDeveInscreverEmCursoInativo() {
        curso.setAtivo(false);

        Inscricao inscricao = criarInscricao("inscricao-1");

        boolean resultado =
                service.realizarInscricao(inscricao);

        assertFalse(resultado);
        assertEquals(0, repository.quantidadeDeInscricoes());
    }

    @Test
    public void naoDeveInscreverEmCursoSemVagas() {
        Curso cursoSemVagas = new Curso(
                "curso-2",
                "Curso Lotado",
                "Curso sem vagas",
                10,
                "Sala 2",
                "",
                "universidade-1",
                0,
                50,
                false,
                "Tecnologia"
        );

        Inscricao inscricao = new Inscricao(
                "inscricao-1",
                aluno,
                cursoSemVagas,
                "25/09/2026"
        );

        boolean resultado =
                service.realizarInscricao(inscricao);

        assertFalse(resultado);
        assertEquals(0, repository.quantidadeDeInscricoes());
    }

    @Test
    public void deveCancelarInscricao() {
        Inscricao inscricao = criarInscricao("inscricao-1");

        assertTrue(service.realizarInscricao(inscricao));
        assertTrue(
                service.cancelarInscricao("inscricao-1")
        );

        assertEquals("CANCELADA", inscricao.getStatus());
        assertFalse(inscricao.estaAtiva());
    }

    @Test
    public void deveDevolverVagaAoCancelarInscricao() {
        Inscricao inscricao = criarInscricao("inscricao-1");

        int vagasIniciais = curso.getVagasDisponiveis();

        assertTrue(service.realizarInscricao(inscricao));

        assertEquals(
                vagasIniciais - 1,
                curso.getVagasDisponiveis()
        );

        assertTrue(
                service.cancelarInscricao("inscricao-1")
        );

        assertEquals(
                vagasIniciais,
                curso.getVagasDisponiveis()
        );
    }

    @Test
    public void deveInformarQueAlunoPodeSeInscrever() {
        assertTrue(
                service.alunoPodeSeInscrever(aluno, curso)
        );
    }

    @Test
    public void deveInformarQueAlunoBloqueadoNaoPodeSeInscrever() {
        aluno.adicionarFalta();
        aluno.adicionarFalta();

        assertFalse(
                service.alunoPodeSeInscrever(aluno, curso)
        );
    }

    private Inscricao criarInscricao(String id) {
        return new Inscricao(
                id,
                aluno,
                curso,
                "25/09/2026"
        );
    }
}