package com.example.app_proxima_etapa;

import com.example.app_proxima_etapa.model.Curso;
import android.content.Context;
import com.example.app_proxima_etapa.storage.CursosCache;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CursoApi {

    private static final String ENDERECO_API =
            "https://proxima-etapa-api-ncc3.onrender.com/courses";

    private final Context context;
    public CursoApi(Context context) { this.context = context.getApplicationContext(); }

    public interface Retorno {
        void sucesso(List<Curso> cursos);
        void erro();
        default void cache(List<Curso> cursos) { sucesso(cursos); }
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
                        new InputStreamReader(conexao.getInputStream())
                );

                StringBuilder resposta = new StringBuilder();
                String linha;

                while ((linha = leitor.readLine()) != null) {
                    resposta.append(linha);
                }

                leitor.close();

                List<Curso> cursos = interpretar(resposta.toString());
                // Salva somente uma resposta válida; falha no cache não descarta dados online.
                try (CursosCache cache = new CursosCache(context)) {
                    cache.salvar(resposta.toString());
                } catch (Exception ignored) { }

                new Handler(Looper.getMainLooper()).post(
                        () -> retorno.sucesso(cursos)
                );

            } catch (Exception e) {
                try (CursosCache cache = new CursosCache(context)) {
                    String salvo = cache.ler();
                    if (salvo != null) {
                        List<Curso> cursos = interpretar(salvo);
                        new Handler(Looper.getMainLooper()).post(() -> retorno.cache(cursos));
                        return;
                    }
                } catch (Exception ignored) { }
                new Handler(Looper.getMainLooper()).post(retorno::erro);
            } finally {
                if (conexao != null) {
                    conexao.disconnect();
                }
            }
        }).start();
    }
    private List<Curso> interpretar(String resposta) throws Exception {
        JSONArray json = new JSONArray(resposta);
        List<Curso> cursos = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject item = json.getJSONObject(i);
            Curso curso = new Curso(item.getString("id"), item.getString("title"),
                    item.optString("description", ""), item.optInt("workload", 0),
                    item.optString("location", ""), "", "", item.optInt("total_spots", 0),
                    0, false, item.optString("category", ""));
            curso.setImagemResId(R.drawable.img_curso_online);
            cursos.add(curso);
        }
        return cursos;
    }
}
