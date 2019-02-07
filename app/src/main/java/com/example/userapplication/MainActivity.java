package com.example.userapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    Button toMap;
    Button toOfflineData;
    LatLng sampleTransfer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toMap = findViewById(R.id.bToMap);
        toOfflineData = findViewById(R.id.bToOfflineData);


        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "toMap", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        toOfflineData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("DummyData");
                final DatabaseReference setRef = FirebaseDatabase.getInstance().getReference().child("AvailableAmbulance");
                GeoFire getGeofire = new GeoFire(dbRef);
                getGeofire.getLocation("Ambulance001", new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        sampleTransfer = new LatLng(location.latitude, location.longitude);
                        Log.d("tag", String.valueOf(sampleTransfer));
                        GeoFire geoFire = new GeoFire(setRef);
                        geoFire.setLocation("Ambulance001", new GeoLocation(sampleTransfer.latitude, sampleTransfer.longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                setRef.child("Ambulance001").child("Availability").setValue(true);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }


}
