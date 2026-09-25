package com.example.app_proxima_etapa;

import com.example.app_proxima_etapa.model.Curso;
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

    public interface Retorno {
        void sucesso(List<Curso> cursos);
        void erro();
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

                JSONArray listaJson = new JSONArray(resposta.toString());
                List<Curso> cursos = new ArrayList<>();

                for (int i = 0; i < listaJson.length(); i++) {
                    JSONObject cursoJson = listaJson.getJSONObject(i);

                    String nome = cursoJson.getString("title");
                    int totalVagas = cursoJson.getInt("total_spots");

                    cursos.add(new Curso(
                            nome,
                            totalVagas,
                            R.drawable.img_curso_online
                    ));
                }

                new Handler(Looper.getMainLooper()).post(
                        () -> retorno.sucesso(cursos)
                );

            } catch (Exception e) {
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
}
