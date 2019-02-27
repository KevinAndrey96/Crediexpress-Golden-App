package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Datos extends Activity{
    private String Palabra = "";
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String PALABRA_URL = C.URL+"palabras.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "empresas";

    private static final String TAG_DOCUMENTO ="Documento";
    private static final String TAG_NOMBRES ="Nombres";
    private static final String TAG_APELLIDOS ="Apellidos";
    private static final String TAG_TELEFONO ="Telefono";
    private static final String TAG_PLACA ="Placa";
    private static final String TAG_EMAIL ="Email";
    private static final String TAG_TRANS ="Trans";
    private boolean FLAG=false;

    private ProgressDialog pDialog;
    JSONArray products = null;
    //ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        empresaList = new ArrayList<HashMap<String, String>>();
        //lista = (ListView) findViewById(R.id.ListaDatos);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Palabra = prefecorr.getString("Docu", "");

        try {
            new Consulta().execute();
        }catch(Exception e){
            Toast.makeText(Datos.this, "La conexión con el servidor falló", Toast.LENGTH_SHORT).show();
        }
    }
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Datos.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String P = Palabra;
            List params = new ArrayList();

            params.add(new BasicNameValuePair("pala", P));


            JSONObject json = jParser.makeHttpRequest(PALABRA_URL, "POST", params);

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        String Documento = c.getString(TAG_DOCUMENTO);
                        String Nombres = c.getString(TAG_NOMBRES);
                        String Apellidos = c.getString(TAG_APELLIDOS);
                        String Telefono = c.getString(TAG_TELEFONO);
                        String Placa = c.getString(TAG_PLACA);

                        String Email = c.getString(TAG_EMAIL);
                        String trans = c.getString(TAG_TRANS);
                        HashMap map = new HashMap();

                        map.put(TAG_DOCUMENTO, "Documento: "+Documento);
                        map.put(TAG_NOMBRES, "Nombres: "+Nombres);
                        map.put(TAG_APELLIDOS, "Apellidos: "+Apellidos);
                        map.put(TAG_TELEFONO, "Telefono: "+Telefono);
                        map.put(TAG_PLACA, "Placa: "+Placa);

                        map.put(TAG_EMAIL, "Email: "+Email);
                        map.put(TAG_TRANS, "Mis Transacciones Hoy: "+trans);



                        empresaList.add(map);
                    }
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
                            Datos.this,
                            empresaList,
                            R.layout.activity_datos,
                            new String[]{
                                    TAG_DOCUMENTO,
                                    TAG_NOMBRES,
                                    TAG_APELLIDOS,
                                    TAG_TELEFONO,
                                    TAG_PLACA,
                                    TAG_EMAIL,
                                    TAG_TRANS,
                            },
                            new int[]{
                                    R.id.SPDDoc,
                                    R.id.SPDnom,
                                    R.id.SPDape,
                                    R.id.SPDtel,
                                    R.id.SPDpla,
                                    R.id.SPDema,
                                    R.id.SPDtran,
                            });
                    if (FLAG)
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //lista.setAdapter(adapter);
                }
            });

        }
    }
}
