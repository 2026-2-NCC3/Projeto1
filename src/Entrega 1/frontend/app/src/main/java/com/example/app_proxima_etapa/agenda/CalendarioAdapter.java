package com.example.app_proxima_etapa.agenda;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_proxima_etapa.R;

import java.util.List;

public class CalendarioAdapter
        extends RecyclerView.Adapter<CalendarioAdapter.DiaViewHolder> {

    private final List<DiaCalendario> dias;

    public CalendarioAdapter(List<DiaCalendario> dias) {
        this.dias = dias;
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_dia_calendario,
                        parent,
                        false
                );

        return new DiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DiaViewHolder holder,
            int position
    ) {

        DiaCalendario dia = dias.get(position);

        /*
         * RecyclerView reutiliza as células.
         * Por isso limpamos qualquer evento anterior.
         */
        holder.containerEventos.removeAllViews();

        /*
         * Reseta o visual básico do número do dia.
         */
        holder.txtNumeroDia.setBackground(null);

        holder.txtNumeroDia.setTextColor(
                Color.parseColor("#35363A")
        );

        /*
         * Se esta posição não pertence ao mês,
         * deixamos o conteúdo invisível.
         *
         * O card continua existindo para manter
         * a grade alinhada.
         */
        if (!dia.isPertenceAoMes()) {

            holder.txtNumeroDia.setText("");

            holder.txtNumeroDia.setVisibility(
                    View.INVISIBLE
            );

            holder.containerEventos.setVisibility(
                    View.INVISIBLE
            );

            return;
        }

        /*
         * Dia pertencente ao mês.
         */
        holder.txtNumeroDia.setVisibility(
                View.VISIBLE
        );

        holder.containerEventos.setVisibility(
                View.VISIBLE
        );

        holder.txtNumeroDia.setText(
                String.valueOf(
                        dia.getNumeroDia()
                )
        );

        /*
         * Nossa agenda agora trabalha nesta ordem:
         *
         * 0 = DOM
         * 1 = SEG
         * 2 = TER
         * 3 = QUA
         * 4 = QUI
         * 5 = SEX
         * 6 = SÁB
         */
        int coluna =
                position % 7;

        /*
         * Domingo e sábado recebem uma
         * tonalidade mais suave.
         */
        if (
                coluna == 0
                        || coluna == 6
        ) {

            holder.txtNumeroDia.setTextColor(
                    Color.parseColor("#8B8D98")
            );
        }
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    /*
     * ViewHolder de cada quadrado do calendário.
     */
    static class DiaViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNumeroDia;
        LinearLayout containerEventos;

        public DiaViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            txtNumeroDia =
                    itemView.findViewById(
                            R.id.txtNumeroDia
                    );

            containerEventos =
                    itemView.findViewById(
                            R.id.containerEventos
                    );
        }
    }
}