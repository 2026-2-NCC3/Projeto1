package com.example.app_proxima_etapa.api;

public final class AuthSession {
    private static String token;
    private static String alunoId;
    public static String getAlunoId() { return alunoId; }
    public static void setAlunoId(String value) { alunoId = value; }
    private AuthSession() {}
    public static void setToken(String value) { token = value; alunoId = null; }
    public static String getToken() { return token; }
    public static void clear() { token = null; alunoId = null; }
}
