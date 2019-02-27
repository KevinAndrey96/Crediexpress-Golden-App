package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Andrey on 13/09/2016.
 */
public class Envia_Caja extends Activity {
    String EsteCobrador,Tipo;
    EditText ValCaj;
    String Info;
    String Recibo="";
    String Fecha="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviacaja);
        Tipo=getIntent().getExtras().getString("Tipo","").toString();
        SharedPreferences Permis3 = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
        Info = Permis3.getString("Permis3", "");
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador = prefecorr.getString("Docu", "");
        TextView f=(TextView) findViewById(R.id.TVValue);
        f.setText("Valor a "+Tipo+"r");

        ValCaj=(EditText) findViewById(R.id.txtValorCaja);

        Calendar c = Calendar.getInstance();
        Fecha="Fecha: "+c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);


        Button btnEnvia=(Button) findViewById(R.id.btnenviacaja);
        btnEnvia.setOnClickListener(enviacaja);

    }

    private View.OnClickListener enviacaja = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Enviar().execute();
        }
    };
    static Config C=new Config();
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    private static String ENVIO_URL = C.URL+"envia_caja.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Envia_Caja.this);
            pDialog.setMessage("Transacción en progreso...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=EsteCobrador;
            Datos[1]=ValCaj.getText().toString();
            Datos[2]=Tipo;
            try {

                List params = new ArrayList();
                for (int i=0; i<Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datos[i]));

                JSONObject json = jParser.makeHttpRequest(ENVIO_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Envia_Caja.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
            Intent l=new Intent(Envia_Caja.this, ImpresionBixolon.class);
            String o;
            if(Tipo.equals("Acredita"))
            {
                o="Entrega";
            }
            else
            {
                o="Retiro";
            }
            Recibo=Fecha+"\nTipo de operación: "+o+"\nValor: $"+ValCaj.getText().toString()+"\nCobrador: "+EsteCobrador+"\nInformes: "+Info;
            l.putExtra("RECIBO", Recibo);
            startActivity(l);
        }
    }
}
