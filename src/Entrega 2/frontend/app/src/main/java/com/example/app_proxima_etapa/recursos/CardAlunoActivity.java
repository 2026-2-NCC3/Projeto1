package com.example.app_proxima_etapa.recursos;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class CardAlunoActivity extends RecursoActivity {
    @Override
    protected String titulo() {
        return "Card do aluno";
    }

    @Override
    protected void carregar() {
        consultar("auth/me", dados -> {
            JsonObject perfil = dados.getAsJsonObject();
            conteudo.removeAllViews();
            texto("PRÓXIMA ETAPA", true);
            texto(campo(perfil, "name"), true);
            texto(campo(perfil, "email"), false);
            texto("Identificação do participante", false);
            try {
                BitMatrix matriz = new MultiFormatWriter().encode(
                        "ALUNO-" + perfil.get("id").getAsString(), BarcodeFormat.QR_CODE, 600, 600);
                Bitmap imagem = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565);
                for (int x = 0; x < 600; x++) {
                    for (int y = 0; y < 600; y++) {
                        imagem.setPixel(x, y, matriz.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                ImageView qr = new ImageView(this);
                qr.setContentDescription("QR Code de identificação do aluno");
                qr.setImageBitmap(imagem);
                qr.setAdjustViewBounds(true);
                conteudo.addView(qr, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            } catch (Exception erro) {
                status.setText("Não foi possível gerar a identificação. Tente atualizar.");
            }
            texto("Apresente este card à equipe. Ele não substitui documento oficial.", false);
        });
    }
}
