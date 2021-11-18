package com.mcarmen.flickrapi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FlickrApi {

//https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=467ae9bf5926d10c684d4f47d7da6434&text=cats&format=json
    public static final String API_KEY="f7ebef53fafdb806c47eba8fbb06d09b";
    public static final String SECRET="3afc3c8e8cb230bd";
    public static final String METHOD="flickr.photos.search";
    public static final String FORMAT="json&nojsoncallback=1";



    @GET("rest/?method="+METHOD+"&api_key="+API_KEY+"&format="+FORMAT)

    Call<PhotoObjeto> getPhotos(@Query("text") String texto);

}
