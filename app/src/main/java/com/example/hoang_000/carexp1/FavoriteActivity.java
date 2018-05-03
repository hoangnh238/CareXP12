package com.example.hoang_000.carexp1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.PlaceDetail;
import com.example.hoang_000.carexp1.Model2.Favorites;
import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
import com.example.hoang_000.carexp1.adapter.FavoriteViewHolder;
import com.example.hoang_000.carexp1.adapter.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * hiển thị list các địa điểm là người dùng yêu thích tại menu(địa điểm yêu thích) và chỉ đường đến địa điểm đó
 * ví dụ: tên địa điểm A
 * Địa chỉ của A
 * button:direction
 */
public class FavoriteActivity extends AppCompatActivity {

    MyPlaces currentPlace;
    SupportMapFragment mapFragment;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    String userID_fav = "";
    RecyclerView recyclerViewfav;
    RecyclerView.LayoutManager layoutManagerFav;
    FirebaseDatabase databasefav;
    DatabaseReference favoritedb;
    SwipeRefreshLayout mSwipeRefreshLayoutfav;
    IGoogleAPIService mService = Common.getGoogleAPIService();
    FirebaseRecyclerAdapter<Favorites, FavoriteViewHolder> adapterfav;
    public String url = "";

    @Override
    protected void attachBaseContext(Context newBase) {

        // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapterfav != null) {
            adapterfav.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);


        userID_fav = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //firebase
        databasefav = FirebaseDatabase.getInstance();
        favoritedb = databasefav.getReference("FavoriteList");
        recyclerViewfav = (RecyclerView) findViewById(R.id.recy_fav);
        layoutManagerFav = new LinearLayoutManager(this);
        recyclerViewfav.setLayoutManager(layoutManagerFav);


        //swipe layout
        mSwipeRefreshLayoutfav = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_fav);
        mSwipeRefreshLayoutfav.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null) {

                    Query query = favoritedb.orderByChild("userIDFav").equalTo(userID_fav);
                    FirebaseRecyclerOptions<Favorites> options = new FirebaseRecyclerOptions.Builder<Favorites>()
                            .setQuery(query, Favorites.class)
                            .build();
                    adapterfav = new FirebaseRecyclerAdapter<Favorites, FavoriteViewHolder>(options) {

                        @Override
                        protected void onBindViewHolder(@NonNull final FavoriteViewHolder holder, final int position, @NonNull Favorites model) {

                            holder.txtNameAddrFav.setText(model.getNameofplacefav());
                            holder.txtAddrFav.setText(model.getAddressFav());

                            final String place = model.getPlaceIDfav();
                            holder.url = getPlaceDetailUrl(place);
                            holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(holder.url));
                                 /*  Common.currentResult = currentPlace.getResults()[position];
                                    Intent intent = new Intent(FavoriteActivity.this, ViewPlace.class);
                                    intent.putExtra("placeid", place);
*/

                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_fav, parent, false);
                            return new FavoriteViewHolder(view);
                        }
                    };
                    loadFavoriteList();


                }


            }

        });


        mSwipeRefreshLayoutfav.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayoutfav.setRefreshing(true);
                if (getIntent() != null) {

                    Query query = favoritedb.orderByChild("userIDFav").equalTo(userID_fav);
                    FirebaseRecyclerOptions<Favorites> options = new FirebaseRecyclerOptions.Builder<Favorites>()
                            .setQuery(query, Favorites.class)
                            .build();
                    adapterfav = new FirebaseRecyclerAdapter<Favorites, FavoriteViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final FavoriteViewHolder holder, final int position, @NonNull final Favorites model) {
                            holder.txtNameAddrFav.setText(model.getNameofplacefav());

                            holder.txtAddrFav.setText(model.getAddressFav());

                            holder.url = getPlaceDetailUrl(model.getPlaceIDfav());


                            holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    /*Common.currentResult = currentPlace.getResults()[position];
                                    Intent intent = new Intent(FavoriteActivity.this, ViewPlace.class);
                                    intent.putExtra("placeid", model.getPlaceIDfav());*/

                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_fav, parent, false);
                            return new FavoriteViewHolder(view);
                        }
                    };
                    loadFavoriteList();


                }


            }
        });

    }


    /**
     * lấy url của địa điểm đó trên google map ví dụ : https://www.google.com/maps?cid=4056373543307805169
     *
     * @param place_id là id của địa điểm đó ví dụ: ChIJ-8yIF1SrNTER8FAE1f87tk0
     * @return
     */
    private String getPlaceDetailUrl(String place_id) {
        //url = "";

        mService.getDetailPlaces(getPlaceUrl(place_id))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        PlaceDetail mPlace = response.body();
                        url = mPlace.getResult().getUrl();
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });
        return url;
    }

    /**
     * lấy url là là file json của địa điểm có id là place_id
     *
     * @param place_id là id của địa điểm đó
     * @return
     */
    private String getPlaceUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");

        url.append("?placeid=" + place_id);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }


    private void loadFavoriteList() {
        adapterfav.startListening();

        recyclerViewfav.setAdapter(adapterfav);
        mSwipeRefreshLayoutfav.setRefreshing(false);
    }
}



