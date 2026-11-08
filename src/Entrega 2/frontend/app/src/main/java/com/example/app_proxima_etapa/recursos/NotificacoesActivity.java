package com.example.app_proxima_etapa.recursos;

import android.Manifest;
import android.os.Build;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NotificacoesActivity extends RecursoActivity {
    @Override
    protected String titulo() {
        return "Novidades e avisos";
    }

    @Override
    protected void carregar() {
        consultar("auth/me", perfil -> {
            String usuario = perfil.getAsJsonObject().get("id").getAsString();
            consultar("me/novidades", dados -> {
                conteudo.removeAllViews();
                texto("Os avisos são consultados ao abrir ou atualizar esta tela. Não há recebimento em segundo plano.", false);
                if (Build.VERSION.SDK_INT >= 33) {
                    botao("Permitir notificações no Android").setOnClickListener(v ->
                            requestPermissions(new String[] { Manifest.permission.POST_NOTIFICATIONS }, 42));
                }
                if (dados.getAsJsonArray().size() == 0) status.setText("Nenhuma novidade no momento.");
                for (JsonElement item : dados.getAsJsonArray()) {
                    JsonObject aviso = item.getAsJsonObject();
                    texto(campo(aviso, "titulo"), true);
                    texto(campo(aviso, "texto"), false);
                    texto(campo(aviso, "criado_em"), false);
                }
                AvisosAndroid.publicar(this, usuario, dados.getAsJsonArray());
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int codigo, String[] permissoes, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permissoes, resultados);
        if (codigo == 42) carregar();
    }
}
