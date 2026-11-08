package com.example.app_proxima_etapa.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.app_proxima_etapa.leitor.LeitorQrActivity;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.*;
import com.example.app_proxima_etapa.login.LoginActivity;
import com.example.app_proxima_etapa.qrcode.QrCodeActivity;
import retrofit2.*;

 
public class PerfilActivity extends AppCompatActivity {
    private Call<PerfilUsuario> pending;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (AuthSession.getToken() == null) { sair(); return; }
        setContentView(R.layout.activity_perfil);
        findViewById(R.id.btnVoltarPerfil).setOnClickListener(v -> finish());
        findViewById(R.id.btnAtualizarPerfil).setOnClickListener(v -> carregar());
        findViewById(R.id.btnQrCode).setEnabled(false);
        findViewById(R.id.btnAbrirLeitor).setOnClickListener(v -> {
            Intent tela = new Intent(this, LeitorQrActivity.class);
            startActivity(tela);
        });
        findViewById(R.id.btnCardAluno).setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.example.app_proxima_etapa.recursos.CardAlunoActivity.class)));
        findViewById(R.id.btnCertificados).setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.example.app_proxima_etapa.recursos.CertificadosActivity.class)));
        findViewById(R.id.btnTestePerfil).setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.example.app_proxima_etapa.recursos.TestePerfilActivity.class)));
        findViewById(R.id.btnMensagens).setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.example.app_proxima_etapa.recursos.MensagensActivity.class)));
        findViewById(R.id.btnNotificacoes).setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.example.app_proxima_etapa.recursos.NotificacoesActivity.class)));
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
                ((TextView) findViewById(R.id.txtCpfPerfil)).setText(formatarCpf(perfil.getCpf()));
                ((TextView) findViewById(R.id.txtTelefonePerfil)).setText(formatarTelefone(perfil.getTelefone()));
                String id = perfil.getId();
                AuthSession.setAlunoId(id);
                if (id != null && !id.isEmpty()) {
                    String codigo = "ALUNO-" + id;
                    findViewById(R.id.btnQrCode).setEnabled(true);
                    findViewById(R.id.btnQrCode).setOnClickListener(v -> {
                        Intent tela = new Intent(PerfilActivity.this, QrCodeActivity.class);
                        tela.putExtra("codigoQrCode", codigo);
                        startActivity(tela);
                    });
                }

                status.setText("Dados atualizados pela Próxima Etapa.");
            }
            @Override public void onFailure(Call<PerfilUsuario> call, Throwable error) {
                if (!call.isCanceled() && !isFinishing() && !isDestroyed())
                    status.setText("Sem conexão. Confira a internet e toque em Atualizar.");
            }
        });
    }
    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return getString(R.string.perfil_nao_informado);
        }

        String numeros = cpf.replaceAll("[^0-9]", "");

        if (numeros.length() != 11) {
            return getString(R.string.perfil_nao_informado);
        }

        return numeros.substring(0, 3) + "."
                + numeros.substring(3, 6) + "."
                + numeros.substring(6, 9) + "-"
                + numeros.substring(9);
    }

    private String formatarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return getString(R.string.perfil_nao_informado);
        }

        String numeros = telefone.replaceAll("[^0-9]", "");

        if ((numeros.length() == 12 || numeros.length() == 13)
                && numeros.startsWith("55")) {
            numeros = numeros.substring(2);
        }

        if (numeros.length() != 10 && numeros.length() != 11) {
            return getString(R.string.perfil_nao_informado);
        }

        int inicioFinal = numeros.length() - 4;

        return "(" + numeros.substring(0, 2) + ") "
                + numeros.substring(2, inicioFinal) + "-"
                + numeros.substring(inicioFinal);
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
