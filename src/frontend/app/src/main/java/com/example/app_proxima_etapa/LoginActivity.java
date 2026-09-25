package com.example.app_proxima_etapa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_proxima_etapa.api.ApiService;
import com.example.app_proxima_etapa.api.LoginRequest;
import com.example.app_proxima_etapa.api.LoginResponse;
import com.example.app_proxima_etapa.api.RetrofitClient;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnEntrar = findViewById(R.id.btnEntrar);
        TextView txtCriarConta = findViewById(R.id.txtCriarConta);

        // Ao clicar em Entrar, vai para o Dashboard (MainActivity)

        btnEntrar.setOnClickListener(v -> {
            // Pega o que o utilizador digitou
            String email = ((EditText) findViewById(R.id.edtEmailLogin)).getText().toString();
            String senha = ((EditText) findViewById(R.id.edtSenhaLogin)).getText().toString();

            // Monta o pedido
            LoginRequest request = new LoginRequest(email, senha);
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            // Faz a chamada para a internet (assíncrona para não travar a tela)
            apiService.fazerLogin(request).enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Deu certo! O backend confirmou o login
                        Toast.makeText(LoginActivity.this, "Login Realizado!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // O backend devolveu erro (ex: senha incorreta)
                        Toast.makeText(LoginActivity.this, "Erro: E-mail ou senha incorretos", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    // Erro de rede (backend offline, sem internet, etc)
                    Toast.makeText(LoginActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}