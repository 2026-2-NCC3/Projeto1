package com.example.app_proxima_etapa.leitor;

import android.os.Bundle;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor_qr);
        resultado = findViewById(R.id.txtResultado);
        SincronizadorPresencas.sincronizar(this);
        findViewById(R.id.btnLerQr).setOnClickListener(v -> {

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

                        String curso =
                                "a3441667-c7b7-4ab1-b7d6-a3de93edfe61";
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
