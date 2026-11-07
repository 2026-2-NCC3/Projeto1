package com.example.app_proxima_etapa.agenda;
import com.example.app_proxima_etapa.model.Curso;
import java.util.ArrayList;
import java.util.List;
public class DiaCalendario {
    private int numeroDia;
    private boolean pertenceAoMes;
    private List<Curso> cursos;
    public DiaCalendario(int numeroDia, boolean pertenceAoMes) {
        this.numeroDia = numeroDia;
        this.pertenceAoMes = pertenceAoMes;
        this.cursos = new ArrayList<>();
    }
    public int getNumeroDia() {
        return numeroDia;
    }
    public boolean isPertenceAoMes() {
        return pertenceAoMes;
    }
    public List<Curso> getCursos() {
        return cursos;
    }
    public void adicionarCurso(Curso curso) {
        cursos.add(curso);
    }
}
