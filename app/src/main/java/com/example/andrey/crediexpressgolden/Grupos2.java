package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 02/12/2016.
 */
public class Grupos2 extends Activity {

    String Add,eldocumento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listasp);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        empresaList = new ArrayList<HashMap<String, String>>();
        lista=(ListView) findViewById(R.id.listViewsp);

        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        String EsteCobrador=prefecorr.getString("Docu", "");

        Add=getIntent().getExtras().getString("Add","");

        if(Add.equals("Claro"))
        {
            eldocumento=getIntent().getExtras().getString("DocumentoCliente","");
        }

        Palabra=EsteCobrador;
        new Consulta().execute();
    }
    ProgressDialog pDialog;
    private String Palabra = "";

    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String CONSULTA_URL = C.URL+"Lista_grupos.php";

    private static final String TAG_DATO1 ="DATO1";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_MESSAGE = "message";

    JSONParser jParser = new JSONParser();
    JSONArray products = null;
    ListView lista;

    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Grupos2.this);
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
                    String[] Datos=new String[1];
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        Datos[0] = c.getString(TAG_DATO1);

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, ""+Datos[0]);

                        empresaList.add(map);
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
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            Grupos2.this,
                            empresaList,
                            R.layout.activity_spgrupos,
                            new String[]{
                                    TAG_DATO1,
                            },
                            new int[]{
                                    R.id.spnamegrupo,
                            }){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = convertView;
                            for(int i=0;i<empresaList.size();i++){
                                if(v == null){
                                    LayoutInflater vi = (LayoutInflater)Grupos2.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    v=vi.inflate(R.layout.activity_spgrupos, null);
                                }

                                TextView ElDato1 = (TextView) v.findViewById(R.id.spnamegrupo);

                                ElDato1.setText("" + empresaList.get(position).get(TAG_DATO1));

                            }
                            return v;
                        }
                    };

                    lista.setAdapter(adapter);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                            String Elcampo = (String) map.get("DATO1");

                            if(!Add.equals("Claro")) {

                                Intent i = new Intent(Grupos2.this, MisClientes.class);
                                i.putExtra("datos", "Si");
                                i.putExtra("esco", "No");
                                i.putExtra("Campo", "Grupo");
                                i.putExtra("Valor", Elcampo);
                                finish();
                                startActivity(i);
                            }
                            else
                            {
                                Intent intent = new Intent(Grupos2.this, NuevoGrupo.class);
                                intent.putExtra("DocumentoCliente", eldocumento);
                                intent.putExtra("auto", "Si");
                                intent.putExtra("NombreGrupo", Elcampo);
                                finish();
                                startActivity(intent);
                            }
                        }
                    });
                }
            });
        }
    }

}
