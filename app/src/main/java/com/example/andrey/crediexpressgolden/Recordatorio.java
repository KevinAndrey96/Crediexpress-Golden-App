package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Andrey on 21/06/2016.
 */
public class Recordatorio extends Activity {
    String[] records;
    int Records=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);

        SharedPreferences prefecorr = getSharedPreferences("Records", Context.MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(prefecorr.getString("Records", "").toString().equals(""))
        {
            Records=0;
        }else {
            Records = Integer.parseInt(prefecorr.getString("Records", "").toString());
        }
        records =new String[Records];

        for(int i=0;i<Records;i++)
        {
            SharedPreferences preferencias = getSharedPreferences("Record"+i, Context.MODE_PRIVATE);
            records[i]=preferencias.getString("Record"+i, "").toString();
        }

        ListView listarecordatorio=(ListView) findViewById(R.id.lstRecordatorios);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, records);
        listarecordatorio.setAdapter(adaptador);

        listarecordatorio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                String ir[]=new String[2];
                if(!records[position].equals("Completo"))
                ir=records[position].split("-");

                Intent i = new Intent(Recordatorio.this, Pago.class);

                i.putExtra("Bandera", "Si");
                i.putExtra("Cedula", ir[1]);
                startActivity(i);

            }

        });
    }
}
