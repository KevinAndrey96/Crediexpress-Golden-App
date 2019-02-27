package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bixolon.android.library.BxlService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Pago extends Activity{
    private String Origen;
    static Config C=new Config();

    JSONParser jParser = new JSONParser();
    int success;
    private int noti=0;

    private static final String TAG_SUCCESS = "success";


    private EditText Dato1, Dato2, Pin;
    private Button EnvioDatos;
    private ProgressDialog pDialog;
    private Button pos, opc;

    private String Dato;
    private static String ENVIO_URL = C.URL+"envio.php";
    JSONParser jsonParser = new JSONParser();

    private static final String TAG_MESSAGE = "message";
    String IdRuta;
    String Bandera="Nain";
    String EsteCobrador;
    String Fechayhora;


    private static final String URLENVIO = C.URL+"envioruta.php";

    ArrayList<HashMap<String, String>> empresaList;
    private static String PALABRA_URL = C.URL+"valorcuota.php";
    private static final String TAG_PRODUCTS = "empresas";

    private static final String TAG_VALOR ="valor";

    JSONArray products = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago5);
        Calendar c = Calendar.getInstance();
        Fechayhora = "" + c.get(Calendar.DATE) + " ";
        Fechayhora += c.get(Calendar.HOUR_OF_DAY);
        Fechayhora += ":" + c.get(Calendar.MINUTE);
        Fechayhora += ":" + c.get(Calendar.SECOND);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Dato1 = (EditText) findViewById(R.id.txtDClientepago);
        Dato2 = (EditText) findViewById(R.id.txtValorPago);
        Pin = (EditText) findViewById(R.id.txtPin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EnvioDatos = (Button) findViewById(R.id.btnEnviar);
        EnvioDatos.setOnClickListener(Enviando);

        Button AutoVal = (Button) findViewById(R.id.btnAutoValor);
        AutoVal.setOnClickListener(AutoValue);

        opc=(Button) findViewById(R.id.btnopci);
        opc.setOnClickListener(opciones);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador=prefecorr.getString("Docu","");

        Bandera=getIntent().getExtras().getString("Bandera");
if(Bandera.equals("No"))
        {
            IdRuta = getIntent().getExtras().getString("Ruta");
            Dato1.setText(getIntent().getExtras().getString("Cedula"));
            Pin.setText(getIntent().getExtras().getString("Pin"));
        }
        if(Bandera.equals("Si"))
        {
            Dato1.setText(getIntent().getExtras().getString("Cedula"));
            Pin.setText(getIntent().getExtras().getString("Pin"));
        }


    }
    private View.OnClickListener AutoValue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new  Consulta().execute();
        }
    };
    private View.OnClickListener opciones = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Origen="Opciones";
            if(Dato2.getText().toString().equals(""))
            {
                Toast.makeText(Pago.this, "Por favor rellene el campo Valor", Toast.LENGTH_SHORT).show();
            }
            else
            Pri();
        }
    };
    private View.OnClickListener posp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Dar="";
            if(!Dato2.getText().toString().equals(""))
            {
                Dar=" - Valor $"+Dato2.getText().toString()+".000";
            }
            Toast.makeText(Pago.this, "Añadido a recordatorios", Toast.LENGTH_SHORT).show();

            Intent in = new Intent(Pago.this, ComprobarPago.class);
            in.putExtra("Cll", Dato1.getText().toString());
            in.putExtra("Pins", Pin.getText().toString());
            if(Dato2.getText().toString().equals(""))
                in.putExtra("val", Dato2.getText().toString());
            else
                in.putExtra("val", "0");

            PendingIntent contIntent = PendingIntent.getActivity(Pago.this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(Pago.this)
                            .setContentIntent(contIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon((((BitmapDrawable) getResources()
                                    .getDrawable(R.mipmap.ic_launcher)).getBitmap()))
                            .setContentTitle("Cobrar a " + Dato1.getText().toString() + Dar)
                            .setContentText("Recordatorio de cobro")
                            .setContentInfo("CrediExpress Golden")
                            .setVibrate(new long[]{100, 250, 100, 500})
                            .setTicker("Recordatorio de cobro CrediExpress Golden");



            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Random rnd = new Random();
            noti= (int)(rnd.nextDouble() * 1000 + 0);

            mNotificationManager.notify(noti, mBuilder.build());
        }
    };
    private View.OnClickListener Enviando = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Envio();
        }
    };

    public void Envio() {
        Origen="Envio";
if(Dato2.getText().toString().equals("") || Pin.getText().toString().equals("") || Dato1.getText().toString().equals(""))
{
    Toast.makeText(Pago.this, "Complete todos los campos por favor", Toast.LENGTH_SHORT).show();
}else {
    if (Bandera.equals("No")) {
        new CLASEENVIAR().execute();
    }
    new Enviar().execute();
    Pri();
}
    }


    public void Pri() {
        if(!Origen.equals("Envio"))
        Toast.makeText(Pago.this, "El credito no se ha pagado esta observando una vista previa", Toast.LENGTH_SHORT).show();
        Intent l=new Intent(Pago.this, ComprobarPago.class);
        l.putExtra("Cll",Dato1.getText().toString());
        l.putExtra("val", Dato2.getText().toString());
        l.putExtra("Pins",Pin.getText().toString());
        l.putExtra("Origen",Origen);
        startActivity(l);
            }

    class CLASEENVIAR extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Pago.this);
            pDialog.setMessage("Cambiando estado...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String[] Datosaenviar=new String[1];
            Datosaenviar[0]=Pin.getText().toString();


            try {

                List params = new ArrayList();
                for(int i=0;i<1;i++)
                {
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datosaenviar[i]));
                }

                JSONObject json = jsonParser.makeHttpRequest(URLENVIO, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

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
                Toast.makeText(Pago.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
    class Enviar extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Pago.this);
            pDialog.setMessage("Enviando Datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Dato10 = Dato1.getText().toString();
            String Dato20 = Dato2.getText().toString();
            String Dato30 = EsteCobrador;
            String Dato40 = Pin.getText().toString();

            try {

                List params = new ArrayList();

                params.add(new BasicNameValuePair("Dato1", Dato10));
                params.add(new BasicNameValuePair("Dato2", Dato20));
                params.add(new BasicNameValuePair("Dato3", Dato30));
                params.add(new BasicNameValuePair("Dato4", Dato40));

                JSONObject json = jsonParser.makeHttpRequest(ENVIO_URL, "POST", params);

                Log.d("Registering attempt", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    finish();

                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Error en el envio", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
                Toast.makeText(Pago.this, file_url, Toast.LENGTH_LONG).show();
        }
    }
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Pago.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String P = Pin.getText().toString();
            List params = new ArrayList();

            params.add(new BasicNameValuePair("pala", P));


            JSONObject json = jParser.makeHttpRequest(PALABRA_URL, "POST", params);

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        Dato = c.getString(TAG_VALOR);
                    }
                }
            } catch (JSONException e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            Dato2.setText(""+Dato);
        }
    }
}
