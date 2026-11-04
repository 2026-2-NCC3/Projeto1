package com.example.app_proxima_etapa.api;

public class RegisterRequest {
    private final String name;
    private final String email;
    private final String cpf;
    private final String telefone;
    private final String password;



    public RegisterRequest(String name, String email, String cpf, String telefone, String password) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.password = password;
    }
}
