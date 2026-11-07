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
        viewPagerPrincipal =
                findViewById(R.id.viewPagerPrincipal);

        MainPagerAdapter pagerAdapter =
                new MainPagerAdapter(this);

        viewPagerPrincipal.setAdapter(pagerAdapter);
        viewPagerPrincipal.setCurrentItem(
                1,
                false
        );

        menuLateral =
                findViewById(R.id.menuLateral);

        btnMenuLateral =
                findViewById(R.id.btnMenuLateral);
        menuLateral.post(() -> {
            fecharMenuSemAnimacao();
        });
        btnMenuLateral.setOnClickListener(v -> {
            if (menuAberto) {
                fecharMenu();
            } else {
                abrirMenu();
            }
        });
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

        opcaoInicio.setOnClickListener(v -> {
            viewPagerPrincipal.setCurrentItem(
                    1,
                    true
            );

            fecharMenu();
        });
        opcaoAgenda.setOnClickListener(v -> {
            viewPagerPrincipal.setCurrentItem(
                    0,
                    true
            );

            fecharMenu();
        });

        opcaoCursos.setOnClickListener(v -> {
            viewPagerPrincipal.setCurrentItem(2, true);
            fecharMenu();
        });
        findViewById(R.id.opcaoHistorico).setOnClickListener(v -> {
            fecharMenu();
            startActivity(new Intent(this,
                    com.example.app_proxima_etapa.historico.HistoricoActivity.class));
        });
        opcaoPerfil.setOnClickListener(v -> {
            fecharMenu();
            startActivity(new Intent(this, com.example.app_proxima_etapa.perfil.PerfilActivity.class));
        });
        opcaoConfiguracoes.setOnClickListener(v -> {

            fecharMenu();

        });
        opcaoSair.setOnClickListener(v -> {
            AuthSession.clear();
            Intent login = new Intent(this, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();
        });
    }
    private void abrirMenu() {
        menuAberto = true;
        menuLateral
                .animate()
                .translationX(0f)
                .setDuration(280)
                .start();
    }
    private void fecharMenu() {
        menuAberto = false;
        float deslocamento = dpParaPx(265);

        menuLateral
                .animate()
                .translationX(-deslocamento)
                .setDuration(280)
                .start();
    }
    private void fecharMenuSemAnimacao() {
        menuAberto = false;
        float deslocamento = dpParaPx(265);
        menuLateral.setTranslationX(
                -deslocamento
        );
    }
    private float dpParaPx(float dp) {
        return dp *
                getResources()
                        .getDisplayMetrics()
                        .density;
    }
}