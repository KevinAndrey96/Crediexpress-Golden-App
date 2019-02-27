package com.example.andrey.crediexpressgolden;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class VerGrupos extends Activity {
    private int cliente;
    private String Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vergrupos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Name = getIntent().getExtras().getString("Name");
        ListView lisgru = (ListView) findViewById(R.id.lstclientesgroup);
        int i = 0;
        while (true) {
            SharedPreferences prefs = getSharedPreferences("Client" + Name + i, Context.MODE_PRIVATE);

            if (!prefs.getString("Client" + Name + i, "").equals("")) {
                i++;
            } else {
                break;
            }
        }
        final String[] Numclien = new String[i];
        if (i != 0) {
            i = 0;
            int j = 0;
            while (true) {
                SharedPreferences prefs = getSharedPreferences("Client" + Name + i, Context.MODE_PRIVATE);
                if (!prefs.getString("Client" + Name + i, "").equals("")) {
                    Numclien[j] = j + " - " + prefs.getString("Client" + Name + i, "");
                    i++;
                    j++;
                } else {
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Numclien);
        lisgru.setAdapter(adapter);

        lisgru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in=new Intent(VerGrupos.this,VerClientes.class);
                String[] datos=Numclien[i].split("-");
                in.putExtra("DDD", datos[1].substring(1));
                startActivity(in);
            }
        });
        lisgru.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                String str = arg0.getItemAtPosition(index).toString();
                cliente=index;
                FragmentManager fragmentManager = getFragmentManager();
                DialogoSeleccion dialogo = new DialogoSeleccion();
                dialogo.show(fragmentManager, "tagAlerta");
                return true;
            }
        });
    }
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [1];
            items[0]="Eliminar de grupo";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("Opciones")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Eliminar de grupo": {
                                    try {
                                        int Nom = cliente;

                                        SharedPreferences prefs = getSharedPreferences("Client"+Name+ Nom, Context.MODE_PRIVATE);

                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("Client" + Name + Nom, "");
                                        editor.commit();


                                        while (true) {

                                            SharedPreferences prefis = getSharedPreferences("Client" +Name+ (Nom + 1), Context.MODE_PRIVATE);
                                            SharedPreferences prefisi = getSharedPreferences("Client" +Name+ (Nom), Context.MODE_PRIVATE);

                                            if (prefis.getString("Client" +Name+ (Nom + 1), "").equals("")) {
                                                break;
                                            }
                                            SharedPreferences.Editor editor2 = prefisi.edit();
                                            editor2.putString("Client" +Name+ Nom, prefis.getString("Client" +Name+ (Nom + 1), ""));


                                            SharedPreferences.Editor editor3 = prefis.edit();
                                            editor3.putString("Client"+Name + (Nom + 1), "");

                                            editor2.commit();
                                            editor3.commit();

                                            Nom++;

                                        }
                                        finish();
                                    }catch(Exception e)
                                    {
                                        Toast.makeText(VerGrupos.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
}