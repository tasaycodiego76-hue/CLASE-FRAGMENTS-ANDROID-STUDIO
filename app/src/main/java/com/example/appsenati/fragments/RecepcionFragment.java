package com.example.appsenati.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appsenati.R;

import org.json.JSONObject;

public class RecepcionFragment extends Fragment {

    Button btnBuscar, btnActualizar;
    EditText edtId, edtNombreH, edtMarcaH, edtDescripcionH;
    RadioButton rbtBuenoH, rbtRegularH, rbtMaloH;
    RadioButton rbtManualH, rbtElectricaH;
    RadioGroup rgCondicionH, rgTipoH;
    RequestQueue requestQueue;
    String condicion = "", tipo = "";

    private final String endPoint = "http://192.168.101.33:3000/api/herramientas/";

    public RecepcionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recepcion, container, false);
    }

    private void seleccionarCondicion(String condicion) {
        if (condicion.equalsIgnoreCase("Bueno"))   { rgCondicionH.check(R.id.rbtBuenoH); }
        if (condicion.equalsIgnoreCase("Regular")) { rgCondicionH.check(R.id.rbtRegularH); }
        if (condicion.equalsIgnoreCase("Malo"))    { rgCondicionH.check(R.id.rbtMaloH); }
    }

    private void seleccionarTipo(String tipo) {
        if (tipo.equalsIgnoreCase("Manual")) { rgTipoH.check(R.id.rbtManualH); }
        if (tipo.equalsIgnoreCase("Eléctrica") || tipo.equalsIgnoreCase("Electrica")) { rgTipoH.check(R.id.rbtElectricaH); }
    }

    private void buscarHerramienta() {
        String idTexto = edtId.getText().toString().trim();

        if (idTexto.isEmpty()) {
            Toast.makeText(getContext(), "Ingrese un ID", Toast.LENGTH_SHORT).show();
            return;
        }

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                endPoint + idTexto,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject registro = jsonObject.getJSONObject("data");
                                edtNombreH.setText(registro.getString("nombre"));
                                edtMarcaH.setText(registro.getString("marca"));
                                edtDescripcionH.setText(registro.getString("descripcion"));
                                seleccionarCondicion(registro.getString("condicion"));
                                seleccionarTipo(registro.getString("tipo"));
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        NetworkResponse response = volleyError.networkResponse;
                        if (response != null && response.data != null) {
                            int statusCode     = response.statusCode;
                            String messageJSON = new String(response.data);
                            try {
                                JSONObject jsonWS = new JSONObject(messageJSON);
                                String message    = jsonWS.getString("message");
                                if (statusCode == 404) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e("ErrorJSON", e.toString());
                            }
                        }
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void actualizarHerramienta() {
        condicion = "";
        if (rbtBuenoH.isChecked())   { condicion = "Bueno"; }
        if (rbtRegularH.isChecked()) { condicion = "Regular"; }
        if (rbtMaloH.isChecked())    { condicion = "Malo"; }

        tipo = "";
        if (rbtManualH.isChecked())    { tipo = "Manual"; }
        if (rbtElectricaH.isChecked()) { tipo = "Eléctrica"; }

        JSONObject datosEnviar = new JSONObject();
        try {
            datosEnviar.put("nombre",      edtNombreH.getText().toString());
            datosEnviar.put("marca",       edtMarcaH.getText().toString());
            datosEnviar.put("descripcion", edtDescripcionH.getText().toString());
            datosEnviar.put("condicion",   condicion);
            datosEnviar.put("tipo",        tipo);
        } catch (Exception e) {
            Log.e("ErrorJSON", e.toString());
        }

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                endPoint + edtId.getText().toString(),
                datosEnviar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String message  = jsonObject.getString("message");
                            if (success) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        NetworkResponse response = volleyError.networkResponse;
                        if (response != null && response.data != null) {
                            String messageJSON = new String(response.data);
                            try {
                                JSONObject jsonWS = new JSONObject(messageJSON);
                                String message    = jsonWS.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e("ErrorJSON", e.toString());
                            }
                        }
                    }
                }
        ) {
        };

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBuscar       = view.findViewById(R.id.btnBuscar);
        btnActualizar   = view.findViewById(R.id.btnActualizar);
        edtId           = view.findViewById(R.id.edtId);
        edtNombreH      = view.findViewById(R.id.edtNombreH);
        edtMarcaH       = view.findViewById(R.id.edtMarcaH);
        edtDescripcionH = view.findViewById(R.id.edtDescripcionH);

        rbtBuenoH     = view.findViewById(R.id.rbtBuenoH);
        rbtRegularH   = view.findViewById(R.id.rbtRegularH);
        rbtMaloH      = view.findViewById(R.id.rbtMaloH);
        rbtManualH    = view.findViewById(R.id.rbtManualH);
        rbtElectricaH = view.findViewById(R.id.rbtElectricaH);

        rgCondicionH  = view.findViewById(R.id.rgCondicionH);
        rgTipoH       = view.findViewById(R.id.rgTipo);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarHerramienta();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarHerramienta();
            }
        });
    }
}