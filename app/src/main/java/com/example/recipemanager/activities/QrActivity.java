package com.example.recipemanager.activities;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.example.recipemanager.R;
        import com.example.recipemanager.utils.HeaderUtil;
        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;

public class QrActivity extends AppCompatActivity {

    Button scan_btn;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        scan_btn = findViewById(R.id.scanner);
        text = findViewById(R.id.text);
        HeaderUtil.setupHeader(this, "Other Activity", R.id.titleTextView, R.id.backButton, true);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(QrActivity.this);
                intentIntegrator.setPrompt("Scan a QR Code");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null){
            String contents = intentResult.getContents();
            if(contents !=null){
                text.setText(intentResult.getContents());

                Intent intent = new Intent(QrActivity.this, HttpActivity.class);
                intent.putExtra("qr_contents", contents);
                String url = "http://10.0.2.2:8080/api/recipe/" + contents;
                intent.putExtra("url", url);
                startActivity(intent);
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}
