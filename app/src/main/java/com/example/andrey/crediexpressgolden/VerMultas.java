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
import android.support.v7.internal.app.ToolbarActionBar;
import android.util.Log;
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

public class VerMultas extends Activity {

    private String Palabra = "";

    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String CONSULTA_URL = C.URL+"lista_multas.php";

    private static final String TAG_DATO1 ="DATO1";
    private static final String TAG_DATO2 ="DATO2";
    private static final String TAG_DATO3 ="DATO3";
    private static final String TAG_DATO4 ="DATO4";
    private static final String TAG_DATO5 ="DATO5";
    private static final String TAG_DATO6 ="DATO6";
    private static final String TAG_DATO7 ="DATO7";


    JSONArray products = null;
    ListView lista;
    String EsteCobrador;
    String Cliente;
    String Document;
    String Tipo;
    String Valor;
    String Pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vermultas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        empresaList = new ArrayList<HashMap<String, String>>();
        //lista = (ListView) findViewById(R.id.Listamultas);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador = prefecorr.getString("Docu", "");
        Tipo=getIntent().getExtras().getString("Tipo");

        lista=(ListView) findViewById(R.id.Listamultas);
        TextView t=(TextView) findViewById(R.id.TVMultas);
        t.setText(("Lista de multas de tipo "+Tipo).toUpperCase());
        Palabra=EsteCobrador+"-"+Tipo;
        try {
            new Consulta().execute();
        }catch(Exception e){
            Toast.makeText(VerMultas.this, "La conexión con el servidor falló", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("validFragment")
    public class DialogoConfirma extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String items[]=new String[2];
                    items[0] = "Si";
                    items[1] = "No";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Pagará la multa de "+Cliente+" - ¿está seguro?")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch(items[item])
                            {
                                case "Si":{
                                    new Enviar().execute();
                                }break;
                                case "No":{
                                    Toast.makeText(VerMultas.this, "Multa NO pagada", Toast.LENGTH_SHORT).show();
                                }break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerMultas.this);
            pDialog.setMessage("Cargando clientes con multas...");
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
                        Datos[1] = c.getString(TAG_DATO2);
                        Datos[2] = c.getString(TAG_DATO3);
                        Datos[3] = c.getString(TAG_DATO4);
                        Datos[4] = c.getString(TAG_DATO5);
                        Datos[5] = c.getString(TAG_DATO6);
                        Datos[6] = c.getString(TAG_DATO7);

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, ""+Datos[0]);
                        map.put(TAG_DATO2, "Pin: "+Datos[1]);
                        map.put(TAG_DATO3, ""+Datos[2]);
                        map.put(TAG_DATO4, "Signo y seguro: "+Datos[3]);
                        map.put(TAG_DATO5, "Cuotas en mora: "+Datos[4]);
                        map.put(TAG_DATO6, "Valor Credito: "+Datos[5]);
                        map.put(TAG_DATO7, "Valor Multa: "+Datos[6]);

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
                            VerMultas.this,
                            empresaList,
                            R.layout.activity_spmultas,
                            new String[]{
                                    TAG_DATO1,
                                    TAG_DATO2,
                                    TAG_DATO3,
                                    TAG_DATO4,
                                    TAG_DATO5,
                                    TAG_DATO6,
                                    TAG_DATO7,
                            },
                            new int[]{
                                    R.id.SPDocumentoMulta,
                                    R.id.SPPinMulta,
                                    R.id.SPNombrecompletoMulta,
                                    R.id.SPSignoYSeguroMulta,
                                    R.id.SPCuotasMoraMulta,
                                    R.id.SPValorCreditoMulta,
                                    R.id.SPValorMoraMulta,
                            });
                    lista.setAdapter(adapter);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                            Cliente = (String) map.get("DATO3");
                            Pin=(String) map.get("DATO2");
                            Pin=Pin.substring(5);
                            Valor=(String) map.get("DATO7");
                            Valor=Valor.substring(12);
                            Document=(String) map.get("DATO1");
                            FragmentManager fragmentManager = getFragmentManager();
                            DialogoConfirma dialogo = new DialogoConfirma();
                            dialogo.show(fragmentManager, "tagAlerta");
                        }
                    });
                }
            });
        }
    }

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    private static String ENVIO_URL = C.URL+"envia_multas.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_MESSAGE = "message";
    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerMultas.this);
            pDialog.setMessage("Pagando Multa...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[5];
            Datos[0]=EsteCobrador;
            Datos[1]=Pin;
            Datos[2]=Tipo;
            Datos[3]=Valor;
            Datos[4]=Document;
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
                Toast.makeText(VerMultas.this, file_url, Toast.LENGTH_LONG).show();
            }
            Intent u=new Intent(VerMultas.this, ComprobarMulta.class);
            u.putExtra("Pin",Pin);
            startActivity(u);
        }
    }
}
