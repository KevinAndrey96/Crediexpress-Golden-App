package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 30/11/2016.
 */
public class MisDatos extends Activity {

    TextView Docum,Nomb,Tele,Plac,Emai,Tran,SaldoT,SaldoR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misdatos);

        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Palabra = prefecorr.getString("Docu", "");

        Docum=(TextView) findViewById(R.id.TxtDatosDocumento);
        Nomb=(TextView) findViewById(R.id.TxtDatosNombre);
        Tele=(TextView) findViewById(R.id.TxtDatosTelefono);
        Plac=(TextView) findViewById(R.id.TxtDatosPlaca);
        Emai=(TextView) findViewById(R.id.TxtDatosEmail);
        Tran=(TextView) findViewById(R.id.TxtDatosTransacciones);
        SaldoT=(TextView) findViewById(R.id.TxtDatosSaldoT);
        SaldoR=(TextView) findViewById(R.id.TxtDatosSaldoR);

        new Consulta().execute();

    }
    ProgressDialog pDialog;
    private String Palabra = "";

    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String CONSULTA_URL = C.URL+"Lista_misdatos.php";

    private static final String TAG_DATO1 ="DATO1";
    private static final String TAG_DATO2 ="DATO2";
    private static final String TAG_DATO3 ="DATO3";
    private static final String TAG_DATO4 ="DATO4";
    private static final String TAG_DATO5 ="DATO5";
    private static final String TAG_DATO6 ="DATO6";
    private static final String TAG_DATO7 ="DATO7";
    private static final String TAG_DATO8 ="DATO8";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_MESSAGE = "message";

    JSONParser jParser = new JSONParser();
    JSONArray products = null;
    String[] Datos;

    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MisDatos.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String P = Palabra;
            List params = new ArrayList();

            params.add(new BasicNameValuePair("Parametro", P));

            JSONObject json = jParser.makeHttpRequest(CONSULTA_URL, "POST", params);

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    products = json.getJSONArray(TAG_RECEIVE);
                    Datos=new String[8];
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        Datos[0] = c.getString(TAG_DATO1);
                        Datos[1] = c.getString(TAG_DATO2);
                        Datos[2] = c.getString(TAG_DATO3);
                        Datos[3] = c.getString(TAG_DATO4);
                        Datos[4] = c.getString(TAG_DATO5);
                        Datos[5] = c.getString(TAG_DATO6);
                        Datos[6] = c.getString(TAG_DATO7);
                        Datos[7] = c.getString(TAG_DATO8);


                        HashMap map = new HashMap();
                    }
                    return json.getString(TAG_MESSAGE);
                }
                else
                {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            Docum.setText(Datos[0]);
            Nomb.setText(Datos[1]);
            Tele.setText(Datos[2]);
            Plac.setText(Datos[3]);
            Emai.setText(Datos[4]);
            Tran.setText(Datos[5]);
            SaldoT.setText(Datos[6]);
            SaldoR.setText(Datos[7]);

        }
    }
}
