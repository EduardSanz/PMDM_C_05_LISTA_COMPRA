package com.cieep.a05_ejercicio_lista_compra;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cieep.a05_ejercicio_lista_compra.adapters.ProductosAdapter;
import com.cieep.a05_ejercicio_lista_compra.configuraciones.Constantes;
import com.cieep.a05_ejercicio_lista_compra.modelos.Producto;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import com.cieep.a05_ejercicio_lista_compra.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private ArrayList<Producto> productosList;
    private ProductosAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private SharedPreferences spDatos;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        spDatos = getSharedPreferences(Constantes.DATOS, MODE_PRIVATE);
        gson = new Gson();



        productosList = new ArrayList<>();

        adapter = new ProductosAdapter(productosList, R.layout.producto_view_holder, this);
        layoutManager = new GridLayoutManager(this, 1);
        binding.contentMain.contenedor.setLayoutManager(layoutManager);
        binding.contentMain.contenedor.setAdapter(adapter);

        leerDatos();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createProducto().show();
            }
        });
    }

    private void leerDatos() {
        if (spDatos.contains(Constantes.LISTA_PRODUCTOS)) {
            String productosJSON = spDatos.getString(Constantes.LISTA_PRODUCTOS, "[]");
            Type tipo = new TypeToken< ArrayList<Producto> >(){}.getType();
            ArrayList<Producto> temp = gson.fromJson(productosJSON, tipo);
            productosList.clear();
            productosList.addAll(temp);
            adapter.notifyItemRangeInserted(0, productosList.size());
        }
    }

    private AlertDialog createProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alert_add_title));
        builder.setCancelable(false);

        View productoViewModel = LayoutInflater.from(this).inflate(R.layout.producto_view_model, null);
        TextView lblTotal = productoViewModel.findViewById(R.id.lblTotalProductoViewModel);
        EditText txtNombre = productoViewModel.findViewById(R.id.txtNombreProductoViewModel);
        EditText txtCantidad = productoViewModel.findViewById(R.id.txtCantidadProductoViewModel);
        EditText txtPrecio = productoViewModel.findViewById(R.id.txtPrecioProductoViewModel);

        builder.setView(productoViewModel);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("EVENTO_TEXTO", "BEFORE "+charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("EVENTO_TEXTO", "ONCHANGED "+charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("EVENTO_TEXTO", "AFTER "+editable.toString());
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());

                    float total = cantidad * precio;
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                    lblTotal.setText(numberFormat.format(total));
                }
                catch (NumberFormatException nfe) {}
            }
        };

        txtCantidad.addTextChangedListener(textWatcher);
        txtPrecio.addTextChangedListener(textWatcher);

        builder.setNegativeButton(R.string.alert_cancel_button, null);
        builder.setPositiveButton(R.string.alert_add_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtNombre.getText().toString().isEmpty() && !txtCantidad.getText().toString().isEmpty() &&
                    !txtPrecio.getText().toString().isEmpty()) {
                    Producto producto = new Producto(
                                                txtNombre.getText().toString(),
                                                Integer.parseInt(txtCantidad.getText().toString()),
                                                Float.parseFloat(txtPrecio.getText().toString())
                                                );
                    productosList.add(0, producto);
                    adapter.notifyItemInserted(0);
                    guardarInformacion();
                }
                else {
                    Toast.makeText(MainActivity.this, "Faltan Datos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return builder.create();
    }

    private void guardarInformacion() {
        String productosJSON = gson.toJson(productosList);
        SharedPreferences.Editor editor = spDatos.edit();
        editor.putString(Constantes.LISTA_PRODUCTOS, productosJSON);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("LISTA", productosList);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        productosList.addAll( (ArrayList<Producto>) savedInstanceState.getSerializable("LISTA") );
        adapter.notifyItemRangeInserted(0, productosList.size());
    }

}