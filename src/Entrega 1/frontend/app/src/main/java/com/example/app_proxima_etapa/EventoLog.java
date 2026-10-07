package com.example.app_proxima_etapa;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class EventoLog {

    public static void RegistrarEvento(String evento){
        String dataHora = new SimpleDateFormat(
                "dd/mm/yyyy HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());
        System.out.println(
                "[EVENTO] "+evento+ " | Data/Hora: "+dataHora
        );

    }
}
