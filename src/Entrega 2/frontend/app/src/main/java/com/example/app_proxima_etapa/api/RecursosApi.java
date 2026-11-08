package com.example.app_proxima_etapa.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RecursosApi {
    @GET
    Call<JsonElement> consultar(@Url String caminho, @Header("Authorization") String token);

    @POST
    Call<JsonElement> enviar(@Url String caminho, @Header("Authorization") String token,
                             @Body JsonObject dados);
}
