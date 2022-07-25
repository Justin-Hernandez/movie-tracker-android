package com.example.practica_final;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

@SuppressLint("ViewConstructor")
public class CardPelicula extends LinearLayout {

    private final Pelicula pelicula;

    public CardPelicula(Context context, Pelicula asignatura) {
        super(context);

        this.pelicula = asignatura;

        init(context);
    }

    @SuppressLint("DefaultLocale")
    private void init(Context context)
    {
        inflate(context, R.layout.card_pelicula, this);

        TextView titulo = findViewById(R.id.tituloPelicula);
        TextView fecha = findViewById(R.id.fechaVisionado);
        TextView ciudad = findViewById(R.id.ciudad);
        TextView actorPrincipal = findViewById(R.id.actorPrincipal);

        ImageView portada = findViewById(R.id.portadaImagen);

        titulo.setText(pelicula.getTitulo());
        fecha.setText(pelicula.getFormatedDate());
        ciudad.setText(pelicula.getCiudad());
        actorPrincipal.setText(pelicula.getActorPrincipal());

        // uso de la biblioteca Glide para cargar la imagen en el
        // imageView a partir de una url devuelta por el api de omdbapi.com
        Glide.with(this).load(pelicula.getImgURL()).error(R.drawable.no_poster_foreground).into(portada);
    }

    public Pelicula getPelicula() {
        return pelicula;
    }
}
