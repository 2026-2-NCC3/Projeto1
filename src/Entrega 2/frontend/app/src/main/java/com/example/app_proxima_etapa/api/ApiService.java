package com.example.app_proxima_etapa.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @retrofit2.http.GET("auth/me")
    Call<PerfilUsuario> consultarPerfil(@retrofit2.http.Header("Authorization") String authorization);

    @POST("auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    @POST("auth/register")
    Call<LoginResponse> cadastrar(@Body RegisterRequest request);
}
