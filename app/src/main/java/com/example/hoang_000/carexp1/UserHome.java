package com.example.hoang_000.carexp1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.example.hoang_000.carexp1.Model.Results;
import com.example.hoang_000.carexp1.Model2.User;
import com.example.hoang_000.carexp1.Remote.IGoogleAPIService;
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
        implements NavigationView.OnNavigationItemSelectedListener ,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    SupportMapFragment mapFragment;
    //LOCATION
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE=8000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=300000;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static int UPDATE_INERVAL = 5000;   //5S
    private static int FATEST_INERVAL = 3000;   //5S
    private static int DISPLACEMENT = 10;   //5S
    public static final int PICK_IMAGE_REQUEST=9999;
    public static User currentUser;

    private static final int MY_PERMISSION_CODE = 1000;



    private double latitude,longitude;

    private Marker mMarker;

    IGoogleAPIService mService;
    MyPlaces currentPlace;

    DatabaseReference ref;
    GeoFire geoFire;
    Marker mUserMarker;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //Maps
        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init service
        mService = Common.getGoogleAPIService();
        //request runtime
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        BottomNavigationView bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_garage:
                        nearByPlace("car_repair");
                        break;
                    case R.id.action_gas:
                        nearByPlace("gas_station");
                        break;
                    case R.id.action_hospital:
                        nearByPlace("hospital");
                        break;
                    case R.id.action_market:
                        nearByPlace("supermarket");
                        break;
                    default: break;
                }
                return true;
            }
        });
        //init firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView txtname= (TextView)navigationHeaderView.findViewById(R.id.txtUserName);
        CircleImageView imgavatar=(CircleImageView)navigationHeaderView.findViewById(R.id.image_avatar);



 /*  txtname.setText(currentUser.getName());

      if(currentUser.getAvatarUrl()!=null
              &&!TextUtils.isEmpty(currentUser.getAvatarUrl()))
      {
          Picasso.with(this)
              .load(currentUser.getAvatarUrl())
                .into(imgavatar);}*/


        //Geo Fire
        ref = FirebaseDatabase.getInstance().getReference("Users");
        geoFire =new GeoFire(ref);


    }


    private void nearByPlace(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude,longitude,placeType);
        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace = response.body(); //assign value  for currentPlace
                        if (response.isSuccessful())
                        {
                            for (int i=0;i<response.body().getResults().length;i++)
                            {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLat());

                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation1().getLng());
                                String placeName   = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat,lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                if (placeType.equals("car_repair"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                else if (placeType.equals("gas_station"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                else if (placeType.equals("hospital"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                    //   Common.currentResult.getRating();
                                else if (placeType.equals("supermarket"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                                markerOptions.snippet(String.valueOf(i));//assign index for marker
                                //add marker to map
                                mMap.addMarker(markerOptions);
                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longitude, String placeType){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json");
        googlePlacesUrl.append("?location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+20000);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl",googlePlacesUrl.toString());
        return googlePlacesUrl.toString();

    }
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))

                ActivityCompat.requestPermissions(this,new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE);
            else

                ActivityCompat.requestPermissions(this,new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE);
            return false;


        }
        else
            return true;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_CODE:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if (mGoogleApiClient==null)
                            buildGoogleApiClien();
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        }else if (id == R.id.nav_change_pwd) {
            showDialogChangePassWord();

        }else if (id == R.id.nav_signout) {
            signOut();
        }
        else if (id == R.id.nav_hotline) {
            showhotline();
        }
        else if (id == R.id.nav_fanpage) {
            intentUrl("https://www.facebook.com/Drive-Tracker-161475257940058/");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCarInfo() {
        AlertDialog.Builder aleartDialog= new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("CAR INFORMATION");
        aleartDialog.setMessage("please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View layout_car=inflater.inflate(R.layout.layout_car_information,null);
        final MaterialEditText edt_tenxe=(MaterialEditText)layout_car.findViewById(R.id.edt_tenxe);
        final MaterialEditText edtdongxe=(MaterialEditText)layout_car.findViewById(R.id.edtdongxe);
        final MaterialEditText edt_ngay_mua=(MaterialEditText)layout_car.findViewById(R.id.edt_ngay_mua);
        final MaterialEditText edt_mieuta=(MaterialEditText)layout_car.findViewById(R.id.edt_mieuta);





        aleartDialog.setView(layout_car);

        //set button
        aleartDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final AlertDialog waitingDialog = new SpotsDialog(UserHome.this);
                waitingDialog.show();

                String tenxe =edt_tenxe.getText().toString();
                String dongxe = edtdongxe.getText().toString();
                String ngaymua = edt_ngay_mua.getText().toString();
                String mieuta = edt_mieuta.getText().toString();

                Map<String,Object>updateInfo=new HashMap<>();
                if (!TextUtils.isEmpty(tenxe))
                    updateInfo.put("Zname",tenxe);
                if (!TextUtils.isEmpty(dongxe))
                    updateInfo.put("Zdongxe",dongxe);
                if (!TextUtils.isEmpty(ngaymua))
                    updateInfo.put("Zngaymua",ngaymua);
                if (!TextUtils.isEmpty(mieuta))
                    updateInfo.put("Zmieuta",mieuta);


                DatabaseReference userInformation =FirebaseDatabase.getInstance().getReference("Users");
                userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(UserHome.this, "Information Updated", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(UserHome.this, "Information Updated Failed", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                            }
                        });

            }
        });
        aleartDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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

    private void showhotline() {
        AlertDialog.Builder aleartDialog= new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("HOTLINE");
        aleartDialog.setMessage("01655907238");

        LayoutInflater inflater = this.getLayoutInflater();


        aleartDialog.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = aleartDialog.create();
        dialog.show();
    }

    private void showabout() {
        AlertDialog.Builder aleartDialog= new AlertDialog.Builder(UserHome.this);
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

    private void showhelp() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","huyhoangvp96@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Question&Commend");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Question and Commend : \n");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));


    }

    private void showDialogUpdateInfo() {
        AlertDialog.Builder aleartDialog= new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("UPDATE INFORMATION");
        aleartDialog.setMessage("please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View layput_pwd=inflater.inflate(R.layout.layout_update_information,null);
        final MaterialEditText edtName=(MaterialEditText)layput_pwd.findViewById(R.id.edtName);
        final MaterialEditText edtPhone=(MaterialEditText)layput_pwd.findViewById(R.id.edtPhone);
        final ImageView image_upload=(ImageView)layput_pwd.findViewById(R.id.img_upload);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        aleartDialog.setView(layput_pwd);

        //set button
        aleartDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final AlertDialog waitingDialog = new SpotsDialog(UserHome.this);
                waitingDialog.show();

                String name =edtName.getText().toString();
                String phone = edtPhone.getText().toString();
                Map<String,Object>updateInfo=new HashMap<>();
                if (!TextUtils.isEmpty(name))
                    updateInfo.put("name",name);
                if (!TextUtils.isEmpty(phone))
                    updateInfo.put("phone",phone);
                DatabaseReference userInformation =FirebaseDatabase.getInstance().getReference("Users");
                userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(UserHome.this, "Information Updated", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(UserHome.this, "Information Updated Failed", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                            }
                        });

            }
        });
        aleartDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode ==RESULT_OK
                &&data!=null && data.getData()!=null)
        {
            Uri saveUri = data.getData();
            if(saveUri!=null)
            {
                final ProgressDialog mDialog= new ProgressDialog(this);
                mDialog.setMessage("Loading....");
                mDialog.show();

                String imageName= UUID.randomUUID().toString();//random picture name upload
                final StorageReference imageFolder =storageReference.child("images/"+imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //update this url to avatar
                                        Map<String,Object>avatarUpdate=new HashMap<>();
                                        avatarUpdate.put("avatarUrl",uri.toString());

                                        DatabaseReference userInformation =FirebaseDatabase.getInstance().getReference("Users");
                                        userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(avatarUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                            Toast.makeText(UserHome.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(UserHome.this, "Uploade Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Updated "+progress+"%");
                            }
                        });

            }
        }
    }

    private void showDialogChangePassWord() {
        AlertDialog.Builder aleartDialog= new AlertDialog.Builder(UserHome.this);
        aleartDialog.setTitle("CHANGE PASSWORD");
        aleartDialog.setMessage("please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View layput_pwd=inflater.inflate(R.layout.layout_change_pwd,null);
        final MaterialEditText edtPassword=(MaterialEditText)layput_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword=(MaterialEditText)layput_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword=(MaterialEditText)layput_pwd.findViewById(R.id.edtRepeatPassword);
        aleartDialog.setView(layput_pwd);

        //set button
        aleartDialog.setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final AlertDialog waitingDialog= new SpotsDialog(UserHome.this);
                waitingDialog.show();
                if(edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                {
                    String email =FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    AuthCredential credential= EmailAuthProvider.getCredential(email,edtPassword.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .updatePassword(edtRepeatPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            //update user information password column
                                                            Map<String,Object >password=new HashMap<>();
                                                            password.put("password",edtRepeatPassword.getText().toString());
                                                            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("Users");
                                                            userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .updateChildren(password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful())
                                                                                Toast.makeText(UserHome.this, "password was changed", Toast.LENGTH_SHORT).show();
                                                                            else
                                                                                Toast.makeText(UserHome.this, "password was changed but dont updatedatabase", Toast.LENGTH_SHORT).show();

                                                                            waitingDialog.dismiss();

                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(UserHome.this, "Password doesnt change", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        waitingDialog.dismiss();
                                        Toast.makeText(UserHome.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(UserHome.this, "Passwords are not same", Toast.LENGTH_SHORT).show();
                }
            }
        });
        aleartDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //show dialog
        aleartDialog.show();
    }

    private void signOut() {
        //reset remember value
        Paper.init(this);
        Paper.book().destroy();

        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(UserHome.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
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
        mLastLocation= location;
        if (mMarker!=null)
            mMarker.remove();
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker = mMap.addMarker(markerOptions);
        //move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient!=null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClien();
                mMap.setMyLocationEnabled(true);
            }
        }
        else
        {
            buildGoogleApiClien();
            mMap.setMyLocationEnabled(true);
        }

        //make event click on marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //when user select marker ,just get result of place and assign  to static variable

                Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                //start new activity
                startActivity(new Intent(UserHome.this,ViewPlace.class));
                return true;
            }
        });
    }

    private synchronized void buildGoogleApiClien() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
}
