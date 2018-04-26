package com.example.hoang_000.carexp1.Remote;

/**
 * Created by hoang_000 on 24/03/2018.
 */

import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * gọi đến 2 lớp là Myplaces và PlaceDetail trong package Model
 *getNearByPlaces  url  : https://maps.googleapis.com/maps/api/place/nearbysearch/json?location= &radius= &type= &sensor=false& key=
 * với location là latitude ,longitude
 * radius là bán kính tìm kiếm
 * type là loại địa điểm
 * key là browserkey of googlemap api
 *
 * getDetailPlaces url  : https://maps.googleapis.com/maps/api/place/details/json?placeid= &key=
 * placeid là id của địa điểm trên googlemap
 * key là browserkey of google map api
 */
public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);
//  @GET("place/search/json")
//  Call<MyPlaces> getNearByPlaces(
//          @Query("location") String location,
//          @Query("radius") String radius,
//          @Query("sensor") String sensorFalse,
//          @Query("key") String api_key,
//          @Query("name") String keyWord
 // );
    @GET
    Call<PlaceDetail> getDetailPlaces(@Url String url);
}