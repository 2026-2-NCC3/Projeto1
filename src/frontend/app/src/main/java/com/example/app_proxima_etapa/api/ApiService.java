package com.example.app_proxima_etapa.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // Caminho que foi criado no backend. Exemplo: "/api/login"
    @POST("/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest loginRequest);
}