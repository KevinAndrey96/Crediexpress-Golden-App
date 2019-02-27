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
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //Toast.makeText(Menu.this, "W="+width+" H="+height, Toast.LENGTH_SHORT).show();
        if(height==1920 || width==1080)
        {
            setContentView(R.layout.activity_menu);
        }
        if(height>1188 || width>720)
        {
            setContentView(R.layout.activity_menu);
        }
        else
        {
            setContentView(R.layout.activity_menu5);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button btnCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(Cerrar);
        Button btnMisDatos = (Button) findViewById(R.id.btnMisDatos);
        btnMisDatos.setOnClickListener(Datos);
        Button btnVerRutas = (Button) findViewById(R.id.btnVerRutas);
        btnVerRutas.setOnClickListener(Rutas);
        Button btnpago = (Button) findViewById(R.id.btnpago);
        btnpago.setOnClickListener(pago);
        Button btngrup = (Button) findViewById(R.id.btnGrupos);
        btngrup.setOnClickListener(grupi);
        Button btngast = (Button) findViewById(R.id.btnOtrosGastos);
        btngast.setOnClickListener(gastos);
        Button btnNewCliente = (Button) findViewById(R.id.btnNewCliente);
        btnNewCliente.setOnClickListener(Cliente);
        Button btnNewCre = (Button) findViewById(R.id.btnNewCredito);
        btnNewCre.setOnClickListener(credi);
        Button btnMiscli = (Button) findViewById(R.id.btnMisClientes);
        btnMiscli.setOnClickListener(Misclient);
        Button btnMultas = (Button) findViewById(R.id.btnMultas);
        btnMultas.setOnClickListener(Multasa);
        Button btnCaja = (Button) findViewById(R.id.btnCaja);
        btnCaja.setOnClickListener(Caja);

        SharedPreferences Permis = getSharedPreferences("Permis", Context.MODE_PRIVATE);
        if(Permis.getString("Permis","").equals("No"))
        {
            Toast.makeText(Menu.this, "NO HAY PERMISOS DE CREDITOS", Toast.LENGTH_SHORT).show();
            btnpago.setEnabled(false);
        }
        SharedPreferences Permis2 = getSharedPreferences("Permis2", Context.MODE_PRIVATE);
        if(Permis2.getString("Permis2","").equals("No"))
        {
            Toast.makeText(Menu.this, "NO HAY PERMISOS DE CLIENTES", Toast.LENGTH_SHORT).show();
            btnNewCliente.setEnabled(false);
        }

    }
    private View.OnClickListener rec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent h=new Intent(Menu.this, Recordatorio.class);
            startActivity(h);
        }
    };
    private View.OnClickListener grupi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent h=new Intent(Menu.this, Grupos2.class);
            h.putExtra("Add", "Nope");
            startActivity(h);
        }
    };


    private View.OnClickListener Caja = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i= new Intent(Menu.this, Caja.class);
            startActivity(i);
        }
    };

    private View.OnClickListener Cerrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            DialogoSeleccion dialogo = new DialogoSeleccion();
            dialogo.show(fragmentManager, "tagAlerta");
        }
    };
    @SuppressLint("validFragment")
    public class DialogoSeleccion extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String items[]=new String [2];
            items[0]="Si";
            items[1]="No";

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle("¿Está Seguro?")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (items[item]) {
                                case "Si": {
                                    Cerrar();
                                }
                                break;
                                case "No": {
                                }
                                break;
                            }
                        }
                    });

            return builder.create();
        }
    }
    public void Cerrar() {

        SharedPreferences LaSes = getSharedPreferences("LaSes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editord = LaSes.edit();
        editord.putString("LaSes", "No");
        editord.commit();


        Toast.makeText(Menu.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
        SharedPreferences preferencias = getSharedPreferences("Docu", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.clear();
        editor.commit();

        SharedPreferences preferencias3 = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = preferencias3.edit();
        editor3.putString("Sesion", "No");
        editor3.commit();

        Intent i = new Intent(Menu.this, Login.class);
        startActivity(i);
        finish();
    }
    private View.OnClickListener Cliente = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Clien();
        }
    };

    public void Clien() {
        Intent i = new Intent(Menu.this, NewCliente.class);
        startActivity(i);

    }
    private View.OnClickListener Datos = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MisDatos();
        }
    };

    public void MisDatos() {

        Intent i = new Intent(Menu.this, MisDatos.class);
        startActivity(i);

    }
    private View.OnClickListener Misclient = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Misc();
        }
    };

    public void Misc() {


        Intent i = new Intent(Menu.this, MisClientes.class);
        i.putExtra("datos","No");
        //i.putExtra("Campo", "Nombres");
        //i.putExtra("Valor", "");
        startActivity(i);

    }

    private View.OnClickListener credi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ncredi();
        }
    };

    public void ncredi() {
        Intent i = new Intent(Menu.this,NewCredito.class);
        i.putExtra("Document", "");
        startActivity(i);

    }
    private View.OnClickListener pago = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            npago();
        }
    };

    public void npago() {


        Intent i = new Intent(Menu.this, Pago.class);
        i.putExtra("Bandera", "Nain");
        startActivity(i);

    }
    private View.OnClickListener gastos = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ogastos();
        }
    };

    public void ogastos() {
        Intent i = new Intent(Menu.this, SelectGastos.class);
        startActivity(i);

    }
    private View.OnClickListener Multasa = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Multi();
        }
    };

    public void Multi() {


        Intent i = new Intent(Menu.this, Multas.class);
        startActivity(i);

    }

    private View.OnClickListener Rutas = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VerRutas();
        }
    };

    public void VerRutas() {


        Intent i = new Intent(Menu.this, SelectRuta.class);
        startActivity(i);

    }
}
