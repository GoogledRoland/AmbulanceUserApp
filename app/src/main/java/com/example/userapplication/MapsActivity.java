package com.example.userapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Pack200;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Button callAmbulance;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    LatLng genLatLng;
    LatLng ambulanceLatLng;
    String userIdentifyer = "";
    String ambulanceIdentifyer = "";
    Marker emergencyMarker;
    Marker ambulanceMarker;
    private SupportMapFragment mapFragment;


    private FusedLocationProviderClient mFusedLocationProviderClient;


    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        callAmbulance = findViewById(R.id.bCallAmbulance);


        userIdentifyer = "YeaItsMe";
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("PotentialEmergency");


        // request a response by putting the current location on "EmergencyLocation"
        callAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EmergencyLocation");
                GeoFire geoFire = new GeoFire(reference);
                geoFire.setLocation(userIdentifyer, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                    }
                });

                getNearestAmbulance();

            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;
        // get latest location here.
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
                mMap.setMyLocationEnabled(true);
                callAmbulance.setVisibility(View.VISIBLE);
                Toast.makeText(this, "already has permission", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "does not have permission", Toast.LENGTH_SHORT).show();
                checkLocationPermission();
            }
        }else{
            Toast.makeText(this, "SDK build Less Than 23", Toast.LENGTH_SHORT).show();
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            callAmbulance.setVisibility(View.VISIBLE);
        }

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//        }
//        buildGoogleApiClient();
//        mMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
//                 function called every second
                mLastLocation = location;
                Log.i(TAG, "onLocationResult: " + mLastLocation);
                genLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(genLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                if (emergencyMarker != null) {
                    emergencyMarker.remove();
                }
                emergencyMarker = mMap.addMarker(new MarkerOptions().position(genLatLng).title("Emergency Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_emergencymarker)));

                GeoFire geoFire = new GeoFire(myRef);
                geoFire.setLocation(userIdentifyer, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
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
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: "+ requestCode + " | " + Arrays.toString(grantResults) + " | " + Arrays.toString(permissions));
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                        callAmbulance.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    //    @Override
//    public void onLocationChanged(Location location) {
//
//        // function called every second
//        mLastLocation = location;
//        genLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(genLatLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//        if (emergencyMarker != null) {
//            emergencyMarker.remove();
//        }
//        emergencyMarker = mMap.addMarker(new MarkerOptions().position(genLatLng).title("Emergency Here"));
//
//        GeoFire geoFire = new GeoFire(myRef);
//        geoFire.setLocation(userIdentifyer, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
//            @Override
//            public void onComplete(String key, DatabaseError error) {
//
//            }
//        });
//
//    }


//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        // get latest location here.
//        mLocationRequest = new LocationRequest();
//        // update interval to 1000ms
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        // get updated Location every second
//
//
//        // Check Permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        // check permission
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }


    // Close the Location Services when the MapsActivity Was closed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkLocationPermission();
        myRef.child(userIdentifyer).removeValue();
        if (mFusedLocationProviderClient != null){
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            mMap.setMyLocationEnabled(false);
        }
    }


    // CREATED FUNCTIONS


    private int rad = 1;
    private Boolean getAHit = false;

    // Recursive Function that will find the nearest ambulance
    private void getNearestAmbulance() {
        callAmbulance.setText("Finding Ambulance...");
        callAmbulance.setClickable(false);
        // database reference that points to Available Ambulance
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("AvailableAmbulance");

        final GeoFire geoFire = new GeoFire(dbRef);

        // Geofire Feature for finding the nearest location using GeoQuery
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), rad);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            // if it gets a hit this callback will be called
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // if statement in order to get stop recursively  getting ambulance in that radius
                if (!getAHit) {
                    // getting the ambulance identifyer
                    getAHit = true;
                    ambulanceIdentifyer = key;

                    final DatabaseReference ambulanceLocationRef = FirebaseDatabase.getInstance().getReference().child("AmbulanceInfo").child(ambulanceIdentifyer);
                    HashMap hashMap = new HashMap();
                    hashMap.put("EmergencyId", userIdentifyer);
                    ambulanceLocationRef.updateChildren(hashMap);
                    getDriverLocation(ambulanceIdentifyer);
                    callAmbulance.setText("Ambulance Found!!!");
                    callAmbulance.setClickable(false);


                    // i need extra device to test this

//                    dbRef.child(ambulanceIdentifyer).child("Availability").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String tmp = String.valueOf(dataSnapshot.getValue());
//                            Boolean isItAvailable = Boolean.valueOf(tmp);
//                            Log.d("shitty", ambulanceIdentifyer + " " + isItAvailable);
//                            if (isItAvailable){
//                                dbRef.child(ambulanceIdentifyer).child("Availability").setValue(false);
//                                GeoFire geoFire1 = new GeoFire(dbRef.child(ambulanceIdentifyer).child("EmergencyInfo"));
//                                geoFire1.setLocation(userIdentifyer, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
//                                    @Override
//                                    public void onComplete(String key, DatabaseError error) {
//
//                                    }
//                                });
//                                getAHit = true;
////                                getAmbulanceLocation(ambulanceIdentifyer);
//                                callAmbulance.setText("Ambulance Found!!!");
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    })
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            // recursive function kapag hindi pa nakakahanap.
            @Override
            public void onGeoQueryReady() {
                if (!getAHit) {
                    rad++;
                    getNearestAmbulance();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private void getDriverLocation(String key) {
        DatabaseReference driverLocRef = FirebaseDatabase.getInstance().getReference().child("WorkingAmbulance").child(key).child("l");
        driverLocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double Lat = 0;
                double Lng = 0;
                if (dataSnapshot.exists()){
                    Lat = Double.parseDouble(dataSnapshot.child("0").getValue().toString());
                    Lng = Double.parseDouble(dataSnapshot.child("1").getValue().toString());
                    ambulanceLatLng = new LatLng(Lat, Lng);

                    Location loc1 = new Location("");
                    loc1.setLatitude(genLatLng.latitude);
                    loc1.setLongitude(genLatLng.longitude);
                    Location loc2 = new Location("");
                    loc2.setLatitude(Lat);
                    loc2.setLongitude(Lng);

                    float distance = loc1.distanceTo(loc2);
                    if (distance < 10){
                        callAmbulance.setText("Ambulance Arrived");

                    }
                    if (ambulanceMarker != null) {
                        ambulanceMarker.remove();
                    }
                    ambulanceMarker = mMap.addMarker(new MarkerOptions().position(ambulanceLatLng).title(ambulanceIdentifyer).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ambulancemarker)));

                }else{
                    if (ambulanceMarker != null) {
                        ambulanceMarker.remove();
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getAmbulanceLocation(final String key) {
        // firebase callback
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("AvailableAmbulance").child(key).child("location").child("l");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double Lat = 0;
                double Lng = 0;
                Lat = Double.parseDouble(dataSnapshot.child("0").getValue().toString());
                Lng = Double.parseDouble(dataSnapshot.child("1").getValue().toString());
                Log.d("sht", "Lat = " + Lat + " | Lng = " + Lng);

                ambulanceLatLng = new LatLng(Lat, Lng);
                if (ambulanceMarker != null) {
                    ambulanceMarker.remove();
                }
                ambulanceMarker = mMap.addMarker(new MarkerOptions().position(ambulanceLatLng).title(key));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    // disabling location services of exited the MapsActivity
    private void disableLocation() {
        checkLocationPermission();
        mMap.setMyLocationEnabled(false);
    }


    // building googleMap Api
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
//        mGoogleApiClient.connect();
//    }
}
