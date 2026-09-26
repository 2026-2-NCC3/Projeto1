package com.example.app_proxima_etapa; // Confirme se o pacote está igual ao seu

import com.example.app_proxima_etapa.model.Curso;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {

    private List<Curso> listaCursos;
    private Context context;

    // Construtor do Adapter
    public CursoAdapter(List<Curso> listaCursos, Context context) {
        this.listaCursos = listaCursos;
        this.context = context;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout item_curso.xml que você acabou de criar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        // Pega o curso atual da lista
        Curso curso = listaCursos.get(position);

        // Preenche os dados na tela
        holder.txtNomeCurso.setText(curso.getNome());
        holder.txtCargaHoraria.setText("Total de vagas: " + curso.getTotalVagas());
        holder.imgCursoIcone.setImageResource(curso.getImagemResId());

        // Ação do botão "Detalhes" usando Intent
        holder.btnDetalhes.setOnClickListener(v -> {
            // Vai ficar vermelho porque ainda não criamos a DetalhesActivity!
            Intent intent = new Intent(context, DetalhesActivity.class);
            intent.putExtra("CURSO_SELECIONADO", curso);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    // Classe interna que mapeia os componentes do item_curso.xml
    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCursoIcone;
        TextView txtNomeCurso, txtCargaHoraria;
        Button btnDetalhes;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCursoIcone = itemView.findViewById(R.id.imgCursoIcone);
            txtNomeCurso = itemView.findViewById(R.id.txtNomeCurso);
            txtCargaHoraria = itemView.findViewById(R.id.txtCargaHoraria);
            btnDetalhes = itemView.findViewById(R.id.btnDetalhes);
        }
    }
}
