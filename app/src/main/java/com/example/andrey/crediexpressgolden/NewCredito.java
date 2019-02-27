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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewCredito extends Activity {
    private EditText Document, Valor, Cuotas, Seguro;
    private EditText diai,mesi,anoi,diaf,mesf,anof;
    private DatePicker Fecha;

    private Button boton,botoncuotas,botonautofecha;
    private Spinner Signo,Tipocredi,indicedias;
    String EsteCobrador;
    final String[] items = {"30", "40", "50", "60"};
    private ProgressDialog pDialog;
    JSONParser jsonParsero = new JSONParser();
    static Config Co=new Config();
    private static final String ER_URL = Co.URL+"credito.php";
    private static final String TAG_SUCCESSo = "success1";
    private static final String TAG_MESSAGEs = "message1";
    String Fec;
    String Feci;
    String var="";

    private String Indice="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcredito7);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Signo = (Spinner) findViewById(R.id.SSS);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.signoszodiacales , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Signo.setAdapter(spinner_adapter);

        Tipocredi = (Spinner) findViewById(R.id.tipocredi);
        ArrayAdapter adapterspinner = ArrayAdapter.createFromResource( this, R.array.TipoCredito , android.R.layout.simple_spinner_item);
        adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Tipocredi.setAdapter(adapterspinner);

        indicedias = (Spinner) findViewById(R.id.Spndias);
        ArrayAdapter adapterspinnerr = ArrayAdapter.createFromResource(this, R.array.Dias, android.R.layout.simple_spinner_item);
        adapterspinnerr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indicedias.setAdapter(adapterspinnerr);

        indicedias.setVisibility(View.INVISIBLE);
        boton = (Button) findViewById(R.id.btnAddCredit);
        boton.setOnClickListener(click);



        botoncuotas = (Button) findViewById(R.id.btnNumerodecuotasprede);
        botoncuotas.setOnClickListener(clicknumcuo);

        botonautofecha = (Button) findViewById(R.id.btnAutoFecha);
        botonautofecha.setOnClickListener(clickautofecha);

        diai=(EditText) findViewById(R.id.txtdiainicial);
        mesi=(EditText) findViewById(R.id.txtmesinicial);
        anoi=(EditText) findViewById(R.id.txtanoinicial);
        diaf=(EditText) findViewById(R.id.txtdiafinal);
        mesf=(EditText) findViewById(R.id.txtmesfinal);
        anof=(EditText) findViewById(R.id.txtanofinal);
        Calendar c = Calendar.getInstance();
        diai.setText(""+c.get(Calendar.DAY_OF_MONTH));
        mesi.setText(""+(c.get(Calendar.MONTH)+1));
        anoi.setText(""+c.get(Calendar.YEAR));

        Seguro=(EditText) findViewById(R.id.NumSegurocredito);
        Document=(EditText) findViewById(R.id.credidocu);
        Valor=(EditText) findViewById(R.id.credivalu);
        Cuotas=(EditText) findViewById(R.id.credicuota);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        EsteCobrador=prefecorr.getString("Docu","");
        var=getIntent().getExtras().getString("Document");
        if(!var.equals(""))
        {
            Document.setText(getIntent().getExtras().getString("Document"));
            Seguro.setText(getIntent().getExtras().getString("NSeguro"));

        }


    }
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(diaf.getText().toString().equals("") || mesf .getText().toString().equals("") || anof.getText().toString().equals(""))
            {
                Toast.makeText(NewCredito.this, "Rellene la fecha final por favor", Toast.LENGTH_SHORT).show();
            }else {
                envialos();
            }
        }
    };

    public void envialos() {
        Fec=""+anof.getText().toString();
        Fec+="-"+mesf.getText().toString();
        Fec+="-"+diaf.getText().toString();
        if(diai.getText().toString().equals("")) {
            Feci="Hoy";
        }
        else
        {
            Feci = "" + anoi.getText().toString();
            Feci += "-" + mesi.getText().toString();
            Feci += "-" + diai.getText().toString();
        }

        if(Tipocredi.getSelectedItem().toString().equals("Semanal") || Tipocredi.getSelectedItem().toString().equals("Quincenal"))
            Indice=indicedias.getSelectedItem().toString();
        new EnviarDatos().execute();
    }
    private View.OnClickListener clicknumcuo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nunumcuo();
        }
    };

    public void nunumcuo() {

        FragmentManager fragmentManager = getFragmentManager();
        DialogoSeleccion dialogo = new DialogoSeleccion();
        dialogo.show(fragmentManager, "tagAlerta");


    }
    private View.OnClickListener clickautofecha = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            autofecha();
        }
    };

    public void autofecha() {
        if(Tipocredi.getSelectedItem().toString().equals("Semanal") || Tipocredi.getSelectedItem().toString().equals("Quincenal")) {
            indicedias.setVisibility(View.VISIBLE);
        }
        if(Cuotas.getText().toString().equals(""))
        {
            Toast.makeText(NewCredito.this, "Por favor indique un número de cuotas", Toast.LENGTH_SHORT).show();
        }else {
            int año = Integer.parseInt(anoi.getText().toString());
            int mes = Integer.parseInt(mesi.getText().toString());
            int dia = Integer.parseInt(diai.getText().toString());
            anof.setText("");
            mesf.setText("");
            anof.setText("" + año);
            switch (Tipocredi.getSelectedItem().toString()) {
                case "Diario": {

                    if (Cuotas.getText().toString().equals("")) {
                        Toast.makeText(NewCredito.this, "No ha rellenado el campo cuotas", Toast.LENGTH_SHORT).show();
                    } else {

                        int diasmes = diasdelmes(mes, año);

                        int diafinal = dia + (Integer.parseInt(Cuotas.getText().toString())) - diasmes;
                        if(diafinal<=0)
                        {
                            diafinal=dia+(Integer.parseInt(Cuotas.getText().toString()));
                        }
                        diaf.setText("" + diafinal);

                        if (Integer.parseInt(Cuotas.getText().toString()) + dia > diasmes) {
                            if (mesi.getText().toString().equals("12")) {
                                mesf.setText("1");
                                anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                            } else {
                                mesf.setText("" + (Integer.parseInt(mesi.getText().toString()) + 1));
                            }
                        }
                        else
                        {
                            mesf.setText(mesi.getText().toString());
                        }
                        int diasmesfinal = diasdelmes(Integer.parseInt(mesf.getText().toString()), Integer.parseInt(anof.getText().toString()));
                        while (Integer.parseInt(diaf.getText().toString()) > diasmesfinal) {
                            int correcciondias = Integer.parseInt(diaf.getText().toString()) - diasmesfinal;
                            diaf.setText("" + (correcciondias));

                            if (mesf.getText().toString().equals("12")) {
                                mesf.setText("1");
                                anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                            } else {
                                mesf.setText("" + (Integer.parseInt(mesf.getText().toString()) + 1));
                            }
                            diasmesfinal = diasdelmes(Integer.parseInt(mesf.getText().toString()), Integer.parseInt(anof.getText().toString()));

                        }

                    }
                }
                break;
                case "Semanal": {

                    if (Cuotas.getText().toString().equals("")) {
                        Toast.makeText(NewCredito.this, "No ha rellenado el campo cuotas", Toast.LENGTH_SHORT).show();
                    } else {

                        int diasmes = diasdelmes(mes, año);

                        int diafinal = dia + (Integer.parseInt(Cuotas.getText().toString())) * 7 - diasmes;
                        if(diafinal<=0)
                        {
                            diafinal=dia+(Integer.parseInt(Cuotas.getText().toString()));
                        }
                        diaf.setText("" + diafinal);

                        if (Integer.parseInt(Cuotas.getText().toString()) * 7 + dia > diasmes) {
                            if (mesi.getText().toString().equals("12")) {
                                mesf.setText("1");
                                anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                            } else {
                                mesf.setText("" + (Integer.parseInt(mesi.getText().toString()) + 1));
                            }
                        }
                        else
                        {
                            mesf.setText(mesi.getText().toString());
                        }
                        int diasmesfinal = diasdelmes(Integer.parseInt(mesf.getText().toString()), Integer.parseInt(anof.getText().toString()));
                        while (Integer.parseInt(diaf.getText().toString()) > diasmesfinal) {
                            int correcciondias = Integer.parseInt(diaf.getText().toString()) - diasmesfinal;
                            diaf.setText("" + (correcciondias));

                            if (mesf.getText().toString().equals("12")) {
                                mesf.setText("1");
                                anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                            } else {
                                mesf.setText("" + (Integer.parseInt(mesf.getText().toString()) + 1));
                            }
                            diasmesfinal = diasdelmes(Integer.parseInt(mesf.getText().toString()), Integer.parseInt(anof.getText().toString()));

                        }

                    }
                }
                break;
                case "Mensual": {

                    if (Cuotas.getText().toString().equals("")) {
                        Toast.makeText(NewCredito.this, "No ha rellenado el campo cuotas", Toast.LENGTH_SHORT).show();
                    } else {

                        diaf.setText(diai.getText().toString());

                        if (mesi.getText().toString().equals("12")) {
                            mesf.setText("0");
                            mesf.setText("" + Integer.parseInt(Cuotas.getText().toString()));
                            anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                        } else {
                            mesf.setText("" + (Integer.parseInt(mesi.getText().toString()) + Integer.parseInt(Cuotas.getText().toString())));
                        }
                    }
                    while (Integer.parseInt(mesf.getText().toString()) > 12) {
                        mesf.setText("" + ((12 - Integer.parseInt(mesf.getText().toString())) * -1));
                        anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                    }
                }
                break;
                case "Quincenal": {

                    if (Cuotas.getText().toString().equals("")) {
                        Toast.makeText(NewCredito.this, "No ha rellenado el campo cuotas", Toast.LENGTH_SHORT).show();
                    } else {

                        int diasmes = diasdelmes(mes, año);

                        int diafinal = dia + (Integer.parseInt(Cuotas.getText().toString())) * 14 - diasmes;
                        if(diafinal<=0)
                        {
                            diafinal=dia+(Integer.parseInt(Cuotas.getText().toString()));
                        }
                        diaf.setText("" + diafinal);

                        if (Integer.parseInt(Cuotas.getText().toString()) * 14 + dia > diasmes) {
                            if (mesi.getText().toString().equals("12")) {
                                mesf.setText("1");
                                anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                            } else {
                                mesf.setText("" + (Integer.parseInt(mesi.getText().toString()) + 1));
                            }
                        }
                        else
                        {
                            mesf.setText(mesi.getText().toString());
                        }
                        int diasmesfinal = diasdelmes(Integer.parseInt(mesf.getText().toString()), Integer.parseInt(anof.getText().toString()));
                        while (Integer.parseInt(diaf.getText().toString()) > diasmesfinal) {
                            int correcciondias = Integer.parseInt(diaf.getText().toString()) - diasmesfinal;
                            diaf.setText("" + (correcciondias));

                            if (mesf.getText().toString().equals("12")) {
                                mesf.setText("1");
                                anof.setText("" + (Integer.parseInt(anof.getText().toString()) + 1));
                            } else {
                                mesf.setText("" + (Integer.parseInt(mesf.getText().toString()) + 1));
                            }
                            diasmesfinal = diasdelmes(Integer.parseInt(mesf.getText().toString()), Integer.parseInt(anof.getText().toString()));

                        }

                    }
                }
                break;

            }
        }
    }
    public int diasdelmes(int mes, int año)
    {
        int diasmes = 0;
        switch (mes) {
            case 1:
                diasmes = 31;break;
            case 2: {
                if (año % 4 == 0) {
                    diasmes = 29;break;
                } else {
                    diasmes = 28;break;
                }
            }
            case 3:
                diasmes = 31;break;
            case 4:
                diasmes = 30;break;
            case 5:
                diasmes = 31;break;
            case 6:
                diasmes = 30;break;
            case 7:
                diasmes = 31;break;
            case 8:
                diasmes = 31;break;
            case 9:
                diasmes = 30;break;
            case 10:
                diasmes = 31;break;
            case 11:
                diasmes = 30;break;
            case 12:
                diasmes = 31;break;

        }
        return diasmes;
    }
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            switch(Tipocredi.getSelectedItem().toString()) {
                case "Diario": {
                    items[0] = "30";
                    items[1] = "40";
                    items[2] = "50";
                    items[3] = "60";
                }break;
                case "Semanal":
                {
                    items[0] = "4";
                    items[1] = "5";
                    items[2] = "7";
                    items[3] = "8";
                }break;
                case "Mensual":
                {
                    items[0] = "1";
                    items[1] = "2";
                    items[2] = "3";
                    items[3] = "4";

                }break;
                case "Quincenal":
                {
                    items[0] = "1";
                    items[1] = "2";
                    items[2] = "3";
                    items[3] = "4";

                }break;

            }
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Número de cuotas")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            //Log.i("Dialogos", "Opción elegida: " + items[item]);
                            Cuotas.setText(items[item]);
                        }
                    });

            return builder.create();
        }
    }
    class EnviarDatos extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewCredito.this);
            pDialog.setMessage("Enviando Datos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int successs=0;
            String[] Datosaenviars=new String[10];
            Datosaenviars[0]=Document.getText().toString();
            Datosaenviars[1]=Valor.getText().toString();
            Datosaenviars[2]=Fec;
            Datosaenviars[3]=Cuotas.getText().toString();
            Datosaenviars[4]=EsteCobrador;
            Datosaenviars[5]=Signo.getSelectedItem().toString();
            Datosaenviars[6]=Seguro.getText().toString();
            Datosaenviars[7]=Feci;
            Datosaenviars[8]=Tipocredi.getSelectedItem().toString();

            Datosaenviars[9]=Indice;

            try {

                List params = new ArrayList();
                for(int i=0;i<10;i++)
                {
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datosaenviars[i]));
                }

                JSONObject jsonl = jsonParsero.makeHttpRequest(ER_URL, "POST", params);

                successs = jsonl.getInt(TAG_SUCCESSo);

                if (successs == 1) {
                    Log.d("Datos Enviados", jsonl.toString());

                    finish();

                    return jsonl.getString(TAG_MESSAGEs);

                } else {
                    Log.d("Error", jsonl.getString(TAG_MESSAGEs));

                    return jsonl.getString(TAG_MESSAGEs);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String filess_url) {

            pDialog.dismiss();
            if (filess_url != null) {
                Toast.makeText(NewCredito.this, filess_url, Toast.LENGTH_LONG).show();
                if (!filess_url.substring(0, 5).equals("Error"))
                {

                if (!filess_url.substring(0, 38).equals("Usted no posee mas credito de prestamo")) {
                    Intent P = new Intent(NewCredito.this, ComprobarCredito.class);
                    P.putExtra("qClien", Document.getText().toString());
                    P.putExtra("qVal", Valor.getText().toString());
                    P.putExtra("qCuotas", Cuotas.getText().toString());
                    P.putExtra("qCobrad", EsteCobrador);
                    P.putExtra("qSigno", Signo.getSelectedItem().toString());
                    P.putExtra("qSeguro", Seguro.getText().toString());
                    P.putExtra("qFecF", Fec);
                    startActivity(P);
                }
            }
            }
        }
    }
}
