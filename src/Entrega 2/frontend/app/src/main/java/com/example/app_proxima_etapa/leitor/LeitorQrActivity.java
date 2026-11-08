package com.example.app_proxima_etapa.leitor;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.app_proxima_etapa.storage.CursosCache;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.ApiService;
import com.example.app_proxima_etapa.api.AuthSession;
import com.example.app_proxima_etapa.api.PresencaRequest;
import com.example.app_proxima_etapa.api.RetrofitClient;
import com.example.app_proxima_etapa.storage.PresencasSQLite;
import com.example.app_proxima_etapa.storage.SincronizadorPresencas;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeitorQrActivity extends AppCompatActivity {

    private TextView resultado;
    private Spinner seletorCurso;
    private final List<String> idsCursos = new ArrayList<>();
    private Call<JsonArray> consultaCursos;
    private String cursoSalvo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor_qr);
        resultado = findViewById(R.id.txtResultado);
        seletorCurso = findViewById(R.id.spinnerCursoPresenca);
        cursoSalvo = savedInstanceState == null ? null : savedInstanceState.getString("curso");
        findViewById(R.id.btnLerQr).setEnabled(false);
        findViewById(R.id.btnAtualizarCursosLeitor).setOnClickListener(v -> carregarCursos());
        carregarCursos();
        SincronizadorPresencas.sincronizar(this);
        findViewById(R.id.btnLerQr).setOnClickListener(v -> {
            int posicao = seletorCurso.getSelectedItemPosition();
            if (posicao < 0 || posicao >= idsCursos.size()) {
                resultado.setText("Selecione um curso antes de escanear.");
                return;
            }
            final String curso = idsCursos.get(posicao);
            GmsBarcodeScanning.getClient(this).startScan()
                    .addOnSuccessListener(qr -> {
                        String codigo = qr.getRawValue();

                        if (codigo == null || !codigo.startsWith("ALUNO-")) {
                            resultado.setText("QR Code inválido");
                            return;
                        }

                        String alunoId = codigo.substring(6);

                        if (alunoId.isEmpty()) {
                            resultado.setText("Aluno inválido");
                            return;
                        }

                        String token = AuthSession.getToken();

                        if (token == null || token.isEmpty()) {
                            resultado.setText("Faça login primeiro");
                            return;
                        }

                        PresencaRequest dados =
                                new PresencaRequest(codigo, curso);
                        ApiService api = RetrofitClient
                                .getRetrofitInstance()
                                .create(ApiService.class);

                        resultado.setText("Verificando presença...");

                        api.registrarPresenca("Bearer " + token, dados)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(
                                            Call<Void> call,
                                            Response<Void> resposta
                                    ) {
                                        if (resposta.code() == 201) {
                                            salvarPresenca(alunoId, curso);
                                        } else if (resposta.code() == 409) {
                                            salvarPresenca(alunoId, curso);
                                        } else if (resposta.code() == 403) {
                                            resultado.setText("Acesso negado!");
                                        } else if (resposta.code() == 401) {
                                            resultado.setText(
                                                    "Faça login novamente"
                                            );

                                        } else {
                                            resultado.setText(
                                                    "Erro: " + resposta.code()
                                            );
                                        }
                                    }
                                    @Override
                                    public void onFailure(
                                            Call<Void> call,
                                            Throwable erro
                                    ) {
                                        salvarPendente(alunoId, curso);
                                    }
                                });
                    })
                    .addOnFailureListener(e ->
                            resultado.setText("Falha ao ler QR Code")
                    );
        });
        findViewById(R.id.btnVoltarLeitor)
                .setOnClickListener(v -> finish());
    }

    private void carregarCursos() {
        if (consultaCursos != null) consultaCursos.cancel();
        findViewById(R.id.btnLerQr).setEnabled(false);
        findViewById(R.id.btnAtualizarCursosLeitor).setEnabled(false);
        resultado.setText("Carregando cursos...");
        consultaCursos = RetrofitClient.getRetrofitInstance().create(ApiService.class)
                .buscarCursosDisponiveis();
        consultaCursos.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> resposta) {
                if (isFinishing() || isDestroyed() || call != consultaCursos) return;
                if (resposta.isSuccessful() && resposta.body() != null) {
                    String catalogo = resposta.body().toString();
                    new Thread(() -> {
                        try (CursosCache cache = new CursosCache(getApplicationContext())) {
                            cache.salvar(catalogo);
                        } catch (Exception erro) {
                        }
                    }).start();
                    exibirCursos(resposta.body(), false);
                } else {
                    carregarCursosSalvos(call);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable erro) {
                if (!call.isCanceled()) carregarCursosSalvos(call);
            }
        });
    }

    private void carregarCursosSalvos(Call<JsonArray> origem) {
        new Thread(() -> {
            JsonArray cursos = null;
            try (CursosCache cache = new CursosCache(getApplicationContext())) {
                String salvo = cache.ler();
                if (salvo != null) cursos = new JsonParser().parse(salvo).getAsJsonArray();
            } catch (Exception erro) {
                cursos = null;
            }
            final JsonArray salvos = cursos;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed() || consultaCursos != origem) return;
                exibirCursos(salvos == null ? new JsonArray() : salvos, true);
            });
        }).start();
    }

    private void exibirCursos(JsonArray cursos, boolean salvo) {
        if (idsCursos.size() > seletorCurso.getSelectedItemPosition()
                && seletorCurso.getSelectedItemPosition() >= 0) {
            cursoSalvo = idsCursos.get(seletorCurso.getSelectedItemPosition());
        }
        idsCursos.clear();
        List<String> nomes = new ArrayList<>();
        for (JsonElement elemento : cursos) {
            JsonObject curso = elemento.getAsJsonObject();
            if (!curso.has("id") || curso.get("id").isJsonNull()
                    || !curso.has("title") || curso.get("title").isJsonNull()) continue;
            idsCursos.add(curso.get("id").getAsString());
            nomes.add(curso.get("title").getAsString());
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nomes);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seletorCurso.setAdapter(adaptador);
        int indice = idsCursos.indexOf(cursoSalvo);
        if (indice >= 0) seletorCurso.setSelection(indice);
        findViewById(R.id.btnAtualizarCursosLeitor).setEnabled(true);
        findViewById(R.id.btnLerQr).setEnabled(!idsCursos.isEmpty());
        resultado.setText(idsCursos.isEmpty()
                ? "Nenhum curso carregado. Conecte-se e toque em Atualizar cursos."
                : salvo ? "Catálogo salvo. Selecione o curso; a presença ficará pendente se não houver conexão."
                : "Selecione o curso e escaneie o QR do aluno.");
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        int posicao = seletorCurso.getSelectedItemPosition();
        if (posicao >= 0 && posicao < idsCursos.size()) {
            estado.putString("curso", idsCursos.get(posicao));
        }
        super.onSaveInstanceState(estado);
    }

    @Override
    protected void onDestroy() {
        if (consultaCursos != null) consultaCursos.cancel();
        super.onDestroy();
    }

    private void salvarPresenca(String alunoId, String cursoId) {
        resultado.setText("Salvando presença...");
        new Thread(() -> {

            boolean salvo;

            try (PresencasSQLite banco =
                         new PresencasSQLite(getApplicationContext())) {
                salvo = banco.salvarConfirmada(alunoId, cursoId);
            } catch (Exception e) {
                salvo = false;
            }

            boolean sucesso = salvo;

            runOnUiThread(() -> {

                if (sucesso) {
                    resultado.setText(
                            "Presença confirmada e salva!"
                    );
                } else {
                    resultado.setText(
                            "Presença confirmada na API, mas não salva no celular"
                    );
                }
            });

        }).start();
    }
    private void salvarPendente(String alunoId, String cursoId) {
        resultado.setText("Sem conexão. Salvando presença...");

        new Thread(() -> {

            boolean salvo;
            boolean existe = false;

            try (PresencasSQLite banco =
                         new PresencasSQLite(getApplicationContext())) {
                salvo = banco.salvarPendente(alunoId, cursoId);

                if (!salvo) {
                    existe = banco.presencaExiste(alunoId, cursoId);
                }
            } catch (Exception e) {
                salvo = false;
            }

            boolean sucesso = salvo;
            boolean jaExiste = existe;

            runOnUiThread(() -> {

                if (sucesso) {
                    resultado.setText(
                            "Presença pendente. Será enviada quando sincronizar."
                    );
                } else if (jaExiste) {
                    resultado.setText(
                            "Presença já salva neste aparelho."
                    );

                } else {
                    resultado.setText(
                            "Erro ao salvar presença."
                    );
                }
            });

        }).start();
    }
}
