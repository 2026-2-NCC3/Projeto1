package com.example.app_proxima_etapa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.repository.AlunoRepository;

import org.junit.Before;
import org.junit.Test;

public class AlunoRepositoryTest {

    private AlunoRepository repository;
    private Aluno alunoArthur;
    private Aluno alunoBeatriz;

    @Before
    public void prepararTeste() {
        repository = new AlunoRepository();

        alunoArthur = new Aluno(
                "1",
                "25028270",
                "Arthur Ferreira",
                "arthur@email.com",
                "Faculdade Exemplo",
                "Ciencia da Computacao"
        );

        alunoBeatriz = new Aluno(
                "2",
                "25028271",
                "Beatriz Silva",
                "beatriz@email.com",
                "Faculdade Exemplo",
                "Administracao"
        );
    }

    @Test
    public void deveCadastrarAluno() {
        boolean cadastrado =
                repository.cadastrar(alunoArthur);

        assertTrue(cadastrado);
        assertEquals(1, repository.quantidadeDeAlunos());
    }

    @Test
    public void naoDeveCadastrarRaDuplicado() {
        Aluno alunoDuplicado = new Aluno(
                "3",
                "25028270",
                "Outro Aluno",
                "outro@email.com",
                "Faculdade Exemplo",
                "Direito"
        );

        assertTrue(repository.cadastrar(alunoArthur));
        assertFalse(repository.cadastrar(alunoDuplicado));
        assertEquals(1, repository.quantidadeDeAlunos());
    }

    @Test
    public void deveBuscarAlunoPorId() {
        repository.cadastrar(alunoArthur);

        Aluno encontrado = repository.buscarPorId("1");

        assertNotNull(encontrado);
        assertEquals(
                "Arthur Ferreira",
                encontrado.getNome()
        );
    }

    @Test
    public void deveBuscarAlunoPorRa() {
        repository.cadastrar(alunoArthur);

        Aluno encontrado =
                repository.buscarPorRa("25028270");

        assertNotNull(encontrado);
        assertEquals(
                "25028270",
                encontrado.getRa()
        );
    }

    @Test
    public void deveRetornarNullParaAlunoInexistente() {
        Aluno encontrado =
                repository.buscarPorId("999");

        assertNull(encontrado);
    }

    @Test
    public void deveEditarAluno() {
        repository.cadastrar(alunoArthur);

        boolean editado = repository.editar(
                "1",
                "Arthur H. Ferreira",
                "arthur.novo@email.com"
        );

        assertTrue(editado);
        assertEquals(
                "Arthur H. Ferreira",
                repository.buscarPorId("1").getNome()
        );
        assertEquals(
                "arthur.novo@email.com",
                repository.buscarPorId("1").getEmail()
        );
    }

    @Test
    public void deveRemoverAluno() {
        repository.cadastrar(alunoArthur);

        boolean removido = repository.remover("1");

        assertTrue(removido);
        assertTrue(repository.estaVazio());
        assertNull(repository.buscarPorId("1"));
    }

    @Test
    public void deveOrdenarAlunosPorNome() {
        repository.cadastrar(alunoBeatriz);
        repository.cadastrar(alunoArthur);

        assertEquals(
                "Arthur Ferreira",
                repository
                        .listarOrdenadosPorNome()
                        .get(0)
                        .getNome()
        );

        assertEquals(
                "Beatriz Silva",
                repository
                        .listarOrdenadosPorNome()
                        .get(1)
                        .getNome()
        );
    }

    @Test
    public void deveBloquearAlunoDepoisDeDuasFaltas() {
        alunoArthur.adicionarFalta();
        alunoArthur.adicionarFalta();

        assertTrue(alunoArthur.isBloqueado());
        assertFalse(alunoArthur.podeAcessar());
        assertEquals(2, alunoArthur.getQuantidadeFaltas());
    }
}