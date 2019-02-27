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
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class Rutas extends Activity {
    private String Palabra = "";
    private String Clien = "";
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private String Pins="";
    private static String PALABRA_URL = C.URL+"rutas.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "empresas";
    private String Signo ="";
    private static final String TAG_NOMBREDESTINO ="NOMBREDESTINO";
    String PIN="";
    private static final String TAG_ENCARGADO ="ENCARGADO";
    private static final String TAG_CIUDAD ="CIUDAD";
    private static final String TAG_BARRIO ="BARRIO";
    private static final String TAG_DIRECCION ="DIRECCION";
    private static final String TAG_ESTADO ="ESTADO";
    private static final String TAG_ID ="ID";
    private static final String TAG_TELEFONO ="TELEFONO";
    private static final String TAG_COOR ="COOR";
    private static final String TAG_CEDULA ="CEDULA";
    private static final String TAG_ELPIN ="ELPIN";
    private Button Ok;
    private ProgressDialog pDialog;
    private TextView TV;
    String IDE;
    String Telefono;
    String Doc;
    String Coordenadas;
    String Encargado;
    private static final String TAG_SUCCESS2 = "success";
    private static final String TAG_MESSAGE2 = "message";
    JSONParser jsonParser = new JSONParser();
    private static final String URLENVIO = C.URL+"moroso.php";
    private static final String URLDMORA = C.URL+"diamoroso.php";
    private static final String TAG_MESSAGE = "message";
    JSONArray products = null;
    private ListView lista;
    boolean FLAG=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verrutas);

        Display display = getWindowManager().getDefaultDisplay();
        String displayName = display.getName();  // minSdkVersion=17+

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        empresaList = new ArrayList<HashMap<String, String>>();
        lista = (ListView) findViewById(R.id.ListaRutas);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Palabra = prefecorr.getString("Docu", "");


        TV= (TextView) findViewById(R.id.TVidruta);
        Button Realizado=(Button)findViewById(R.id.btnRealizado);
        Realizado.setOnClickListener(realizando);

        Button Llamar=(Button)findViewById(R.id.btnLlamar);
        Llamar.setOnClickListener(llamando);

        Button Mapa=(Button)findViewById(R.id.btnMapa);
        Mapa.setOnClickListener(ubicando);

        new Consulta().execute();



    }
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [4];
            items[0]="Estado Moroso";
            items[1]="Agregar día de mora";
            items[2]="Disminuir día de mora";
            items[3]="Agregar/Editar ubicación";

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
                                case "Agregar/Editar ubicación": {
                                    Intent intent = new Intent(Rutas.this, Ubicacion.class);
                                    intent.putExtra("Cedu", Clien);
                                    startActivity(intent);
                                }break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    private View.OnClickListener realizando = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            realizado();
        }
    };

    public void realizado() {
if(TV.getText().toString().equals(""))
{
    Toast.makeText(Rutas.this, "No ha seleccionado ninguna ruta", Toast.LENGTH_SHORT).show();
}
        else
{

    Intent i = new Intent(Rutas.this,Pago.class);
    i.putExtra("Ruta", TV.getText().toString());
    i.putExtra("Bandera", "No");
    i.putExtra("Cedula", Doc);
    i.putExtra("Pin", Pins);
    startActivity(i);
}

    }
    private View.OnClickListener llamando = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            llamo();
        }
    };

    public void llamo() {
        if(TV.getText().toString().equals(""))
        {
            Toast.makeText(Rutas.this, "No ha seleccionado ninguna ruta", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+Telefono+""));
            startActivity(intent);
        }
    }

    private View.OnClickListener ubicando = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ubico();
        }
    };

    public void ubico() {
        if(TV.getText().toString().equals(""))
        {
            Toast.makeText(Rutas.this, "No ha seleccionado ninguna ruta", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(Coordenadas.equals(""))
            {
                Toast.makeText(Rutas.this, "No hay coordenadas disponibles en esta ruta. por favor contacte un administrador", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:" + Coordenadas + "?q=" + Coordenadas + "(" + Encargado + ")"));
                startActivity(intent);
            }
        }
    }

    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Rutas.this);
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

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        String DATO1 = c.getString(TAG_NOMBREDESTINO);
                        String DATO2 = c.getString(TAG_ENCARGADO);
                        String DATO3 = c.getString(TAG_CIUDAD);
                        String DATO4 = c.getString(TAG_BARRIO);
                        String DATO5 = c.getString(TAG_DIRECCION);
                        String DATO6 = c.getString(TAG_ESTADO);
                        String DATO7 = c.getString(TAG_ID);
                        String DATO8 = c.getString(TAG_TELEFONO);
                        String DATO9 = c.getString(TAG_CEDULA);
                        String DATO1O = c.getString(TAG_COOR);

                        String DATO11 = c.getString(TAG_ELPIN);

                        HashMap map = new HashMap();

                        map.put(TAG_NOMBREDESTINO, ""+DATO1);
                        map.put(TAG_ENCARGADO, ""+DATO2);
                        map.put(TAG_CIUDAD, ""+DATO3);
                        map.put(TAG_BARRIO, ""+DATO4);
                        map.put(TAG_DIRECCION, ""+DATO5);
                        map.put(TAG_ESTADO, ""+DATO6);
                        map.put(TAG_ID, ""+DATO7);
                        map.put(TAG_TELEFONO, ""+DATO8);
                        map.put(TAG_CEDULA, ""+DATO9);
                        map.put(TAG_COOR, ""+DATO1O);
                        map.put(TAG_ELPIN, ""+DATO11);

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
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            Rutas.this,
                            empresaList,

                            R.layout.activity_sprutas,
                            new String[]{
                                    TAG_NOMBREDESTINO,

                                    TAG_CIUDAD,
                                    TAG_BARRIO,
                                    TAG_DIRECCION,

                                    TAG_ID,
                                    TAG_TELEFONO,
                                    TAG_ELPIN,

                            },
                            new int[]{


                                    R.id.SPDestino,

                                    R.id.SPCiudad,
                                    R.id.SPBarrio,
                                    R.id.SPDireccion,

                                    R.id.SPID,
                                    R.id.SPTel,
                                    R.id.SPPinruta,


                            });

                    lista.setAdapter(adapter);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String listChoice = (lista.getItemAtPosition(position)).toString();
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                            String iddelproducto = (String) map.get("ID");
                            IDE = iddelproducto;

                            TV.setText(IDE);
                            Telefono = (String) map.get("TELEFONO");
                            Doc = (String) map.get("CEDULA");
                            Coordenadas = (String) map.get("COOR");
                            Encargado = (String) map.get("NOMBREDESTINO");
                            Pins = (String) map.get("ELPIN");

                        }
                    });
                    lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                                       int index, long arg3) {
                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(index);
                            PIN=(String) map.get("ELPIN");
                            Clien=(String) map.get("CEDULA");
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
    class CLASEMOROSO extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Rutas.this);
            pDialog.setMessage("Cambiando estado moroso...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String[] Datosaenviar=new String[1];
            Datosaenviar[0]=Clien;


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
                Toast.makeText(Rutas.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
    class CLASEDIAMOROSO extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Rutas.this);
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
                Toast.makeText(Rutas.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
