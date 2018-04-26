package com.example.hoang_000.carexp1;

/**
 * Created by hoang_000 on 24/03/2018.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.Result;
import com.example.hoang_000.carexp1.Model.Results;

import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.Model2.User;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
import com.example.hoang_000.carexp1.Remote.RetrofitClient;



public class Common {
    public static final String INTENT_PLACE_ID="placeRatingID";
    public static final String INTENT_PLACE_ID_FAV="placeIDfav";
    public static final String INTENT_USER_ID_FAV="userIDFav";

    public static String placeId="";
    public static User currentUser;
    public static String driverId = "";
    public static Results currentResult;
    public static Result currentResult2;
    public static Rating ratings;
    private static final String GOOGLE_MAPS_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService() {
        return RetrofitClient.getClient(GOOGLE_MAPS_URL).create(IGoogleAPIService.class);
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info!=null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return  true;
                }
            }
        }
        return false;
    }
}