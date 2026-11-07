package com.example.app_proxima_etapa.agenda;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_proxima_etapa.R;
import com.example.app_proxima_etapa.model.Curso;
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
        View view = LayoutInflater.from(parent.getContext())
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
        holder.containerEventos.removeAllViews();
        holder.txtNumeroDia.setBackground(null);
        holder.txtNumeroDia.setTextColor(
                Color.parseColor("#35363A")
        );
        holder.itemView.setOnClickListener(null);
        holder.itemView.setClickable(false);
        if (!dia.isPertenceAoMes()) {
            holder.txtNumeroDia.setText("");
            holder.txtNumeroDia.setVisibility(View.INVISIBLE);
            holder.containerEventos.setVisibility(View.INVISIBLE);
            return;
        }
        holder.txtNumeroDia.setVisibility(View.VISIBLE);
        holder.containerEventos.setVisibility(View.VISIBLE);
        holder.txtNumeroDia.setText(
                String.valueOf(dia.getNumeroDia())
        );
        int coluna = position % 7;
        if (coluna == 0 || coluna == 6) {
            holder.txtNumeroDia.setTextColor(
                    Color.parseColor("#8B8D98")
            );
        }
        if (!dia.getCursos().isEmpty()) {
            TextView indicador = new TextView(
                    holder.itemView.getContext()
            );
            indicador.setText("●");
            indicador.setTextSize(14);
            indicador.setTextColor(
                    Color.parseColor("#2196F3")
            );
            indicador.setGravity(
                    android.view.Gravity.CENTER
            );
            holder.containerEventos.addView(indicador);
            holder.itemView.setOnClickListener(v -> {
                StringBuilder detalhes = new StringBuilder();
                for (Curso curso : dia.getCursos()) {
                    detalhes.append(curso.getTitulo());
                    if (curso.getLocal() != null
                            && !curso.getLocal().isEmpty()) {
                        detalhes.append("\n");
                        detalhes.append(curso.getLocal());
                    }
                    detalhes.append("\n\n");
                }
                new AlertDialog.Builder(v.getContext())
                        .setTitle(
                                "Cursos do dia " + dia.getNumeroDia()
                        )
                        .setMessage(detalhes.toString().trim())
                        .setPositiveButton("Fechar", null)
                        .show();
            });
        }
    }
    @Override
    public int getItemCount() {
        return dias.size();
    }
    static class DiaViewHolder
            extends RecyclerView.ViewHolder {
        TextView txtNumeroDia;
        LinearLayout containerEventos;
        public DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumeroDia = itemView.findViewById(
                    R.id.txtNumeroDia
            );
            containerEventos = itemView.findViewById(
                    R.id.containerEventos
            );
        }
    }
}
