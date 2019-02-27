package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Ubicacion extends Activity {
    public String Coord, Docum, Dire;
    private Button h;
    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();
    static Config C = new Config();
    private static String ENVIO_URL = C.URL + "envia_ubicacion.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public TextView f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        Docum = getIntent().getExtras().getString("Cedu", "");
        f = (TextView) findViewById(R.id.txtubicacion);
        h = (Button) findViewById(R.id.btnEnviaubicacion);
        h.setEnabled(false);
        h.setOnClickListener(env);
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(Ubicacion.this);
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                Local);

    }

    private View.OnClickListener env = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Coord.equals("")) {
                Toast.makeText(Ubicacion.this, "Por favor espere un momento, mientras se detecta su ubicación", Toast.LENGTH_SHORT).show();

            } else {
                new Enviar().execute();

            }
        }
    };

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //Toast.makeText(NewCliente.this,  DirCalle.getAddressLine(0), Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Localizacion implements LocationListener {
        Ubicacion mainActivity;

        public Ubicacion getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(Ubicacion mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            Coord = loc.getLatitude() + ", " + loc.getLongitude();
            //Toast.makeText(Ubicacion.this, Coord, Toast.LENGTH_SHORT).show();
            f.setText("Ubicación actual\n"+Coord);
            //this.mainActivity.setLocation(loc);
            h.setEnabled(true);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //mensaje1.setText("GPS Desactivado");
            Toast.makeText(Ubicacion.this, "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            ///////  mensaje1.setText("GPS Activado");
            Toast.makeText(Ubicacion.this, "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizacion (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }
    class Enviar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Ubicacion.this);
            pDialog.setMessage("Configurando ubicación");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


            int success;

            String Datos[] = new String[2];
            Datos[0] = Docum;
            Datos[1] = Coord;
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
                Toast.makeText(Ubicacion.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}

