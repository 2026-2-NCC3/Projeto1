
package com.example.app_proxima_etapa.api;

public class PresencaRequest {

    private String codigoQrCode;
    private String course_id;

    public PresencaRequest(String codigoQrCode, String course_id) {
        this.codigoQrCode = codigoQrCode;
        this.course_id = course_id;
    }
}
