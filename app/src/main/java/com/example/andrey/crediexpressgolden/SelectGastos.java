package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Andrey on 19/07/2016.
 */
public class SelectGastos extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectgastos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button btnGasolina=(Button) findViewById(R.id.btnGasolina);
        Button btnGast=(Button) findViewById(R.id.btnGastosVarios);
        btnGasolina.setOnClickListener(gasolina);
        btnGast.setOnClickListener(gasto);
    }
    private View.OnClickListener gasolina= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(SelectGastos.this, Gasolina.class);
            finish();
            startActivity(i);
        }
    };
    private View.OnClickListener gasto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(SelectGastos.this, Gastos.class);
            finish();
            startActivity(i);
        }
    };
}
