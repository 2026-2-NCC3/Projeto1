package com.example.app_proxima_etapa.recursos;

import android.text.InputFilter;
import android.widget.EditText;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AtendimentoActivity extends RecursoActivity {

    @Override
    protected String titulo() {
        return "Fale com a equipe";
    }

    @Override
    protected void carregar() {
        consultar("me/atendimento", dados -> {
            conteudo.removeAllViews();
            texto("A equipe responderá por este canal. Toque em Atualizar para buscar respostas.", false);
            for (JsonElement item : dados.getAsJsonArray()) {
                JsonObject mensagem = item.getAsJsonObject();
                texto("equipe".equals(campo(mensagem, "autor")) ? "Próxima Etapa" : "Você", true);
                texto(campo(mensagem, "texto"), false);
                texto(campo(mensagem, "criado_em"), false);
            }
            EditText entrada = new EditText(this);
            entrada.setHint("Escreva sua dúvida");
            entrada.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                    | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            entrada.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2000) });
            entrada.setMinLines(3);
            conteudo.addView(entrada);
            MaterialButton enviar = botao("Enviar mensagem");
            enviar.setOnClickListener(v -> {
                String mensagem = entrada.getText().toString().trim();
                if (mensagem.isEmpty()) {
                    entrada.setError("Escreva uma mensagem.");
                    return;
                }
                JsonObject corpo = new JsonObject();
                corpo.addProperty("texto", mensagem);
                enviar.setEnabled(false);
                enviar("me/atendimento", corpo, resposta -> {
                    carregar();
                });
            });
        });
    }
}
