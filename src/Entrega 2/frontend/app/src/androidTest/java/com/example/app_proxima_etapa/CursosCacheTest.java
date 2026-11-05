package com.example.app_proxima_etapa;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.app_proxima_etapa.storage.CursosCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CursosCacheTest {
    @Test public void cachePersisteEAtualizaInclusiveListaVazia() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("catalogo-test.db");
        try {
            try (CursosCache cache = new CursosCache(context, "catalogo-test.db")) {
                assertNull(cache.ler());
                cache.salvar("[{\"id\":\"curso-teste\"}]");
            }
            try (CursosCache reaberto = new CursosCache(context, "catalogo-test.db")) {
                assertEquals("[{\"id\":\"curso-teste\"}]", reaberto.ler());
                reaberto.salvar("[]");
                assertEquals("[]", reaberto.ler());
            }
        } finally { context.deleteDatabase("catalogo-test.db"); }
    }
}
