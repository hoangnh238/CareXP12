package com.example.hoang_000.carexp1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaCasException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang_000.carexp1.Model.MyPlaces;
import com.example.hoang_000.carexp1.Model.PlaceDetail;
import com.example.hoang_000.carexp1.Model.Results;
import com.example.hoang_000.carexp1.Model2.Favorites;
import com.example.hoang_000.carexp1.Model2.User;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
import com.example.hoang_000.carexp1.adapter.FavoriteViewHolder;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private boolean exit = false;
    SupportMapFragment mapFragment;
    //LOCATION
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 8000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300000;
    PlaceDetail mPlace;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    private static int UPDATE_INERVAL = 5000;   //5S
    private static int FATEST_INERVAL = 3000;   //5S
    private static int DISPLACEMENT = 10;   //5S
    public static final int PICK_IMAGE_REQUEST = 9999;
    public static User currentUser = new User();
    public User user2;
    private static final int MY_PERMISSION_CODE = 1000;


    String placeAddressId = "";
    String userID_fav = "";
    public static double latitude, longitude;

    private Marker mMarker;


    DatabaseReference ref;
    GeoFire geoFire;
    Marker mUserMarker;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    IGoogleAPIService mService;
    MyPlaces currentPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init service
        mService = Common.getGoogleAPIService();
        //Yêu cầu truy cập với phiên bản Android từ 6.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_garage:
                        nearByPlace_car_repair("car_repair");
                        break;
                    case R.id.action_gas:
                        nearByPlace_gas_station("gas_station");
                        break;
                    case R.id.action_hospital:
                        nearByPlace_hospital("hospital");
                        break;
                    case R.id.action_carwash:
                        nearByPlace_car_wash("car_wash");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //init firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView txtname = navigationHeaderView.findViewById(R.id.txtUserName);

        CircleImageView imgavatar = navigationHeaderView.findViewById(R.id.image_avatar);

        txtname.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        if (Common.currentUser.getAvatarUrl() != null && !TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
            Picasso.with(this)
                    .load(Common.currentUser.getAvatarUrl())
                    .into(imgavatar);
        }

        //Geo Fire
        ref = FirebaseDatabase.getInstance().getReference("Users");
        geoFire = new GeoFire(ref);
        // setUpLocation();

    }


    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION

            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices2()) {
                buildGoogleApiClien();
                createLocationRequest2();
                disPlayLocation2();

            }
        }
    }

    private void createLocationRequest2() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INERVAL);
        mLocationRequest.setFastestInterval(FATEST_INERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void disPlayLocation2() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));
            //update to firebase
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref = FirebaseDatabase.getInstance().getReference("LocationUsers");

            geoFire = new GeoFire(ref);
            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //add marker
                    if (mUserMarker != null)
                        mUserMarker.remove();
                    mUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(String.format("You"))
                    );

                    //move camera to this option
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                }
            });
            Log.d("HELLO", String.format("Your location was changed: %f/%f", latitude, longitude));
        } else
            Log.d("HELLO", "can not get your location");
    }

    private boolean checkPlayServices2() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(this, "this device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    private void nearByPlace_hospital(final String placeType) {
        mMap.clear();
        String url = getUrl_hospital(latitude, longitude, placeType);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace = response.body(); //assign value  for currentPlace
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLat());

                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(googlePlace.getPlace_id());

                                if (placeType.equals("hospital"))

                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.emergency_ambulance));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                                markerOptions.snippet(String.valueOf(i));//assign index for marker
                                //add marker to map
                                mMap.addMarker(markerOptions);
                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private void nearByPlace_car_wash(final String placeType) {
        mMap.clear();


        String url = getUrl_car_wash(latitude, longitude, placeType);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace = response.body(); //assign value  for currentPlace
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLat());

                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(googlePlace.getPlace_id());

                                if (placeType.equals("car_wash"))
                                    //   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_wash));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                                markerOptions.snippet(String.valueOf(i));//assign index for marker
                                //add marker to map
                                mMap.addMarker(markerOptions);
                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private void nearByPlace_gas_station(final String placeType) {
        mMap.clear();


        String url = getUrl_gas_station(latitude, longitude, placeType);

        mService.getNearByPlaces(url)
//        String inputlocation=mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
//         mService.getNearByPlaces(inputlocation,"5000","false",getString(R.string.google_maps_key),"gas_station")
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace = response.body(); //assign value  for currentPlace
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLat());

                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(googlePlace.getPlace_id());
                                if (placeType.equals("gas_station"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));

                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                                markerOptions.snippet(String.valueOf(i));//assign index for marker
                                //add marker to map
                                mMap.addMarker(markerOptions);
                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    /**
     * Lấy các thông tin của các loại địa điểm : địa điểm rửa xe,gara,bệnh viện,trạm xăng,..
     * sau đó add marker cho các loại địa điểm
     *
     * @param placeType: có 4 loại địa điểm : car_wash,gara,hospial,gas_station
     */
    private void nearByPlace_car_repair(final String placeType) {
        mMap.clear();


        String url = getUrl_car_repair(latitude, longitude, placeType);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace = response.body(); //assign value  for currentPlace
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLat());

                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(googlePlace.getPlace_id());
                                if (placeType.equals("car_repair"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                                    //                                  markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                                else if (placeType.equals("gas_station"))
//                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                                else if (placeType.equals("hospital"))
//                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//                                    //   Common.currentResult.getRating();
//                                else if (placeType.equals("car_wash"))
//                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                                markerOptions.snippet(String.valueOf(i));//assign index for marker
                                //add marker to map
                                mMap.addMarker(markerOptions);
                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    /**
     * Lấy Url dưới dạng file json với browser_key trong res/values/google_maps_api.xml
     * thông tin của các địa điểm hiển thị dưới dạng file json
     *
     * @param latitude  : vĩ độ
     * @param longitude : kinh độ
     * @param placeType : loại địa điểm
     * @return
     */
    private String getUrl_car_repair(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        googlePlacesUrl.append("?location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=false");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        googlePlacesUrl.append("&name=garage");
        Log.d("getUrl_car_repair", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();

    }

    private String getUrl_car_wash(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        googlePlacesUrl.append("?location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=false");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        //googlePlacesUrl.append("&name=car_wash");
        Log.d("getUrl_car_wash", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();

    }

    private String getUrl_hospital(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        googlePlacesUrl.append("?location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=false");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        googlePlacesUrl.append("&name=hospital");
        Log.d("getUrl_hospital", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();

    }

    private String getUrl_gas_station(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        googlePlacesUrl.append("?location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=false");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        googlePlacesUrl.append("&name=xăng");
        Log.d("getUrl_gas_station", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();

    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))

                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else

                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;


        } else
            return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null)
                            buildGoogleApiClien();
                        mMap.setMyLocationEnabled(true);
                    }
                } else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        //     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Nhấn nút trở về một lấn nữa để thoát.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            showCarInfo();
            // Handle the camera action
        } else if (id == R.id.nav_help) {
            showhelp();
        } else if (id == R.id.nav_about) {
            showabout();

        } else if (id == R.id.nav_update_info) {
            showDialogUpdateInfo();

        } else if (id == R.id.nav_change_pwd) {
            showDialogChangePassWord();

        } else if (id == R.id.nav_signout) {
            signOut();
        } else if (id == R.id.nav_hotline) {
            showhotline();

        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(UserHome.this, FavoriteActivity.class);

            startActivity(intent);

        } else if (id == R.id.nav_khuyenmai) {


        } else if (id == R.id.nav_binhluan) {
            Intent intent = new Intent(UserHome.this, UserShowComment.class);

            startActivity(intent);

        } else if (id == R.id.nav_fanpage) {
            intentUrl("https://www.facebook.com/Drive-Tracker-161475257940058/");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * cập nhật thông tin xe cho người dùng và lưu lại rên firebase
     * các thông tin như doi xe,phiên bản,hãng xe,ngày mua,...
     */
    private void showCarInfo() {
        AlertDialog.Builder aleartDialog = new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("Thông tin xe");
        //aleartDialog.setMessage("Vui lòng nhập các thông tin về xe của bạn");

        LayoutInflater inflater = this.getLayoutInflater();
        View layout_car = inflater.inflate(R.layout.layout_car_information, null);
        final MaterialEditText edt_tenxe = (MaterialEditText) layout_car.findViewById(R.id.edt_tenxe);
        final MaterialEditText edtdongxe = (MaterialEditText) layout_car.findViewById(R.id.edtdongxe);
        final MaterialEditText edt_ngay_mua = (MaterialEditText) layout_car.findViewById(R.id.edt_ngay_mua);
        final MaterialEditText edt_mieuta = (MaterialEditText) layout_car.findViewById(R.id.edt_mieuta);
        final MaterialEditText edt_phienban = (MaterialEditText) layout_car.findViewById(R.id.edtphienban);
        final MaterialEditText edt_doixe = (MaterialEditText) layout_car.findViewById(R.id.edtdoixe);


        aleartDialog.setView(layout_car);

        //set button
        aleartDialog.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final AlertDialog waitingDialog = new SpotsDialog(UserHome.this);
                waitingDialog.show();

                String tenxe = edt_tenxe.getText().toString();
                String dongxe = edtdongxe.getText().toString();
                String ngaymua = edt_ngay_mua.getText().toString();
                String mieuta = edt_mieuta.getText().toString();
                String phienban = edt_phienban.getText().toString();
                String doixe = edt_doixe.getText().toString();


                Map<String, Object> updateInfo = new HashMap<>();
                if (!TextUtils.isEmpty(tenxe))
                    updateInfo.put("Zname", tenxe);
                if (!TextUtils.isEmpty(dongxe))
                    updateInfo.put("Zdongxe", dongxe);
                if (!TextUtils.isEmpty(ngaymua))
                    updateInfo.put("Zngaymua", ngaymua);
                if (!TextUtils.isEmpty(mieuta))
                    updateInfo.put("Zmieuta", mieuta);
                if (!TextUtils.isEmpty(phienban))
                    updateInfo.put("Zphienban", phienban);
                if (!TextUtils.isEmpty(doixe))
                    updateInfo.put("Zdoixe", doixe);


                DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("Users");
                userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(UserHome.this, "Thông tin đã được cật nhập", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(UserHome.this, "Lỗi cật nhật thông tin", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                            }
                        });

            }
        });
        aleartDialog.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        aleartDialog.show();
    }


    private void intentUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    /**
     * số điện thoại đường day nóng trong trường hợp khẩn cấp
     */
    private void showhotline() {
        AlertDialog.Builder aleartDialog = new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("Đường dây nóng");
        aleartDialog.setMessage("01655907238");

        LayoutInflater inflater = this.getLayoutInflater();


        aleartDialog.setPositiveButton("GỌI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //String phone = "01655907238";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+841655907238", null));
                startActivity(intent);
            }
        });
        AlertDialog dialog = aleartDialog.create();
        dialog.show();
    }

    /**
     * thông tin về ứng dụng
     */
    private void showabout() {
        AlertDialog.Builder aleartDialog = new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("THÔNG TIN ỨNG DỤNG");
        aleartDialog.setMessage("ĐATN KỲ 20171 \n SINH VIÊN : NGUYỄN HUY HOÀNG 20146303");

        LayoutInflater inflater = this.getLayoutInflater();
        // View layout_setting=inflater.inflate(R.layout.layout_about,null);
        //final RelativeLayout layoutAbout=(RelativeLayout) layout_setting.findViewById(R.id.layoutAbout);

        //aleartDialog.setView(layout_setting);

        aleartDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = aleartDialog.create();
        dialog.show();
    }

    /**
     * các thắc mắc gửi về email của quản trị viên
     */
    private void showhelp() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "huyhoangvp96@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thắc mắc cần giải đáp");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Câu hỏi là : \n");
        // emailIntent.putExtra(Intent.EXTRA_TEXT, mLastLocation.getLatitude());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));


    }

    /**
     * update thông tin các nhân của người dùng: tên,số điện thoại,..
     */
    private void showDialogUpdateInfo() {

        AlertDialog.Builder aleartDialog = new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("Cập nhật thông tin các nhân");
        aleartDialog.setMessage("Vui lòng nhập thông tin cá nhân bạn cần cập nhật");

        LayoutInflater inflater = this.getLayoutInflater();
        View layput_pwd = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText edtName = (MaterialEditText) layput_pwd.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText) layput_pwd.findViewById(R.id.edtPhone);
        final ImageView image_upload = (ImageView) layput_pwd.findViewById(R.id.img_upload);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        aleartDialog.setView(layput_pwd);

        //set button
        aleartDialog.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final AlertDialog waitingDialog = new SpotsDialog(UserHome.this);
                waitingDialog.show();

                String name = edtName.getText().toString();
                String phone = edtPhone.getText().toString();
                Map<String, Object> updateInfo = new HashMap<>();
                if (!TextUtils.isEmpty(name))
                    updateInfo.put("name", name);
                if (!TextUtils.isEmpty(phone))
                    updateInfo.put("phone", phone);
                DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("Users");
                userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(UserHome.this, "Thông tin đã được cập nhật", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(UserHome.this, "Lỗi cập nhật thông tin", Toast.LENGTH_LONG).show();
                                waitingDialog.dismiss();
                            }
                        });

            }
        });
        aleartDialog.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        aleartDialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri saveUri = data.getData();
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Loading....");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();//random picture name upload
                final StorageReference imageFolder = storageReference.child("images/" + imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //update this url to avatar
                                        Map<String, Object> avatarUpdate = new HashMap<>();
                                        avatarUpdate.put("avatarUrl", uri.toString());

                                        DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("Users");
                                        userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(avatarUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                            Toast.makeText(UserHome.this, "Đã tải lên", Toast.LENGTH_LONG).show();
                                                        else
                                                            Toast.makeText(UserHome.this, "Lỗi tải lên", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Updated " + progress + "%");
                            }
                        });

            }
        }
    }

    /**
     * chức năng thay đổi mật khẩu cho người dùng
     */
    private void showDialogChangePassWord() {
        AlertDialog.Builder aleartDialog = new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("Thay đổi mật khẩu");
        aleartDialog.setMessage("Vui lòng nhập đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View layput_pwd = inflater.inflate(R.layout.layout_change_pwd, null);
        final MaterialEditText edtPassword = (MaterialEditText) layput_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = (MaterialEditText) layput_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = (MaterialEditText) layput_pwd.findViewById(R.id.edtRepeatPassword);
        aleartDialog.setView(layput_pwd);

        //set button
        aleartDialog.setPositiveButton("Thay đổi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final AlertDialog waitingDialog = new SpotsDialog(UserHome.this);
                waitingDialog.show();
                if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, edtPassword.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .updatePassword(edtRepeatPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //update user information password column
                                                            Map<String, Object> password = new HashMap<>();
                                                            password.put("password", edtRepeatPassword.getText().toString());
                                                            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("Users");
                                                            userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .updateChildren(password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                                Toast.makeText(UserHome.this, "Đã thay đổi mật khẩu", Toast.LENGTH_LONG).show();
                                                                            else
                                                                                Toast.makeText(UserHome.this, "Đã thay đổi nhưng không cật nhật lên cơ sở dữ liệu", Toast.LENGTH_LONG).show();

                                                                            waitingDialog.dismiss();

                                                                        }
                                                                    });
                                                        } else {
                                                            Toast.makeText(UserHome.this, "Mật khẩu không được thay đổi", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        waitingDialog.dismiss();
                                        Toast.makeText(UserHome.this, "Mật khẩu cũ không đúng", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(UserHome.this, "2 Mật khẩu không giống nhau", Toast.LENGTH_LONG).show();
                }
            }
        });
        aleartDialog.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //show dialog
        aleartDialog.show();
    }

    /**
     * chức năng đăng xuất
     */
    private void signOut() {
        //reset remember value
        Paper.init(this);
        Paper.book().destroy();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(UserHome.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


        mLastLocation = location;


        if (mMarker != null)
            mMarker.remove();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //   LatLng latLng = new LatLng(latitude,longitude);
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref = FirebaseDatabase.getInstance().getReference("LocationUsers");

        geoFire = new GeoFire(ref);
        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                //add marker

                mUserMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(String.format("You"))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_gara))
                );

                //move camera to this option
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
            }
        });


        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


    }


    /**
     * Hàm callback được gọi ra khi bản đồ Google Map đã được vẽ xong/đã có dữ liệu đầy đủ và hiển thị lên màn hình
     *
     * @param googleMap Đối tượng chứa bản đồ
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean isSuccess = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.my_style_map)
            );
            if (!isSuccess)
                Log.e("ERROR", "Maps styles load failed");

        } catch (Resources.NotFoundException ex) {
            ex.printStackTrace();
        }

        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClien();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClien();
            mMap.setMyLocationEnabled(true);
        }

        //make event click on marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //when user select marker ,just get result of place and assign  to static variable
                if (marker.getSnippet() != null) {
                    Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                    //start new activity
                    Intent intent = new Intent(UserHome.this, ViewPlace.class);
                    intent.putExtra("placeid", marker.getTitle());
                    startActivity(intent);
                }

                return true;
            }
        });


    }

    private synchronized void buildGoogleApiClien() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


}
