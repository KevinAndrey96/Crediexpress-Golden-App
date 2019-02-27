package com.example.andrey.crediexpressgolden;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.Map;

/**
 * Created by Andrey on 25/05/2016.
 */
public class VerClientes extends Activity {
    private String Palabra = "";
    private String Signo ="";
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String PALABRA_URL = C.URL+"vercreditodeclientes.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "empresas";

    private static final String TAG_DATO1 ="Dato1";
    private static final String TAG_DATO2 ="Dato2";
    private static final String TAG_DATO3 ="Dato3";
    private static final String TAG_DATO4 ="Dato4";
    private static final String TAG_DATO5 ="Dato5";
    private static final String TAG_DATO6 ="Dato6";
    private static final String TAG_DATO7 ="Dato7";
    private static final String TAG_DATO8 ="Dato8";
    private static final String TAG_DATO9 ="Dato9";
    private static final String TAG_DATO10 ="Dato10";
    String PIN="";
    private ProgressDialog pDialog;
    JSONArray products = null;

    int success;
    ListView lista;

    Button btnMoroso;
    private static final String TAG_SUCCESS2 = "success";
    private static final String TAG_MESSAGE2 = "message";
    JSONParser jsonParser = new JSONParser();
    private static final String URLENVIO = C.URL+"moroso.php";
    private static final String URLDMORA = C.URL+"diamoroso.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vercliente);
        empresaList = new ArrayList<HashMap<String, String>>();
        lista = (ListView) findViewById(R.id.ListaVerCliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Palabra = getIntent().getExtras().getString("DDD");
        new Consulta().execute();
    }
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [3];
            items[0]="Estado Moroso";
            items[1]="Agregar día de mora";
            items[2]="Disminuir día de mora";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Opciones")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Estado Moroso": {
                                    moro();
                                }
                                break;
                                case "Agregar día de mora": {
                                    Signo="suma";

                                    new CLASEDIAMOROSO().execute();
                                }
                                break;
                                case "Disminuir día de mora":{
                                    Signo="resta";
                                    new CLASEDIAMOROSO().execute();
                                }break;
                            }
                        }
                    });

            return builder.create();
        }
    }



    private View.OnClickListener mor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moro();
        }
    };
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerClientes.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String P = Palabra;
            List params = new ArrayList();

            params.add(new BasicNameValuePair("pala", P));


            JSONObject json = jParser.makeHttpRequest(PALABRA_URL, "POST", params);


            try {

                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject cl = products.getJSONObject(i);

                        String Dato1 = cl.getString(TAG_DATO1);
                        String Dato2 = cl.getString(TAG_DATO2);
                        String Dato3 = cl.getString(TAG_DATO3);
                        String Dato4 = cl.getString(TAG_DATO4);
                        String Dato5 = cl.getString(TAG_DATO5);
                        String Dato6 = cl.getString(TAG_DATO6);
                        String Dato7 = cl.getString(TAG_DATO7);
                        String Dato8 = cl.getString(TAG_DATO8);
                        String Dato9 = cl.getString(TAG_DATO9);
                        String Dato10 = cl.getString(TAG_DATO10);
                        //PIN=Dato9;

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, "Valor: "+Dato1);
                        map.put(TAG_DATO2, ""+Dato2);
                        map.put(TAG_DATO3, "Interes: $"+Dato3);
                        map.put(TAG_DATO4, "Seguro: $"+Dato4);
                        map.put(TAG_DATO5, "Cuotas restantes: "+Dato5);
                        map.put(TAG_DATO6, " de "+Dato6);
                        map.put(TAG_DATO7, "Estado: "+Dato7);
                        map.put(TAG_DATO8, "Deuda Restante: "+Dato8);
                        map.put(TAG_DATO9, "Pin: "+Dato9);
                        map.put(TAG_DATO10, "Dias de mora: "+Dato10 );

                        empresaList.add(map);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(success!=1) {
                Toast.makeText(VerClientes.this, "No hay creditos activos para este cliente", Toast.LENGTH_SHORT).show();
                finish();
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            VerClientes.this,
                            empresaList,
                            R.layout.activity_spvercliente,
                            new String[]{

                                    TAG_DATO1,
                                    TAG_DATO2,
                                    TAG_DATO3,
                                    TAG_DATO4,
                                    TAG_DATO5,
                                    TAG_DATO6,
                                    TAG_DATO7,
                                    TAG_DATO8,
                                    TAG_DATO9,
                                    TAG_DATO10,
                            },
                            new int[]{
                                    R.id.SPValorQ,
                                    R.id.SPFechaQ,
                                    R.id.SPInteresQ,
                                    R.id.SPSeguroQ,
                                    R.id.SPCuotasQ,
                                    R.id.SPTotalQ,
                                    R.id.SPEstadoQ,
                                    R.id.SPDeudaQ,
                                    R.id.SPPIN,
                                    R.id.SPDiasMora
                            });

                    lista.setAdapter(adapter);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String listChoice = (lista.getItemAtPosition(position)).toString();
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                              String Estado = (String) map.get("Dato7");
                            Estado = Estado.substring(8);
                            String p = (String) map.get("Dato9");
                            p = p.substring(5);
                            if (Estado.equals("Activo")) {
                                Intent i = new Intent(VerClientes.this, Pago.class);

                                i.putExtra("Bandera", "Si");
                                i.putExtra("Cedula", Palabra);
                                i.putExtra("Pin", p);
                                startActivity(i);
                            } else {
                                Toast.makeText(VerClientes.this, "Este no es un credito activo, ya está pago", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                                       int index, long arg3) {
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(index);
                            String p = (String) map.get("Dato9");
                            p = p.substring(5);

                            PIN=p;
                            FragmentManager fragmentManager = getFragmentManager();
                            DialogoSeleccion dialogo = new DialogoSeleccion();
                            dialogo.show(fragmentManager, "tagAlerta");
                            return true;
                        }
                    });
                }
            });
        }
    }
    public void moro()
    {
        new CLASEMOROSO().execute();
    }
    //INICIO CLASE MOROSO
    class CLASEMOROSO extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerClientes.this);
            pDialog.setMessage("Cambiando estado moroso...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String[] Datosaenviar=new String[1];
            Datosaenviar[0]=Palabra;


            try {

                List params = new ArrayList();
                for(int i=0;i<1;i++)
                {
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datosaenviar[i]));
                }

                JSONObject json = jsonParser.makeHttpRequest(URLENVIO, "POST", params);
                success = json.getInt(TAG_SUCCESS2);
                if (success == 1) {
                    return json.getString(TAG_MESSAGE2);
                } else {
                    return json.getString(TAG_MESSAGE2);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(VerClientes.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
    class CLASEDIAMOROSO extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerClientes.this);
            pDialog.setMessage("Cambiando estado dias de mora...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String[] Datosaenviar=new String[1];
            Datosaenviar[0]=PIN+"-"+Signo;


            try {
                List params = new ArrayList();
                for(int i=0;i<1;i++)
                {
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datosaenviar[i]));
                }
                JSONObject json = jsonParser.makeHttpRequest(URLDMORA, "POST", params);
                success = json.getInt(TAG_SUCCESS2);
                if (success == 1) {
                    return json.getString(TAG_MESSAGE2);
                } else {
                    return json.getString(TAG_MESSAGE2);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(VerClientes.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
