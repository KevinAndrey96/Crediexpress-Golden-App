package com.example.andrey.crediexpressgolden;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectRuta extends Activity {
    private Button Mensual,Semanal,Diario,Quincenal,Reset;
    private Button Mensualesp,Semanalesp,Quincenalesp;
    private String[] items = {"Diario", "Semanal", "Quincenal", "Mensual"};

    private ProgressDialog pDialog;
    JSONArray products = null;
    private String Palabra = "";
    JSONParser jParser = new JSONParser();
    static Config C = new Config();
    private static String PALABRA_URL = C.URL + "resetrutas.php";
    private static final String TAG_SUCCESS = "success1";
    private static final String TAG_PRODUCTS = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectrutas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Mensual = (Button) findViewById(R.id.btnMensual);
        Mensual.setOnClickListener(clickMensual);
        Diario = (Button) findViewById(R.id.btnDiario);
        Diario.setOnClickListener(clickDiario);
        Semanal = (Button) findViewById(R.id.btnSemanal);
        Semanal.setOnClickListener(clickSemanal);
        Quincenal = (Button) findViewById(R.id.btnQuincenal);
        Quincenal.setOnClickListener(clickQuincenal);
        Reset = (Button) findViewById(R.id.btnReset);
        Reset.setOnClickListener(clickReset);

        Mensualesp= (Button) findViewById(R.id.btnMensualesp);
        Mensualesp.setOnClickListener(clickMensualesp);
        Semanalesp = (Button) findViewById(R.id.btnSemanalesp);
        Semanalesp.setOnClickListener(clickSemanalesp);
        Quincenalesp = (Button) findViewById(R.id.btnQuincenalesp);
        Quincenalesp.setOnClickListener(clickQuincenalesp);
    }
    private View.OnClickListener clickMensual=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,RutasMensuales.class);
            k.putExtra("Indic","No");
            startActivity(k);
        }
    };
    private View.OnClickListener clickQuincenal=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,RutasQuincenales.class);
            k.putExtra("Indic","No");
            startActivity(k);
        }
    };
    private View.OnClickListener clickMensualesp=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,RutasMensuales.class);
            k.putExtra("Indic","Si");
            startActivity(k);
        }
    };
    private View.OnClickListener clickQuincenalesp=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,RutasQuincenales.class);
            k.putExtra("Indic","Si");
            startActivity(k);
        }
    };
    private View.OnClickListener clickDiario=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,Rutas.class);
            startActivity(k);
        }
    };
    private View.OnClickListener clickSemanalesp=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,RutasSemanales.class);
            k.putExtra("Indic","Si");
            startActivity(k);
        }
    };
    private View.OnClickListener clickSemanal=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent k=new Intent(SelectRuta.this,RutasSemanales.class);
            k.putExtra("Indic","No");
            startActivity(k);
        }
    };
    private View.OnClickListener clickReset=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            DialogoSeleccion dialogo = new DialogoSeleccion();
            dialogo.show(fragmentManager, "tagAlerta");
        }
    };
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Rutas a resetear")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Toast.makeText(SelectRuta.this, "Reseteado", Toast.LENGTH_SHORT).show();
                            SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
                            String EsteCobrador=prefecorr.getString("Docu", "");
                            Palabra=items[item]+"-"+EsteCobrador;
                            new Consulta().execute();
                        }
                    });

            return builder.create();
        }
    }
    class Consulta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SelectRuta.this);
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
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(SelectRuta.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
