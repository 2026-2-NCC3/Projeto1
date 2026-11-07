package com.example.app_proxima_etapa.storage;

import android.content.Context;
import android.widget.Toast;
import com.example.app_proxima_etapa.api.ApiService;
import com.example.app_proxima_etapa.api.AuthSession;
import com.example.app_proxima_etapa.api.PerfilUsuario;
import com.example.app_proxima_etapa.api.PresencaRequest;
import com.example.app_proxima_etapa.api.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SincronizadorPresencas {

    public static void sincronizar(Context contexto) {
        String token = AuthSession.getToken();
        Context app = contexto.getApplicationContext();

        if (token == null || token.isEmpty()) {
            Toast.makeText(app, "Faça login primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient
                .getRetrofitInstance()
                .create(ApiService.class);
        List<String[]> pendentes;

        try (PresencasSQLite banco = new PresencasSQLite(app)) {
            pendentes = banco.listarPendentes();
        }
        if (pendentes.isEmpty()) {
            api.consultarPerfil("Bearer " + token)
                    .enqueue(new Callback<PerfilUsuario>() {
                        @Override
                        public void onResponse(
                                Call<PerfilUsuario> call,
                                Response<PerfilUsuario> resposta) {

                            String mensagem = resposta.isSuccessful()
                                    ? "Nenhuma presença pendente. API conectada!"
                                    : "Nenhuma pendente. Erro da API: " + resposta.code();

                            Toast.makeText(app, mensagem, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(
                                Call<PerfilUsuario> call,
                                Throwable erro) {

                            Toast.makeText(app,
                                    "Nenhuma pendente. Sem conexão com a API.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

            return;
        }

        for (String[] registro : pendentes) {

            String alunoId = registro[0];
            String cursoId = registro[1];
            PresencaRequest dados = new PresencaRequest(
                    "ALUNO-" + alunoId,
                    cursoId
            );
            api.registrarPresenca("Bearer " + token, dados)
                    .enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(
                                Call<Void> call,
                                Response<Void> resposta) {
                            if (resposta.isSuccessful()
                                    || resposta.code() == 409) {
                                try (PresencasSQLite banco =
                                             new PresencasSQLite(app)) {

                                    banco.marcarSincronizado(alunoId, cursoId);
                                }
                                Toast.makeText(app,
                                        "Presença sincronizada!",
                                        Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(app,
                                        "Erro ao sincronizar: " + resposta.code(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(
                                Call<Void> call,
                                Throwable erro) {

                            Toast.makeText(app,
                                    "Sem conexão. Presença continua pendente.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
