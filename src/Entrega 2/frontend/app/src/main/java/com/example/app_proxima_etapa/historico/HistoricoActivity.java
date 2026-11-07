package com.example.app_proxima_etapa.historico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.*;
import com.example.app_proxima_etapa.login.LoginActivity;
import com.example.app_proxima_etapa.storage.PresencasSQLite;
import java.util.List;
import retrofit2.*;

public class HistoricoActivity extends AppCompatActivity {
    private TextView status;
    private ListView lista;
    private Call<PerfilUsuario> consulta;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (AuthSession.getToken() == null) { abrirLogin(); return; }
        setContentView(R.layout.activity_historico);
        status = findViewById(R.id.statusHistorico);
        lista = findViewById(R.id.listaHistorico);
        findViewById(R.id.voltarHistorico).setOnClickListener(v -> finish());
        findViewById(R.id.atualizarHistorico).setOnClickListener(v -> carregar());
        carregar();
    }

    private void carregar() {
        findViewById(R.id.atualizarHistorico).setEnabled(false);
        status.setText("Carregando histórico…");
        if (AuthSession.getAlunoId() != null) {
            lerBanco(AuthSession.getAlunoId());
            return;
        }
        consulta = RetrofitClient.getRetrofitInstance().create(ApiService.class)
                .consultarPerfil("Bearer " + AuthSession.getToken());
        consulta.enqueue(new Callback<PerfilUsuario>() {
            @Override public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> resposta) {
                if (isFinishing() || isDestroyed()) return;
                if (resposta.code() == 401) {
                    AuthSession.clear();
                    abrirLogin();
                    return;
                }
                if (!resposta.isSuccessful() || resposta.body() == null
                        || resposta.body().getId() == null || resposta.body().getId().isEmpty()) {
                    mostrarErro("Não foi possível identificar o aluno. Toque em Atualizar.");
                    return;
                }
                AuthSession.setAlunoId(resposta.body().getId());
                lerBanco(AuthSession.getAlunoId());
            }
            @Override public void onFailure(Call<PerfilUsuario> call, Throwable erro) {
                if (!call.isCanceled()) mostrarErro("Conecte-se à internet para identificar o aluno e tente novamente.");
            }
        });
    }

    private void lerBanco(String alunoId) {
        new Thread(() -> {
            try (PresencasSQLite banco = new PresencasSQLite(getApplicationContext())) {
                List<String> registros = banco.listarHistorico(alunoId);
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    lista.setAdapter(new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1, registros));
                    status.setText(registros.isEmpty()
                            ? "Nenhuma presença salva neste aparelho para você."
                            : "Presenças salvas neste aparelho: " + registros.size());
                    findViewById(R.id.atualizarHistorico).setEnabled(true);
                });
            } catch (Exception erro) {
                runOnUiThread(() -> mostrarErro("Não foi possível ler o histórico. Tente novamente."));
            }
        }).start();
    }
    private void mostrarErro(String mensagem) {
        if (isFinishing() || isDestroyed()) return;
        status.setText(mensagem);
        findViewById(R.id.atualizarHistorico).setEnabled(true);
    }
    private void abrirLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }
    @Override protected void onDestroy() {
        if (consulta != null) consulta.cancel();
        super.onDestroy();
    }
}
