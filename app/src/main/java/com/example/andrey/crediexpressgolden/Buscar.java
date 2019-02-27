package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Buscar extends Activity {
    private String Elcampo="";
    Spinner spncampos;
    EditText campo;
    String Cod="No";
    String Clien="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button btnBusqueda=(Button) findViewById(R.id.btnbuscando);
        Cod=getIntent().getExtras().getString("Code","");
        if(Cod.equals("Si"))
        {
            Toast.makeText(Buscar.this, "Por favor busque al codeudor a asociar", Toast.LENGTH_SHORT).show();
        }
        Clien=getIntent().getExtras().getString("Clien","");
        spncampos=(Spinner) findViewById(R.id.spnCampos);
        campo=(EditText) findViewById(R.id.txtBuscar);

        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.Campos , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spncampos.setAdapter(spinner_adapter);

        btnBusqueda.setOnClickListener(buscalo);
    }
    private View.OnClickListener buscalo= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Cod.equals("Si"))
            {
                Intent i = new Intent(Buscar.this, MisClientes.class);
                i.putExtra("datos", "Si");
                i.putExtra("esco", "Si");
                Elcampo = campo.getText().toString();
                i.putExtra("Campo", spncampos.getSelectedItem().toString());
                i.putExtra("Valor", Elcampo);
                i.putExtra("Clien", Clien);
                finish();
                startActivity(i);
            }
            else {
                Intent i = new Intent(Buscar.this, MisClientes.class);
                i.putExtra("datos", "Si");
                i.putExtra("esco", "No");
                Elcampo = campo.getText().toString();
                i.putExtra("Campo", spncampos.getSelectedItem().toString());
                i.putExtra("Valor", Elcampo);
                finish();
                startActivity(i);
            }
        }
    };
}
