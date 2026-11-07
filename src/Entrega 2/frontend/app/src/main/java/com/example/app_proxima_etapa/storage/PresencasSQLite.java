package com.example.app_proxima_etapa.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.app_proxima_etapa.model.Presenca;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PresencasSQLite extends SQLiteOpenHelper {
    private final Context contexto;
    public PresencasSQLite(Context context) {
        super(context, "presencas.db", null, 1);
        contexto = context.getApplicationContext();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS presencas (" +
                        "id TEXT PRIMARY KEY, " +
                        "aluno_id TEXT NOT NULL, " +
                        "atividade_id TEXT NOT NULL, " +
                        "data_hora TEXT NOT NULL, " +
                        "sincronizado INTEGER DEFAULT 0, " +
                        "UNIQUE(aluno_id, atividade_id))"
        );
    }
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int versaoAntiga,
            int versaoNova
    ) {
        onCreate(db);
    }
    public boolean salvar(Presenca presenca) {
        if (presenca == null
                || presenca.getAluno() == null
                || presenca.getAtividade() == null) {
            return false;
        }

        String alunoId = presenca.getAluno().getId();
        String atividadeId = presenca.getAtividade().getId();

        if (alunoId == null || alunoId.isEmpty()
                || atividadeId == null || atividadeId.isEmpty()) {
            return false;
        }

        String id = presenca.getId();

        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        String data = presenca.getDataHoraRegistro();

        if (data == null || data.isEmpty()) {
            data = dataAtual();
        }
        ContentValues valores = new ContentValues();
        valores.put("id", id);
        valores.put("aluno_id", alunoId);
        valores.put("atividade_id", atividadeId);
        valores.put("data_hora", data);
        valores.put("sincronizado", 0);

        return getWritableDatabase().insertWithOnConflict(
                "presencas",
                null,
                valores,
                SQLiteDatabase.CONFLICT_IGNORE
        ) != -1;
    }
    public boolean presencaExiste(
            String alunoId,
            String atividadeId
    ) {
        if (alunoId == null || atividadeId == null) {
            return false;
        }
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id FROM presencas " +
                        "WHERE aluno_id = ? AND atividade_id = ?",
                new String[]{alunoId, atividadeId}
        )) {
            return cursor.moveToFirst();
        }
    }
    public boolean salvarConfirmada(
            String alunoId,
            String cursoId
    ) {

        if (alunoId == null || alunoId.isEmpty()
                || cursoId == null || cursoId.isEmpty()) {
            return false;
        }

        SQLiteDatabase banco = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("sincronizado", 1);

        int atualizados = banco.update(
                "presencas",
                valores,
                "aluno_id = ? AND atividade_id = ?",
                new String[]{alunoId, cursoId}
        );
        if (atualizados > 0) {
            return true;
        }

        valores.put("id", UUID.randomUUID().toString());
        valores.put("aluno_id", alunoId);
        valores.put("atividade_id", cursoId);
        valores.put("data_hora", dataAtual());
        return banco.insertWithOnConflict(
                "presencas",
                null,
                valores,
                SQLiteDatabase.CONFLICT_IGNORE
        ) != -1;
    }
    public java.util.List<String> listarHistorico(String alunoId) {
        java.util.List<String> registros = new java.util.ArrayList<>();
        if (alunoId == null || alunoId.isEmpty()) return registros;
        java.util.Map<String, String> nomes = new java.util.HashMap<>();
        try (CursosCache cache = new CursosCache(contexto)) {
            String salvo = cache.ler();
            if (salvo != null) {
                org.json.JSONArray cursos = new org.json.JSONArray(salvo);
                for (int i = 0; i < cursos.length(); i++) {
                    org.json.JSONObject curso = cursos.getJSONObject(i);
                    nomes.put(curso.getString("id"), curso.getString("title"));
                }
            }
        } catch (Exception ignored) {
             
        }
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT atividade_id, data_hora, sincronizado FROM presencas " +
                        "WHERE aluno_id = ? ORDER BY data_hora DESC",
                new String[]{alunoId})) {
            while (cursor.moveToNext()) {
                String cursoId = cursor.getString(0);
                String nome = nomes.get(cursoId);
                if (nome == null) nome = "Curso: " + cursoId;
                String situacao = cursor.getInt(2) == 1
                        ? "Confirmada no servidor" : "Pendente de sincronização";
                registros.add(nome + "\nRegistrada em: " + cursor.getString(1)
                        + "\n" + situacao);
            }
        }
        return registros;
    }

    private String dataAtual() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());
    }


    public boolean salvarPendente(String alunoId, String cursoId) {

        if (alunoId == null || cursoId == null
                || alunoId.isEmpty() || cursoId.isEmpty()) {
            return false;
        }

        ContentValues valores = new ContentValues();

        valores.put("id", UUID.randomUUID().toString());
        valores.put("aluno_id", alunoId);
        valores.put("atividade_id", cursoId);
        valores.put("data_hora", dataAtual());
        valores.put("sincronizado", 0);

        long resultado = getWritableDatabase().insertWithOnConflict(
                "presencas",
                null,
                valores,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        return resultado != -1;
    }

    public java.util.List<String[]> listarPendentes() {

        java.util.List<String[]> lista = new java.util.ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT aluno_id, atividade_id FROM presencas " +
                        "WHERE sincronizado = 0",
                null
        );

        while (cursor.moveToNext()) {

            String aluno = cursor.getString(0);
            String curso = cursor.getString(1);

            lista.add(new String[]{aluno, curso});
        }

        cursor.close();

        return lista;
    }
    public boolean marcarSincronizado(String alunoId, String cursoId) {
        ContentValues valores = new ContentValues();
        valores.put("sincronizado", 1);
        int resultado = getWritableDatabase().update(
                "presencas",
                valores,
                "aluno_id = ? AND atividade_id = ?",
                new String[]{alunoId, cursoId}
        );
        return resultado > 0;
    }

}
