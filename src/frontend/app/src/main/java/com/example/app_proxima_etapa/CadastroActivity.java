package com.example.app_proxima_etapa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> {
            // Em vez de ir para outra tela, mostramos uma mensagem e voltamos ao Login
            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
            finish(); // Encerra a tela de registo e volta para o ecrã anterior (Login)
        });
    }
}