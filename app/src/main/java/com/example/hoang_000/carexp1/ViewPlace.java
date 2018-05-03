package com.example.hoang_000.carexp1;

/**
 * Created by hoang_000 on 24/03/2018.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang_000.carexp1.Model.Location1;
import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.Photos;
import com.example.hoang_000.carexp1.Model.PlaceDetail;

import com.example.hoang_000.carexp1.Model.Result;
import com.example.hoang_000.carexp1.Model.Results;
import com.example.hoang_000.carexp1.Model2.Favorites;
import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.Model2.User;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
//import android.app.AlertDialog;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import dmax.dialog.SpotsDialog;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlace extends AppCompatActivity implements RatingDialogListener {
    // public double latitude,longitude;
    ImageView photo;
    GoogleMap mMap;

    TextView txtStars;
    TextView opening_hours, place_address, place_name, place_phone;
    Button btnViewOnMap, btnShowCmt;
    IGoogleAPIService mService;
    PlaceDetail mPlace;
    MyPlaces myPlaces;
    Results mResult;
    Location1 mLocation1;
    Button btnSubmit;
    ImageView imgsms, imgphone, imginformation;
    FirebaseDatabase database;
    DatabaseReference rateDetailRef;
    DatabaseReference placeDetailRef;
    DatabaseReference placeFav;
    String placeid = "";
    String userID_fav = "";
    String placeShowRatingId = "";
    String placeAddressId = "";
    String placeAddressId2 = "";
    String location123;
    FloatingActionButton btnRating;
    RatingBar ratingBar;
    UserHome userHome;
    ImageView imgfav;
    public Button btnDirection2;
    float ratingStars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);


        //load data
        Intent callerIntent = this.getIntent();
        placeShowRatingId = callerIntent.getStringExtra("placeid");
        placeAddressId = callerIntent.getStringExtra("placeid");
        placeAddressId2 = callerIntent.getStringExtra("placeid");

        //init firebase
        database = FirebaseDatabase.getInstance();
        rateDetailRef = database.getReference("RatingBarDetail");
        // driverDetailRef=database.getReference("Users");
        placeDetailRef = database.getReference("PlaceRatingDetail");
        placeFav = database.getReference("FavoriteList");


        //getIntent
        if (getIntent() != null) {
            placeAddressId2 = getIntent().getStringExtra("placeid");

        }
        if (!Common.currentResult.getPlace_id().isEmpty() && !TextUtils.isEmpty(Common.currentResult.getPlace_id()))

        {
            getRatingPlace(placeAddressId2);

        }


        imgfav = (ImageView) findViewById(R.id.img_fav);
        imgsms = (ImageView) findViewById(R.id.img_sms);
        imginformation = (ImageView) findViewById(R.id.img_information);
        imgphone = (ImageView) findViewById(R.id.img_call);
        mService = Common.getGoogleAPIService();
        photo = (ImageView) findViewById(R.id.photo);
        btnShowCmt = (Button) findViewById(R.id.btn_show_comment);
        btnDirection2 = (Button) findViewById(R.id.btn_chiduong);

        place_address = (TextView) findViewById(R.id.place_address);
        place_name = (TextView) findViewById(R.id.place_name);
        place_phone = (TextView) findViewById(R.id.place_phone);
        opening_hours = (TextView) findViewById(R.id.place_open_hour);
        btnViewOnMap = (Button) findViewById(R.id.btn_show_map);

        btnRating = (FloatingActionButton) findViewById(R.id.btn_rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //empty all view
        place_name.setText("");
        place_address.setText("");
        opening_hours.setText("");
        place_phone.setText("");


        //event

        btnShowCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPlace.this, ShowComment.class);
                intent.putExtra(Common.INTENT_PLACE_ID, placeShowRatingId);
                startActivity(intent);
            }
        });
        imgfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFavorite();
            }
        });
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        imginformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getWebsite()));
                startActivity(mapIntent);
            }
        });

        imgsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Sendsms();
            }
        });
        imgphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallGara();
            }
        });
        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(mapIntent);
            }
        });


        //photo
        if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
        }

        // rating
     /*if (Common.currentResult.getRating()!=null && !TextUtils.isEmpty(Common.currentResult.getRating()))
        {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else
        {
            ratingBar.setVisibility(View.GONE);
        }*/


        //opening hours

        if (Common.currentResult.getOpening_hours() != null) {
            opening_hours.setText("Hiện tại đang mở cửa : " + Common.currentResult.getOpening_hours().getOpen_now());
        } else {
            opening_hours.setVisibility(View.GONE);
        }


        //user Service to fetch Address and name
        mService.getDetailPlaces(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace = response.body();
                        place_address.setText(mPlace.getResult().getFormatted_address());
                        place_name.setText(mPlace.getResult().getName());
                        //  place_name.setText(mPlace.getResult().getUrl());
                        place_phone.setText(mPlace.getResult().getFormatted_phone_number());

                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });
    }

    /**
     * thêm địa điểm yêu thích của người dùng vào databse
     */
    private void AddFavorite() {
        final Favorites favorites = new Favorites(mPlace.getResult().getName(), mPlace.getResult().getPlace_id(), mPlace.getResult().getFormatted_address(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        placeFav
                //.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push().setValue(favorites)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        imgfav.setImageResource(R.drawable.heart_black);
                        Toast.makeText(ViewPlace.this, "Đã thêm vào mục yêu thích", Toast.LENGTH_LONG).show();
                    }
                });

    }

    /**
     * gọi đến gara
     */
    private void CallGara() {
        String phone = mPlace.getResult().getFormatted_phone_number();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    /**
     * gửi tin nhắn đến gara kèm theo vị trí của thiết bị
     */
    private void Sendsms() {

        String mes = "Vị trí hiện tại của tôi là : \n  " + new LatLng(UserHome.latitude, UserHome.longitude) + "\n" + "Dòng xe : \n" + "Đời xe : \n";
        Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", mPlace.getResult().getFormatted_phone_number());


        smsIntent.putExtra("sms_body", mes);

        startActivity(smsIntent);
    }

    // đây là đoạn get mà đoạn set cơ
    private void getRatingPlace(String placeAddressId2) {

        Query placeRating = placeDetailRef.orderByChild("placeRatingID").equalTo(placeAddressId2);
        placeRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sum = 0, count = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;

                    ratingBar.setRating(average);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()

                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Rất tệ", "Không tốt lắm", "Tạm ổn", "Khá tốt", "Tuyệt vời"))
                .setDefaultRating(1)
                .setTitle("Đánh giá địa điểm")
                .setDescription("Vui lòng đánh giá sao và bình luận")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Vui lòng viết bình luận tại đây... ")
                .setHintTextColor(R.color.rippleColor)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ViewPlace.this)
                .show();


    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid=" + place_id);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + maxWidth);
        url.append("&photoreference=" + photo_reference);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }

    @Override
    public void onPositiveButtonClicked(int value, final String comments) {
        //get rating and upload to firebase

        final Rating rating = new Rating(FirebaseAuth.getInstance().getCurrentUser().getEmail(), mPlace.getResult().getName(), FirebaseAuth.getInstance().getCurrentUser().getUid()
                , String.valueOf(value), comments, mPlace.getResult().getPlace_id());

        placeDetailRef
                //  .child(mPlace.getResult().getPlace_id())

                .push().setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ViewPlace.this, "THANKS YOU FOR RATING !!!", Toast.LENGTH_LONG).show();
                    }
                });
     /*  placeDetailRef
               .push().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).exists())
                {
                 placeDetailRef.child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).removeValue();

                    placeDetailRef.child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).setValue(rating);

                }
                else
                {
                    placeDetailRef.child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).setValue(rating);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public void onNegativeButtonClicked() {

    }


}
