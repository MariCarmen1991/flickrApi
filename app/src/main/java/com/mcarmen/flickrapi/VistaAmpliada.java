package com.mcarmen.flickrapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class VistaAmpliada extends AppCompatActivity {
    private static SQLiteDatabase db;
    private static SQLiteOpenHelper dbh;
    private static Url urlRecibidia;
    TextView titulo;
    ImageView foto;
    ImageView corazonVacio;
    Boolean isPressed;
    Drawable corazon;
    ImageButton btnVolver;
    private static int posicionRecibida=0;
    Drawable corazonPintado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_ampliada);
        titulo=findViewById(R.id.titulo_caja);
        foto=findViewById(R.id.foto_ampliada);
        btnVolver=findViewById(R.id.btn_volver);

        corazonVacio=(ImageView) findViewById(R.id.empty_heart);
        corazon= AppCompatResources.getDrawable(this, R.drawable.ic_favorite_black_24dp);
        corazonPintado=DrawableCompat.wrap(corazon);
        isPressed=false;
        urlRecibidia=new Url();

        dbh= new DbHelper(this,"BaseDatosUrl", null,1);


        recibirIntent();
        estadoFavorito();
        isPressed=true;




        corazonVacio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isPressed && !urlRecibidia.getFavorito()){
                    DrawableCompat.setTint(corazonPintado, Color.RED);
                    corazonVacio.setImageDrawable(corazonPintado);
                    urlRecibidia.setFavorito(true);
                    guardarFoto();

                    Log.d("MARICARMEN", "LLENO");
                }
                else {
                    corazonVacio.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Log.d("MARICARMEN", "VACÍO");
                    urlRecibidia.setFavorito(false);
                    eliminarFoto();
                }
                isPressed=!isPressed;
            }
        });


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devuelveIntent();
                finish();
            }
        });


}

    public void recibirIntent(){

        urlRecibidia= (Url) getIntent().getExtras().getSerializable("infoFoto");
        posicionRecibida=getIntent().getExtras().getInt("posicion");
        Picasso.get().load(urlRecibidia.getUrl()+"_b.jpg").into(foto); //extensión _b.jpg se añade a la url para ampliar el tamaño de la foto
        titulo.setText(urlRecibidia.getNombre());
        Log.d("MARICARMEN", "posicion"+ posicionRecibida);


    }


    public void devuelveIntent(){
        Intent devuelveDatos=new Intent();
        devuelveDatos.putExtra("UrlDevuelta",urlRecibidia );
        devuelveDatos.putExtra("posicionDevuelta", posicionRecibida);
        setResult(RESULT_OK, devuelveDatos);
    }


    public void estadoFavorito(){


        if(urlRecibidia.getFavorito()){
            DrawableCompat.setTint(corazonPintado, Color.RED);
            corazonVacio.setImageDrawable(corazonPintado);
        }
        else {
            corazonVacio.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }



    public static Boolean guardarFoto() {
        Boolean datosGuardados = false;

        db = dbh.getWritableDatabase();//abrir base de datos guardar cosis
        db.execSQL("INSERT INTO direccion(nombre, url, favorito) VALUES('" + ""
                +posicionRecibida+ "', '"
                + urlRecibidia.getUrl() + "','"
                + urlRecibidia.getFavorito() + "')");

        db.close();

        return datosGuardados=true;
    }

    public void eliminarFoto(){
        final String TABLE_NAME="direccion";
        final String URL_COLUMN_NAME="URL";

        db = dbh.getWritableDatabase();//abrir base de datos guardar cosis
        String borrarUrl=urlRecibidia.getUrl();

        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + URL_COLUMN_NAME + "= '" + borrarUrl + "'");
        db.close();

    }

    public void leerFoto(){
        db=dbh.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT url FROM direccion", null);
        if(c.moveToFirst()){
            do{
                Log.d("MARICARMEN", "ESTA FOTO SE HA GUARDADO  "+c.getString(0));
            }
            while(c.moveToNext());
        }
        db.close();
    }


    @Override
    protected void onDestroy() {
        Log.d("MARICARMEN", "lololo  "+urlRecibidia.getUrl());

        leerFoto();
        super.onDestroy();
    }
}
