package com.example.hoang_000.carexp1.Remote;

/**
 * Created by hoang_000 on 24/03/2018.
 */

import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by hoang_000 on 08/03/2018.
 */

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);
    @GET
    Call<PlaceDetail> getDetailPlaces(@Url String url);
}