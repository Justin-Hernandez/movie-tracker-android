package com.example.practica_final;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;

    private static final String nombreDB = "PeliculasDB";
    private static final String nombreTabla = "Peliculas";

    private static final String campoId = "id";
    private static final String campoTitulo = "titulo";
    private static final String campoFechaVisionado = "fecha_visionado";
    private static final String campoCiudad = "ciudad";
    private static final String campoImageURL = "img_url";
    private static final String campoActor = "actor_principal";

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat formatoDate = new SimpleDateFormat("dd-MM-yyyy");

    public SQLiteManager(Context context) {
        super(context, nombreDB, null, 1);
    }

    public static SQLiteManager instanceOfDatabase(Context context)
    {
        if(sqLiteManager == null)
        {
            sqLiteManager = new SQLiteManager(context);
        }

        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // crea la tabla si no existe
        String initDB = "CREATE TABLE " +
                nombreTabla +
                "(" +
                campoId +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                campoTitulo +
                " TEXT, " +
                campoFechaVisionado +
                " TEXT, " +
                campoCiudad +
                " TEXT, " +
                campoImageURL +
                " TEXT, " +
                campoActor +
                " TEXT)"
                ;
        sqLiteDatabase.execSQL(initDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // nada
    }

    public void insertarPelicula(Pelicula pelicula)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(campoTitulo, pelicula.getTitulo());
        contentValues.put(campoFechaVisionado, pelicula.getFormatedDate());
        contentValues.put(campoCiudad, pelicula.getCiudad());
        contentValues.put(campoImageURL, pelicula.getImgURL());
        contentValues.put(campoActor, pelicula.getActorPrincipal());

        // devuelve el id con el que se ha insertado
        Long insertedId = sqLiteDatabase.insert(nombreTabla, null, contentValues);

        // asignalo a la pelicula
        pelicula.setId(insertedId);
    }

    public void eliminarPelicula(Pelicula pelicula)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(nombreTabla, "id=?", new String[]{String.valueOf(pelicula.getId())});
    }

    // auxiliar para limpiar el contenido de base de datos
    public void eliminarPeliculas()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + nombreTabla);

        // crea la tabla otra vez
        String initDB = "CREATE TABLE " +
                nombreTabla +
                "(" +
                campoId +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                campoTitulo +
                " TEXT, " +
                campoFechaVisionado +
                " TEXT, " +
                campoCiudad +
                " TEXT, " +
                campoImageURL +
                " TEXT, " +
                campoActor +
                " TEXT)"
                ;
        sqLiteDatabase.execSQL(initDB);
    }

    // recupera todas las peliculas de base de datos
    public ArrayList<Pelicula> getPeliculas()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ArrayList<Pelicula> peliculas = new ArrayList<>();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + nombreTabla, null))
        {
            if(result.getCount() != 0)
            {
                while(result.moveToNext())
                {
                    Long id = result.getLong(0);
                    String titulo = result.getString(1);
                    String ciudad = result.getString(3);
                    String imgURL = result.getString(4);
                    String actorPrincipal = result.getString(5);

                    Date fechaVisionado = null;

                    try
                    {
                        fechaVisionado = formatoDate.parse(result.getString(2));

                    }catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Pelicula pelicula = new Pelicula(id, titulo, fechaVisionado, ciudad, imgURL, actorPrincipal);

                    peliculas.add(pelicula);
                }
            }
        }

        return peliculas;
    }
}
