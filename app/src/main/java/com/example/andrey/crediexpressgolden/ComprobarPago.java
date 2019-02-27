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
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ComprobarPago extends Activity{
    private String Palabra = "";
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String PALABRA_URL = C.URL+"ObtenerDatosPago.php";
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

    private Button Imp,sms;

    private String Fecha="";
    private String Cancelado="";
    private String Pinn="";
    private String Cobrad="";
    private String Info;

    private String Recibo="";
    private String Tele="";
    private ProgressDialog pDialog;
    JSONArray products = null;
    ListView lista;
    String Ori;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compruebapago);
        SharedPreferences Permis3 = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
        Info = Permis3.getString("Permis3", "");
        empresaList = new ArrayList<HashMap<String, String>>();
        lista = (ListView) findViewById(R.id.lstcomprobacion);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Cobrad = prefecorr.getString("Docu", "");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Palabra=getIntent().getExtras().getString("Cll");
        Cancelado=getIntent().getExtras().getString("val")+"000";
        Pinn=getIntent().getExtras().getString("Pins");

        Calendar c = Calendar.getInstance();
        Fecha="Fecha: "+c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);

        Ori=getIntent().getExtras().getString("Origen");

        Imp=(Button) findViewById(R.id.btnimprime);
        Imp.setOnClickListener(Imprik);

        sms=(Button) findViewById(R.id.btnelsms);
        sms.setOnClickListener(smss);

        Button Posponer=(Button) findViewById(R.id.btnpospon);
        Posponer.setOnClickListener(pospo);

        if(!Ori.equals("Opciones"))
            Posponer.setVisibility(View.INVISIBLE);

        new Consulta().execute();
    }
    private View.OnClickListener pospo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Dar="";

            String Dato2=Cancelado;
            if(Dato2.equals("000"))
            {
                Dar=" - Valor $"+Dato2;
            }
            Toast.makeText(ComprobarPago.this, "Añadido a recordatorios", Toast.LENGTH_SHORT).show();

            Intent in = new Intent(ComprobarPago.this, Pago.class);

            in.putExtra("Bandera", "Si");
            in.putExtra("Cedula", Palabra);
            in.putExtra("Pin", Pinn);

            if(!Cancelado.equals("000"))
                in.putExtra("val", Cancelado);
            else
                in.putExtra("val", "0");

            PendingIntent contIntent = PendingIntent.getActivity(ComprobarPago.this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ComprobarPago.this)
                            .setContentIntent(contIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon((((BitmapDrawable) getResources()
                                    .getDrawable(R.mipmap.ic_launcher)).getBitmap()))
                            .setContentTitle("Cobrar a " + Palabra/*.getText().toString()*/ + Dar)
                            .setContentText("Recordatorio de cobro")
                            .setContentInfo("CrediExpress Golden")
                            .setVibrate(new long[]{100, 250, 100, 500})
                            .setTicker("Recordatorio de cobro CrediExpress Golden");

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Random rnd = new Random();
            int noti= (int)(rnd.nextDouble() * 1000 + 0);

            mNotificationManager.notify(noti, mBuilder.build());
        }
    };
    private View.OnClickListener Imprik = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent l=new Intent(ComprobarPago.this, ImpresionBixolon.class);
            l.putExtra("RECIBO",Recibo);
            startActivity(l);
        }
    };
    private View.OnClickListener smss = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sm();
        }
    };

    public void sm() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Tele, null, Recibo, null, null);


        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("address", Tele);
        sendIntent.putExtra("sms_body", Recibo);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);

    }
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ComprobarPago.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String P = Palabra+"-"+Pinn;
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


                        HashMap map = new HashMap();

                        map.put(Fecha, Fecha);
                        map.put(Pinn, "Pin de credito: "+Pinn);
                        map.put(Palabra,"Cliente: "+ Palabra);
                        map.put(TAG_DATO1, "Seguro N°: "+Dato1);
                        map.put(TAG_DATO2, "Signo Zodiacal: "+Dato2);
                        map.put(TAG_DATO10, "Fecha Inicial de credito: "+Dato10);
                        map.put(TAG_DATO5, "Valor Total de credito: $"+Dato5);
                        map.put(Cancelado, "Valor Cancelado: $"+Cancelado);
                        if(Ori.equals("Opciones")) {
                            map.put(TAG_DATO3, "Valor restante de credito: $" + (Integer.parseInt(Dato3) - Integer.parseInt(Cancelado)));
                        }
                            else {
                            map.put(TAG_DATO3, "Valor restante de credito: $" + (Integer.parseInt(Dato3)/*-Integer.parseInt(Cancelado)*/));
                        }
                        map.put(TAG_DATO4, "Cuotas restantes :"+Dato4);
                        map.put(TAG_DATO9, "\nCuotas mora: "+Dato9);
                        map.put(Cobrad, "Código de cobrador: "+Cobrad.substring(Cobrad.length()-4));
                        map.put(Info, ""+Info);
                        map.put(TAG_DATO8, "\nMoroso: "+Dato8);
                        if(Ori.equals("Opciones")) {
                            Recibo = Fecha + "\nPin de credito: " + Pinn + "\nCliente: " + Palabra + "\n" + "Seguro N°: " + Dato1 + "\nSigno Zodiacal: " + Dato2 +"\nFecha Inicial de credito: "+Dato10 +"\nValor Total de credito: $" + Dato5 + "\nValor Cancelado: $" + Cancelado
                                    + "\nRestante de credito: $" + (Integer.parseInt(Dato3) - Integer.parseInt(Cancelado)) + "\nCuotas restantes" + Dato4 +"\nCuotas en mora: "+Dato9+"\nMoroso: "+Dato8+
                                    "\nCódigo de cobrador: " + Cobrad.substring((Cobrad.length() - 4)) +
                                    "\nInformes:" + Info;
                        }else {
                            Recibo = Fecha + "\nPin de credito: " + Pinn + "\nCliente: " + Palabra + "\n" + "Seguro N°: " + Dato1 + "\nSigno Zodiacal: " + Dato2 +"\nFecha Inicial de credito: "+Dato10+ "\nValor Total de credito: $" + Dato5 + "\nValor Cancelado: $" + Cancelado
                                    + "\nRestante de credito: $" + (Integer.parseInt(Dato3)) + "\nCuotas restantes " + Dato4 +"\nCuotas en mora: "+Dato9+"\nMoroso: "+Dato8+
                                    "\nCódigo de cobrador: " + Cobrad.substring((Cobrad.length() - 4)) +
                                    "\n" + Info;
                        }
                        Tele=Dato6;
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

            SharedPreferences Permis = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
            String teladmin = Permis.getString("Permis3","");
            teladmin=teladmin.substring(25,35);

            String strPhone = teladmin;
            String strMessage = Recibo;
            SmsManager sms = SmsManager.getDefault();
            ArrayList messageParts = sms.divideMessage(strMessage);
            sms.sendMultipartTextMessage(strPhone, null, messageParts, null, null);

            String strPhone2 = Tele;
            String strMessage2 = Recibo;
            SmsManager sms2 = SmsManager.getDefault();
            ArrayList messageParts2 = sms2.divideMessage(strMessage2);
            sms2.sendMultipartTextMessage(strPhone2, null, messageParts2, null, null);

            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            ComprobarPago.this,
                            empresaList,
                            R.layout.activity_spcomprueba,
                            new String[]{
                                    Fecha,
                                    Pinn,
                                    Palabra,
                                    TAG_DATO1,
                                    TAG_DATO2,
                                    TAG_DATO5,
                                    Cancelado,
                                    TAG_DATO3,
                                    TAG_DATO4,
                                    Cobrad,
                                    Info,
                                    TAG_DATO8,
                                    TAG_DATO9,
                                    TAG_DATO10,
                            },
                            new int[]{

                                    R.id.SPFechayhorapago,
                                    R.id.SPPins,
                                    R.id.SPDocumentoclientePago,
                                    R.id.SPSeguroPago,
                                    R.id.SPSignoPago,
                                    R.id.SPTotalcreditoPago,
                                    R.id.SPCanceladoPago,
                                    R.id.SPDeudaRestantePago,
                                    R.id.SPCuotasPago,
                                    R.id.SPCobradorPago,
                                    R.id.SPInformesPago,
                                    R.id.SPMoroso,
                                    R.id.SPCuotasatras,
                                    R.id.SPFechaIn,
                            });

                    lista.setAdapter(adapter);
                }
            });
        }
    }
}
