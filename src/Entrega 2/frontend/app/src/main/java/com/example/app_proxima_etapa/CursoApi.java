package com.example.app_proxima_etapa;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.app_proxima_etapa.model.Curso;
import com.example.app_proxima_etapa.storage.CursosCache;
import com.example.app_proxima_etapa.api.ApiService;
import com.example.app_proxima_etapa.api.AuthSession;
import com.example.app_proxima_etapa.api.RetrofitClient;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CursoApi {
    private static final String ENDERECO_API =
            "https://proxima-etapa-api-ncc3.onrender.com/courses";
    private final Context context;
    public CursoApi(Context context) {
        this.context = context.getApplicationContext();
    }
    public interface Retorno {
        void sucesso(List<Curso> cursos);
        void erro();
        default void cache(List<Curso> cursos) {
            sucesso(cursos);
        }
    }
    public void buscarCursos(Retorno retorno) {
        new Thread(() -> {
            HttpURLConnection conexao = null;
            try {
                URL url = new URL(ENDERECO_API);
                conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");
                conexao.setConnectTimeout(70000);
                conexao.setReadTimeout(70000);
                if (conexao.getResponseCode() != 200) {
                    throw new Exception("Erro na resposta da API");
                }
                BufferedReader leitor = new BufferedReader(
                        new InputStreamReader(
                                conexao.getInputStream()
                        )
                );
                StringBuilder resposta = new StringBuilder();
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    resposta.append(linha);
                }
                leitor.close();
                List<Curso> cursos =
                        interpretar(resposta.toString());
                try (CursosCache cache = new CursosCache(context)) {
                    cache.salvar(resposta.toString());
                } catch (Exception ignored) {
                }
                new Handler(Looper.getMainLooper()).post(
                        () -> retorno.sucesso(cursos)
                );
            } catch (Exception e) {
                try (CursosCache cache = new CursosCache(context)) {
                    String salvo = cache.ler();
                    if (salvo != null) {
                        List<Curso> cursos = interpretar(salvo);
                        new Handler(Looper.getMainLooper()).post(
                                () -> retorno.cache(cursos)
                        );
                        return;
                    }
                } catch (Exception ignored) {
                }
                new Handler(Looper.getMainLooper()).post(
                        retorno::erro
                );

            } finally {
                if (conexao != null) {
                    conexao.disconnect();
                }
            }
        }).start();
    }
    public void buscarMeusCursos(Retorno retorno) {
        String token = AuthSession.getToken();
        if (token == null) {
            retorno.erro();
            return;
        }
        ApiService api = RetrofitClient.getRetrofitInstance()
                .create(ApiService.class);
        api.buscarMeusCursos("Bearer " + token)
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(
                            Call<JsonArray> call,
                            Response<JsonArray> resposta
                    ) {
                        if (!resposta.isSuccessful()
                                || resposta.body() == null) {

                            retorno.erro();
                            return;
                        }
                        try {
                            List<Curso> cursos = interpretar(
                                    resposta.body().toString()
                            );
                            retorno.sucesso(cursos);
                        } catch (Exception e) {
                            retorno.erro();
                        }
                    }
                    @Override
                    public void onFailure(
                            Call<JsonArray> call,
                            Throwable erro
                    ) {
                        retorno.erro();
                    }
                });
    }
    private List<Curso> interpretar(String resposta)
            throws Exception {
        JSONArray json = new JSONArray(resposta);
        List<Curso> cursos = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject item = json.getJSONObject(i);
            Curso curso = new Curso(
                    item.getString("id"),
                    item.getString("title"),
                    item.optString("description", ""),
                    item.optInt("workload", 0),
                    item.optString("location", ""),
                    "",
                    "",
                    item.optInt("total_spots", 0),
                    0,
                    false,
                    item.optString("category", "")
            );
            curso.setImagemResId(
                    R.drawable.img_curso_online
            );
            curso.setDataCurso(
                    item.optString("course_date", "")
            );
            cursos.add(curso);
        }
        return cursos;
    }
}
