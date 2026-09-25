package com.example.app_proxima_etapa;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Curso> listaDeCursos;
    private CursoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ligar a variável ao RecyclerView criado no activity_main.xml
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCursos);

        // Configurar a lista para aparecer na vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaDeCursos = new ArrayList<>();

        // Ligar a lista ao Adapter
        adapter = new CursoAdapter(listaDeCursos, this);
        recyclerView.setAdapter(adapter);

        carregarCursos();
    }

    private void carregarCursos() {
        CursoApi cursoApi = new CursoApi();

        cursoApi.buscarCursos(new CursoApi.Retorno() {
            @Override
            public void sucesso(List<Curso> cursos) {
                listaDeCursos.clear();
                listaDeCursos.addAll(cursos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void erro() {
                Toast.makeText(
                        MainActivity.this,
                        "Não foi possível carregar os cursos.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}