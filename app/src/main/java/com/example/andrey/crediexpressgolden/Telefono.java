package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Telefono extends Activity {
    static Config C=new Config();
    private static String ENVIO_URL = C.URL+"envia_telefono.php";
    private ProgressDialog pDialog;
    private String Cliente="";
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> empresaList;
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_MESSAGE = "message";
    private EditText tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono);
        Cliente=getIntent().getExtras().getString("DCli","");
        tel=(EditText) findViewById(R.id.txttelefononuevo);
        Button but= (Button) findViewById(R.id.btnnuevotelefono);
        but.setOnClickListener(entel);
    }
    private View.OnClickListener entel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Enviar().execute();
        }
    };
    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Telefono.this);
            pDialog.setMessage("Cambiando Teléfono...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[]= new String[5];
            Datos[0]=Cliente;
            Datos[1]=tel.getText().toString();
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
                Toast.makeText(Telefono.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
