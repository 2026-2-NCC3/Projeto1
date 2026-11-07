package com.example.app_proxima_etapa.api;

import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Response;

public final class AuthErrors {
    private AuthErrors() {}

    public static String message(Response<?> response) {
        try (ResponseBody body = response.errorBody()) {
            if (body != null) {
                String message = new JSONObject(body.string()).optString("erro", "");
                if (!message.isEmpty()) return message;
            }
        } catch (Exception ignored) {
             
        }
        return "Não foi possível concluir. Tente novamente mais tarde.";
    }
}
