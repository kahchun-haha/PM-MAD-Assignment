package com.example.horapp;
import java.util.Calendar;
import java.util.Random;
import static android.view.LayoutInflater.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity implements OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {
    private ImageView camera;

    private ImageView gallery;
    private ImageView cameraBackground;
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
    private boolean access;  // variable to determine red button can be pressed or not
    private LocationManager locationManager;
    private Location lastKnownLocation;
    private LocationListener locationListener;

    private Criteria criteria;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_CAMERA_REQUEST_CODE = 1;

    private static final int CAMERA_REQUEST = 1888;

    private TextView homeWord;

    private FirebaseStorage storage;
    private Uri uri;
    private String url;
    private ImageView toReplace;

    private TextView profileName;

    private ImageView toJournal;

    private ImageView toRisk;

    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (TryService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void disableViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                disableViews(viewGroup.getChildAt(i));
            }
        } else {
            view.setEnabled(false);
        }
    }

    public void enableViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                enableViews(viewGroup.getChildAt(i));
            }
        } else {
            view.setEnabled(true);
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            View exitView = LayoutInflater.from(Dashboard.this).inflate(R.layout.exit,null,false);
            builder.setView(exitView);
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Button yes = exitView.findViewById(R.id.yesExit);
            Button no = exitView.findViewById(R.id.noExit);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();

                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

        } else {
            getSupportFragmentManager().popBackStack();
            View rootView = findViewById(R.id.rootLo);
            enableViews(rootView);
            bottomBar.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        System.out.println("Dashboard onCreate()");
        setContentView(R.layout.activity_dashboard);

        profileName = findViewById(R.id.dashboard_profileName);
        Shader textShader_2 = new LinearGradient(profileName.getPaint().measureText(profileName.getText().toString()), profileName.getTextSize(),0,0,
                new int[]{
                        Color.parseColor("#FF000000"),
                        Color.parseColor("#FFFFFFFF"),
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        profileName.getPaint().setShader(textShader_2);
        profileName.setTextColor(Color.argb(255,0,0,0));

        DatabaseReference databaseReference =  FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot username: snapshot.getChildren()){
                    if(username.child("Email").getValue(String.class).equals(currentUserEmail)){
                        profileName.setText(username.getKey().substring(0,2));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference =  FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot username: snapshot.getChildren()){
                            if(username.child("Email").getValue(String.class).equals(currentUserEmail)){

                                View rootView = findViewById(R.id.rootLo);
                                disableViews(rootView);
                                bottomBar.setVisibility(View.GONE);
                                String name = username.getKey();
                                Bundle bundle = new Bundle();
                                bundle.putString("name",name);
                                bundle.putString("email",currentUserEmail);

                                ProfileFragment profileFragment = new ProfileFragment();
                                profileFragment.setArguments(bundle);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.anim.slide_in_left, R.anim.slide_out_left,android.R.anim.slide_in_left, R.anim.slide_out_left);
                                ft.replace(R.id.profilePage, profileFragment,"lol");
                                ft.addToBackStack(null);
                                ft.commit();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        homeWord = findViewById(R.id.home_pageTitle);
        Shader textShader = new LinearGradient(0, 0,homeWord.getPaint().measureText(homeWord.getText().toString()),homeWord.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        homeWord.getPaint().setShader(textShader);
        homeWord.setTextColor(Color.argb(255,0,0,0));


        camera = findViewById(R.id.camera_icon);
        gallery = findViewById(R.id.gallery_icon);

        cameraBackground = findViewById(R.id.camera_placeholder);

        toReplace = findViewById(R.id.toBeReplaced);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    checkCameraPermission();
                }
                else{
                   Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                  
                }
            }
        });

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
                Intent i = new Intent(Dashboard.this, Login.class);
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




        redButton = findViewById(R.id.redButton);

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!access){
                    redButton.setImageAlpha(100);
                    Toast.makeText(getApplicationContext(),"Please enable location permission first",Toast.LENGTH_SHORT).show();

                    return;
                }
                redButton.setImageAlpha(100);
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot userName : dataSnapshot.getChildren()) {
                            String name = userName.getKey();
                            if (userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
                                if (userName.child("E-Contacts").getChildrenCount() == 0) {
                                    Toast.makeText(getApplicationContext(), "Please add emergency contacts first", Toast.LENGTH_SHORT).show();
                                    break;
                                } else {
                                    for (DataSnapshot e_contacts : userName.child("E-Contacts").getChildren()) {
                                        DatabaseReference parent1 = mDatabase.child(name).child("Chat").child(e_contacts.getValue(String.class)).push().child("me").getParent();
                                        parent1.child("me").child("latitude").setValue(latitude);
                                        parent1.child("me").child("longitude").setValue(longitude);

                                        DatabaseReference parent2 = mDatabase.child(e_contacts.getValue(String.class)).child("Chat").child(name).push().child("other").getParent();
                                        parent2.child("other").child("latitude").setValue(latitude);
                                        parent2.child("other").child("longitude").setValue(longitude);


                                        String e_contactName = dataSnapshot.child(e_contacts.getValue(String.class)).getKey();
                                        DatabaseReference fromWho = dataSnapshot.child("notification").child(e_contactName).child("from").getRef();
                                        fromWho.setValue(name);

                                        String e_ContactEmail = dataSnapshot.child(e_contacts.getValue(String.class)).child("Email").getValue(String.class);
                                        DatabaseReference email = dataSnapshot.child("notification").child(e_contactName).child("email").getRef();
                                        email.setValue(e_ContactEmail);

                                        DatabaseReference text = dataSnapshot.child("notification").child(e_contactName).child("message").getRef();
                                        text.setValue("Sent a map");


                                    }
                                    Toast.makeText(getApplicationContext(), "Successfully notified contacts", Toast.LENGTH_SHORT).show();
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


        toJournal = findViewById(R.id.journal_Container);
        toJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this,MyJournal.class);
                startActivity(i);
            }
        });

        toRisk = findViewById(R.id.risk_Container);
        toRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this,Risk.class);
                startActivity(i);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/horapp-68eec.appspot.com/o/image2200?alt=media&token=3d83bfc7-e78c-4d11-bbec-5e7bfcd6a9d0").into(toReplace);
//                toReplace.setVisibility(View.VISIBLE);

                Intent i = new Intent(Dashboard.this, Picture.class);
                startActivity(i);
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                System.out.println("U come here?");
                ;
                Toast.makeText(getApplicationContext(), "Latitiude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_SHORT).show();

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

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        checkLocationPermission();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // set the photo taken by the camera into the placeholder ImageView
            ImageView toReplace = findViewById(R.id.toBeReplaced);
            toReplace.setImageBitmap(photo);

            Random random = new Random();
            int label = random.nextInt(10000);

            storage = FirebaseStorage.getInstance("gs://horapp-68eec.appspot.com");
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("image"+label);

            toReplace.setDrawingCacheEnabled(true);
            toReplace.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) toReplace.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            // Get the data from an ImageView as bytes
            byte[] someArray = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(someArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    System.out.println(exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = imagesRef.getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url = downloadUrl.getResult().toString();

                            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            DatabaseReference mDatabase = FirebaseDatabase
                                                   .getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app")
                                                   .getReference("Users");
                            // saving the url to images stored in Firebase Cloud Storage into Realtime Database for retrieval by user
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot username: snapshot.getChildren()){
                                        if(username.child("Email").getValue(String.class).equals(currentUserEmail)){
                                            username.getRef().child("Image").push().setValue(url);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i = null;


        if(item.getItemId()==R.id.helpline){
            i = new Intent(this,Helpline.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        else if(item.getItemId()==R.id.learn) {

            i = new Intent(this, Learn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        else if(item.getItemId()==R.id.forum){
            i = new Intent(Dashboard.this, Forum.class);
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

        }

        else if(item.getItemId()==R.id.messaging){

        }

        else if(item.getItemId()==R.id.forum){

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
//        for(int grant: grantResults)
//         System.out.println("grant "+grant);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                System.out.println("location "+grantResults.length);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        access = true;
                        //Request location updates:
                        locationManager.requestSingleUpdate(criteria, locationListener, looper);
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    access = false;
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

                }
               break;

            }

            case MY_CAMERA_REQUEST_CODE:{
                System.out.println("camera "+grantResults.length);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        System.out.println("Dashboard onResume()");
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


        }
        else{
            access = true;
            locationManager.requestSingleUpdate(criteria, locationListener, looper);

        }



    }

    public void checkCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }
    }



}





