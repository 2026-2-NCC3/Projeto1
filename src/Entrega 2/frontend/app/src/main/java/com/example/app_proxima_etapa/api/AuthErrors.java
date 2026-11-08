package com.example.app_proxima_etapa.api;

import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Response;

public final class AuthErrors {
    private AuthErrors() {}

    public static String message(Response<?> response) {
        return message(response, "Não foi possível concluir. Tente novamente mais tarde.");
    }

    public static String message(Response<?> response, String alternativa) {
        try (ResponseBody body = response.errorBody()) {
            if (body != null) {
                String message = new JSONObject(body.string()).optString("erro", "");
                if (!message.isEmpty()) return message;
            }
        } catch (Exception ignored) {
             
        }
        return alternativa;
    }
}
