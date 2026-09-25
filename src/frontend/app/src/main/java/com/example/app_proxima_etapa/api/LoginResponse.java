package com.example.app_proxima_etapa.api;

public class LoginResponse {
    private String token; // O seu colega de backend dirá se ele devolve um token, uma mensagem, etc.
    private String mensagem;

    public String getToken() { return token; }
    public String getMensagem() { return mensagem; }
}