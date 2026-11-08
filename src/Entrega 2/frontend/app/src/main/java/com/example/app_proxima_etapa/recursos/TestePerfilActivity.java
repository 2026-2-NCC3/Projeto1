package com.example.app_proxima_etapa.recursos;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class TestePerfilActivity extends RecursoActivity {
    private final List<RadioGroup> grupos = new ArrayList<>();
    private ArrayList<Integer> respostasSalvas;

    @Override
    protected String titulo() {
        return "Meus interesses";
    }

    @Override
    protected void onCreate(Bundle estado) {
        respostasSalvas = estado == null ? null : estado.getIntegerArrayList("respostas");
        super.onCreate(estado);
    }

    @Override
    protected void carregar() {
        consultar("me/teste-perfil", dados -> {
            JsonObject teste = dados.getAsJsonObject();
            conteudo.removeAllViews();
            grupos.clear();
            texto(campo(teste, "titulo"), true);
            texto("Explore seus interesses. Este questionário não é um diagnóstico psicológico ou clínico.", false);
            botao("Ver meu último resultado").setOnClickListener(v ->
                    consultar("me/resultado-perfil", salvo -> {
                        if (salvo.getAsJsonObject().get("resultado").isJsonNull()) {
                            status.setText("Você ainda não concluiu um teste.");
                        } else {
                            mostrarResultado(salvo.getAsJsonObject().getAsJsonObject("resultado"));
                        }
                    }));
            for (JsonElement item : teste.getAsJsonArray("perguntas")) {
                JsonObject pergunta = item.getAsJsonObject();
                texto(campo(pergunta, "texto"), true);
                RadioGroup grupo = new RadioGroup(this);
                for (JsonElement alternativa : pergunta.getAsJsonArray("alternativas")) {
                    RadioButton opcao = new RadioButton(this);
                    opcao.setId(android.view.View.generateViewId());
                    opcao.setText(campo(alternativa.getAsJsonObject(), "texto"));
                    opcao.setTextSize(16);
                    grupo.addView(opcao);
                }
                if (respostasSalvas != null && grupos.size() < respostasSalvas.size()) {
                    int selecionada = respostasSalvas.get(grupos.size());
                    if (selecionada >= 0 && selecionada < grupo.getChildCount()) {
                        grupo.check(grupo.getChildAt(selecionada).getId());
                    }
                }
                grupos.add(grupo);
                conteudo.addView(grupo);
            }
            MaterialButton concluir = botao("Concluir questionário");
            concluir.setOnClickListener(v -> {
                JsonArray respostas = new JsonArray();
                for (RadioGroup grupo : grupos) {
                    int indice = grupo.indexOfChild(grupo.findViewById(grupo.getCheckedRadioButtonId()));
                    if (indice < 0) {
                        status.setText("Responda todas as perguntas antes de concluir.");
                        return;
                    }
                    respostas.add(indice);
                }
                JsonObject corpo = new JsonObject();
                corpo.addProperty("questionario_id", campo(teste, "id"));
                corpo.add("respostas", respostas);
                concluir.setEnabled(false);
                enviar("me/teste-perfil", corpo, resultado -> {
                    respostasSalvas = null;
                    mostrarResultado(resultado.getAsJsonObject());
                }, () -> concluir.setEnabled(true));
            });
        });
    }

    private void mostrarResultado(JsonObject resultado) {
        conteudo.removeAllViews();
        grupos.clear();
        texto("Seu resultado", true);
        texto(campo(resultado, "titulo"), true);
        texto(campo(resultado, "descricao"), false);
        texto(campo(resultado, "aviso"), false);
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        ArrayList<Integer> respostas = new ArrayList<>();
        for (RadioGroup grupo : grupos) {
            respostas.add(grupo.indexOfChild(grupo.findViewById(grupo.getCheckedRadioButtonId())));
        }
        estado.putIntegerArrayList("respostas", respostas);
        super.onSaveInstanceState(estado);
    }
}
