package com.example.app_proxima_etapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.api.*;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Call<LoginResponse> pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AuthSession.clear();
        Button entrar = findViewById(R.id.btnEntrar);
        TextView criar = findViewById(R.id.txtCriarConta);
        EditText emailField = findViewById(R.id.edtEmailLogin);
        EditText passwordField = findViewById(R.id.edtSenhaLogin);
        criar.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));

        entrar.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim().toLowerCase(Locale.ROOT);
            String password = passwordField.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Informe um e-mail válido.");
                return;
            }
            if (password.isEmpty()) {
                passwordField.setError("Informe a senha.");
                return;
            }
            entrar.setEnabled(false);
            criar.setEnabled(false);
            entrar.setText("Entrando...");
            pending = RetrofitClient.getRetrofitInstance().create(ApiService.class)
                    .fazerLogin(new LoginRequest(email, password));
            pending.enqueue(new Callback<LoginResponse>() {
                @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (isFinishing() || isDestroyed()) return;
                    entrar.setEnabled(true);
                    criar.setEnabled(true);
                    entrar.setText("Entrar");
                    LoginResponse body = response.body();
                    if (response.isSuccessful() && body != null
                            && body.getToken() != null && !body.getToken().isEmpty()) {
                        AuthSession.setToken(body.getToken());
                        passwordField.setText("");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, AuthErrors.message(response), Toast.LENGTH_LONG).show();
                    }
                }
                @Override public void onFailure(Call<LoginResponse> call, Throwable error) {
                    if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                    entrar.setEnabled(true);
                    criar.setEnabled(true);
                    entrar.setText("Entrar");
                    Toast.makeText(LoginActivity.this,
                            "Não foi possível conectar. Confira a internet e tente novamente.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override protected void onDestroy() {
        if (pending != null) pending.cancel();
        super.onDestroy();
    }
}
