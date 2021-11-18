package com.mcarmen.flickrapi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class photoAdapater extends RecyclerView.Adapter<photoAdapater.ViewHolder> implements View.OnClickListener {

    ArrayList<Url> listPhoto;
    private LayoutInflater mInflater;
    private Context context;
    private View.OnClickListener listener;

    public photoAdapater(ArrayList<Url> itemlist, Context context) {
        this.listPhoto = itemlist;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public photoAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=mInflater.inflate(R.layout.list_photo, null);
        v.setOnClickListener(this);
        return new photoAdapater.ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull photoAdapater.ViewHolder holder, int position) {

        holder.bindData(listPhoto.get(position));
        Picasso.get()
                .load(Uri.parse(listPhoto.get(position).getUrl()+".jpg")) // internet path
                .placeholder(R.mipmap.ic_launcher)  // preload
                .error(R.mipmap.ic_launcher_round)        // load error
                .into(holder.imagenFlickr);

        holder.corazonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listPhoto.get(position).getFavorito()){
                    listPhoto.get(position).setFavorito(false);
                    holder.corazonLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);


                }
                else{
                    listPhoto.get(position).setFavorito(true);
                    holder.corazonLike.setImageResource(R.drawable.ic_favorite_black_24dp);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPhoto.size();
    }


    public void setOnClickListener(View.OnClickListener listener){

        this.listener=listener;
    }
    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }

    }




    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagenFlickr, corazonLike;
        TextView titulo;

        ViewHolder(View itemView){
            super(itemView);
            imagenFlickr=itemView.findViewById(R.id.imagen_id);
            titulo=itemView.findViewById(R.id.titulo_id);
            corazonLike=itemView.findViewById(R.id.corazon_vacio);
        }
        public void bindData(final Url item){
            titulo.setText(item.getNombre());
            if(item.favorito){
                corazonLike.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
            else {
                corazonLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }



        }
    }






}
