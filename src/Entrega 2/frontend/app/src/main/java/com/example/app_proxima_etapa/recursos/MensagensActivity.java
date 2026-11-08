package com.example.app_proxima_etapa.recursos;

import android.content.Intent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MensagensActivity extends RecursoActivity {
    @Override
    protected String titulo() {
        return "Comunicados e atendimento";
    }

    @Override
    protected void carregar() {
        conteudo.removeAllViews();
        botao("Conversar com a equipe").setOnClickListener(v ->
                startActivity(new Intent(this, AtendimentoActivity.class)));
        consultar("me/comunicados", dados -> {
            if (dados.getAsJsonArray().size() == 0) status.setText("Nenhum comunicado no momento.");
            for (JsonElement item : dados.getAsJsonArray()) {
                JsonObject aviso = item.getAsJsonObject();
                texto(campo(aviso, "titulo"), true);
                texto(campo(aviso, "texto"), false);
                texto(campo(aviso, "criado_em"), false);
            }
        });
    }
}
