package com.example.app_proxima_etapa.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class FilaEncontros extends SQLiteOpenHelper {
    public FilaEncontros(Context context) { super(context, "encontros.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE fila (dono TEXT NOT NULL, encontro TEXT NOT NULL, codigo TEXT NOT NULL, PRIMARY KEY(dono,encontro,codigo))");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int antiga, int nova) { }
    public void salvar(String dono, String encontro, String codigo) {
        ContentValues values = new ContentValues();
        values.put("dono", dono); values.put("encontro", encontro); values.put("codigo", codigo);
        getWritableDatabase().insertWithOnConflict("fila", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
    public List<String[]> listar(String dono) {
        List<String[]> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT encontro,codigo FROM fila WHERE dono = ?", new String[]{dono})) {
            while (cursor.moveToNext()) result.add(new String[]{cursor.getString(0), cursor.getString(1)});
        }
        return result;
    }
    public void confirmar(String dono, String encontro, String codigo) {
        getWritableDatabase().delete("fila", "dono = ? AND encontro = ? AND codigo = ?", new String[]{dono,encontro,codigo});
    }
}
