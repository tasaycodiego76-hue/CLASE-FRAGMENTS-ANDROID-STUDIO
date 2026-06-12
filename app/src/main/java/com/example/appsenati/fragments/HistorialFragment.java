package com.example.appsenati.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appsenati.R;
import com.example.appsenati.adapters.HerramientaAdapter;
import com.example.appsenati.entity.Herramienta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    RecyclerView recyclerHerramientas;
    List<Herramienta> listaHerramientas = new ArrayList<>();

    RequestQueue requestQueue;

    private final String URL = "http://192.168.101.33:3000/api/herramientas/";


    public HistorialFragment(){

    }

    //Metodo que se ejecuta al iniciar el Fragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Asociar el fragment con el XML
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerHerramientas = view.findViewById(R.id.recyclerHerramientas);

        recyclerHerramientas.setLayoutManager(new LinearLayoutManager(getContext()));

        obtenerDatos();
    }
    private void obtenerDatos(){

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("Resultado",jsonObject.toString());
                        try{

                            if (jsonObject.getBoolean("success")){

                                //Objetos es una lista (array) de JSON
                                JSONArray objetos = jsonObject.getJSONArray("data");
                                Herramienta herramienta;


                                for (int i = 0; i < objetos.length(); i ++){
                                    //Obj es un JSON
                                    JSONObject obj = objetos.getJSONObject(i);
                                    herramienta= new Herramienta();
                                    herramienta.setIdherramienta(obj.getInt("idherramienta"));
                                    herramienta.setNombre(obj.getString("nombre"));
                                    herramienta.setDescripcion(obj.getString("descripcion"));

                                    Log.d("Resultado", obj.getString("nombre"));
                                    listaHerramientas.add(herramienta);
                                }
                                //Hasta este punto listaHerramientas ya esta cargada

                                HerramientaAdapter adapter = new HerramientaAdapter(listaHerramientas,getContext());

                                recyclerHerramientas.setAdapter(adapter);
                            }
                        }catch (Exception e){
                            Log.e("ErrorJSON", "NO podemos leer JSON");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorWS", volleyError.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }
}
