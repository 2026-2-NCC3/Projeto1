package com.example.app_proxima_etapa.recursos;

import android.content.Intent;
import android.net.Uri;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CertificadosActivity extends RecursoActivity {
    @Override
    protected String titulo() {
        return "Meus certificados";
    }

    @Override
    protected void carregar() {
        consultar("me/certificados", dados -> {
            conteudo.removeAllViews();
            if (dados.getAsJsonArray().size() == 0) {
                status.setText("Você ainda não tem certificados liberados.");
            }
            for (JsonElement item : dados.getAsJsonArray()) {
                JsonObject certificado = item.getAsJsonObject();
                texto(campo(certificado, "titulo"), true);
                texto("Emissão: " + campo(certificado, "emitido_em"), false);
                botao("Visualizar certificado").setOnClickListener(v -> {
                    Uri endereco = Uri.parse(campo(certificado, "documento_url"));
                    if (!"https".equalsIgnoreCase(endereco.getScheme()) || endereco.getHost() == null) {
                        status.setText("O endereço do certificado é inválido.");
                        return;
                    }
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, endereco));
                    } catch (android.content.ActivityNotFoundException erro) {
                        status.setText("Instale um navegador ou leitor para abrir o documento.");
                    }
                });
            }
        });
    }
}
