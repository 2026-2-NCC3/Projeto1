package com.example.app_proxima_etapa.leitor;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.*;
import com.example.app_proxima_etapa.service.CodigoEncontro;
import com.example.app_proxima_etapa.storage.FilaEncontros;
import com.google.gson.*;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Response;

public class LeitorQrActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<String> ids = new ArrayList<>();
    private TextView resultado;
    private Spinner seletor;
    private EditText codigo;
    private ApiService api;
    private String dono;
    private String token;
    private boolean ocupado;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_leitor_qr);
        resultado = findViewById(R.id.txtResultado);
        seletor = findViewById(R.id.spinnerCursoPresenca);
        codigo = findViewById(R.id.campoCodigoAluno);
        api = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        dono = AuthSession.getAlunoId();
        token = AuthSession.getToken();
        findViewById(R.id.btnVoltarLeitor).setOnClickListener(v -> finish());
        if (dono == null || token == null) {
            resultado.setText("Abra o perfil e entre novamente para registrar presença.");
            habilitar(false);
            return;
        }
        findViewById(R.id.btnLerQr).setOnClickListener(v -> {
            if (ocupado) return;
            GmsBarcodeScanning.getClient(this).startScan()
                    .addOnSuccessListener(qr -> registrar(qr.getRawValue()))
                    .addOnFailureListener(error -> resultado.setText("Leitura não concluída. Tente novamente."));
        });
        findViewById(R.id.btnRegistrarManual).setOnClickListener(v -> registrar(codigo.getText().toString().trim()));
        findViewById(R.id.btnAtualizarCursosLeitor).setOnClickListener(v -> atualizar());
        findViewById(R.id.btnHistoricoEncontros).setOnClickListener(v -> historico());
        atualizar();
    }

    private boolean sessaoAtual() {
        return dono != null && dono.equals(AuthSession.getAlunoId()) && token.equals(AuthSession.getToken());
    }
    private void habilitar(boolean enabled) {
        for (int id : new int[]{R.id.btnLerQr, R.id.btnRegistrarManual, R.id.btnAtualizarCursosLeitor, R.id.btnHistoricoEncontros}) findViewById(id).setEnabled(enabled);
        seletor.setEnabled(enabled);
    }
    private void iniciar() { ocupado = true; habilitar(false); resultado.setText("Aguarde…"); }
    private void terminar(String message) {
        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            ocupado = false;
            habilitar(sessaoAtual());
            resultado.setText(sessaoAtual() ? message : "A sessão mudou. Abra esta tela novamente.");
        });
    }
    private void registrar(String value) {
        if (ocupado || !sessaoAtual()) return;
        String encontro;
        try {
            int position = seletor.getSelectedItemPosition();
            encontro = CodigoEncontro.encontro(value, position >= 0 && position < ids.size() ? ids.get(position) : null);
        } catch (IllegalArgumentException error) { resultado.setText(error.getMessage()); return; }
        iniciar();
        executor.execute(() -> {
            try (FilaEncontros fila = new FilaEncontros(getApplicationContext())) {
                fila.salvar(dono, encontro, value);
                terminar(enviar(fila, encontro, value));
            } catch (Exception error) { terminar("Não foi possível salvar no aparelho. Nenhuma confirmação foi obtida."); }
        });
    }
    private String enviar(FilaEncontros fila, String encontro, String value) {
        if (!sessaoAtual()) return "A sessão mudou. A presença continua pendente.";
        try {
            Response<EncontroResposta> response = api.registrarPresencaEncontro("Bearer " + token, encontro, new EncontroRequest(value)).execute();
            if (response.isSuccessful() && response.body() != null && response.body().isConfirmado()) {
                fila.confirmar(dono, encontro, value);
                return "Presença confirmada no encontro.";
            }
            if (response.code() == 401) return "Entre novamente. A presença continua pendente.";
            if (response.code() == 403) return "Acesso negado. Um aluno deve ler o QR do encontro; somente responsáveis registram outro aluno.";
            if (response.code() == 422) return "Não confirmada: confira a inscrição e se o encontro está aberto. O registro continua pendente.";
            return "Não confirmada pela API (" + response.code() + "). Atualize para tentar sincronizar.";
        } catch (Exception error) { return "Sem confirmação da API. Presença salva como pendente; atualize quando a conexão voltar."; }
    }
    private void atualizar() {
        if (ocupado || !sessaoAtual()) return;
        iniciar();
        executor.execute(() -> {
            String info = "";
            try (FilaEncontros fila = new FilaEncontros(getApplicationContext())) {
                for (String[] item : fila.listar(dono)) info = enviar(fila, item[0], item[1]);
                int pendentes = fila.listar(dono).size();
                info = "Pendentes: " + pendentes + ". " + info;
            } catch (Exception error) { info = "Não foi possível consultar a fila local."; }
            JsonArray encontros = null;
            boolean cache = false;
            try {
                Response<JsonArray> response = api.buscarEncontros("Bearer " + token).execute();
                if (response.isSuccessful() && response.body() != null) {
                    encontros = response.body();
                    getSharedPreferences("encontros_cache", MODE_PRIVATE).edit().putString(dono, encontros.toString()).apply();
                } else if (response.code() == 401 || response.code() == 403) {
                    terminar("Acesso negado. Entre novamente."); return;
                }
            } catch (Exception ignored) { }
            if (encontros == null) {
                cache = true;
                try { encontros = new JsonParser().parse(getSharedPreferences("encontros_cache", MODE_PRIVATE).getString(dono, "[]")).getAsJsonArray(); }
                catch (Exception ignored) { encontros = new JsonArray(); }
            }
            JsonArray rows = encontros;
            String message = (cache ? "Sem atualização da API. Exibindo encontros salvos. " : "Escolha o encontro para ler o QR de um aluno, ou leia o QR do encontro para sua própria presença. ") + info;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed() || !sessaoAtual()) return;
                int position = seletor.getSelectedItemPosition();
                String previous = position >= 0 && position < ids.size() ? ids.get(position) : null;
                ids.clear();
                List<String> labels = new ArrayList<>();
                for (JsonElement element : rows) {
                    try {
                        JsonObject item = element.getAsJsonObject();
                        String id = item.get("id").getAsString();
                        String label = item.get("curso").getAsString() + " — " + item.get("titulo").getAsString() + " (" + item.get("status").getAsString() + ")";
                        ids.add(id); labels.add(label);
                    } catch (Exception ignored) { }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                seletor.setAdapter(adapter);
                int index = ids.indexOf(previous);
                if (index >= 0) seletor.setSelection(index);
                terminar(message);
            });
        });
    }
    private void historico() {
        if (ocupado || !sessaoAtual()) return;
        iniciar();
        executor.execute(() -> {
            try {
                Response<JsonArray> response = api.buscarHistoricoEncontros("Bearer " + token).execute();
                if (!response.isSuccessful() || response.body() == null) { terminar("Não foi possível consultar o histórico."); return; }
                StringBuilder text = new StringBuilder("Suas presenças confirmadas por encontro:\n");
                for (JsonElement element : response.body()) {
                    JsonObject item = element.getAsJsonObject();
                    text.append(item.get("curso").getAsString()).append(" — ").append(item.get("encontro").getAsString()).append("\n").append(item.get("data_hora").getAsString()).append("\n\n");
                }
                if (response.body().size() == 0) text.append("Nenhuma presença confirmada.");
                terminar(text.toString());
            } catch (Exception error) { terminar("Sem conexão. Não foi possível consultar o histórico no servidor."); }
        });
    }
    @Override protected void onDestroy() { executor.shutdown(); super.onDestroy(); }
}
