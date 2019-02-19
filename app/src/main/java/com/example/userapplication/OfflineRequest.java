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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.userapplication.RecycleView.AmbulanceAdapter;
import com.example.userapplication.RecycleView.AmbulanceData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OfflineRequest extends AppCompatActivity {

    private static final String TAG = "myTag";

    RecyclerView recyclerView;
    private AmbulanceAdapter ambulanceAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public LatLng genLatLng;

    private List<AmbulanceData> ambulanceDataList = new ArrayList<>();


    String myPhoneNumber, myCoordinates;
    String Lat, Lng;
    String mblNumVar, smsMsgVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_request);

//        Intent intent = getIntent();
//        Lat = intent.getStringExtra("Lat");
//        Lng = intent.getStringExtra("Lng");
//        Toast.makeText(this, ""+ Lat + " | " + Lng, Toast.LENGTH_SHORT).show();
        Double dLat; Double dLng;
//        dLat = Double.parseDouble(Lat);
//        dLng = Double.parseDouble(Lng);


        // dummy LatLng
        dLat = 15.3240763;
        dLng = 119.9840701;
        genLatLng = new LatLng(dLat, dLng);







        // RECYCLERVIEW CODES

        recyclerView = findViewById(R.id.mRecyclerView);

        ambulanceAdapter = new AmbulanceAdapter(ambulanceDataList, new AmbulanceAdapter.AmbulanceAdapterListener() {
            @Override
            public void sendSmsRequest(View v, int position) {
                AmbulanceData ambulanceData = ambulanceDataList.get(position);
                // do get data using ambulanceData
                Toast.makeText(OfflineRequest.this, "" + ambulanceData.getAmbulanceName() + " | " + ambulanceData.getAmbulancePhoneNumber(), Toast.LENGTH_SHORT).show();
                mblNumVar = ambulanceData.getAmbulancePhoneNumber();
                smsMsgVar = "EMERGENCY " + genLatLng;
                sendSmsMsg(mblNumVar, smsMsgVar);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ambulanceAdapter);

        dataPreparation(genLatLng);


        // RECYCLERVIEW CODES

    }// END OF oNcREATE





    private void dataPreparation(LatLng myCoordinates){

        Location myLocation = new Location("");
        myLocation.setLatitude(myCoordinates.latitude);
        myLocation.setLongitude(myCoordinates.longitude);




        AmbulanceData ambulanceData = new AmbulanceData("SampleName001", "PRMH", "15.316537,119.991479", "09995444632", comparedLocation("15.316537", "119.991479", myLocation));
        ambulanceDataList.add(ambulanceData);


        ambulanceData = new AmbulanceData("SampleName002", "PRMH", "15.363737,119.998699", "09995444632", comparedLocation("15.363737", "119.998699", myLocation));
        ambulanceDataList.add(ambulanceData);

        ambulanceData = new AmbulanceData("SampleName003", "PRMH", "15.363152,119.914282", "09995444632", comparedLocation("15.363152", "119.914282", myLocation));
        ambulanceDataList.add(ambulanceData);

        ambulanceData = new AmbulanceData("SampleName007", "PRMH", "15.320069,119.984887", "09995444632", comparedLocation("15.320069", "119.984887", myLocation));
        ambulanceDataList.add(ambulanceData);

        Collections.sort(ambulanceDataList, new Comparator<AmbulanceData>() {
            @Override
            public int compare(AmbulanceData o1, AmbulanceData o2) {
                Float priority1 = o1.getComparedLocation();
                Float priority2 = o2.getComparedLocation();
                return priority1.compareTo(priority2);
            }
        });
        ambulanceAdapter.notifyDataSetChanged();

    }

//    public class CustomComparator implements Comparator<AmbulanceData>{
//
//        @Override
//        public int compare(AmbulanceData o1, AmbulanceData o2) {
//            Float priority1 = o1.getComparedLocation();
//            Float priority2 = o2.getComparedLocation();
//            return priority2.compareTo(priority1);
//        }
//    }

    private float comparedLocation(String Lat, String Lng, Location myLocation){
//        Location myLocation = new Location("");
//        myLocation.setLatitude(myCoordinates.latitude);
//        myLocation.setLongitude(myCoordinates.longitude);
//
        Location loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(Lat));
        loc1.setLongitude(Double.parseDouble(Lng));
        return loc1.distanceTo(myLocation);
    }

    void sendSmsMsg(String mblNumVar, String smsMsgVar) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "sdk is greater than 23 and has Permission", Toast.LENGTH_SHORT).show();
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
            }else{
                Toast.makeText(this, "does not have permission", Toast.LENGTH_SHORT).show();
                checkSmsPermission();
            }
        }else{
            Toast.makeText(this, "SDK build Less than 23", Toast.LENGTH_SHORT).show();
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



//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                SmsManager smsMgrVar = SmsManager.getDefault();
//                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
//                Toast.makeText(getApplicationContext(), "Message Sent",
//                        Toast.LENGTH_LONG).show();
//            }
//            catch (Exception ErrVar) {
//                Toast.makeText(getApplicationContext(),ErrVar.getMessage().toString(), Toast.LENGTH_LONG).show();
//                ErrVar.printStackTrace();
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
//            }
//        }

    }

    private void checkSmsPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
                new AlertDialog.Builder(this).setTitle("Permission").setMessage("Please Give Permission").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(OfflineRequest.this, new String[]{Manifest.permission.SEND_SMS}, 69);
                    }
                }).create().show();
            }else{
                ActivityCompat.requestPermissions(OfflineRequest.this, new String[]{Manifest.permission.SEND_SMS}, 69);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 69:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (mblNumVar != null){
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
                    }else{

                    }

                }
        }
    }
}
