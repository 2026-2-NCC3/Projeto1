package com.example.app_proxima_etapa;

import com.example.app_proxima_etapa.model.Curso;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetalhesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        ImageView imgCurso = findViewById(R.id.imgDetalheCurso);
        TextView txtNome = findViewById(R.id.txtDetalheNome);
        TextView txtCarga = findViewById(R.id.txtDetalheCargaHoraria);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        if (getIntent().hasExtra("CURSO_SELECIONADO")) {
            Curso curso = (Curso) getIntent().getSerializableExtra("CURSO_SELECIONADO");

            if (curso != null) {
                txtNome.setText(curso.getNome());
                txtCarga.setText("Total de vagas: " + curso.getTotalVagas());
                imgCurso.setImageResource(curso.getImagemResId());
            }
        }
        btnVoltar.setOnClickListener(v -> finish());
    }
}