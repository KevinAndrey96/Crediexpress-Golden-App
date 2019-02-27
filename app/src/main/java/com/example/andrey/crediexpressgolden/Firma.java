package com.example.andrey.crediexpressgolden;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class Firma extends AppCompatActivity {

    private CanvasView canvas = null;

String Documento="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_firma);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Documento=getIntent().getExtras().getString("Cedu");
        /*getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();*/
        this.canvas = (CanvasView)this.findViewById(R.id.canvas);
        this.canvas.setBaseColor(Color.BLACK);
        this.canvas.setPaintStrokeColor(Color.WHITE);
        Firma.this.canvas.setPaintStrokeWidth(10F);

        Button btnBorrar = (Button) findViewById(R.id.btnclear);
        btnBorrar.setOnClickListener(clickclear);

        Button btnEnviar = (Button) findViewById(R.id.btnenviarcanvas);
        btnEnviar.setOnClickListener(clickenviar);

    }
    private View.OnClickListener clickclear = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //MainActivity.this.canvas.clear();
            Firma.this.canvas.undo();
        }
    };
    private View.OnClickListener clickenviar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bitmap bitmap = Firma.this.canvas.getScaleBitmap(300, 200);
            saveCanvasImage(bitmap);
            foto = Environment.getExternalStorageDirectory() +"/Golden/C4-" +Documento+".jpg";
            UploaderFoto nuevaTarea = new UploaderFoto();
            nuevaTarea.execute(foto);

        }
    };

    public void saveCanvasImage(Bitmap bm) {

        File fPath = Environment.getExternalStorageDirectory();
        File f = null;
        f = new File(fPath, "/Golden/C4-" +Documento+".jpg");

        try {
            FileOutputStream strm = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, strm);
            strm.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    String foto;
    Config C=new Config();
    ProgressDialog pDialog;
    public class UploaderFoto extends AsyncTask<String, String, String> {

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
            pDialog = new ProgressDialog(Firma.this);
            pDialog.setMessage("Subiendo firma...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Firma.this, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
    public CanvasView getCanvas() {
        return this.canvas;
    }

}
