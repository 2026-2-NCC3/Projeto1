package com.example.app_proxima_etapa;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ligar a variável ao RecyclerView que foi criado no activity_main.xml
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCursos);

        // Configurar o LayoutManager (define que a lista será vertical)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Criar a base de dados fictícios de cursos aleatórios
        List<Curso> listaDeCursos = new ArrayList<>();

        listaDeCursos.add(new Curso("Introduação a Programação", 40, R.drawable.img_curso_online));
        listaDeCursos.add(new Curso("Oficina de Currículo", 60, R.drawable.img_curso_online));
        listaDeCursos.add(new Curso("Preparação para o Enem", 80, R.drawable.img_curso_online));

        // Instanciar o Adapter criado, passando a lista, e ligar ele ao RecyclerView
        CursoAdapter adapter = new CursoAdapter(listaDeCursos, this);
        recyclerView.setAdapter(adapter);
    }
}