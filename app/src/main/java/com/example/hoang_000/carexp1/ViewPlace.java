package com.example.hoang_000.carexp1;

/**
 * Created by hoang_000 on 24/03/2018.
 */

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hoang_000.carexp1.Model.Photos;
import com.example.hoang_000.carexp1.Model.PlaceDetail;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlace extends AppCompatActivity {
    ImageView photo;
    RatingBar ratingBar;
    TextView opening_hours,place_address,place_name;
    Button btnViewOnMap;
    IGoogleAPIService mService;
    PlaceDetail mPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);
        mService = Common.getGoogleAPIService();
        photo= (ImageView)findViewById(R.id.photo);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        place_address=(TextView)findViewById(R.id.place_address);
        place_name=(TextView)findViewById(R.id.place_name);
        opening_hours = (TextView)findViewById(R.id.place_open_hour);
        btnViewOnMap= (Button)findViewById(R.id.btn_show_map);

        //empty all view
        place_name.setText("");
        place_address.setText("");
        opening_hours.setText("");

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(mapIntent);
            }
        });
        //photo
        if (Common.currentResult.getPhotos()!=null && Common.currentResult.getPhotos().length>0)
        {
            Picasso.with(this )
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(),1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
        }
        // rating
        if (Common.currentResult.getRating()!=null && !TextUtils.isEmpty(Common.currentResult.getRating()))
        {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else
        {
            ratingBar.setVisibility(View.GONE);
        }
        //opening hours
        if (Common.currentResult.getOpening_hours()!=null )
        {
            opening_hours.setText("open now : " + Common.currentResult.getOpening_hours().getOpen_now());
        }
        else
        {
            opening_hours.setVisibility(View.GONE);
        }
        //user Service to fetch Adress and name
        mService.getDetailPlaces(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace = response.body();
                        place_address.setText(mPlace.getResult().getFormatted_address());
                        place_name.setText(mPlace.getResult().getName());
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference,int maxWidth) {
        StringBuilder url= new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreference="+photo_reference);
        url.append("&key="+getResources().getString(R.string.browser_key));
        return url.toString();
    }
}
