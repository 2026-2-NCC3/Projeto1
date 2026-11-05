package com.example.app_proxima_etapa.storage;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Cache apenas do catálogo público. Nenhuma credencial ou dado do aluno é salvo. */
public final class CursosCache extends SQLiteOpenHelper {
    public CursosCache(Context context) { this(context, "catalogo.db"); }
    public CursosCache(Context context, String databaseName) {
        super(context.getApplicationContext(), databaseName, null, 1);
    }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE catalogo (id INTEGER PRIMARY KEY, json TEXT NOT NULL)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    public void salvar(String json) {
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("json", json);
        getWritableDatabase().insertWithOnConflict("catalogo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public String ler() {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT json FROM catalogo WHERE id = 1", null)) {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
    }
}
