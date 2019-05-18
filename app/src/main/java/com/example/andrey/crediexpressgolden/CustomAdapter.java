package com.example.andrey.crediexpressgolden;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CustomAdapter implements ListAdapter {
    ArrayList<SubjectData> arrayList;
    Context context;

    public CustomAdapter(Context context, ArrayList<SubjectData> arrayList) {
        this.arrayList=arrayList;
        this.context=context;


    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public String toString() {
        return "CustomAdapter{" +
                "arrayList=" + arrayList +
                ", context=" + context +
                ", pDialog=" + pDialog +
                ", jParser=" + jParser +
                ", DClien='" + DClien + '\'' +
                ", DCodeu='" + DCodeu + '\'' +
                '}';
    }

    public String getDocumento(int position) {
        final SubjectData subjectData = arrayList.get(position);
        return subjectData.Documento;
    }

    public String getNombreCompleto(int position) {
        final SubjectData subjectData = arrayList.get(position);
        return subjectData.SubjectName;
    }
    public String getTelefono(int position) {
        final SubjectData subjectData = arrayList.get(position);
        return subjectData.Telefono;
    }
    public String getCoordenadas(int position) {
        final SubjectData subjectData = arrayList.get(position);
        return subjectData.Coordenadas;
    }


    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SubjectData subjectData = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.activity_spclientes, null);

            TextView tittle = (TextView) convertView.findViewById(R.id.txtspcnombre);
            ImageView imag = (ImageView) convertView.findViewById(R.id.imgCliente);
            TextView doc = (TextView) convertView.findViewById(R.id.txtspcDocumento);
            TextView tel = (TextView) convertView.findViewById(R.id.txtspcTelefono);
            TextView dir = (TextView) convertView.findViewById(R.id.txtspcDireccion);
            TextView sig = (TextView) convertView.findViewById(R.id.txtspcSignoSeg);

            tittle.setText(subjectData.SubjectName);
            doc.setText(subjectData.Documento);
            tel.setText(subjectData.Telefono);
            dir.setText(subjectData.Direccion);
            sig.setText(subjectData.Seguro);
            Picasso.with(context)
                    .load(subjectData.Image)
                    .into(imag);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //DCodeu=subjectData.Documento;
                    DClien=subjectData.DClien;
                    DCodeu = subjectData.Documento.substring(11);

                    String Codeudor1=subjectData.Codeud;

                    //Toast.makeText(context, Codeudor1+" - "+DCodeu+" - "+DClien, Toast.LENGTH_SHORT).show();
                    if (Codeudor1.equals("Si")) {
                        AddCodeudor();
                    } else {
                        VerCR(DCodeu);
                    }
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   //Toast.makeText(view.getContext(), "Oprimido largo", Toast.LENGTH_SHORT).show();
                   return false;
               }
           });
        }
        return convertView;
    }

    public void VerCR(String cliente) {
        if(cliente.equals(""))
        {
            Toast.makeText(context, "No ha seleccionado ningún cliente", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent i=new Intent(context,VerClientes.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Intent i=new Intent(context,VerClientes.class);
            i.putExtra("DDD", cliente);
            context.startActivity(i);
        }

    }

    private void AddCodeudor()
    {
        new CustomAdapter.Codeudor().execute();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    private String DClien;
    private String DCodeu;
    static Config C=new Config();
    private static String Codeudor_URL = C.URL+"envia_codeudor.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    class Codeudor extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Configurando codeudor");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            String Datos[] = new String[2];
            Datos[0] = DClien;
            Datos[1] = DCodeu;
            try {
                List params = new ArrayList();
                for (int i = 0; i < Datos.length; i++)
                    params.add(new BasicNameValuePair("Dato" + (i + 1), Datos[i]));
                JSONObject json = jParser.makeHttpRequest(Codeudor_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    //((Activity)context).finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
            }
            ((Activity)context).finish();
        }

    }
}


