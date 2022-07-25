package com.example.practica_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class InsertarPeliculaActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private final OkHttpClient client = new OkHttpClient();

    private TextInputLayout buscadorTitulo;
    private TextInputLayout fechaVisualizacion;
    private TextInputLayout inputCiudad;
    private ViewGroup mainLayout;

    private Date selectedDate;

    ProgressBar barraProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_pelicula);

        barraProgreso = new ProgressBar(this);
        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        inputCiudad = findViewById(R.id.inputCiudad);

        initBackButton();
        initDatePicker();
        mostrarDatepickerOnClick();
        initBuscadorPeliculas();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                fechaVisualizacion.getEditText().setText(String.format("%d/%d/%d", i2, i1, i));

                LocalDate date = LocalDate.of(i, i1 + 1, i2);
                selectedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        };

        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                this,
                R.style.datepicker,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private void mostrarDatepickerOnClick()
    {
        fechaVisualizacion = findViewById(R.id.fechaVisualizacion);

        fechaVisualizacion.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void initBackButton()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initBuscadorPeliculas()
    {
        buscadorTitulo = findViewById(R.id.bucadorTitulo);

        buscadorTitulo.setErrorIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mainLayout.removeAllViews();
                    mainLayout.addView(barraProgreso);
                    getPeliculas(buscadorTitulo.getEditText().getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buscadorTitulo.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mainLayout.removeAllViews();
                    mainLayout.addView(barraProgreso);
                    getPeliculas(buscadorTitulo.getEditText().getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getPeliculas(String nombre) throws Exception
    {
        Activity activity = this;

        // crear nueva url con los parametros de query:
        // apikey ---> autenticacion
        // s ---> devuelve peliculas cuyo nombre contenga el valor de este parametro de query
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.omdbapi.com/").newBuilder();
        urlBuilder.addQueryParameter("apikey", "346322e8");
        urlBuilder.addQueryParameter("s", nombre);
        urlBuilder.addQueryParameter("type", "movie");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buscadorTitulo.setError(getResources().getString(R.string.error_red));
                        buscadorTitulo.clearFocus();
                        mainLayout.removeView(barraProgreso);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {

                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    try {
                        JSONArray peliculas = new JSONObject(responseBody.string()).getJSONArray("Search");
                        ArrayList<Pelicula> listaPeliculas = new ArrayList<>();

                        for(int i=0; i < peliculas.length(); i++)
                        {
                            JSONObject objPelicula = peliculas.getJSONObject(i);

                            String id = objPelicula.getString("imdbID");
                            String titulo = objPelicula.getString("Title");
                            String imgURL = objPelicula.getString("Poster");

                            Pelicula pelicula = new Pelicula(titulo, null, "", imgURL, getActorPrincipal(activity, id));
                            listaPeliculas.add(pelicula);
                        }

                        // musetra los resultados
                        mostrarCardPeliculas(activity, listaPeliculas);

                    } catch (JSONException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buscadorTitulo.setError(getResources().getString(R.string.pelicula_no_encontrada));
                                buscadorTitulo.clearFocus();
                                mainLayout.removeView(barraProgreso);
                            }
                        });
                    }
                }
            }
        });
    }

    private String getActorPrincipal(Activity mainActivity, String id) throws IOException
    {
        String actor = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.omdbapi.com/").newBuilder();
        urlBuilder.addQueryParameter("apikey", "346322e8");
        urlBuilder.addQueryParameter("i", id);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        try (ResponseBody responseBody = response.body()) {

            JSONObject respuesta = new JSONObject(responseBody.string());

            // el campo Actors contiene una lista de lo actores
            // hago split(",") y recupero el primero de ellos
            actor = respuesta.getString("Actors").split(",")[0];

        }catch(Exception e)
        {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buscadorTitulo.setError(getResources().getString(R.string.error_red));
                    buscadorTitulo.clearFocus();
                    mainLayout.removeView(barraProgreso);
                }
            });
        }

        return actor;
    }

    private void mostrarCardPeliculas(Activity mainActivity, ArrayList<Pelicula> peliculas)
    {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainLayout.removeAllViews();

                for(Pelicula pelicula : peliculas)
                {
                    CardPelicula cardPelicula = new CardPelicula(mainActivity.getBaseContext(), pelicula);

                    // listener para cada tarjeta de pelicula
                    // pide confirmacion antes de registrar la pelicula
                    cardPelicula.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

                            builder.setTitle(R.string.titulo_alerta_registrar);
                            builder.setMessage(getResources().getString(R.string.confirmacion_registro_pelicula) + " \"" + cardPelicula.getPelicula().getTitulo() + "\"?");

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
                                    registrarPelicula(cardPelicula.getPelicula());
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                    });

                    mainLayout.addView(cardPelicula);
                }

                buscadorTitulo.setErrorEnabled(false);
            }
        });
    }

    private void registrarPelicula(Pelicula pelicula)
    {
        boolean datosCorrectos = true;

        // comprueba si se ha insertado la fecha
        if(selectedDate == null)
        {
            fechaVisualizacion.setError(getResources().getString(R.string.requerido));
            fechaVisualizacion.clearFocus();
            datosCorrectos = false;
        }else
        {
            fechaVisualizacion.setErrorEnabled(false);
        }

        // comprueba si se ha insertado la ciudad
        if(inputCiudad.getEditText().getText().toString().isEmpty())
        {
            inputCiudad.setError(getResources().getString(R.string.requerido));
            inputCiudad.clearFocus();
            datosCorrectos = false;
        }else
        {
            inputCiudad.setErrorEnabled(false);
        }

        if(datosCorrectos)
        {
            // inserta la nueva pelicula
            SQLiteManager manager = SQLiteManager.instanceOfDatabase(this);

            // setea la ciudad y la fecha de visionado
            pelicula.setCiudad(inputCiudad.getEditText().getText().toString());
            pelicula.setFechaVisionado(selectedDate);

            // inserta en base de datos
            manager.insertarPelicula(pelicula);

            // acaba la actividad
            this.finish();
        }
    }
}