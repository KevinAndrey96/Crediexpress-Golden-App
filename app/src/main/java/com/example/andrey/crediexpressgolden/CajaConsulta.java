package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
 * Created by Andrey on 13/09/2016.
 */
public class CajaConsulta extends Activity {
    public TextView valu;
    String V;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja_consulta);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador = prefecorr.getString("Docu", "");
        Palabra=EsteCobrador;
        valu= (TextView) findViewById(R.id.tvconsultacaja);
        new Consulta().execute();

    }
    private String Palabra = "";

    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String CONSULTA_URL = C.URL+"lista_consultacaja.php";

    private static final String TAG_DATO1 ="DATO1";
    JSONParser jParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    JSONArray products = null;
    String EsteCobrador;
    String Valor;
    String Pin;
    private ProgressDialog pDialog;
    private static final String TAG_RECEIVE = "receive";

    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CajaConsulta.this);
            pDialog.setMessage("Cargando dato de caja...");
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
                    String[] Datos=new String [7];
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        Datos[0] = c.getString(TAG_DATO1);

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, "" + Datos[0]);
                        V=Datos[0];

                    }

                }
            } catch (JSONException e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            valu.setText("$"+V);
        }
    }
}
