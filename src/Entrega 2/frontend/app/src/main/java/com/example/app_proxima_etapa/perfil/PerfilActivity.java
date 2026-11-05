package com.example.app_proxima_etapa.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.*;
import com.example.app_proxima_etapa.login.LoginActivity;
import retrofit2.*;

/** Consulta o próprio usuário. O servidor obtém sua identidade pelo token. */
public class PerfilActivity extends AppCompatActivity {
    private Call<PerfilUsuario> pending;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (AuthSession.getToken() == null) { sair(); return; }
        setContentView(R.layout.activity_perfil);
        findViewById(R.id.btnVoltarPerfil).setOnClickListener(v -> finish());
        findViewById(R.id.btnAtualizarPerfil).setOnClickListener(v -> carregar());
        carregar();
    }
    private void carregar() {
        if (pending != null) pending.cancel();
        TextView status = findViewById(R.id.txtStatusPerfil);
        status.setText("Carregando perfil…");
        pending = RetrofitClient.getRetrofitInstance().create(ApiService.class)
                .consultarPerfil("Bearer " + AuthSession.getToken());
        pending.enqueue(new Callback<PerfilUsuario>() {
            @Override public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                if (isFinishing() || isDestroyed()) return;
                if (response.code() == 401) { AuthSession.clear(); sair(); return; }
                if (!response.isSuccessful() || response.body() == null) {
                    status.setText("Não foi possível consultar o perfil. Tente novamente."); return;
                }
                PerfilUsuario perfil = response.body();
                ((TextView) findViewById(R.id.txtNomePerfil)).setText(perfil.getName());
                ((TextView) findViewById(R.id.txtEmailPerfil)).setText(perfil.getEmail());
                status.setText("Dados atualizados pela Próxima Etapa.");
            }
            @Override public void onFailure(Call<PerfilUsuario> call, Throwable error) {
                if (!call.isCanceled() && !isFinishing() && !isDestroyed())
                    status.setText("Sem conexão. Confira a internet e toque em Atualizar.");
            }
        });
    }
    private void sair() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent); finish();
    }
    @Override protected void onDestroy() {
        if (pending != null) pending.cancel();
        super.onDestroy();
    }
}
