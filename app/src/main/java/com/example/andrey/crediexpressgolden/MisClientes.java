package com.example.andrey.crediexpressgolden;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.Random;


public class MisClientes extends Activity{
    private String Palabra = "";
    ArrayList<SubjectData> arrayList = new ArrayList<SubjectData>();
    private String[] NumGrups;
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String PALABRA_URL = C.URL+"verclientes.php";
    private static String Codeudor_URL = C.URL+"envia_codeudor.php";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "empresas";
    private String Codeudor="Si";
    private String DClien="";
    private String DCodeu="";
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
    private static final String TAG_DATO11 ="Dato11";
    private String Coordenadas="";
    String Telefono="";
    private static final String TAG_SUCCESS2 = "success";
    private static final String TAG_MESSAGE2 = "message";
    JSONParser jsonParser = new JSONParser();
    private static final String URLENVIO = C.URL+"bloqueo.php";

    private ProgressDialog pDialog;
    private TextView tx;
    String nombrecliente;
    private String campo,valor;
    JSONArray products = null;
    ListView lista;
    ListView lista2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misclientes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(getIntent().getExtras().getString("datos").equals("Si")) {
            campo = getIntent().getExtras().getString("Campo");
            valor = getIntent().getExtras().getString("Valor");
            //Toast.makeText(this, "Esco: "+getIntent().getExtras().getString("esco"), Toast.LENGTH_SHORT).show();
            if(getIntent().getExtras().getString("esco").equals("Si"))
            {
                DClien=getIntent().getExtras().getString("Clien");
                Codeudor="Si";
                //Toast.makeText(this, "Codeudor: "+Codeudor, Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Codeudor="No";
            //Toast.makeText(this, "No datos, Codeudor: "+Codeudor, Toast.LENGTH_SHORT).show();
            campo="Nombres";
            valor="";
        }
        empresaList = new ArrayList<HashMap<String, String>>();
        lista = (ListView) findViewById(R.id.listademisclientes);
        //lista2 = (ListView) findViewById(R.id.listademisclientes);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Palabra = prefecorr.getString("Docu", "");
        tx=(TextView) findViewById(R.id.lblID);
        Button find=(Button) findViewById(R.id.btnfind);
        find.setOnClickListener(findc);
        new Consulta().execute();

    }

    private View.OnClickListener findc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(MisClientes.this,Buscar.class);
            i.putExtra("Code","No");
            finish();
            startActivity(i);
        }
    };
    public void BloqCR() {
        if(tx.getText().toString().equals(""))
        {
            Toast.makeText(MisClientes.this, "No ha seleccionado ningún cliente", Toast.LENGTH_SHORT).show();
        }
        else
        {
            new CLASEBLOQUEO().execute();
            Intent i=new Intent(MisClientes.this,Menu.class);
            startActivity(i);
        }

    }
    private View.OnClickListener VerC = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VerCR();
        }
    };
    public void VerCR() {
        if(tx.getText().toString().equals(""))
        {
            Toast.makeText(MisClientes.this, "No ha seleccionado ningún cliente", Toast.LENGTH_SHORT).show();
        }
        else
        {

            Intent i=new Intent(MisClientes.this,VerClientes.class);
            i.putExtra("DDD", tx.getText().toString());
            startActivity(i);
        }

    }
    class CLASEBLOQUEO extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MisClientes.this);
            pDialog.setMessage("Bloqueando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String[] Datosaenviar=new String[1];
            Datosaenviar[0]=tx.getText().toString();


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
                Toast.makeText(MisClientes.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MisClientes.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String P = Palabra+"-"+campo+"-"+valor;
            List params = new ArrayList();

            params.add(new BasicNameValuePair("pala", P));


            JSONObject json = jParser.makeHttpRequest(PALABRA_URL, "POST", params);


            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {

                        JSONObject c = products.getJSONObject(i);

                        String Dato1 = c.getString(TAG_DATO1);
                        String Dato2 = c.getString(TAG_DATO2);
                        String Dato3 = c.getString(TAG_DATO3);
                        String Dato4 = c.getString(TAG_DATO4);
                        String Dato5 = c.getString(TAG_DATO5);
                        String Dato6 = c.getString(TAG_DATO6);
                        String Dato7 = c.getString(TAG_DATO7);
                        String Dato8 = c.getString(TAG_DATO8);
                        String Dato9 = c.getString(TAG_DATO9);
                        String Dato10 = c.getString(TAG_DATO10);
                        String Dato11 = c.getString(TAG_DATO11);

                        HashMap map = new HashMap();

                        map.put(TAG_DATO1, "Documento: "+Dato1);
                        map.put(TAG_DATO2, ""+Dato2);
                        map.put(TAG_DATO3, " "+Dato3);
                        map.put(TAG_DATO4, "Dirección: "+Dato4);
                        map.put(TAG_DATO5, "Telefono: "+Dato5);
                        map.put(TAG_DATO6, "Signo y seguro: "+Dato6);
                        map.put(TAG_DATO7, " - "+Dato7);
                        map.put(TAG_DATO8, "Moroso: "+Dato8);
                        map.put(TAG_DATO9, "Estado: "+Dato9);
                        map.put(TAG_DATO10, ""+Dato10);
                        map.put(TAG_DATO11, ""+Dato11);

                        //String CCodeudor="Si";
                        arrayList.add(new SubjectData(Dato2+" "+Dato3, "Documento: "+ Dato1,"Dirección: "+Dato4, "Telefono: "+Dato5, "Signo & Seguro: "+ Dato6+" - "+Dato7  , Dato11, Codeudor, DClien, Dato10));

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
                            MisClientes.this,
                            empresaList,
                            R.layout.activity_spclientes,
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
                                    TAG_DATO11,
                            },
                            new int[]{


                                    R.id.txtSPDocumentoclientes,
                                    R.id.txtSPNombresclientes,
                                    R.id.txtSPApellidosclientes,
                                    R.id.txtSPDireccionclientes,
                                    R.id.txtSPtelefonoclientes,
                                    R.id.txtSPSignoclientes,
                                    R.id.txtSPNumeroseguroclientes,
                                    R.id.txtSPMorosoclientes,
                                    R.id.txtSPEstadoClientes,
                                    R.id.imgCliente
                            });


                    final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), arrayList);

                    //lista.setAdapter(adapter);
                    //lista2.setAdapter(adapter);
                    lista.setOnTouchListener(null);
                    lista.setClickable(true);
                    lista.setLongClickable(true);


                    lista.setAdapter(customAdapter);

                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(position);
                            String docu = (String) map.get("Dato1");
                            String h = docu.substring(11);
                            tx.setText(h);
                            if (Codeudor.equals("Si")) {
                                AddCodeudor();
                            } else {
                                VerCR();
                            }
                        }
                    });
                    lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                                       int index, long arg3) {

                            String docu=customAdapter.getDocumento(index).substring(11);
                            nombrecliente=customAdapter.getNombreCompleto(index);

                            Telefono=customAdapter.getTelefono(index).substring(10);
                            Coordenadas=customAdapter.getCoordenadas(index);
                            Toast.makeText(MisClientes.this, ""+docu+","+nombrecliente+","+Telefono+","+Coordenadas, Toast.LENGTH_SHORT).show();
                            //Map<String, Object> map = (Map<String, Object>) lista.getItemAtPosition(index);
/*

                            String docu = (String) map.get("Dato1");
                            nombrecliente = (String) map.get("Dato2");
                            nombrecliente += " " + (String) map.get("Dato3");

                            Telefono = (String) map.get("Dato5");
                            Telefono = Telefono.substring(10);
                            String h = docu.substring(11);

                            Coordenadas = (String) map.get("Dato10");
*/
                            tx.setText(docu);

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
    @SuppressLint("validFragment")
    public class DialogoSeleccion3 extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [2];
            items[0]="Agregar a grupo existente";
            items[1]="Agregar a grupo nuevo";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Opciones")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Agregar a grupo existente": {

                                    Intent intent = new Intent(MisClientes.this, Grupos2.class);
                                    intent.putExtra("Add", "Claro");
                                    intent.putExtra("DocumentoCliente", tx.getText().toString());
                                    startActivity(intent);

                                }
                                break;
                                case "Agregar a grupo nuevo": {
                                    Intent intent = new Intent(MisClientes.this, NuevoGrupo.class);
                                    intent.putExtra("DocumentoCliente", tx.getText().toString());
                                    intent.putExtra("auto", "No");
                                    startActivity(intent);
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    @SuppressLint("validFragment")
    public class DialogoSeleccion2 extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [6];
            items[0]="Ver frontal documento";
            items[1]="Ver posterior documento";
            items[2]="Ver rostro cliente";

            items[3]="Agregar/Editar frontal documento";
            items[4]="Agregar/Editar posterior documento";
            items[5]="Agregar/Editar rostro cliente";



            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Opciones")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Ver frontal documento": {

                                    Intent intent = new Intent(MisClientes.this, VerDocumento.class);
                                    intent.putExtra("Cedu", "C1-" + tx.getText().toString());
                                    startActivity(intent);

                                }
                                break;
                                case "Ver posterior documento": {

                                    Intent intent = new Intent(MisClientes.this, VerDocumento.class);
                                    intent.putExtra("Cedu", "C2-" + tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Ver rostro cliente": {

                                    Intent intent = new Intent(MisClientes.this, VerDocumento.class);
                                    intent.putExtra("Cedu", "C3-" + tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Agregar/Editar frontal documento": {
                                    Intent intent = new Intent(MisClientes.this, AgregaDocumento.class);
                                    intent.putExtra("Cedu", "C1-" + tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Agregar/Editar posterior documento": {
                                    Intent intent = new Intent(MisClientes.this, AgregaDocumento.class);
                                    intent.putExtra("Cedu", "C2-" + tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Agregar/Editar rostro cliente": {
                                    Intent intent = new Intent(MisClientes.this, AgregaDocumento.class);
                                    intent.putExtra("Cedu", "C3-" + tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [12];
            items[0]="Llamar a cliente";
            items[1]="Agregar a grupo";
            items[2]="Bloquear";
            items[3]="Agregar credito";
            items[4]="Documento y rostro";
            items[5]="Agregar/Editar ubicación";
            items[6]="Ubicar";
            items[7]="Editar teléfono";
            items[8]="Firma";
            items[9]="Ver Firma";
            items[10]="Favorito";
            items[11]="Recordar";


            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Opciones")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Llamar a cliente": {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + Telefono + ""));
                                    startActivity(intent);
                                }
                                break;
                                case "Agregar a grupo": {
                                    /*int i = 0;
                                    while (true) {
                                        SharedPreferences prefs = getSharedPreferences("Groups" + i, Context.MODE_PRIVATE);
                                        if (!prefs.getString("Groups" + i, "").equals("")) {
                                            i++;
                                        } else {
                                            break;
                                        }
                                    }

                                    NumGrups = new String[i];
                                    if (i != 0) {
                                        i = 0;
                                        int j = 0;
                                        while (true) {
                                            SharedPreferences prefs = getSharedPreferences("Groups" + i, Context.MODE_PRIVATE);
                                            if (!prefs.getString("Groups" + i, "").equals("")) {
                                                NumGrups[j] = j + " - " + prefs.getString("Groups" + i, "");
                                                i++;
                                                j++;
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    FragmentManager fragmentManager = getFragmentManager();
                                    Dialogogrupos dialogou = new Dialogogrupos();
                                    dialogou.show(fragmentManager, "tagAlerta");*/
                                    FragmentManager fragmentManager = getFragmentManager();
                                    DialogoSeleccion3 dialogo3 = new DialogoSeleccion3();
                                    dialogo3.show(fragmentManager, "tagAlerta");
                                }
                                break;
                                case "Bloquear": {
                                    BloqCR();
                                }
                                break;
                                case "Agregar credito": {
                                    SharedPreferences Permis = getSharedPreferences("Permis", Context.MODE_PRIVATE);
                                    if (Permis.getString("Permis", "").equals("No")) {
                                        Toast.makeText(MisClientes.this, "Usted no tiene permiso para realizar esta acción", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent i = new Intent(MisClientes.this, NewCredito.class);
                                        i.putExtra("Document", tx.getText().toString());
                                        i.putExtra("NSeguro", "");
                                        finish();
                                        startActivity(i);
                                    }
                                }
                                break;
                                case "Documento y rostro": {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    DialogoSeleccion2 dialogo2 = new DialogoSeleccion2();
                                    dialogo2.show(fragmentManager, "tagAlerta");

                                }
                                break;
                                case "Agregar/Editar ubicación": {
                                    Intent intent = new Intent(MisClientes.this, Ubicacion.class);
                                    intent.putExtra("Cedu", tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Ubicar": {
                                    if (Coordenadas.equals("")) {
                                        Toast.makeText(MisClientes.this, "No hay coordenadas disponibles para este cliente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("geo:" + Coordenadas + "?q=" + Coordenadas + "(" + nombrecliente + ")"));
                                        startActivity(intent);
                                    }
                                }
                                break;
                                case "Editar teléfono": {
                                    Intent intent = new Intent(MisClientes.this, Telefono.class);
                                    intent.putExtra("DCli", tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Firma": {
                                    Intent intent = new Intent(MisClientes.this, Firma.class);
                                    intent.putExtra("Cedu", tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Ver Firma": {
                                    Intent intent = new Intent(MisClientes.this, VerDocumento.class);
                                    intent.putExtra("Cedu", "C4-"+tx.getText().toString());
                                    startActivity(intent);
                                }
                                break;
                                case "Favorito": {
                                    new EnviarFav().execute();
                                }
                                break;
                                case "Recordar": {

                                    Toast.makeText(MisClientes.this, "Añadido a recordatorios", Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent(MisClientes.this,VerClientes.class);
                                    i.putExtra("DDD", tx.getText().toString());
                                    startActivity(i);

                                    PendingIntent contIntent = PendingIntent.getActivity(MisClientes.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(MisClientes.this)
                                                    .setContentIntent(contIntent)
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setLargeIcon((((BitmapDrawable) getResources()
                                                            .getDrawable(R.mipmap.ic_launcher)).getBitmap()))
                                                    .setContentTitle("Cobrar a " + nombrecliente)
                                                    .setContentText("Recordatorio de cobro")
                                                    .setContentInfo("CrediExpress Golden")
                                                    .setVibrate(new long[]{100, 250, 100, 500})
                                                    .setTicker("Recordatorio de cobro CrediExpress Golden");



                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    Random rnd = new Random();
                                    int noti = (int)(rnd.nextDouble() * 1000 + 0);

                                    mNotificationManager.notify(noti, mBuilder.build());
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    @SuppressLint("validFragment")
    public class Dialogogrupos extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Grupos Disponibles")
                    .setItems(NumGrups, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            String Name=NumGrups[item];
                            int i = 0;
                            while (true) {
                                SharedPreferences prefs = getSharedPreferences("Client" + Name + i, Context.MODE_PRIVATE);
                                if (!prefs.getString("Client" + Name + i, "").equals("")) {
                                    i++;
                                } else {
                                    break;
                                }
                            }

                            String Nom = tx.getText().toString();
                            SharedPreferences prefs = getSharedPreferences("Client"+NumGrups[item]+i,Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("Client" + NumGrups[item] + i, "" + Nom+"-"+nombrecliente);
                            editor.commit();
                            Toast.makeText(MisClientes.this, "Agregado a grupo "+NumGrups[item], Toast.LENGTH_SHORT).show();

                        }
                    });

            return builder.create();
        }
    }
    private void AddCodeudor()
    {
        DCodeu=tx.getText().toString();
        new MisClientes.Codeudor().execute();
    }

    class Codeudor extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MisClientes.this);
            pDialog.setMessage("Configurando codeudor");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[] = new String[2];
            Datos[0] = DClien;
            Datos[1] = DCodeu;
            try {

                List params = new ArrayList();
                for (int i = 0; i < Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato" + (i + 1), Datos[i]));


                JSONObject json = jParser.makeHttpRequest(Codeudor_URL, "POST", params);

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
                Toast.makeText(MisClientes.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    private static String ENVIOFAV_URL = C.URL+"envia_favorito.php";
    class EnviarFav extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MisClientes.this);
            pDialog.setMessage("Cambiando Estado");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[5];
            Datos[0]=tx.getText().toString();
            try {

                List params = new ArrayList();
                for (int i=0; i<Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datos[i]));

                JSONObject json = jParser.makeHttpRequest(ENVIOFAV_URL, "POST", params);

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
                Toast.makeText(MisClientes.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

}
