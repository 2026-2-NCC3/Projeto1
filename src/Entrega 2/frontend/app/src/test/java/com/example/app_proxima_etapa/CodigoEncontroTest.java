package com.example.app_proxima_etapa;

import com.example.app_proxima_etapa.service.CodigoEncontro;
import org.junit.Test;
import static org.junit.Assert.*;

public class CodigoEncontroTest {
    private final String encontro = "22222222-2222-4222-8222-222222222222";
    private final String aluno = "11111111-1111-4111-8111-111111111111";
    @Test public void qrDoEncontroIndependeDoSeletor() {
        assertEquals(encontro, CodigoEncontro.encontro("ENCONTRO-" + encontro, null));
    }
    @Test public void qrDoAlunoExigeEncontroSelecionado() {
        assertEquals(encontro, CodigoEncontro.encontro("ALUNO-" + aluno, encontro));
        assertThrows(IllegalArgumentException.class, () -> CodigoEncontro.encontro("ALUNO-" + aluno, null));
    }
    @Test public void qrMalformadoNaoViraRequisicao() {
        assertThrows(IllegalArgumentException.class, () -> CodigoEncontro.encontro("ENCONTRO-invalido", encontro));
        assertThrows(IllegalArgumentException.class, () -> CodigoEncontro.encontro(null, encontro));
    }
}
