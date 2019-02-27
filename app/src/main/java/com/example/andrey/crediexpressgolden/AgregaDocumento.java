package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;

public class AgregaDocumento extends Activity {
    private Button camara;
    private ImageView imagen;
    private Uri output;
    private String foto,Documento;
    private File file;

    String Parametro="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregadocumento);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        camara = (Button) findViewById(R.id.btnenviafoto);
        camara.setOnClickListener(fotografia);
        imagen = (ImageView) findViewById(R.id.Imgdocumento);
        Documento=getIntent().getExtras().getString("Cedu");
    }
    private View.OnClickListener fotografia = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!Documento.equalsIgnoreCase("")){
                getCamara();
            }else{
                Toast.makeText(AgregaDocumento.this, "Error inesperado, intente de nuevo", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void getCamara(){
        foto = Environment.getExternalStorageDirectory() +"/Golden/" +Documento+".jpg";
        file=new File(foto);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        output=Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory() +
                            "/Golden/"+Documento+ ".jpg");
            imagen.setImageBitmap(bMap);
            UploaderFoto nuevaTarea = new UploaderFoto();
            nuevaTarea.execute(foto);
        }

    }
    Config C=new Config();
    public class UploaderFoto extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        String miFoto = "";
        @Override
        protected String doInBackground(String... args) {
            miFoto = foto;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpPost httppost = new HttpPost(C.URL+"upload_foto.php");
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
            pDialog = new ProgressDialog(AgregaDocumento.this);
            pDialog.setMessage("Subiendo...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(AgregaDocumento.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
