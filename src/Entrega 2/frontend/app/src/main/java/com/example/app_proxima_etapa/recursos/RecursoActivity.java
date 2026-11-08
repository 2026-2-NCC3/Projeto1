package com.example.app_proxima_etapa.recursos;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.AuthSession;
import com.example.app_proxima_etapa.api.AuthErrors;
import com.example.app_proxima_etapa.api.RecursosApi;
import com.example.app_proxima_etapa.api.RetrofitClient;
import com.example.app_proxima_etapa.login.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RecursoActivity extends AppCompatActivity {
    protected LinearLayout conteudo;
    protected TextView status;
    protected MaterialButton atualizar;
    private final List<Call<JsonElement>> chamadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        if (AuthSession.getToken() == null) {
            encerrarSessao();
            return;
        }
        setContentView(R.layout.activity_recursos);
        conteudo = findViewById(R.id.conteudoRecurso);
        status = findViewById(R.id.statusRecurso);
        atualizar = findViewById(R.id.atualizarRecurso);
        ((TextView) findViewById(R.id.tituloRecurso)).setText(titulo());
        findViewById(R.id.voltarRecurso).setOnClickListener(v -> finish());
        atualizar.setOnClickListener(v -> carregar());
        carregar();
    }

    protected abstract String titulo();
    protected abstract void carregar();

    protected interface Retorno {
        void receber(JsonElement dados);
    }

    protected void consultar(String caminho, Retorno retorno) {
        executar(api().consultar(caminho, token()), retorno, null);
    }

    protected void enviar(String caminho, JsonObject dados, Retorno retorno) {
        enviar(caminho, dados, retorno, null);
    }

    protected void enviar(String caminho, JsonObject dados, Retorno retorno,
                          Runnable aoFalhar) {
        executar(api().enviar(caminho, token(), dados), retorno, aoFalhar);
    }

    private RecursosApi api() {
        return RetrofitClient.getRetrofitInstance().create(RecursosApi.class);
    }

    private String token() {
        return "Bearer " + AuthSession.getToken();
    }

    private void executar(Call<JsonElement> chamada, Retorno retorno, Runnable aoFalhar) {
        status.setText("Aguarde...");
        atualizar.setEnabled(false);
        chamadas.add(chamada);
        chamada.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> resposta) {
                chamadas.remove(call);
                if (isFinishing() || isDestroyed()) return;
                atualizar.setEnabled(true);
                if (resposta.code() == 401) {
                    encerrarSessao();
                    return;
                }
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    String alternativa = resposta.code() == 404
                            ? "Este recurso ainda não foi publicado no servidor."
                            : "Não foi possível concluir. Confira os dados e tente novamente.";
                    status.setText(AuthErrors.message(resposta, alternativa));
                    if (aoFalhar != null) aoFalhar.run();
                    return;
                }
                status.setText("");
                retorno.receber(resposta.body());
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable erro) {
                chamadas.remove(call);
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                atualizar.setEnabled(true);
                status.setText("Sem conexão com o servidor. Tente atualizar. Se você estava enviando, confira a lista antes de tentar novamente.");
                if (aoFalhar != null) aoFalhar.run();
            }
        });
    }

    protected TextView texto(String valor, boolean destaque) {
        TextView campo = new TextView(this);
        campo.setText(valor);
        campo.setTextSize(destaque ? 20 : 16);
        campo.setTextColor(getColor(R.color.perfil_texto));
        campo.setPadding(0, 20, 0, 12);
        if (destaque) campo.setTypeface(null, Typeface.BOLD);
        conteudo.addView(campo);
        return campo;
    }

    protected MaterialButton botao(String rotulo) {
        MaterialButton botao = new MaterialButton(this);
        botao.setText(rotulo);
        botao.setAllCaps(false);
        botao.setMinHeight((int) (48 * getResources().getDisplayMetrics().density));
        conteudo.addView(botao);
        return botao;
    }

    protected String campo(JsonObject objeto, String nome) {
        return objeto.has(nome) && !objeto.get(nome).isJsonNull()
                ? objeto.get(nome).getAsString() : "Não informado";
    }

    private void encerrarSessao() {
        AuthSession.clear();
        Intent login = new Intent(this, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    @Override
    protected void onDestroy() {
        for (Call<JsonElement> chamada : chamadas) chamada.cancel();
        super.onDestroy();
    }
}
