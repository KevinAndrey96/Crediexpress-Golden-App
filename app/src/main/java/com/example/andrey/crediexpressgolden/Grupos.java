package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Grupos extends Activity{
    private EditText txtnombre;
    private int conteogrup;
    private String is;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ListView l=(ListView)findViewById(R.id.lstgroups);
        txtnombre=(EditText)findViewById(R.id.txtnombredelgrupo);

        Button nuevo=(Button)findViewById(R.id.btnNewGroup);
        nuevo.setOnClickListener(nuevogrupo);

        int i=0;

        while(true)
        {
            SharedPreferences prefs = getSharedPreferences("Groups"+i, Context.MODE_PRIVATE);
            if(!prefs.getString("Groups"+i, "").equals(""))
            {

                i++;
            }
            else
            {
                break;
            }
        }

        conteogrup=i;
        String[] NumGrups=new String[i];
        if(i!=0) {
            i = 0;
            int j = 0;
            while (true) {
                SharedPreferences prefs = getSharedPreferences("Groups" + i, Context.MODE_PRIVATE);
                if (!prefs.getString("Groups" + i, "").equals("")) {
                    NumGrups[j] = j+" - "+prefs.getString("Groups" + i, "");
                    i++;
                    j++;
                } else {
                        break;
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NumGrups);
            l.setAdapter(adapter);
            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String itemSeleccionado = adapterView.getItemAtPosition(i).toString();
                    Intent k = new Intent(Grupos.this, VerGrupos.class );
                    k.putExtra("Name",itemSeleccionado);
                    startActivity(k);

                }
            });

        }
        else
        {
            Toast.makeText(Grupos.this, "No hay grupos en el momento", Toast.LENGTH_SHORT).show();
        }
    }
    private View.OnClickListener nuevogrupo=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NewGroup();
        }
    };
    private void NewGroup(){
        String Nom = txtnombre.getText().toString();
        SharedPreferences prefs = getSharedPreferences("Groups"+conteogrup,Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Groups" + conteogrup, "" + Nom);
        editor.commit();
        Toast.makeText(Grupos.this, "Grupo agregado", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(Grupos.this, Menu.class);
        startActivity(i);
    }


    private void borralo()
    {
        try {
            int Nom = Integer.parseInt(txtnombre.getText().toString());
            SharedPreferences prefs = getSharedPreferences("Groups" + Nom, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Groups" + Nom, "");
            editor.commit();
            Toast.makeText(Grupos.this, "Grupo eliminado", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(Grupos.this, Menu.class);
            startActivity(i);

            while (true) {

                SharedPreferences prefis = getSharedPreferences("Groups" + (Nom + 1), Context.MODE_PRIVATE);
                SharedPreferences prefisi = getSharedPreferences("Groups" + (Nom), Context.MODE_PRIVATE);

                if (prefis.getString("Groups" + (Nom + 1), "").equals("")) {
                    break;
                }
                SharedPreferences.Editor editor2 = prefisi.edit();
                editor2.putString("Groups" + Nom, prefis.getString("Groups" + (Nom + 1), ""));


                SharedPreferences.Editor editor3 = prefis.edit();
                editor3.putString("Groups" + (Nom + 1), "");

                editor2.commit();
                editor3.commit();

                Nom++;

            }

        }catch(Exception e)
        {
            Toast.makeText(Grupos.this, "Para borrar debe escribir el indice del grupo", Toast.LENGTH_SHORT).show();
        }


        try {
            int Nom = 0;
            String [] array= new String [2];
            array=is.split("-");
            array[0].substring(0,array[0].length()-1);
            int h=Integer.parseInt(array[0])-1;
            String Namea=is;
            String Namen=h+" - "+array[1];

            SharedPreferences prefs = getSharedPreferences("Client"+Namea+ Nom, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Client"+Namea + Nom, "");
            editor.commit();
            Toast.makeText(Grupos.this, "Eliminado correctamente Cliente:" +Namea+ Nom, Toast.LENGTH_SHORT).show();

            while (true) {

                SharedPreferences prefis = getSharedPreferences("Client" +Namea+ (Nom + 1), Context.MODE_PRIVATE);
                SharedPreferences prefisi = getSharedPreferences("Client" +Namen+ (Nom), Context.MODE_PRIVATE);

                if (prefis.getString("Client" +Namea+ (Nom + 1), "").equals("")) {
                    break;
                }
                SharedPreferences.Editor editor2 = prefisi.edit();
                editor2.putString("Client" +Namen+ Nom, prefis.getString("Client" +Namen+ (Nom + 1), ""));


                SharedPreferences.Editor editor3 = prefis.edit();
                editor3.putString("Client"+Namea + (Nom + 1), "");

                editor2.commit();
                editor3.commit();

                Nom++;

            }

        }catch(Exception e)
        {
            Toast.makeText(Grupos.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
        }

    }
}
