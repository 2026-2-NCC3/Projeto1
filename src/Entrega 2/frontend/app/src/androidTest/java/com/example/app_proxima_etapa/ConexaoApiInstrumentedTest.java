package com.example.app_proxima_etapa;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.app_proxima_etapa.api.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ConexaoApiInstrumentedTest {
    @Test
    public void loginRespondeNoEmuladorSemEnviarCredenciais() throws Exception {
        Request requisicao = new Request.Builder()
                .url(RetrofitClient.getRetrofitInstance().baseUrl().resolve("auth/login"))
                .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                .build();

        try (Response resposta = RetrofitClient.getRetrofitInstance()
                .callFactory().newCall(requisicao).execute()) {
            assertEquals(400, resposta.code());
        }
    }
}
