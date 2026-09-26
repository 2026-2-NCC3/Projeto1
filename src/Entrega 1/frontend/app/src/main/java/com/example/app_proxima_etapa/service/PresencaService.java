package com.example.app_proxima_etapa.service;

import com.example.app_proxima_etapa.model.Aluno;
import com.example.app_proxima_etapa.model.Atividade;
import com.example.app_proxima_etapa.model.Presenca;
import com.example.app_proxima_etapa.repository.PresencaRepository;

public class PresencaService {

    private final PresencaRepository presencaRepository;

    public PresencaService(
            PresencaRepository presencaRepository
    ) {
        this.presencaRepository = presencaRepository;
    }

    public boolean registrarPresenca(
            Presenca presenca
    ) {
        if (presenca == null) {
            return false;
        }

        Aluno aluno = presenca.getAluno();
        Atividade atividade = presenca.getAtividade();

        if (aluno == null || atividade == null) {
            return false;
        }

        if (!aluno.podeAcessar()) {
            return false;
        }

        if (!presenca.validarEntrada()) {
            return false;
        }

        if (presencaRepository.presencaJaRegistrada(
                aluno,
                atividade
        )) {
            return false;
        }

        return presencaRepository.cadastrar(presenca);
    }

    public boolean validarQrCode(
            Aluno aluno,
            String codigoQrCode
    ) {
        if (aluno == null || codigoQrCode == null) {
            return false;
        }

        if (!aluno.podeAcessar()) {
            return false;
        }

        String codigoDoAluno = aluno.getCodigoQrCode();

        if (codigoDoAluno == null) {
            return false;
        }

        return codigoDoAluno.equals(codigoQrCode);
    }

    public boolean podeRegistrarEntrada(
            Aluno aluno,
            Atividade atividade
    ) {
        if (aluno == null || atividade == null) {
            return false;
        }

        if (!aluno.podeAcessar()) {
            return false;
        }

        return !presencaRepository.presencaJaRegistrada(
                aluno,
                atividade
        );
    }

    public boolean removerPresenca(String presencaId) {
        if (presencaId == null) {
            return false;
        }

        return presencaRepository.remover(presencaId);
    }
}