package com.example.hoang_000.carexp1;

/**
 * Created by hoang_000 on 24/03/2018.
 */
import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.Results;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
import com.example.hoang_000.carexp1.Remote.RetrofitClient;

/**
 * Created by hoang_000 on 08/03/2018.
 */

public class Common {
    public static Results currentResult;
    private static final String GOOGLE_MAPS_URL = "https://maps.googleapis.com/";
    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getClient(GOOGLE_MAPS_URL).create(IGoogleAPIService.class);
    }
}