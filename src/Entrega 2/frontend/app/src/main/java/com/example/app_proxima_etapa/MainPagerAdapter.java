package com.example.app_proxima_etapa;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.os.Bundle;

import com.example.app_proxima_etapa.agenda.AgendaFragment;
import com.example.app_proxima_etapa.curso.CursosFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(
            @NonNull FragmentActivity fragmentActivity
    ) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new AgendaFragment();
        }
        CursosFragment tela = new CursosFragment();
        Bundle dados = new Bundle();
        dados.putBoolean("meusCursos", position == 2);
        tela.setArguments(dados);
        return tela;
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}