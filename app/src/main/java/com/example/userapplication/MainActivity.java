package com.example.userapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    Button toMap;
    Button requestAmbulanceOffline;


    private final static String TAG = "myTag";

    private Location mLocation;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LatLng genLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toMap = findViewById(R.id.bToMap);
        requestAmbulanceOffline = findViewById(R.id.bRequestAmbulanceOffline);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "toMap", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        try{
            requestAmbulanceOffline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, OfflineRequest.class);
                    intent.putExtra("Lat", String.valueOf(genLatLng.latitude));
                    intent.putExtra("Lng", String.valueOf(genLatLng.longitude));
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
        }



        mLocationRequest = new LocationRequest();
        // update interval to 1000ms
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // get updated Location every second


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SDK greater than 23", Toast.LENGTH_SHORT).show();
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                Toast.makeText(this, "already has permission", Toast.LENGTH_SHORT).show();

//                requestAmbulanceOffline.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(MainActivity.this, "EMERGENCY | " + String.valueOf(genLatLng), Toast.LENGTH_SHORT).show();
//                        sendSmsMsg("09157353517", "EMERGENCY " + String.valueOf(genLatLng));
//                    }
//                });

            } else {

                Toast.makeText(this, "does not have permission", Toast.LENGTH_SHORT).show();
                checkLocationPermission();

            }
        }else{
            Toast.makeText(this, "SDK build Less Than 23", Toast.LENGTH_SHORT).show();
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

//            requestAmbulanceOffline.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this, "EMERGENCY | " + String.valueOf(genLatLng), Toast.LENGTH_SHORT).show();
//                    sendSmsMsg("09157353517", "EMERGENCY " + String.valueOf(genLatLng));
//                }
//            });

        }
    }




    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()){
                mLocation = location;
                genLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                Log.d(TAG, "onLocationResult: " + genLatLng);
            }
        }
    };

    private void checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission: ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Give Permission")
                        .setMessage("Grant it a Location Permission")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                        requestAmbulanceOffline.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
////                                Toast.makeText(MainActivity.this, "EMERGENCY " + String.valueOf(genLatLng), Toast.LENGTH_SHORT).show();
////                                sendSmsMsg("09157353517", "EMERGENCY " + String.valueOf(genLatLng));
//                            }
//                        });
                    }
                }
            }
        }
    }

    void sendSmsMsg(String mblNumVar, String smsMsgVar) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception ErrVar) {
                Toast.makeText(getApplicationContext(),ErrVar.getMessage().toString(), Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
            }
        }

    }


}
