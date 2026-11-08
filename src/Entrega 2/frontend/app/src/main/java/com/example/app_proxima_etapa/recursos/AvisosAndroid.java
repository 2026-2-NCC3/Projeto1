package com.example.app_proxima_etapa.recursos;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.app_proxima_etapa.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class AvisosAndroid {
    private AvisosAndroid() { }

    public static void publicar(Context contexto, String usuarioId, JsonArray avisos) {
        if (usuarioId == null) return;
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        NotificationManager gerente = contexto.getSystemService(NotificationManager.class);
        if (gerente == null || !gerente.areNotificationsEnabled()) return;
        if (Build.VERSION.SDK_INT >= 26) {
            gerente.createNotificationChannel(new NotificationChannel(
                    "novidades_aluno", "Novidades da Próxima Etapa", NotificationManager.IMPORTANCE_DEFAULT));
        }
        SharedPreferences preferencias = contexto.getSharedPreferences("avisos_" + usuarioId, Context.MODE_PRIVATE);
        int enviados = 0;
        for (JsonElement elemento : avisos) {
            JsonObject aviso = elemento.getAsJsonObject();
            String versao = aviso.get("id").getAsString() + "_" + aviso.get("criado_em").getAsString();
            if (preferencias.getBoolean(versao, false)) continue;
            if (enviados >= 3) break;
            Intent tela = new Intent(contexto, NotificacoesActivity.class);
            PendingIntent abrir = PendingIntent.getActivity(contexto, 0, tela,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            gerente.notify(versao.hashCode(), new NotificationCompat.Builder(contexto, "novidades_aluno")
                    .setSmallIcon(R.drawable.ic_aviso)
                    .setContentTitle(aviso.get("titulo").getAsString())
                    .setContentText(aviso.get("texto").getAsString())
                    .setContentIntent(abrir)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setAutoCancel(true)
                    .build());
            preferencias.edit().putBoolean(versao, true).apply();
            enviados++;
        }
    }
}
