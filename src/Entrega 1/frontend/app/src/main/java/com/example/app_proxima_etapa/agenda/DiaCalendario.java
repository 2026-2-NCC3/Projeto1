package com.example.app_proxima_etapa.agenda;

public class DiaCalendario {

    private int numeroDia;
    private boolean pertenceAoMes;

    public DiaCalendario(int numeroDia, boolean pertenceAoMes) {
        this.numeroDia = numeroDia;
        this.pertenceAoMes = pertenceAoMes;
    }

    public int getNumeroDia() {
        return numeroDia;
    }

    public boolean isPertenceAoMes() {
        return pertenceAoMes;
    }
}