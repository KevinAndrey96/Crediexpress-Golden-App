package com.example.andrey.crediexpressgolden;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewCliente extends Activity{

    private Button camara;
    private String Dire2,Alias;
    private String im="NO";
    private ImageView imagen;
    private Uri output;
    private String foto;
    private String Coord="";
    private File file;
    private EditText Documento, Nombres,Dir2,Ali, Apellidos, Direccion, Telefono, Email, NumSeguro,Ciudad,Barrio,bar2;
    private Spinner SignoZ;
    private Button mRegister;
    CheckBox chk;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    static Config C=new Config();
    private static final String REGISTER_URL = C.URL+"register.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String Cobra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Dir2 = (EditText) findViewById(R.id.txtDireccion2);
        Ali = (EditText) findViewById(R.id.txtAlias);
        bar2 = (EditText) findViewById(R.id.txtbarrio2);
        Documento = (EditText) findViewById(R.id.txtDocumento);
        Nombres = (EditText) findViewById(R.id.txtNombres);
        Apellidos = (EditText) findViewById(R.id.txtApellidos);
        Direccion = (EditText) findViewById(R.id.txtDireccion);
        Telefono = (EditText) findViewById(R.id.txtTelefono);
        Email = (EditText) findViewById(R.id.txtEmail);
        NumSeguro = (EditText) findViewById(R.id.txtNumSeguro);
        Ciudad = (EditText) findViewById(R.id.txtBarrio);
        Barrio = (EditText) findViewById(R.id.txtCiudad);
        chk=(CheckBox) findViewById(R.id.chkubik);
        SignoZ = (Spinner) findViewById(R.id.Signo);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.signoszodiacales , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SignoZ.setAdapter(spinner_adapter);
        SharedPreferences prefecorr = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        Cobra = prefecorr.getString("Docu", "");
        mRegister = (Button) findViewById(R.id.btnRegistro);
        mRegister.setOnClickListener(enviar);

        camara = (Button) findViewById(R.id.btnFoto);
        camara.setOnClickListener(fotografia);
        imagen = (ImageView) findViewById(R.id.ImgCedula);
    }
    private View.OnClickListener fotografia = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!Documento.getText().toString().trim().equalsIgnoreCase("")){
                    getCamara();
            }else{
                Toast.makeText(NewCliente.this, "Por favor llene el campo documento", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void getCamara(){
        foto = Environment.getExternalStorageDirectory() +"/Clientes/" +Documento.getText().toString().trim()+".jpg";
        file=new File(foto);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        output=Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            im="OK";
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory() +
                            "/Clientes/" + Documento.getText().toString().trim() + ".jpg");
            imagen.setImageBitmap(bMap);
        }

    }
   public class UploaderFoto extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        String miFoto = "";
        @Override
        protected String doInBackground(String... args) {
            miFoto = foto;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpPost httppost = new HttpPost("http://crediexpressgolden.com.co/Android/upload_foto.php");
                File file = new File(miFoto);
                MultipartEntity mpEntity = new MultipartEntity();
                ContentBody foto = new FileBody(file, "image/jpeg");
                mpEntity.addPart("fotoUp", foto);
                httppost.setEntity(mpEntity);
                httpclient.execute(httppost);
                httpclient.getConnectionManager().shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewCliente.this);
            pDialog.setMessage("Subiendo documento");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
       protected void onPostExecute(String file_url) {
           pDialog.dismiss();
           if (file_url != null) {
               Toast.makeText(NewCliente.this, file_url, Toast.LENGTH_LONG).show();
           }
       }
    }
    @SuppressLint("validFragment")
    public class DialogoConfirmacion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setMessage("¿Desea agregar un credito a este cliente?")
                    .setTitle("Agregar Credito")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener()  {
                        public void onClick(DialogInterface dialog, int id) {
                            //Log.i("Dialogos", "Confirmacion Aceptada.");
                            Intent i = new Intent(NewCliente.this,NewCredito.class);
                            i.putExtra("Document", Documento.getText().toString());
                            i.putExtra("NSeguro", NumSeguro.getText().toString());
                            finish();
                            startActivity(i);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Log.i("Dialogos", "Confirmacion Cancelada.");
                            finish();
                            dialog.cancel();
                        }
                    });

            return builder.create();
        }
    }
    @SuppressLint("validFragment")
    public class DialogCodeudor extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setMessage("¿Desea agregar un codeudor a este cliente?")
                    .setTitle("Codeudor")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener()  {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent e= new Intent(NewCliente.this,Buscar.class);
                            e.putExtra("Code","Si");
                            e.putExtra("Clien",""+Documento.getText().toString());
                            finish();
                            startActivity(e);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FragmentManager fragmentManager = getFragmentManager();
                            DialogoConfirmacion dialogo = new DialogoConfirmacion();
                            dialogo.show(fragmentManager, "tagAlerta");
                            dialog.cancel();
                        }
                    });

            return builder.create();
        }
    }
    public boolean isInteger( String input )
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }
    private View.OnClickListener enviar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(chk.isChecked())
            {
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Localizacion Local = new  Localizacion();
                Local.setMainActivity(NewCliente.this);
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                        (LocationListener) Local);
            }
            if(Telefono.getText().toString().length()==10) {
                if(isInteger(Documento.getText().toString()))
                envialo();
                else
                    Toast.makeText(NewCliente.this, "Por favor compruebe el campo Documento", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(NewCliente.this, "El Teléfono indicado no es valido", Toast.LENGTH_SHORT).show();
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
        NewCliente mainActivity;

        public NewCliente getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(NewCliente mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            Coord=loc.getLatitude()+", "+loc.getLongitude();
            //Toast.makeText(NewCliente.this, Text, Toast.LENGTH_SHORT).show();
            //this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //mensaje1.setText("GPS Desactivado");
            Toast.makeText(NewCliente.this, "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            ///////  mensaje1.setText("GPS Activado");
            Toast.makeText(NewCliente.this, "GPS Activado", Toast.LENGTH_SHORT).show();
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

    public void envialo() {
        new CreateUser().execute();
    }

   public class CreateUser extends AsyncTask<String, String, String> {
       public AlertDialog.Builder alertDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewCliente.this);
            pDialog.setMessage("Registrando Cliente...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            int success;

            String[] Datosaenviar=new String[15];
            Datosaenviar[0]=Documento.getText().toString();
            Datosaenviar[1]=Nombres.getText().toString();
            Datosaenviar[2]=Apellidos.getText().toString();
            Datosaenviar[3]=Direccion.getText().toString();
            Datosaenviar[4]=Telefono.getText().toString();
            Datosaenviar[6]=Email.getText().toString();
            Datosaenviar[7]=NumSeguro.getText().toString();
            Datosaenviar[5]=SignoZ.getSelectedItem().toString();
            Datosaenviar[8]=Cobra;
            Datosaenviar[10]=Ciudad.getText().toString();
            Datosaenviar[9]=Barrio.getText().toString();
            Datosaenviar[11]=Coord;
            Datosaenviar[12]=Dir2.getText().toString();
            Datosaenviar[13]=Ali.getText().toString();
            Datosaenviar[14]=bar2.getText().toString();


            try {
                List params = new ArrayList();
                for(int i=0;i<15;i++)
                {
                    params.add(new BasicNameValuePair("Dato"+(i+1), Datosaenviar[i]));
                }

                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    FragmentManager fragmentManager = getFragmentManager();
                    DialogCodeudor dialogo = new DialogCodeudor();
                    dialogo.show(fragmentManager, "tagAlerta");

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
                Toast.makeText(NewCliente.this, file_url, Toast.LENGTH_LONG).show();
                if(file_url.equals("El usuario se ha agregado correctamente"))
                {
                    if(im.equals("OK")) {
                        UploaderFoto nuevaTarea = new UploaderFoto();
                        nuevaTarea.execute(foto);
                    }
                }
            }
        }
    }
}
