package com.example.app_proxima_etapa;

import com.example.app_proxima_etapa.api.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;
import retrofit2.http.POST;
import static org.junit.Assert.*;

public class AuthContractTest {
    private final Gson gson = new Gson();

    @Test public void loginUsesBackendPasswordField() {
        JsonObject json = gson.toJsonTree(new LoginRequest("aluno@example.com", "SenhaTeste123!")).getAsJsonObject();
        assertEquals("SenhaTeste123!", json.get("password").getAsString());
        assertFalse(json.has("senha"));
        assertEquals("aluno@example.com", json.get("email").getAsString());
    }

    @Test public void registrationUsesBackendFields() {
        JsonObject json = gson.toJsonTree(new RegisterRequest("Aluno", "aluno@example.com", "SenhaTeste123!")).getAsJsonObject();
        assertEquals("Aluno", json.get("name").getAsString());
        assertEquals(3, json.size());
        assertTrue(json.has("email"));
        assertTrue(json.has("password"));
    }

    @Test public void endpointsMatchDeployedApi() throws Exception {
        assertEquals("auth/login", ApiService.class.getMethod("fazerLogin", LoginRequest.class)
                .getAnnotation(POST.class).value());
        assertEquals("auth/register", ApiService.class.getMethod("cadastrar", RegisterRequest.class)
                .getAnnotation(POST.class).value());
    }

    @Test public void loginResponseReadsToken() {
        LoginResponse response = gson.fromJson("{\"token\":\"test-token\",\"mensagem\":\"Login realizado com sucesso.\"}", LoginResponse.class);
        assertEquals("test-token", response.getToken());
    }

    @Test public void sessionCanBeCleared() {
        AuthSession.setToken("test-token");
        assertEquals("test-token", AuthSession.getToken());
        AuthSession.clear();
        assertNull(AuthSession.getToken());
    }
}
