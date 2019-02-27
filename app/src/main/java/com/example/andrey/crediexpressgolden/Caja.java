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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Andrey on 13/09/2016.
 */
public class Caja extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        Button btnRecoge=(Button) findViewById(R.id.btnRecoger);
        Button btnEntregar=(Button) findViewById(R.id.btnEntregar);
        Button btnConsultar=(Button) findViewById(R.id.btnConsulta);
        Button btnCobrar=(Button) findViewById(R.id.btnCobrarSueldo);

        btnCobrar.setOnClickListener(cobra);
        btnRecoge.setOnClickListener(recoge);
        btnEntregar.setOnClickListener(entrega);
        btnConsultar.setOnClickListener(consulta);

        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador = prefecorr.getString("Docu", "");

        SharedPreferences Permis3 = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
        Info = Permis3.getString("Permis3", "");

        Calendar c = Calendar.getInstance();
        Fecha="Fecha: "+c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);


    }
    private View.OnClickListener cobra = new View.OnClickListener() {
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

            final String items[]=new String [2];
            items[0]="Si";
            items[1]="No";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("¿Está Seguro?")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Si": {
                                    new Enviar().execute();
                                }
                                break;
                                case "No": {
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    private View.OnClickListener recoge = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent h=new Intent(Caja.this, Envia_Caja.class);
            h.putExtra("Tipo","Debita");
            startActivity(h);
        }
    };
    private View.OnClickListener entrega = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent h=new Intent(Caja.this, Envia_Caja.class);
            h.putExtra("Tipo","Acredita");
            startActivity(h);
        }
    };

    private View.OnClickListener consulta = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent h=new Intent(Caja.this, CajaConsulta.class);
            startActivity(h);
        }
    };



    static Config C=new Config();
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    private static String ENVIO_URL = C.URL+"envia_cobrarsueldo.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String Fecha,EsteCobrador,Info,sueldo;


    public class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Caja.this);
            pDialog.setMessage("Cobrando Sueldo...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[3];
            Datos[0]=EsteCobrador;
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
                Toast.makeText(Caja.this, "Cobrado: "+file_url, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(Caja.this, "Error al cobrar sueldo", Toast.LENGTH_LONG).show();
            }
            finish();

            Intent l=new Intent(Caja.this,ImpresionBixolon.class);
            String Recibo=Fecha+"\nCobro de sueldo"+"\nValor: $"+sueldo+"\nCobrador: "+EsteCobrador+"\nInformes: "+Info;
            l.putExtra("RECIBO", Recibo);
            startActivity(l);
        }
    }

}
