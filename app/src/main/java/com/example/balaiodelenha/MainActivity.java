package com.example.balaiodelenha;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private EditText txtConsumoTotal;
    private EditText txtCouvert;
    private EditText txtPessoas;
    private EditText taxa;
    private EditText contaTotal;
    private EditText valorPessoa;


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
        txtConsumoTotal = (EditText) findViewById(R.id.contaTotal);
        txtCouvert = (EditText) findViewById(R.id.couvertArtistico);
        txtPessoas = (EditText) findViewById(R.id.pessoas);
        taxa = (EditText) findViewById(R.id.taxa);
        contaTotal = (EditText) findViewById(R.id.contaTotal);
        valorPessoa = (EditText) findViewById(R.id.valorPessoa);

        Button btn = findViewById(R.id.btnCalcular);


        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ico_balaio_lenha);
        getSupportActionBar().setTitle("BalaioDeLenha");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#808080")));
    }

    public void onClickCalc(View view){
        int pessoas = Integer.parseInt(txtPessoas.getText().toString());
        double consumoTotal = Double.parseDouble(txtConsumoTotal.getText().toString());
        double counvert = Double.parseDouble(txtCouvert.getText().toString());

        String resultadoContaTotal = calcConsumoTot();
        contaTotal.setText(resultadoContaTotal);

        String resultado10 = calcula10(resultadoContaTotal);
        taxa.setText(resultado10);

        String resultadoPessoa = calcularPorPessoa(resultadoContaTotal, pessoas);
        valorPessoa.setText(resultadoPessoa);

        new HttpAsyncTask().execute();
    }

    public String calcConsumoTot(){
        String totalString = txtConsumoTotal.getText().toString();
        String couvertString = txtCouvert.getText().toString();
        String pessoasString = txtPessoas.getText().toString();

        double cTotal = Double.parseDouble(totalString);
        double couvert = Double.parseDouble(couvertString);
        double pessoas = Double.parseDouble(pessoasString);

        double calcTotal = cTotal + (couvert * pessoas);

        String resultadoFormatado = String.format("%.2f", calcTotal);

        String resultado = ("R$ " + resultadoFormatado);

        return resultado;
    }


    public String calcula10(String total){

        double contaTotal = Double.parseDouble(total);
        double total10 = contaTotal * 0.1;

        String total10String = String.format("%.2f", total10);
        String resultado = ("R$ " + total10String);
        return resultado;
    }

    public String calcularPorPessoa(String total, int pessoas){

        double contaTotal = Double.parseDouble(total);
        double porPessoa = contaTotal / pessoas;

        String contaPessoa = String.format("%.2f", porPessoa);
        String resultado = ("R$ " + contaPessoa);
        return resultado;
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

                    double conta = Double.parseDouble(txtConsumoTotal.getText().toString());
                    double resultado = conta / compra;
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