package com.example.app_proxima_etapa.curso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_proxima_etapa.CursoApi;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.model.Curso;

import java.util.ArrayList;
import java.util.List;

public class CursosFragment extends Fragment {

    private List<Curso> listaDeCursos;
    private CursoAdapter adapter;

    public CursosFragment() {
        // Construtor vazio obrigatório do Fragment
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_cursos,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // Encontra o RecyclerView dentro do fragment_cursos.xml
        RecyclerView recyclerView =
                view.findViewById(R.id.recyclerViewCursos);

        // Lista vertical
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // Cria a lista que receberá os cursos da API
        listaDeCursos = new ArrayList<>();

        // Liga a lista ao mesmo CursoAdapter que você já utilizava
        adapter = new CursoAdapter(
                listaDeCursos,
                requireContext()
        );

        recyclerView.setAdapter(adapter);

        // Busca os cursos
        view.findViewById(R.id.btnAtualizarCursos).setOnClickListener(v -> carregarCursos());
        carregarCursos();
    }

    private void carregarCursos() {

        final View tela = requireView();
        tela.findViewById(R.id.btnAtualizarCursos).setEnabled(false);
        ((TextView) tela.findViewById(R.id.txtStatusCursos)).setText("Carregando cursos…");
        CursoApi cursoApi = new CursoApi(requireContext());

        cursoApi.buscarCursos(new CursoApi.Retorno() {

            @Override
            public void sucesso(List<Curso> cursos) {

                if (!isAdded() || getView() != tela) return;
                listaDeCursos.clear();
                listaDeCursos.addAll(cursos);

                adapter.notifyDataSetChanged();
                tela.findViewById(R.id.btnAtualizarCursos).setEnabled(true);
                ((TextView) tela.findViewById(R.id.txtStatusCursos)).setText(cursos.isEmpty()
                        ? "Nenhum curso disponível no momento." : "Cursos atualizados.");
            }

            @Override public void cache(List<Curso> cursos) {
                sucesso(cursos);
                if (isAdded() && getView() == tela)
                    ((TextView) tela.findViewById(R.id.txtStatusCursos)).setText(
                            "Servidor indisponível. Exibindo a última consulta salva. Toque em Atualizar para tentar novamente.");
            }

            @Override
            public void erro() {

                if (!isAdded() || getView() != tela) return;
                tela.findViewById(R.id.btnAtualizarCursos).setEnabled(true);
                ((TextView) tela.findViewById(R.id.txtStatusCursos)).setText(
                        "Não foi possível carregar os cursos. Confira a conexão e toque em Atualizar.");
            }
        });
    }
}