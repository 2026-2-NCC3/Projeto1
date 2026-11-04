package com.example.app_proxima_etapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_proxima_etapa.login.LoginActivity;
import com.example.app_proxima_etapa.api.AuthSession;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPagerPrincipal;

    private View menuLateral;
    private View btnMenuLateral;

    private boolean menuAberto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica se existe usuário autenticado
        if (AuthSession.getToken() == null) {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // =================================================
        // VIEWPAGER
        // =================================================

        viewPagerPrincipal =
                findViewById(R.id.viewPagerPrincipal);

        MainPagerAdapter pagerAdapter =
                new MainPagerAdapter(this);

        viewPagerPrincipal.setAdapter(pagerAdapter);

        // 0 = Agenda
        // 1 = Cursos
        //
        // O aplicativo começa na tela de Cursos
        viewPagerPrincipal.setCurrentItem(
                1,
                false
        );


        // =================================================
        // MENU LATERAL
        // =================================================

        menuLateral =
                findViewById(R.id.menuLateral);

        btnMenuLateral =
                findViewById(R.id.btnMenuLateral);

        /*
         * Depois que o layout estiver pronto,
         * fecha o menu sem animação.
         *
         * Assim somente a pontinha verde fica visível.
         */
        menuLateral.post(() -> {
            fecharMenuSemAnimacao();
        });


        // Clique na pontinha
        btnMenuLateral.setOnClickListener(v -> {

            if (menuAberto) {
                fecharMenu();
            } else {
                abrirMenu();
            }
        });


        // =================================================
        // ITENS DO MENU
        // =================================================

        TextView opcaoInicio =
                findViewById(R.id.opcaoInicio);

        TextView opcaoAgenda =
                findViewById(R.id.opcaoAgenda);

        TextView opcaoCursos =
                findViewById(R.id.opcaoCursos);

        TextView opcaoPerfil =
                findViewById(R.id.opcaoPerfil);

        TextView opcaoConfiguracoes =
                findViewById(R.id.opcaoConfiguracoes);

        TextView opcaoSair =
                findViewById(R.id.opcaoSair);


        // INÍCIO
        opcaoInicio.setOnClickListener(v -> {

            viewPagerPrincipal.setCurrentItem(
                    1,
                    true
            );

            fecharMenu();
        });


        // AGENDA
        opcaoAgenda.setOnClickListener(v -> {

            viewPagerPrincipal.setCurrentItem(
                    0,
                    true
            );

            fecharMenu();
        });


        // MEUS CURSOS
        opcaoCursos.setOnClickListener(v -> {

            viewPagerPrincipal.setCurrentItem(
                    1,
                    true
            );

            fecharMenu();
        });


        // MEU PERFIL
        // Ainda criaremos essa tela
        opcaoPerfil.setOnClickListener(v -> {

            fecharMenu();

        });


        // CONFIGURAÇÕES
        // Ainda criaremos essa tela
        opcaoConfiguracoes.setOnClickListener(v -> {

            fecharMenu();

        });


        // SAIR
        // Depois ligamos ao logout real da AuthSession
        opcaoSair.setOnClickListener(v -> {

            fecharMenu();

        });
    }


    // =====================================================
    // ABRIR MENU
    // =====================================================

    private void abrirMenu() {

        menuAberto = true;

        /*
         * Volta o componente inteiro para a posição original.
         *
         * Painel + pontinha se movem juntos.
         */
        menuLateral
                .animate()
                .translationX(0f)
                .setDuration(280)
                .start();
    }


    // =====================================================
    // FECHAR MENU
    // =====================================================

    private void fecharMenu() {

        menuAberto = false;

        /*
         * O painel possui 265dp.
         *
         * Movemos todo o componente 265dp
         * para a esquerda.
         *
         * Assim o painel desaparece,
         * enquanto a pontinha permanece visível.
         */
        float deslocamento = dpParaPx(265);

        menuLateral
                .animate()
                .translationX(-deslocamento)
                .setDuration(280)
                .start();
    }


    // =====================================================
    // FECHAR SEM ANIMAÇÃO
    // =====================================================

    private void fecharMenuSemAnimacao() {

        menuAberto = false;

        float deslocamento = dpParaPx(265);

        menuLateral.setTranslationX(
                -deslocamento
        );
    }


    // =====================================================
    // CONVERTER DP PARA PIXELS
    // =====================================================

    private float dpParaPx(float dp) {

        return dp *
                getResources()
                        .getDisplayMetrics()
                        .density;
    }
}