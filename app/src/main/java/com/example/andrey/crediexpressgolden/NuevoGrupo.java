package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
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
import java.util.List;

/**
 * Created by Andrey on 03/12/2016.
 */
public class NuevoGrupo extends Activity {
    String DocumentoCliente,NombreGrupo;
    String auto;
    EditText ng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevogrupo);

        DocumentoCliente=getIntent().getExtras().getString("DocumentoCliente","");
        auto=getIntent().getExtras().getString("auto","");

        ng = (EditText) findViewById(R.id.txtnombredelgruponuevo);

        if(auto.equals("Si"))
        {
            NombreGrupo=getIntent().getExtras().getString("NombreGrupo","");
            ng.setText(NombreGrupo);
            envialo();
        }

        Button btnaceptar7=(Button) findViewById(R.id.btnnewgroup2354);
        btnaceptar7.setOnClickListener(clickaceptar00);



    }

    private View.OnClickListener clickaceptar00 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            envialo();
        }
    };

    private void envialo()
    {
        new Enviar().execute();
    }
    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();
    static Config C = new Config();
    private static String ENVIO_URL = C.URL + "Envia_nuevogrupo.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NuevoGrupo.this);
            pDialog.setMessage("Configurando ubicación");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[] = new String[2];
            Datos[0] = DocumentoCliente;
            Datos[1] = ng.getText().toString();
            try {

                List params = new ArrayList();
                for (int i = 0; i < Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato" + (i + 1), Datos[i]));

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
                Toast.makeText(NuevoGrupo.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
