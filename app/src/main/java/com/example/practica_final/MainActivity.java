package com.example.practica_final;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Pelicula> peliculas;
    private SQLiteManager sqLiteManager;
    private ViewGroup mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setear layout
        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

        // floating action button
        initFab(this);

        // recuperar instancia de SQLiteManager
        // y todas las peliculas de base de datos
        sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        peliculas = sqLiteManager.getPeliculas();

        // añade al layout los elementos CardPelicula
        for(Pelicula pelicula : peliculas)
        {
            CardPelicula cardPelicula = new CardPelicula(this, pelicula);
            setOnClickListener(cardPelicula);
            mainLayout.addView(cardPelicula);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // al volver de la actividad InsertarPeliculaActivity
        // recupera las peliculas de base de datos
        ArrayList<Pelicula> nuevasPeliculas = sqLiteManager.getPeliculas();

        // cmprueba si se ha insertado una pelicula
        if((nuevasPeliculas.size() - peliculas.size()) > 0)
        {
            // recupera la ultima pelicula e inserta en el layout
            Pelicula nuevaPelicula = nuevasPeliculas.get(nuevasPeliculas.size() - 1);
            CardPelicula cardPelicula = new CardPelicula(this, nuevaPelicula);
            setOnClickListener(cardPelicula);
            mainLayout.addView(cardPelicula);

            // actualiza el array de peliculas
            peliculas = nuevasPeliculas;
        }
    }

    public void initFab(Context context)
    {
        FloatingActionButton nuevaPeliculaButton = findViewById(R.id.fab);

        // al pulsar, inicia InsertarPeliculaActivity
        nuevaPeliculaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("PrivateResource")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InsertarPeliculaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setOnClickListener(CardPelicula cardPelicula)
    {
        // al pulsar cada tarjeta, pide confirmacion
        // antes de eliminarla de la vista y base de datos
        cardPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.titulo_alerta_eliminar);
                builder.setMessage(getResources().getString(R.string.confirmacion_eliminacion_pelicula) + " \"" + cardPelicula.getPelicula().getTitulo() + "\"?");

                // no
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

                // si
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        // elimina de la vista, elimina de base de datos
                        // y actualiza la lista de peliculas en base a
                        // lo que exista en base de datos
                        mainLayout.removeView(cardPelicula);
                        sqLiteManager.eliminarPelicula(cardPelicula.getPelicula());
                        peliculas = sqLiteManager.getPeliculas();
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
    }
}