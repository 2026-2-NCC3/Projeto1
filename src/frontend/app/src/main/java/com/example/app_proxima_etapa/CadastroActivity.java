package com.example.app_proxima_etapa;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.api.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {
    private Call<LoginResponse> pending;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Button registrar = findViewById(R.id.btnRegistrar);
        EditText nomeField = findViewById(R.id.edtNomeCadastro);
        EditText emailField = findViewById(R.id.edtEmailCadastro);
        EditText senhaField = findViewById(R.id.edtSenhaCadastro);

        registrar.setOnClickListener(v -> {
            String nome = nomeField.getText().toString().trim();
            String email = emailField.getText().toString().trim().toLowerCase(Locale.ROOT);
            String senha = senhaField.getText().toString();
            if (nome.isEmpty()) {
                nomeField.setError("Informe seu nome.");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Informe um e-mail válido.");
                return;
            }
            if (senha.length() < 8 || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
                senhaField.setError("Use pelo menos 8 caracteres e no máximo 72 bytes.");
                return;
            }
            registrar.setEnabled(false);
            registrar.setText("Cadastrando...");
            pending = RetrofitClient.getRetrofitInstance().create(ApiService.class)
                    .cadastrar(new RegisterRequest(nome, email, senha));
            pending.enqueue(new Callback<LoginResponse>() {
                @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (isFinishing() || isDestroyed()) return;
                    registrar.setEnabled(true);
                    registrar.setText("Criar Conta");
                    if (response.code() == 201 && response.body() != null) {
                        senhaField.setText("");
                        Toast.makeText(CadastroActivity.this,
                                "Conta criada! Entre com seu e-mail e senha.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(CadastroActivity.this, AuthErrors.message(response), Toast.LENGTH_LONG).show();
                    }
                }
                @Override public void onFailure(Call<LoginResponse> call, Throwable error) {
                    if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                    registrar.setEnabled(true);
                    registrar.setText("Criar Conta");
                    Toast.makeText(CadastroActivity.this,
                            "Não foi possível confirmar o cadastro. Confira a conexão. Se já enviou, tente entrar.",
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override protected void onDestroy() {
        if (pending != null) pending.cancel();
        super.onDestroy();
    }
}
