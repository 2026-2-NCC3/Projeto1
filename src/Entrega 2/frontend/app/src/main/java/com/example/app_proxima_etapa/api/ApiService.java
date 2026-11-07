
package com.example.app_proxima_etapa.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.google.gson.JsonArray;
import retrofit2.http.POST;
import java.util.List;

public interface ApiService {

    @GET("auth/me")
    Call<PerfilUsuario> consultarPerfil(@Header("Authorization") String authorization);

    @POST("auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    @POST("auth/register")
    Call<LoginResponse> cadastrar(@Body RegisterRequest request);
    @POST("presencas")
    Call<Void> registrarPresenca(
            @Header("Authorization") String token,
            @Body PresencaRequest dados);
    @GET("me/courses")
    Call<JsonArray> buscarMeusCursos(
            @Header("Authorization") String token
    );
    @GET("me/presencas")
    Call<List<HistoricoPresenca>> buscarHistorico(
            @Header("Authorization") String token
    );
}
