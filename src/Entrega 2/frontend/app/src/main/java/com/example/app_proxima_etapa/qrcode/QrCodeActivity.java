package com.example.app_proxima_etapa.qrcode;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_proxima_etapa.MainActivity;
import com.example.app_proxima_etapa.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class QrCodeActivity extends AppCompatActivity {
    ImageView imgQrCode;
    TextView txtCodigoAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        imgQrCode = findViewById(R.id.imgQrCode);
        txtCodigoAluno = findViewById(R.id.txtCodigoAluno);

        findViewById(R.id.btnVoltarQr).setOnClickListener(v -> {
            Intent tela = new Intent(this, MainActivity.class);
            startActivity(tela);
            finish();
        });

        String codigo = getIntent().getStringExtra("codigoQrCode");

        if (codigo != null && !codigo.isEmpty()) {
            gerarQrCode(codigo);
            txtCodigoAluno.setText(codigo);
            findViewById(R.id.btnCopiarCodigo).setOnClickListener(v -> {
                ClipboardManager area = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                area.setPrimaryClip(ClipData.newPlainText("Código do aluno", codigo));
                Toast.makeText(this, "Código copiado!", Toast.LENGTH_SHORT).show();
            });
        } else {
            txtCodigoAluno.setText("Código não encontrado");
            findViewById(R.id.btnCopiarCodigo).setEnabled(false);
            Toast.makeText(this, "Código não encontrado", Toast.LENGTH_SHORT).show();
        }
    }
    private void gerarQrCode(String codigo) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    codigo, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);

            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imgQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}
