package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VerDocumento extends Activity {
    Config C=new Config();
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verdocumento);
        String D=getIntent().getExtras().getString("Cedu");
        TextView t= (TextView) findViewById(R.id.txtDocumentoFoto);
        t.setText(D.substring(3));
        img=(ImageView) findViewById(R.id.imgverdocumento);

        //UrlImageViewHelper.setUrlDrawable(img, "http://crediexpressgolden.com.co/Clientes/no-image.jpg");
        if(D.substring(0,2).equals("C1") || D.substring(0,2).equals("C2"))
            UrlImageViewHelper.setUrlDrawable(img, C.FotosURL+D+".jpg", R.drawable.noimage);
        if(D.substring(0,2).equals("C3"))
            UrlImageViewHelper.setUrlDrawable(img, C.FotosURL+D+".jpg", R.drawable.noimagec3);
        if(D.substring(0,2).equals("C4"))
            UrlImageViewHelper.setUrlDrawable(img, C.FotosURL+D+".jpg", R.drawable.noimagec4);


    }
}
