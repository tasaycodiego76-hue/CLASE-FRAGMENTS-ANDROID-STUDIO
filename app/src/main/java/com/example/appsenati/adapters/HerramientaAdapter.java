package com.example.appsenati.adapters;


import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appsenati.R;
import com.example.appsenati.entity.Herramienta;

import org.json.JSONObject;

import java.util.List;

public class HerramientaAdapter extends RecyclerView.Adapter<HerramientaAdapter.ViewHolder> {

    //PASO 1
    //Contenedor de herramienta (objeto derivado de una clase)
    private List<Herramienta> listaHerramientas;
    private Context context;

    RequestQueue requestQueue;

    private final String URL = "http://192.168.101.33:3000/api/herramientas/";


    //PASO 2
    //Contructor para la clase principal(HerramientaAdapter)
    //Subclase

    public HerramientaAdapter(List<Herramienta> listaHerramientas, Context context){
        this.listaHerramientas = listaHerramientas;
        this.context = context;


    }

    //PASO 5
    //Cual es la PLantilla

    @NonNull
    @Override
    public HerramientaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_herramientas, parent, false);
        return new HerramientaAdapter.ViewHolder(view);
    }

    //PASO 4
    //Bind (unir, vincular, asociar)
    @Override
    public void onBindViewHolder(@NonNull HerramientaAdapter.ViewHolder holder, int position) {

        Herramienta herramienta = listaHerramientas.get(position);
        holder.edtNombreRV.setText(herramienta.getNombre());//getNombre (entity)
        holder.edtDescripcionRV.setText(herramienta.getDescripcion());

        //Botomes no traen datos, activa un evento | lambda java
        holder.btnEliminarRV.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Herramientas");
            builder.setMessage("¿Desea eliminar la herramienta ?"+ herramienta.getNombre()+ "?");
            builder.setPositiveButton("Si",(x, y)->{
                eliminarHerramienta(herramienta.getIdherramienta(), position);
            });
            builder.setNegativeButton("No",null);
            builder.show();
            //Algoritmo para eliminar
            Toast.makeText(context,"Eliminando ID:"+herramienta.getIdherramienta(), Toast.LENGTH_LONG).show();
        });

    }

    //Eliminacion WS
    private void eliminarHerramienta(int idherramienta, int position) {
        requestQueue = Volley.newRequestQueue(context);
        String endPoint = URL + String.valueOf(idherramienta);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                endPoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try{
                            if (jsonObject.getBoolean("success"));

                            listaHerramientas.remove(position);

                            notifyItemRemoved(position);
                            notifyItemChanged(position, listaHerramientas.size());
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        }catch(Exception e){
                            Log.e("Error JSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Log.e("ErrorEliminando", volleyError.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    //PASO 6
    //EL adaptador debe conocer el total de elementos
    @Override
    public int getItemCount() {

        return listaHerramientas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //Referencias
        TextView edtNombreRV, edtDescripcionRV;
        Button btnEliminarRV;
        //PASO 3
        //Acceso a los Widget del XML (plantilla)
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edtNombreRV = itemView.findViewById(R.id.edtNombreRV);
            edtDescripcionRV = itemView.findViewById(R.id.edtDescripcionRV);
            btnEliminarRV = itemView.findViewById(R.id.btnEliminarRV);
        }
    }
}
