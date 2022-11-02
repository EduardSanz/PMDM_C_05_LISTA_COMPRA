package com.cieep.a05_ejercicio_lista_compra;

import android.content.DialogInterface;
import android.os.Bundle;

import com.cieep.a05_ejercicio_lista_compra.modelos.Producto;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import com.cieep.a05_ejercicio_lista_compra.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private ArrayList<Producto> productosList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        productosList = new ArrayList<>();


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createProducto().show();
            }
        });
    }

    private AlertDialog createProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Producto a la cesta");
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

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtNombre.getText().toString().isEmpty() && !txtCantidad.getText().toString().isEmpty() &&
                    !txtPrecio.getText().toString().isEmpty()) {
                    Producto producto = new Producto(
                                                txtNombre.getText().toString(),
                                                Integer.parseInt(txtCantidad.getText().toString()),
                                                Float.parseFloat(txtPrecio.getText().toString())
                                                );
                    productosList.add(producto);
                }
                else {
                    Toast.makeText(MainActivity.this, "Faltan Datos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return builder.create();
    }
}