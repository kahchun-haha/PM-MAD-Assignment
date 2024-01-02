package com.example.horapp;

import static android.view.LayoutInflater.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
 
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity implements OnMapReadyCallback,BottomNavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemReselectedListener{

private ImageView signOut;
    BottomNavigationView bottomBar;
    final Looper looper = null;
    Button toSearch;
    private ImageView redButton;
    private ImageView justClick;
    private ImageView message;
    AutoCompleteTextView search;
    private MapView mapView;
    private double latitude;
    private double longitude;
    private boolean access;
    private LocationManager locationManager;
    private Location lastKnownLocation;
    private LocationListener locationListener;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(TryService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Dashboard.this.finish();
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
        //AlertDialog alert = builder.create();
        //  alert.show();
        // super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        System.out.println("Dashboard onCreate()");
        setContentView(R.layout.activity_dashboard);

        //if(!foregroundServiceRunning()) {
          //  System.out.println("only once");
            Intent serviceIntent = new Intent(this, TryService.class);
            startForegroundService(serviceIntent);
       // }
        signOut = findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut.setImageAlpha(100);
                Intent i = new Intent(Dashboard.this,Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stopService(serviceIntent);
                FirebaseAuth.getInstance().signOut();
                startActivity(i);
            }
        });
        bottomBar = findViewById(R.id.navigationBar);

        bottomBar.setSelectedItemId(R.id.home);

        bottomBar.setOnNavigationItemSelectedListener(this);
        bottomBar.setOnNavigationItemReselectedListener(this);


//        toSearch = findViewById(R.id.search_btn);

        redButton = findViewById(R.id.redButton);

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                redButton.setImageAlpha(100);
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot userName : dataSnapshot.getChildren()) {
                            String name = userName.getKey();
                            if (userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
                                if(userName.child("E-Contacts").getChildrenCount()==0){
                                    Toast.makeText(getApplicationContext(),"Please add emergency contacts first", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                else{
                                    for(DataSnapshot e_contacts : userName.child("E-Contacts").getChildren()){
                                        DatabaseReference parent1 = mDatabase.child(name).child("Chat").child(e_contacts.getValue(String.class)).push().child("me").getParent();
                                        parent1.child("me").child("latitude").setValue(latitude);
                                        parent1.child("me").child("longitude").setValue(longitude);

                                        DatabaseReference parent2 =  mDatabase.child(e_contacts.getValue(String.class)).child("Chat").child(name).push().child("other").getParent();
                                        parent2.child("other").child("latitude").setValue(latitude);
                                        parent2.child("other").child("longitude").setValue(longitude);



                                        String e_contactName = dataSnapshot.child(e_contacts.getValue(String.class)).getKey();
                                        DatabaseReference fromWho = dataSnapshot.child("notification").child(e_contactName).child("from").getRef();
                                        fromWho.setValue(name);

                                        String e_ContactEmail = dataSnapshot.child(e_contacts.getValue(String.class)).child("Email").getValue(String.class);
                                        DatabaseReference email = dataSnapshot.child("notification").child(e_contactName).child("email").getRef();
                                        email.setValue(e_ContactEmail);

                                        DatabaseReference text= dataSnapshot.child("notification").child(e_contactName).child("message").getRef();
                                        text.setValue("Sent a map");

//                                        sendNotificationToUser("hor","Sent a map");
//                                        String username = "puf";
//                                        FirebaseMessaging.getInstance().subscribeToTopic("user_"+username);
                                    }
                                    Toast.makeText(getApplicationContext(),"Successfully notified contacts", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        redButton.setImageAlpha(255);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors, if any
                    }
                });
            }
        });

        justClick = findViewById(R.id.justClick);
        justClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, emergency_contacts.class);
                startActivity(i);
            }
        });
      //  mapView = findViewById(R.id.map);

    message = findViewById(R.id.red_BTN);
    message.setOnClickListener(new View.OnClickListener() {
    @Override
        public void onClick(View view) {
                Intent i = new Intent(Dashboard.this,Forum.class);
                startActivity(i);
            }
        });

       locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                System.out.println("U come here?");;
                Toast.makeText(getApplicationContext(), "Latitiude: "+latitude +" Longitude: "+longitude, Toast.LENGTH_SHORT).show();

                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userName : dataSnapshot.getChildren()) {


                            String name = userName.getKey();
                            if (userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
                               mDatabase.child(name).child("Latitude").setValue(latitude);
                                mDatabase.child(name).child("Longitude").setValue(longitude);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors, if any
                    }
                });

            }
        };

        // Now first make a criteria with your requirements
        // this is done to save the battery life of the device
        // there are various other other criteria you can search for..
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        checkLocationPermission();
        locationManager.requestSingleUpdate(criteria, locationListener, looper);




//        toSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkLocationPermission();
//                locationManager.requestSingleUpdate(criteria, locationListener, looper);
//                mapView.onCreate(null);
//                mapView.onStart();
//                mapView.onResume();
//                mapView.getMapAsync(googleMap -> {
//                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
//                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                    googleMap.getUiSettings().setAllGesturesEnabled(false);
//                   // Toast.makeText(getApplicationContext(), "Come B", Toast.LENGTH_SHORT).show();
//                });
//
//
//                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
//                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot userName : dataSnapshot.getChildren()) {
//                            String name = userName.getKey();
//                            if (userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
//                                DatabaseReference parent1 = mDatabase.child(name).child("Chat").child("hor").push().child("me").getParent();
//                                parent1.child("me").child("latitude").setValue(latitude);
//                                parent1.child("me").child("longitude").setValue(longitude);
//
//                                DatabaseReference parent2 =  mDatabase.child("hor").child("Chat").child(name).push().child("other").getParent();
//                                parent2.child("other").child("latitude").setValue(latitude);
//                                parent2.child("other").child("longitude").setValue(longitude);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle errors, if any
//                    }
//                });
//
//
//            }
//        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i = null;

        if(item.getItemId()==R.id.home){
                i = new Intent(this,Dashboard.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
        }

        else if(item.getItemId()==R.id.messaging){

                i = new Intent(this,ChatList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
               //overridePendingTransition(20,20);
        }



        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.home){
            //  if(Dashboard.currentActivity!=currentActivity){
//            Intent i = new Intent(this,Dashboard.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);
            // }
        }

        else if(item.getItemId()==R.id.messaging){
            // if(ChatList.currentActivity!=currentActivity){
//            Intent i = new Intent(this,ChatList.class);
//            startActivity(i);
            // }
        }



    }








   //  Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(latitude, longitude))
//                .title("Marker"));
//
        Toast.makeText(getApplicationContext(), "Come here?", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  mapView.onStart();
     ;
        System.out.println("Dashboard onStart()");

        // This verification should be done during onStart() because the system calls
        // this method when the user returns to the activity, which ensures the desired
        // location provider is enabled each time the activity resumes from the stopped state.

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            System.out.println("2");
            enableLocationSettings();
        }

    }
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        access = true;
                        //Request location updates:
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    access = false;
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    @Override
    protected void onResume () {
                super.onResume();

                //  mapView.onResume();
                System.out.println("Dashboard onResume()");

            //    search = findViewById(R.id.search_bar);
            //    toSearch = findViewById(R.id.search_btn);
//                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_list_item_1);
//                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
//                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        adapter.clear();
//                        for (DataSnapshot userName : dataSnapshot.getChildren()) {
//                            String name = userName.getKey();
//                            if (!userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
//                                adapter.add(name);
//                            }
//                            // adapter.add(name);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle errors, if any
//                    }
//                });
//
//                search.setThreshold(1);
//                search.setAdapter(adapter);
//                search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        Intent intent = new Intent(Dashboard.this, Chat.class);
//                        intent.putExtra("otherUser", adapterView.getItemAtPosition(i).toString());
//                        startActivity(intent);
//                        finish();
//                    }
//                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Dashboard onStop()");
    }

    @Override
    protected void onPause () {
        super.onPause();
        System.out.println("Dashboard onPause()");
                //   mapView.onPause();
            }

   @Override
   protected void onDestroy () {
        super.onDestroy();
                //   mapView.onDestroy();
       System.out.println("Dashboard onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("Dashboard onRestart()");
    }

    public void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

    }



}





