package com.example.andrey.crediexpressgolden;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.andrey.crediexpressgolden.JSONParser;

public class Login extends Activity implements OnClickListener {

    private EditText user, pass;
    private Button mSubmit;
    static Config C=new Config();
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = C.URL+"login.php";
    private String Usuario, Contraseña;
    private boolean flag = false;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        flag = false;
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);

        SharedPreferences prefepass = getSharedPreferences("Cont", Context.MODE_PRIVATE);


        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        mSubmit = (Button) findViewById(R.id.login);
        mSubmit.setOnClickListener(this);

        SharedPreferences LaSes = getSharedPreferences("LaSes", Context.MODE_PRIVATE);
        String Ses=LaSes.getString("LaSes","");

        /*if(Ses.equals("Si")){
            Intent j=new Intent(Login.this,Menu.class);
            startActivity(j);
            finish();
        }else{*/
            if (prefecorr.getString("Docu", "").equals("")) {
                Toast.makeText(Login.this, "Por favor inicie sesión", Toast.LENGTH_SHORT).show();
                Reinicio();
            } else {
                Usuario = prefecorr.getString("Docu", "");
                Contraseña = prefepass.getString("Cont", "");

                flag = true;
                onClick(mSubmit);
            }
        //}
    }
    public boolean VerificarConexion() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (VerificarConexion() == true) {
                    new AttemptLogin().execute();
                }
                else
                {
                    Toast.makeText(Login.this, "Usted no tiene conexión a internet, imposible loguear usuario", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Iniciando Sesión CrediExpress Golden");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;

            String username;
            String password;

            if (flag == true) {
                username = Usuario;
                password = Contraseña;
                flag = false;
            } else {
                username = user.getText().toString();
                password = pass.getText().toString();
            }
            try {

                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password23", password));

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    Guardar(username, password);

                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    Editor edit = sp.edit();
                    edit.putString("username", username);
                    edit.commit();
                    Intent i = new Intent(Login.this, Menu.class);

                    SharedPreferences LaSes = getSharedPreferences("LaSes", Context.MODE_PRIVATE);
                    Editor editord = LaSes.edit();
                    editord.putString("LaSes", "Si");
                    editord.commit();

                    finish();
                    startActivity(i);
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

                if(file_url.substring(0,2).equals("Si") || file_url.substring(0,2).equals("No"))
                {
                    Toast.makeText(Login.this, "El usuario "+file_url.substring(0,2)+" tiene privilegios de credito", Toast.LENGTH_LONG).show();
                    SharedPreferences Permis = getSharedPreferences("Permis", Context.MODE_PRIVATE);
                    Editor editorp = Permis.edit();
                    editorp.putString("Permis", file_url.substring(0,2));
                    editorp.commit();
                    if(file_url.substring(3,5).equals("Si") || file_url.substring(3,5).equals("No"))
                    {
                        Toast.makeText(Login.this, "El usuario "+file_url.substring(3,5)+" tiene privilegios de clientes", Toast.LENGTH_LONG).show();
                        SharedPreferences Permis2 = getSharedPreferences("Permis2", Context.MODE_PRIVATE);
                        Editor editorp2 = Permis2.edit();
                        editorp2.putString("Permis2", file_url.substring(3,5));
                        editorp2.commit();
                    }
                    SharedPreferences Permis3 = getSharedPreferences("Permis3", Context.MODE_PRIVATE);
                    Editor editorp3 = Permis3.edit();
                    editorp3.putString("Permis3", file_url.substring(6));
                    editorp3.commit();
                    Toast.makeText(Login.this, file_url.substring(6), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Reinicio();
                    Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();

                }
            }
        }

    }
    private void Guardar(String C, String P) {

        SharedPreferences preferencias = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Editor editor = preferencias.edit();
        editor.putString("Docu", C);
        editor.commit();

        SharedPreferences preferencias2 = getSharedPreferences("Cont", Context.MODE_PRIVATE);
        Editor editor2 = preferencias2.edit();
        editor2.putString("Cont", P);
        editor2.commit();

        SharedPreferences preferencias3 = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        Editor editor3 = preferencias3.edit();
        editor3.putString("Sesion", "Si");
        editor3.commit();

    }
    public void Reinicio()
    {


        SharedPreferences preferencias = getSharedPreferences("Correo", Context.MODE_PRIVATE);
        Editor editor = preferencias.edit();
        editor.putString("Docu", "");
        editor.commit();

        SharedPreferences preferencias2 = getSharedPreferences("Contra", Context.MODE_PRIVATE);
        Editor editor2 = preferencias2.edit();
        editor2.putString("Cont", "");
        editor2.commit();

        SharedPreferences preferencias3 = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        Editor editor3 = preferencias3.edit();
        editor3.putString("Sesion", "No");
        editor3.commit();
    }
}

