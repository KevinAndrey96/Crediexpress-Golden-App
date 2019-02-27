package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ComprobarCredito extends Activity {
    private String NS, SZ;
    private String Recibo;
    private String Tele;
    private ProgressDialog pDialog;
    JSONArray products = null;
    private String Palabra = "";
    JSONParser jParser = new JSONParser();
    static Config C = new Config();
    private static String PALABRA_URL = C.URL + "obtenerdatoscredi.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "empresas";

    private static final String TAG_DATO1 = "Dato1";
    private static final String TAG_DATO2 = "Dato2";
    private static final String TAG_DATO3 = "Dato3";
    private static final String TAG_DATO4 = "Dato4";
    String Codeu="";

    private String Inf;
    TextView Fecha, Pin, Cliente, Seguro, Signo, ValCred, ValDeu, ValSeg, NumCuo, Cobrador, Info, FF, Codeuu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compruebacredito);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences Permis3 = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
        Inf=Permis3.getString("Permis3", "");

        Fecha = (TextView) findViewById(R.id.tx1);

        Cliente = (TextView) findViewById(R.id.tx3);
        Seguro = (TextView) findViewById(R.id.tx4);
        Signo = (TextView) findViewById(R.id.tx5);
        ValCred = (TextView) findViewById(R.id.tx6);
        ValDeu = (TextView) findViewById(R.id.tx7);
        ValSeg = (TextView) findViewById(R.id.tx8);
        NumCuo = (TextView) findViewById(R.id.tx9);
        Cobrador = (TextView) findViewById(R.id.tx10);
        Info = (TextView) findViewById(R.id.tx11);
        FF = (TextView) findViewById(R.id.txFec);
        Codeuu = (TextView) findViewById(R.id.txcodeu);

        Info.setText(Inf);
        Calendar c = Calendar.getInstance();
        Fecha.setText("Fecha: " + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));

        Cliente.setText("Cliente: " + getIntent().getExtras().getString("qClien"));



        ValCred.setText("Valor total de credito: $" + getIntent().getExtras().getString("qVal") + "000");
        ValDeu.setText("Valor total de deuda: $" + (Integer.parseInt(getIntent().getExtras().getString("qVal") + "000") + (Integer.parseInt(getIntent().getExtras().getString("qVal") + "000") * 0.2)));

        NumCuo.setText("N° Cuotas: " + getIntent().getExtras().getString("qCuotas"));
        String p = getIntent().getExtras().getString("qCobrad");
        Cobrador.setText("Código cobrador: " + p.substring(p.length() - 4));
        String FechaFinal = getIntent().getExtras().getString("qFecF");
        FF.setText("Fecha limite: " + FechaFinal);
        int $Dato2 = Integer.parseInt(getIntent().getExtras().getString("qVal") + "000");
        int $Seguro= (int) ($Dato2*0.1);

        ValSeg.setText("Valor Seguro: $" + $Seguro);

        Recibo = Fecha.getText().toString() + "\n" + Cliente.getText().toString() + "\n" + Seguro.getText().toString() + "\n" + Signo.getText().toString() + "\n" + ValCred.getText().toString() + "\n" + ValDeu.getText().toString() + "\n" + ValSeg.getText().toString() + "\n" + NumCuo.getText().toString() + "\n" + FF.getText().toString() + "\n" + Cobrador.getText().toString() + "\n"+Inf;

        Button Btn = (Button) findViewById(R.id.btnimpridiac);
        Btn.setOnClickListener(Imprik);

        Button BtnS = (Button) findViewById(R.id.btnSmsxD);
        BtnS.setOnClickListener(elsms);

        Palabra=getIntent().getExtras().getString("qClien");
        new Consulta().execute();



    }

    private View.OnClickListener Imprik = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent l = new Intent(ComprobarCredito.this, ImpresionBixolon.class);
            l.putExtra("RECIBO", Recibo);
            startActivity(l);
        }
    };
    private View.OnClickListener elsms = new View.OnClickListener() {
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
            pDialog = new ProgressDialog(ComprobarCredito.this);
            pDialog.setMessage("Cargando...");
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
                        String Dato1 = c.getString(TAG_DATO1);
                        String Dato2 = c.getString(TAG_DATO2);
                        String Dato3 = c.getString(TAG_DATO3);
                        String Dato4 = c.getString(TAG_DATO4);

                        Tele = Dato1;

                            NS = Dato2;
                            SZ = Dato3;

                        Codeu= Dato4;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {


            pDialog.dismiss();
            Seguro.setText("Seguro N°: " + SZ);
            Signo.setText("Signo Zodiacal: " + NS);
            Codeuu.setText("Codeudor: "+Codeu);
            Recibo = Fecha.getText().toString() + "\n" + Cliente.getText().toString() + "\n" + Seguro.getText().toString() + "\n" + Signo.getText().toString() + "\n" + ValCred.getText().toString() + "\n" + ValDeu.getText().toString() + "\n" + ValSeg.getText().toString() + "\n" + NumCuo.getText().toString() + "\n" + FF.getText().toString() + "\n" + Cobrador.getText().toString() + "\n"+"Codeudor: "+Codeu+"\n"+Inf;


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

        }
    }
}