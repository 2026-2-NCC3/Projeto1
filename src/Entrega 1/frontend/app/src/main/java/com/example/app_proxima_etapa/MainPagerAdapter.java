package com.example.app_proxima_etapa;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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

        // Posição 0 = Agenda (lado esquerdo)
        if (position == 0) {
            return new AgendaFragment();
        }

        // Posição 1 = Cursos (tela inicial)
        return new CursosFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}