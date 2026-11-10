
package com.example.app_proxima_etapa.leitor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.api.*;
import com.example.app_proxima_etapa.storage.*;
import com.google.gson.*;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeitorQrActivity extends AppCompatActivity {
    private TextView resultado;
    private Spinner seletorCurso;
    private EditText campoCodigoAluno;
    private List<String> idsCursos = new ArrayList<>();
    private Call<JsonArray> consultaCursos;
    private String cursoSalvo;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor_qr);
        resultado = findViewById(R.id.txtResultado);
        seletorCurso = findViewById(R.id.spinnerCursoPresenca);
        campoCodigoAluno = findViewById(R.id.campoCodigoAluno);
        api = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        if (savedInstanceState != null) {
            cursoSalvo = savedInstanceState.getString("curso");
        }

        findViewById(R.id.btnLerQr).setEnabled(false);
        findViewById(R.id.btnRegistrarManual).setEnabled(false);
        findViewById(R.id.btnAtualizarCursosLeitor).setOnClickListener(v -> carregarCursos());
        findViewById(R.id.btnLerQr).setOnClickListener(v -> {
            GmsBarcodeScanning.getClient(this).startScan()
                    .addOnSuccessListener(qr -> registrarPresenca(qr.getRawValue()))
                    .addOnFailureListener(erro -> resultado.setText("Erro ao ler QR Code."));
        });
        findViewById(R.id.btnRegistrarManual).setOnClickListener(v -> {
            String codigo = campoCodigoAluno.getText().toString().trim();
            registrarPresenca(codigo);
        });
        findViewById(R.id.btnVoltarLeitor).setOnClickListener(v -> finish());
        carregarCursos();
        SincronizadorPresencas.sincronizar(this);
    }
    private void registrarPresenca(String codigo) {
        int posicao = seletorCurso.getSelectedItemPosition();

        if (posicao < 0 || posicao >= idsCursos.size()) {
            resultado.setText("Selecione um curso.");
            return;
        }
        if (codigo == null || !codigo.startsWith("ALUNO-") || codigo.length() <= 6) {
            resultado.setText("Código inválido.");
            return;
        }
        String token = AuthSession.getToken();
        if (token == null || token.isEmpty()) {
            resultado.setText("Faça login primeiro.");
            return;
        }
        String alunoId = codigo.substring(6);
        String cursoId = idsCursos.get(posicao);
        PresencaRequest dados = new PresencaRequest(codigo, cursoId);
        resultado.setText("Verificando presença...");

        api.registrarPresenca("Bearer " + token, dados).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resposta) {
                int status = resposta.code();
                if (status == 201 || status == 409) {
                    salvarNoCelular(alunoId, cursoId, true);
                } else if (status == 401) {
                    resultado.setText("Faça login novamente.");
                } else if (status == 403) {
                    resultado.setText("Acesso negado.");
                } else if (status == 404) {
                    resultado.setText("Aluno não inscrito.");
                } else {
                    resultado.setText("Erro: " + status);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable erro) {
                salvarNoCelular(alunoId, cursoId, false);
            }
        });
    }
    private void salvarNoCelular(String alunoId, String cursoId, boolean confirmado) {
        resultado.setText("Salvando presença...");
        new Thread(() -> {
            boolean salvo = false;
            boolean existe = false;
            try (PresencasSQLite banco = new PresencasSQLite(getApplicationContext())) {
                if (confirmado) {
                    salvo = banco.salvarConfirmada(alunoId, cursoId);
                } else {
                    salvo = banco.salvarPendente(alunoId, cursoId);
                    if (!salvo) {
                        existe = banco.presencaExiste(alunoId, cursoId);
                    }
                }
            } catch (Exception erro) {
            }
            boolean sucesso = salvo;
            boolean jaExiste = existe;
            runOnUiThread(() -> {
                if (confirmado) {
                    resultado.setText(sucesso
                            ? "Presença confirmada e salva!"
                            : "Confirmada na API, mas não salva no celular.");
                } else if (sucesso) {
                    resultado.setText("Sem conexão. Presença salva como pendente.");
                } else if (jaExiste) {
                    resultado.setText("Presença já salva neste aparelho.");
                } else {
                    resultado.setText("Erro ao salvar presença.");
                }
            });
        }).start();
    }
    private void carregarCursos() {
        if (consultaCursos != null) {
            consultaCursos.cancel();
        }
        resultado.setText("Carregando cursos...");
        findViewById(R.id.btnLerQr).setEnabled(false);
        findViewById(R.id.btnRegistrarManual).setEnabled(false);
        findViewById(R.id.btnAtualizarCursosLeitor).setEnabled(false);
        consultaCursos = api.buscarCursosDisponiveis();
        consultaCursos.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> resposta) {
                if (call != consultaCursos || isFinishing()) return;
                if (resposta.isSuccessful() && resposta.body() != null) {
                    JsonArray cursos = resposta.body();

                    new Thread(() -> {
                        try (CursosCache cache = new CursosCache(getApplicationContext())) {
                            cache.salvar(cursos.toString());
                        } catch (Exception erro) {
                        }
                    }).start();
                    mostrarCursos(cursos);
                } else {
                    carregarCache(call);
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable erro) {
                if (!call.isCanceled()) {
                    carregarCache(call);
                }
            }
        });
    }
    private void carregarCache(Call<JsonArray> chamada) {
        new Thread(() -> {
            JsonArray cursos = new JsonArray();
            try (CursosCache cache = new CursosCache(getApplicationContext())) {
                String salvo = cache.ler();
                if (salvo != null) {
                    cursos = new JsonParser().parse(salvo).getAsJsonArray();
                }
            } catch (Exception erro) {
            }
            JsonArray lista = cursos;
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed() && chamada == consultaCursos) {
                    mostrarCursos(lista);
                }
            });
        }).start();
    }
    private void mostrarCursos(JsonArray cursos) {
        int posicao = seletorCurso.getSelectedItemPosition();
        if (posicao >= 0 && posicao < idsCursos.size()) {
            cursoSalvo = idsCursos.get(posicao);
        }
        idsCursos.clear();
        List<String> nomes = new ArrayList<>();

        for (JsonElement item : cursos) {
            JsonObject curso = item.getAsJsonObject();
            if (!curso.has("id") || curso.get("id").isJsonNull()
                    || !curso.has("title") || curso.get("title").isJsonNull()) {
                continue;
            }
            idsCursos.add(curso.get("id").getAsString());
            nomes.add(curso.get("title").getAsString());
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nomes);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seletorCurso.setAdapter(adaptador);

        int indice = idsCursos.indexOf(cursoSalvo);
        if (indice >= 0) {
            seletorCurso.setSelection(indice);
        }

        boolean temCursos = !idsCursos.isEmpty();
        findViewById(R.id.btnLerQr).setEnabled(temCursos);
        findViewById(R.id.btnRegistrarManual).setEnabled(temCursos);
        findViewById(R.id.btnAtualizarCursosLeitor).setEnabled(true);

        resultado.setText(temCursos
                ? "Selecione o curso e informe o código do aluno."
                : "Nenhum curso encontrado.");
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
        if (consultaCursos != null) {
            consultaCursos.cancel();
        }
        super.onDestroy();
    }
}
