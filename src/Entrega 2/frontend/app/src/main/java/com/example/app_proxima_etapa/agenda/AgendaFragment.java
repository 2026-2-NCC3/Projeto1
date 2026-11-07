
package com.example.app_proxima_etapa.agenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_proxima_etapa.CursoApi;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.model.Curso;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AgendaFragment extends Fragment {

    private TextView txtMesAno;
    private RecyclerView recyclerCalendario;
    private CalendarioAdapter calendarioAdapter;
    private Calendar mesAtual;

    private final List<Curso> cursos = new ArrayList<>();

    public AgendaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_agenda,
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

        txtMesAno = view.findViewById(R.id.txtMesAno);

        recyclerCalendario = view.findViewById(
                R.id.recyclerCalendario
        );

        MaterialButton btnMesAnterior =
                view.findViewById(R.id.btnMesAnterior);

        MaterialButton btnProximoMes =
                view.findViewById(R.id.btnProximoMes);

        recyclerCalendario.setLayoutManager(
                new GridLayoutManager(requireContext(), 7)
        );

        recyclerCalendario.setItemAnimator(null);

        mesAtual = Calendar.getInstance();
        mesAtual.set(Calendar.DAY_OF_MONTH, 1);

        atualizarCalendario();

        btnMesAnterior.setOnClickListener(v -> {
            mesAtual.add(Calendar.MONTH, -1);
            atualizarCalendario();
        });

        btnProximoMes.setOnClickListener(v -> {
            mesAtual.add(Calendar.MONTH, 1);
            atualizarCalendario();
        });

        carregarCursos(view);
    }
    private void carregarCursos(View view) {

        CursoApi cursoApi = new CursoApi(requireContext());

        cursoApi.buscarMeusCursos(new CursoApi.Retorno() {

            @Override
            public void sucesso(List<Curso> listaCursos) {

                if (!isAdded() || getView() != view) {
                    return;
                }

                cursos.clear();
                cursos.addAll(listaCursos);

                Toast.makeText(
                        requireContext(),
                        "Meus cursos recebidos: " + cursos.size(),
                        Toast.LENGTH_LONG
                ).show();

                atualizarCalendario();
            }

            @Override
            public void erro() {

                if (!isAdded() || getView() != view) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Erro ao carregar meus cursos",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void atualizarCalendario() {

        atualizarTituloMes();

        List<DiaCalendario> dias = gerarDiasDoMes();

        calendarioAdapter = new CalendarioAdapter(dias);

        recyclerCalendario.setAdapter(calendarioAdapter);
    }

    private void atualizarTituloMes() {

        int numeroMes = mesAtual.get(Calendar.MONTH);
        int ano = mesAtual.get(Calendar.YEAR);

        Locale localeBrasil = new Locale("pt", "BR");

        String nomeMes = new DateFormatSymbols(localeBrasil)
                .getMonths()[numeroMes];

        if (nomeMes != null && !nomeMes.isEmpty()) {

            nomeMes = nomeMes.substring(0, 1)
                    .toUpperCase(localeBrasil)
                    + nomeMes.substring(1);
        }

        txtMesAno.setText(nomeMes + " " + ano);
    }

    private List<DiaCalendario> gerarDiasDoMes() {

        List<DiaCalendario> dias = new ArrayList<>();

        Calendar calendario = (Calendar) mesAtual.clone();
        calendario.set(Calendar.DAY_OF_MONTH, 1);

        int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);
        int espacosAntes = diaSemana - 1;

        for (int i = 0; i < espacosAntes; i++) {
            dias.add(new DiaCalendario(0, false));
        }

        int quantidadeDias = calendario.getActualMaximum(
                Calendar.DAY_OF_MONTH
        );

        int ano = mesAtual.get(Calendar.YEAR);
        int mes = mesAtual.get(Calendar.MONTH) + 1;

        for (int numeroDia = 1;
             numeroDia <= quantidadeDias;
             numeroDia++) {

            DiaCalendario dia = new DiaCalendario(
                    numeroDia,
                    true
            );

            String data = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    ano,
                    mes,
                    numeroDia
            );
            for (Curso curso : cursos) {
                String dataCurso = curso.getDataCurso();
                if (dataCurso != null
                        && dataCurso.startsWith(data)) {
                    dia.adicionarCurso(curso);
                }
            }
            dias.add(dia);
        }
        while (dias.size() % 7 != 0) {
            dias.add(new DiaCalendario(0, false));
        }
        return dias;
    }
}
