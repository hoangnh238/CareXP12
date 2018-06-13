package com.example.hoang_000.carexp1.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by hoang_000 on 10/06/2018.
 */

public class RetrofitScalarClient {
    private static Retrofit retrofit=null;


    public static  Retrofit getScalarClient(String baseUrl){
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
