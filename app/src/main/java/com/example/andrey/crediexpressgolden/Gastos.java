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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Gastos extends Activity{
    private String Nombredelgato;
    private EditText Nombre,Valor;
    private Button Enviardatos;

    private String EsteCobrador;

    private ProgressDialog pDialog;
    private Spinner SPNGastos;
    JSONParser jsonParsero = new JSONParser();
    static Config Co=new Config();
    private static final String ER_URL = Co.URL+"gastos.php";
    private static final String TAG_SUCCESSo = "success1";
    private static final String TAG_MESSAGEs = "message1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Nombre=(EditText) findViewById(R.id.txtGasto);
        Valor=(EditText) findViewById(R.id.txtValorGasto);
        Enviardatos =(Button) findViewById(R.id.btnEnviarGasto);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador=prefecorr.getString("Docu","");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Enviardatos.setOnClickListener(click);

        SPNGastos= (Spinner) findViewById(R.id.SPNGastos);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource(this, R.array.Gastos, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPNGastos.setAdapter(spinner_adapter);


        SPNGastos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position!=0)
                    Nombre.setVisibility(View.INVISIBLE);
                if(position==0)
                    Nombre.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Nombre.setVisibility(View.VISIBLE);
            }

        });
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            envialos();
        }
    };

    public void envialos() {

        new EnviarDatos().execute();
    }
    class EnviarDatos extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Gastos.this);
            pDialog.setMessage("Enviando Datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int successs=0;

            String[] Datosaenviars=new String[3];
            if(SPNGastos.getSelectedItem().toString().equals("Otro"))
                Datosaenviars[0]=Nombre.getText().toString();
            else
                Datosaenviars[0]=SPNGastos.getSelectedItem().toString();
            Datosaenviars[1]=Valor.getText().toString();
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

            pDialog.dismiss();
            if (filess_url != null) {
                Toast.makeText(Gastos.this, filess_url, Toast.LENGTH_LONG).show();
            }
            Toast.makeText(Gastos.this, "El gasto se ha agregado exitosamente", Toast.LENGTH_SHORT).show();
            Intent k=new Intent(Gastos.this,Menu.class);
            finish();
            startActivity(k);
        }
    }
}
