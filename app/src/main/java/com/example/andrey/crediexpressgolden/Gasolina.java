package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Gasolina extends Activity {
    private EditText txtKilos, txtValorgas;
    private String EsteCobrador;
    private ProgressDialog pDialog;

    JSONParser jsonParsero = new JSONParser();
    static Config Co=new Config();
    private static final String ER_URL = Co.URL+"gasolina.php";
    private static final String TAG_SUCCESSo = "success1";
    private static final String TAG_MESSAGEs = "message1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasolina);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txtKilos=(EditText) findViewById(R.id.txtkil);
        txtValorgas=(EditText) findViewById(R.id.txtvalg);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador=prefecorr.getString("Docu","");

        Button Gas =(Button) findViewById(R.id.btnEnviaGas);
        Gas.setOnClickListener(Gaso);
    }
    private View.OnClickListener Gaso= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new EnviarDatos().execute();

            Intent i=new Intent(Gasolina.this, Menu.class);
            finish();
            startActivity(i);
        }
    };
    class EnviarDatos extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Gasolina.this);
            pDialog.setMessage("Enviando Datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            int successs=0;

            String[] Datosaenviars=new String[3];
            Datosaenviars[0]=txtKilos.getText().toString();
            Datosaenviars[1]=txtValorgas.getText().toString();
            Datosaenviars[2]=EsteCobrador;

            try {

                List params = new ArrayList();
                for(int i=0;i<3;i++)
                {
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datosaenviars[i]));
                }

                JSONObject jsonl = jsonParsero.makeHttpRequest(ER_URL, "POST", params);

                successs = jsonl.getInt(TAG_SUCCESSo);

                if (successs == 1) {
                    finish();

                    return jsonl.getString(TAG_MESSAGEs);

                } else {


                    return jsonl.getString(TAG_MESSAGEs);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String filess_url) {
            Toast.makeText(Gasolina.this, filess_url, Toast.LENGTH_LONG).show();
        }
    }
}
