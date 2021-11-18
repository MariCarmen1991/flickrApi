package com.mcarmen.flickrapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FotosGuardas extends AppCompatActivity {
    ArrayList<Url> fotosFav;
    RecyclerView recyclerView;
    GridLayoutManager galeria;
    photoAdapater adapter;
    SQLiteOpenHelper dbh;
    SQLiteDatabase db;
    Url urlFav;
    Button btnSalir;
    Button btnSalirEliminar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_guardas);
        fotosFav=new ArrayList<Url>();
        dbh= new DbHelper(this,"BaseDatosUrl", null,1);
        btnSalir=findViewById(R.id.btn_salir);
        btnSalirEliminar=findViewById(R.id.btn_salir_eliminar);

        leerBasedeDatos();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED,null);
                finish();
            }
        });


        btnSalirEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db=dbh.getWritableDatabase();
                getApplicationContext().deleteDatabase("BaseDatosUrl");
                db.close();

                Intent devuelveIntent = new Intent();
                devuelveIntent.putExtra("estadoFotos", true);
                setResult(RESULT_OK,devuelveIntent);
                finish();
            }
        });

        recycler(fotosFav);

        //for de comprobación
        for(int i=0; i<fotosFav.size(); i++){
            Log.d("MARICARMEN", "ARRAY RECYCLER ... "+fotosFav.get(i).getUrl());
        }

    }

    //leer base de datos y meter fotos en el array para cargar el recyclerview
    public void leerBasedeDatos(){

        db=dbh.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT url FROM direccion", null);
        Cursor d=db.rawQuery("SELECT nombre FROM direccion", null);

        if(c.moveToFirst()){
            do{
                urlFav=new Url();
                urlFav.setUrl(c.getString(0));
                Log.d("MARICARMEN", "FOTOS GUARDADAS CURSOR ... "+c.getString(0));
                fotosFav.add(urlFav);
            }
            while(c.moveToNext());
        }
        db.close();
    }




   private void recycler(ArrayList<Url> listaDeDirecciones){

        recyclerView = findViewById(R.id.recyclerFotos);
        recyclerView.setHasFixedSize(true);
        galeria= new GridLayoutManager(FotosGuardas.this, 2);
        recyclerView.setLayoutManager(galeria);
        adapter= new photoAdapater(listaDeDirecciones, FotosGuardas.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}