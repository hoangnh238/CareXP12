package com.example.hoang_000.carexp1.Remote;

/**
 * Created by hoang_000 on 24/03/2018.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hoang_000 on 08/03/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit=null;
    public static  Retrofit getClient(String baseUrl){
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}