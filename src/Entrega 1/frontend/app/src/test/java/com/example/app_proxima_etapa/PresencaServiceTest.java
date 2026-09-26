package com.example.app_proxima_etapa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.model.Atividade;
import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.model.Presenca;
import com.example.app_proxima_etapa.model.TipoAtividade;
import com.example.app_proxima_etapa.repository.PresencaRepository;
import com.example.app_proxima_etapa.service.PresencaService;

import org.junit.Before;
import org.junit.Test;

public class PresencaServiceTest {

    private PresencaRepository repository;
    private PresencaService service;
    private Aluno aluno;
    private Curso curso;
    private Atividade atividade;

    @Before
    public void prepararTeste() {
        repository = new PresencaRepository();
        service = new PresencaService(repository);

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

        atividade = new Atividade(
                "atividade-1",
                "Aula de Logica",
                "Primeira aula do curso",
                "25/09/2026 19:00",
                "Sala 1",
                TipoAtividade.AULA,
                curso
        );
    }

    @Test
    public void deveRegistrarPresenca() {
        Presenca presenca =
                criarPresenca("presenca-1");

        boolean resultado =
                service.registrarPresenca(presenca);

        assertTrue(resultado);
        assertEquals(
                1,
                repository.quantidadeDePresencas()
        );
    }

    @Test
    public void naoDeveRegistrarPresencaDuplicada() {
        Presenca primeiraPresenca =
                criarPresenca("presenca-1");

        Presenca segundaPresenca =
                criarPresenca("presenca-2");

        assertTrue(
                service.registrarPresenca(primeiraPresenca)
        );

        assertFalse(
                service.registrarPresenca(segundaPresenca)
        );

        assertEquals(
                1,
                repository.quantidadeDePresencas()
        );
    }

    @Test
    public void deveValidarQrCodeCorreto() {
        String codigoQrCode =
                aluno.getCodigoQrCode();

        assertTrue(
                service.validarQrCode(
                        aluno,
                        codigoQrCode
                )
        );
    }

    @Test
    public void naoDeveValidarQrCodeIncorreto() {
        assertFalse(
                service.validarQrCode(
                        aluno,
                        "CODIGO-INCORRETO"
                )
        );
    }

    @Test
    public void naoDeveValidarQrCodeDeAlunoBloqueado() {
        aluno.adicionarFalta();
        aluno.adicionarFalta();

        assertFalse(
                service.validarQrCode(
                        aluno,
                        aluno.getCodigoQrCode()
                )
        );
    }

    @Test
    public void naoDeveRegistrarPresencaDeAlunoBloqueado() {
        aluno.adicionarFalta();
        aluno.adicionarFalta();

        Presenca presenca =
                criarPresenca("presenca-1");

        boolean resultado =
                service.registrarPresenca(presenca);

        assertFalse(resultado);
        assertEquals(
                0,
                repository.quantidadeDePresencas()
        );
    }

    @Test
    public void deveBuscarPresencaPorAluno() {
        Presenca presenca =
                criarPresenca("presenca-1");

        assertTrue(service.registrarPresenca(presenca));

        assertEquals(
                1,
                repository
                        .buscarPorAluno("aluno-1")
                        .size()
        );
    }

    @Test
    public void deveBuscarPresencaPorAtividade() {
        Presenca presenca =
                criarPresenca("presenca-1");

        assertTrue(service.registrarPresenca(presenca));

        assertEquals(
                1,
                repository
                        .buscarPorAtividade("atividade-1")
                        .size()
        );
    }

    @Test
    public void deveRemoverPresenca() {
        Presenca presenca =
                criarPresenca("presenca-1");

        assertTrue(service.registrarPresenca(presenca));

        assertTrue(
                service.removerPresenca("presenca-1")
        );

        assertEquals(
                0,
                repository.quantidadeDePresencas()
        );
    }

    @Test
    public void devePermitirRegistroAntesDaPresenca() {
        assertTrue(
                service.podeRegistrarEntrada(
                        aluno,
                        atividade
                )
        );
    }

    @Test
    public void naoDevePermitirNovoRegistroDepoisDaPresenca() {
        Presenca presenca =
                criarPresenca("presenca-1");

        assertTrue(service.registrarPresenca(presenca));

        assertFalse(
                service.podeRegistrarEntrada(
                        aluno,
                        atividade
                )
        );
    }

    private Presenca criarPresenca(String id) {
        return new Presenca(
                id,
                aluno,
                atividade,
                "25/09/2026 19:05",
                aluno.getCodigoQrCode()
        );
    }
}