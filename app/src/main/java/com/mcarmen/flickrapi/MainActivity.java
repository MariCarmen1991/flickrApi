package com.mcarmen.flickrapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static Url url;
    private static ArrayList<Url> urls;
    public static final String API_BASE_URL="https://api.flickr.com/services/";
    private static SQLiteOpenHelper dbh;
    private static  SQLiteDatabase db;
    private static int num=0;
    private static String direccion;
    RecyclerView recyclerView;
    Toolbar bar;
    Boolean eliminarFotos;
    Boolean fotosEliminadas;
    GridLayoutManager galeria;
    Retrofit retrofit;
    Button btn;
    EditText cajaBuscar;
    photoAdapater adapter;
    PhotoObjeto photoLists;
    String search="";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FIND
        btn=findViewById(R.id.button);
        cajaBuscar=findViewById(R.id.caja_buscar);
        bar=findViewById(R.id.toolbar);

        //INICIALIZACIÓN
        urls=new ArrayList<Url>();
        dbh= new DbHelper(this,"BaseDatosUrl", null,1);

        //ACCIONES
        setSupportActionBar(bar);
        setTitle("TUS FOTOS");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MARICARMEN","detalle "+cajaBuscar.getText().toString());

                if(cajaBuscar.getText().toString().isEmpty()){
                    Toast t=Toast.makeText(MainActivity.this, "NO HAS ESCRITO NINGUN TEXTO", Toast.LENGTH_SHORT);
                    t.show();
                }
                else{
                 //ASIGNO EL TEXTO DEL EDITEXT A LA VARIABLE SEARCH Y LA METO EN RETROFIT PARA HACER LA BÚSQUEDA EN LA API DE FLIC  KR
                search=cajaBuscar.getText().toString();
                obtenerPhotos(search);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        guardarFavoritos();
        Intent mandaDatos=new Intent(MainActivity.this, FotosGuardas.class);
        mandaDatos.putExtra("estadoFotos",eliminarFotos);
        startActivityForResult(mandaDatos, 2);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {

            fotosEliminadas = data.getExtras().getBoolean("estadoFotos");

            if (requestCode == 1 && resultCode == RESULT_OK) {
                Url nuevaUrl = new Url();
                nuevaUrl = (Url) data.getExtras().getSerializable("UrlDevuelta");
                int posicionRecibida = data.getExtras().getInt("posicionDevuelta");
                urls.set(posicionRecibida, nuevaUrl);
                adapter.notifyItemChanged(posicionRecibida);
            }

            if (requestCode == 2 && resultCode == RESULT_OK) {
                if (fotosEliminadas) {
                    for (int i = 0; i < urls.size(); i++) {
                        urls.get(i).setFavorito(false); //si  el usuario elimina todos los datos a la vuelta reviso el array list y elimino los likes
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
        else{
            adapter.notifyDataSetChanged();
        }

    }


    private void obtenerPhotos(String búsqueda){

        retrofit= new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FlickrApi flickrApi= retrofit.create(FlickrApi.class);
        Call<PhotoObjeto> call;

        call= flickrApi.getPhotos(búsqueda);

        call.enqueue(new Callback<PhotoObjeto>() {
            @Override
            public void onResponse(Call<PhotoObjeto> call, Response<PhotoObjeto> response) {
                if(!response.isSuccessful()){
                    return;
                }
                   try {
                       photoLists = response.body(); //photoObjeto
                       generarUrl(photoLists);
                       recycler(urls);
                   }
                   catch(Exception e){
                       Log.d("MARICARMEN", "ERROR  " + e);
                   }
            }

            @Override
            public void onFailure(Call<PhotoObjeto> call, Throwable t) {
                Log.d("MARICARMEN", t.getMessage());
            }
        });
    }



    public static ArrayList<Url> generarUrl(PhotoObjeto objeto){

        final String PHOTO_BASE_URL="https://live.staticflickr.com/";
        //ESTRUCTURA -- https://live.staticflickr.com/{server-id}/{id}_{secret}.jpg

        for (int i=0; i<objeto.getPhotos().getPhoto().size(); i++){
            //guardar en un objeto las urls a partir de los datos
            url = new Url();
            direccion="";
            String id= objeto.getPhotos().getPhoto().get(i).getId();
            String server= objeto.getPhotos().getPhoto().get(i).getServer();
            String secret=objeto.getPhotos().getPhoto().get(i).getSecret();
            String titulo=objeto.getPhotos().getPhoto().get(i).getTitle();

            direccion=PHOTO_BASE_URL+""+server+"/"+id+"_"+secret;
            url.setUrl(direccion);
            url.setNombre(titulo);
            num=i;
            urls.add(url);
        }
        return urls;
    }

    public void recycler(ArrayList<Url> listaDeDirecciones){

        recyclerView = findViewById(R.id.recyclerPhotos);
        recyclerView.setHasFixedSize(true);
        galeria= new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(galeria);
        adapter= new photoAdapater(listaDeDirecciones, MainActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =recyclerView.getChildAdapterPosition(v);
                Intent iDatos=new Intent(MainActivity.this, VistaAmpliada.class);
                iDatos.putExtra("infoFoto", listaDeDirecciones.get(position));
                iDatos.putExtra("posicion",position);
                startActivityForResult(iDatos,1);
            }
        });

    }


//BASE DE DATOS SQLITE

    public static Boolean guardarFavoritos() {
        Boolean datosGuardados = false;

        db = dbh.getWritableDatabase();//abrir base de datos guardar cosis

        for (int i = 0; i < urls.size(); i++){
            String[] args= new String[]{urls.get(i).getUrl()};
            Cursor cursor = db.rawQuery( "SELECT url FROM direccion WHERE url=?", args);

            if (cursor.getCount() == 0) {
                if(urls.get(i).getFavorito()) {
                    db.execSQL("INSERT INTO direccion(nombre, url, favorito) VALUES('" + ""
                            + i + "', '"
                            + urls.get(i).getUrl() + "','" //captura url
                            + urls.get(i).getFavorito() + "')"); //captura like
                }
            }
        }
        db.close();

        return datosGuardados;
    }

    public void leerBasedeDatos(){
        db=dbh.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT url FROM direccion", null);
        if(c.moveToFirst()){
            do{
                Log.d("MARICARMEN", "DATOS LEÍDOS  "+c.getString(0));
            }
            while(c.moveToNext());
        }
        db.close();
    }


}