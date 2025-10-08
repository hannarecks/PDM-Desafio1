package com.example.balaiodelenha;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText txtDolar, txtDataCota;
    private EditText txtContaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtDolar = (EditText) findViewById(R.id.txtDolar);
        txtDataCota = (EditText) findViewById(R.id.txtDatacota);
        txtContaTotal = (EditText) findViewById(R.id.contaTotal);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ico_balaio_lenha);
        getSupportActionBar().setTitle("BalaioDeLenha");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#808080")));
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://br.dolarapi.com/v1/cotacoes/usd");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int status = urlConnection.getResponseCode();

                if(status == 200){
                    InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();

                    String inputString;
                    while((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }
                    urlConnection.disconnect();
                    return builder.toString();
                }
            } catch (Exception ex) {
                Log.e("URL", ex.toString());
            }
            return null;
        }

        // onPostExecute mostra os resultados da AsyncTask
        public void onPostExecute(String result) {
            dialog.dismiss();
            if(result != null) {
                try {
                    JSONObject obj = new JSONObject (result);
                    double compra = obj.getDouble("compra");
                    String dataAtualizacao = obj.getString("dataAtualizacao");

                    double conta = Double.parseDouble(txtContaTotal.getText().toString());
                    double resultado = conta * compra;
                    String resultadoFormatado = String.format("%.2f", resultado);

                    txtDolar.setText(resultadoFormatado);
                    txtDataCota.setText(dataAtualizacao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onPreExecute(){
            dialog = new ProgressDialog(MainActivity.this);
            dialog.show();
        }
    }
}