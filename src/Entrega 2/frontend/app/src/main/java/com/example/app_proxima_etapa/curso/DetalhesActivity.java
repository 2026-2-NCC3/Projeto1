package com.example.app_proxima_etapa.curso;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.api.AuthSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetalhesActivity extends AppCompatActivity {
    private Button btnInscrever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        ImageView imgCurso = findViewById(R.id.imgDetalheCurso);
        TextView txtNome = findViewById(R.id.txtDetalheNome);
        TextView txtCarga = findViewById(R.id.txtDetalheCargaHoraria);
        TextView txtDescricao = findViewById(R.id.txtDetalheDescricao);
        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnInscrever = findViewById(R.id.btnInscrever);
        btnInscrever.setEnabled(false);
        if (getIntent().hasExtra("CURSO_SELECIONADO")) {
            Curso curso = (Curso) getIntent()
                    .getSerializableExtra("CURSO_SELECIONADO");
            if (curso != null) {
                txtNome.setText(curso.getNome());
                txtDescricao.setText(obterDescricao(curso));
                txtCarga.setText(
                        "Total de vagas: " + curso.getTotalVagas()
                );
                imgCurso.setImageResource(curso.getImagemResId());
                btnInscrever.setEnabled(true);
                btnInscrever.setOnClickListener(v -> {
                    inscreverCurso(curso);
                });
            }
        }
        btnVoltar.setOnClickListener(v -> finish());
    }
    private String obterDescricao(Curso curso) {
        String nome = curso.getNome() == null ? "" : curso.getNome().trim();

        if ("Introdução à Programação".equalsIgnoreCase(nome)) {
            return getString(R.string.detalhes_descricao_programacao);
        }
        if ("Oficina de Currículo".equalsIgnoreCase(nome)) {
            return getString(R.string.detalhes_descricao_curriculo);
        }
        if ("Preparação para o Enem".equalsIgnoreCase(nome)) {
            return getString(R.string.detalhes_descricao_enem);
        }

        String descricao = curso.getDescricao();
        if (descricao == null || descricao.trim().isEmpty()) {
            return getString(R.string.detalhes_descricao_indisponivel);
        }
        return descricao;
    }

    private void inscreverCurso(Curso curso) {
        String token = AuthSession.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(
                    this,
                    "Faça login para se inscrever",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (curso.getId() == null || curso.getId().isEmpty()) {
            Toast.makeText(
                    this,
                    "Curso inválido",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        btnInscrever.setEnabled(false);
        new Thread(() -> {
            HttpURLConnection conexao = null;
            int codigo = 0;
            try {
                String endereco =
                        "https://proxima-etapa-api-ncc3.onrender.com/courses/"
                                + curso.getId() + "/register";
                URL url = new URL(endereco);
                conexao = (HttpURLConnection)
                        url.openConnection();
                conexao.setRequestMethod("POST");
                conexao.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );
                conexao.setConnectTimeout(30000);
                conexao.setReadTimeout(30000);
                codigo = conexao.getResponseCode();
            } catch (Exception e) {
                codigo = 0;
            } finally {
                if (conexao != null) {
                    conexao.disconnect();
                }
            }
            int resposta = codigo;
            runOnUiThread(() -> {
                if (resposta == 201) {
                    Toast.makeText(
                            this,
                            "Inscrição realizada com sucesso!",
                            Toast.LENGTH_LONG
                    ).show();
                    btnInscrever.setText("Inscrito");
                } else {
                    btnInscrever.setEnabled(true);
                    String mensagem = "Erro ao realizar inscrição";
                    if (resposta == 401) {
                        mensagem = "Faça login novamente";
                    } else if (resposta == 403) {
                        mensagem = "Inscrição não permitida";
                    } else if (resposta == 409) {
                        mensagem = "Inscrição já existente ou sem vagas";
                    } else if (resposta == 404) {
                        mensagem = "Curso ou rota não encontrada";
                    }
                    Toast.makeText(
                            this,
                            mensagem,
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }).start();
    }
}
