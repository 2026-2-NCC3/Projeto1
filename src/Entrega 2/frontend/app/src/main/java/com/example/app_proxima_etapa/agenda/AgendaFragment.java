package com.example.app_proxima_etapa.agenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_proxima_etapa.R;
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

    public AgendaFragment() {
        // Construtor vazio obrigatório
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

        // Texto que mostra o mês e o ano
        txtMesAno =
                view.findViewById(R.id.txtMesAno);

        // RecyclerView que mostra os dias
        recyclerCalendario =
                view.findViewById(R.id.recyclerCalendario);

        // Botão para voltar um mês
        MaterialButton btnMesAnterior =
                view.findViewById(R.id.btnMesAnterior);

        // Botão para avançar um mês
        MaterialButton btnProximoMes =
                view.findViewById(R.id.btnProximoMes);

        /*
         * O calendário possui 7 colunas:
         *
         * DOM SEG TER QUA QUI SEX SÁB
         */
        recyclerCalendario.setLayoutManager(
                new GridLayoutManager(
                        requireContext(),
                        7
                )
        );

        // Evita animações estranhas quando o mês é alterado
        recyclerCalendario.setItemAnimator(null);

        // Pega a data atual do aparelho
        mesAtual = Calendar.getInstance();

        // Trabalhamos sempre com o primeiro dia do mês
        mesAtual.set(
                Calendar.DAY_OF_MONTH,
                1
        );

        // Monta o calendário inicial
        atualizarCalendario();

        /*
         * MÊS ANTERIOR
         */
        btnMesAnterior.setOnClickListener(v -> {

            mesAtual.add(
                    Calendar.MONTH,
                    -1
            );

            atualizarCalendario();
        });

        /*
         * PRÓXIMO MÊS
         */
        btnProximoMes.setOnClickListener(v -> {

            mesAtual.add(
                    Calendar.MONTH,
                    1
            );

            atualizarCalendario();
        });
    }

    /*
     * Atualiza toda a grade quando:
     *
     * - a tela abre;
     * - o usuário volta um mês;
     * - o usuário avança um mês.
     */
    private void atualizarCalendario() {

        atualizarTituloMes();

        // Gera todos os dias necessários
        List<DiaCalendario> dias =
                gerarDiasDoMes();

        // Cria o Adapter
        calendarioAdapter =
                new CalendarioAdapter(dias);

        // Exibe no RecyclerView
        recyclerCalendario.setAdapter(
                calendarioAdapter
        );
    }

    /*
     * Atualiza o título:
     *
     * Outubro 2026
     * Novembro 2026
     * Dezembro 2026
     * Janeiro 2027
     */
    private void atualizarTituloMes() {

        int numeroMes =
                mesAtual.get(
                        Calendar.MONTH
                );

        int ano =
                mesAtual.get(
                        Calendar.YEAR
                );

        Locale localeBrasil =
                new Locale(
                        "pt",
                        "BR"
                );

        String nomeMes =
                new DateFormatSymbols(localeBrasil)
                        .getMonths()[numeroMes];

        /*
         * Deixa a primeira letra maiúscula:
         *
         * outubro
         *
         * vira
         *
         * Outubro
         */
        if (
                nomeMes != null
                        && !nomeMes.isEmpty()
        ) {

            nomeMes =
                    nomeMes
                            .substring(0, 1)
                            .toUpperCase(localeBrasil)
                            + nomeMes.substring(1);
        }

        String titulo =
                nomeMes + " " + ano;

        txtMesAno.setText(titulo);
    }

    /*
     * Gera os dias que serão mostrados
     * no calendário.
     */
    private List<DiaCalendario> gerarDiasDoMes() {

        List<DiaCalendario> dias =
                new ArrayList<>();

        /*
         * Fazemos uma cópia para não modificar
         * diretamente a variável mesAtual.
         */
        Calendar calendario =
                (Calendar) mesAtual.clone();

        /*
         * Vai para o primeiro dia daquele mês.
         */
        calendario.set(
                Calendar.DAY_OF_MONTH,
                1
        );

        /*
         * Calendar trabalha desta forma:
         *
         * DOMINGO = 1
         * SEGUNDA  = 2
         * TERÇA    = 3
         * QUARTA   = 4
         * QUINTA   = 5
         * SEXTA    = 6
         * SÁBADO   = 7
         *
         * Nossa agenda agora trabalha assim:
         *
         * DOM SEG TER QUA QUI SEX SÁB
         *
         *  0   1   2   3   4   5   6
         */

        int diaSemana =
                calendario.get(
                        Calendar.DAY_OF_WEEK
                );

        /*
         * Como nossa primeira coluna é domingo,
         * basta subtrair 1.
         *
         * Exemplos:
         *
         * Domingo:
         * 1 - 1 = 0 espaços
         *
         * Segunda:
         * 2 - 1 = 1 espaço
         *
         * Quinta:
         * 5 - 1 = 4 espaços
         */
        int espacosAntes =
                diaSemana - 1;

        /*
         * Adiciona os espaços vazios antes
         * do primeiro dia do mês.
         */
        for (
                int i = 0;
                i < espacosAntes;
                i++
        ) {

            dias.add(
                    new DiaCalendario(
                            0,
                            false
                    )
            );
        }

        /*
         * Descobre quantos dias existem
         * naquele mês.
         *
         * Exemplo:
         *
         * Outubro = 31
         * Novembro = 30
         * Fevereiro = 28 ou 29
         */
        int quantidadeDias =
                calendario.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                );

        /*
         * Adiciona os dias reais:
         *
         * 1
         * 2
         * 3
         * ...
         * 31
         */
        for (
                int dia = 1;
                dia <= quantidadeDias;
                dia++
        ) {

            dias.add(
                    new DiaCalendario(
                            dia,
                            true
                    )
            );
        }

        /*
         * Completa a última semana.
         *
         * O total precisa sempre ser
         * múltiplo de 7 para manter:
         *
         * DOM SEG TER QUA QUI SEX SÁB
         */
        while (
                dias.size() % 7 != 0
        ) {

            dias.add(
                    new DiaCalendario(
                            0,
                            false
                    )
            );
        }

        return dias;
    }
}