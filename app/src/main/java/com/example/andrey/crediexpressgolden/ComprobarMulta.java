package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComprobarMulta extends Activity {
    private String Palabra = "";
    JSONParser jParser = new JSONParser();
    static Config C=new Config();
    ArrayList<HashMap<String, String>> empresaList;
    private static String PALABRA_URL = C.URL+"lista_recibomultas.php";
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
    private ProgressDialog pDialog;
    JSONArray products = null;
    ListView lista;

    String FechaYHora,Pin,CodigoCobrador,Informes,Tele, Parametro,Recibo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compruebamulta);
        empresaList = new ArrayList<HashMap<String, String>>();
        lista = (ListView) findViewById(R.id.lstcompruebamulta);
        Calendar c = Calendar.getInstance();
        FechaYHora = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        Pin = getIntent().getExtras().getString("Pin");
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        String EsteCobrador = prefecorr.getString("Docu", "");
        CodigoCobrador = EsteCobrador.substring(EsteCobrador.length() - 4);
        SharedPreferences Permis3 = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
        Informes = Permis3.getString("Permis3", "");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Parametro = EsteCobrador + "-" + Pin;

        Button btnImpri = (Button) findViewById(R.id.btnimprimemulta);
        btnImpri.setOnClickListener(Imprik);

        Button btnsms = (Button) findViewById(R.id.btnsmsmulta);
        btnsms.setOnClickListener(smss);

        new Consulta().execute();
    }
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
    private View.OnClickListener Imprik = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent l=new Intent(ComprobarMulta.this, ImpresionBixolon.class);
            l.putExtra("RECIBO",Recibo);
            startActivity(l);
        }
    };
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ComprobarMulta.this);
            pDialog.setMessage("Cargando datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String P = Parametro;
            List params = new ArrayList();

            params.add(new BasicNameValuePair("Parametro", P));

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

                        HashMap map = new HashMap();

                        map.put(FechaYHora, "\nFecha: "+FechaYHora);
                        map.put(Pin, "Pin: "+Pin);
                        map.put(TAG_DATO1,"Cliente: "+ Dato1);
                        map.put(TAG_DATO2, "Seguro N°: "+Dato2);
                        map.put(TAG_DATO3, "Signo Zodiacal: "+Dato3);
                        map.put(TAG_DATO5, "Valor credito: $"+Dato5);
                        map.put(TAG_DATO6, "Valor Multa: $"+Dato6);
                        map.put(TAG_DATO7, "Deuda Restante: $"+Dato7);
                        map.put(TAG_DATO8, "Cuotas restantes:"+Dato8);
                        map.put(TAG_DATO9, "Cuotas mora: "+Dato9);
                        map.put(CodigoCobrador, "Código de cobrador: "+CodigoCobrador);
                        map.put(Informes, ""+Informes);
                        Tele=Dato4;
                        Recibo="Fecha: "+FechaYHora+"\nPin de credito: "+Pin+"\nDocumento: "+Dato1+"\nSeguro N°: "+Dato2+"\nSigno Zodiacal: "+Dato3+"\nValor credito: $"+Dato5
                                +"\nValor Multa: $"+Dato6+"\nDeuda Restante: $"+Dato7+"\nCuotas restantes:"+Dato8+"\nCuotas mora: "+Dato9+"\nCódigo de cobrador: "+CodigoCobrador+"\n"+Informes;
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
                            ComprobarMulta.this,
                            empresaList,
                            R.layout.activity_spcompruebamulta,
                            new String[]{
                                    FechaYHora,
                                    Pin,
                                    TAG_DATO1,
                                    TAG_DATO2,
                                    TAG_DATO3,
                                    TAG_DATO5,
                                    TAG_DATO6,
                                    TAG_DATO7,
                                    TAG_DATO8,
                                    TAG_DATO9,
                                    CodigoCobrador,
                                    Informes,
                            },
                            new int[]{

                                    R.id.SPComMulFechaYHora,
                                    R.id.SPComMulPin,
                                    R.id.SPComMulDocumento,
                                    R.id.SPComMulSeguro,
                                    R.id.SPComMulSigno,
                                    R.id.SPComMulValorCredito,
                                    R.id.SPComMulValorMulta,
                                    R.id.SPComMulRestanteCredi,
                                    R.id.SPComMulCuotas,
                                    R.id.SPComMulCuotasMora,
                                    R.id.SPComMulCodigoCobra,
                                    R.id.SPComMulInfo,
                            });

                    lista.setAdapter(adapter);
                }
            });
        }
    }
}
