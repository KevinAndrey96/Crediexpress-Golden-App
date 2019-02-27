package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Multas extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button Btnmultasdiarias = (Button) findViewById(R.id.btnMultasDiario);
        Button Btnmultassemanales = (Button) findViewById(R.id.btnMultasSemanal);
        Button Btnmultasquincenales = (Button) findViewById(R.id.btnMultasQuincenal);
        Button Btnmultasmensuales = (Button) findViewById(R.id.btnMultasMensual);

        Btnmultasdiarias.setOnClickListener(muldiari);
        Btnmultassemanales.setOnClickListener(mulsema);
        Btnmultasquincenales.setOnClickListener(mulquince);
        Btnmultasmensuales.setOnClickListener(mulmensu);
    }

    private View.OnClickListener muldiari= new View.OnClickListener(){
      @Override
        public void onClick(View v){
          Intent i=new Intent(Multas.this, VerMultas.class);
          i.putExtra("Tipo","Diario");
          startActivity(i);
        }

    };
    private View.OnClickListener mulsema = new View.OnClickListener(){
        @Override
    public void onClick(View v)
        {
            Intent i=new Intent(Multas.this, VerMultas.class);
            i.putExtra("Tipo","Semanal");
            startActivity(i);
        }
    };

    private View.OnClickListener mulquince = new View.OnClickListener(){
        @Override
    public void onClick(View v){
            Intent i=new Intent(Multas.this, VerMultas.class);
            i.putExtra("Tipo","Quincenal");
            startActivity(i);
        }
    };

    private View.OnClickListener mulmensu = new View.OnClickListener(){
        @Override
    public void onClick(View v){
            Intent i=new Intent(Multas.this, VerMultas.class);
            i.putExtra("Tipo","Mensual");
            startActivity(i);
        }
    };
}
