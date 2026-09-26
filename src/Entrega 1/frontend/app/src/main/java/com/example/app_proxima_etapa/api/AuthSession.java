package com.example.app_proxima_etapa.api;

// Sessão apenas em memória: o token não vai para arquivos, logs ou backups.
// Ao encerrar o processo do aplicativo, é necessário entrar novamente.
public final class AuthSession {
    private static String token;
    private AuthSession() {}
    public static void setToken(String value) { token = value; }
    public static String getToken() { return token; }
    public static void clear() { token = null; }
}
