package com.example.practica_final;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Pelicula {

    private Long id;
    private String titulo;
    private Date fechaVisionado;
    private String ciudad;
    private String actorPrincipal;
    private String imgURL;

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat formatoDate = new SimpleDateFormat("dd-MM-yyyy");

    public Pelicula(String titulo, Date fechaVisionado, String ciudad, String imgURL, String actorPrincipal) {
        this.id = null;
        this.titulo = titulo;
        this.fechaVisionado = fechaVisionado;
        this.ciudad = ciudad;
        this.imgURL = imgURL;
        this.actorPrincipal = actorPrincipal;
    }

    public Pelicula(Long id, String titulo, Date fechaVisionado, String ciudad, String imgURL, String actorPrincipal) {
        this.id = id;
        this.titulo = titulo;
        this.fechaVisionado = fechaVisionado;
        this.ciudad = ciudad;
        this.imgURL = imgURL;
        this.actorPrincipal = actorPrincipal;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getActorPrincipal() {
        return actorPrincipal;
    }

    public void setActorPrincipal(String actorPrincipal) {
        this.actorPrincipal = actorPrincipal;
    }

    public static SimpleDateFormat getFormatoDate() {
        return formatoDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaVisionado() {
        return fechaVisionado;
    }

    public void setFechaVisionado(Date fechaVisionado) {
        this.fechaVisionado = fechaVisionado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getFormatedDate()
    {
        if(fechaVisionado == null)
        {
            return "";
        }

        return formatoDate.format(fechaVisionado);
    }
}
