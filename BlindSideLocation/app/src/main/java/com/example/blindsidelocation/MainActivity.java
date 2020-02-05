package com.example.blindsidelocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //DatabaseReference rootRef, demoRef;
    int count = 0;
    LocationManager locationManager;
    LocationListener locationListener;
    int changeInLocationCounter = 0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location mCurrentLocation;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;
    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference mref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mref = mdatabase.getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                    changeInLocationCounter++;
                    double latitude = mCurrentLocation.getLatitude();
                    double longitude = mCurrentLocation.getLongitude();
//                    if(changeInLocationCounter>1){
//                        latitude+=1;
//                        longitude+=1;
//                    }
                    Toast.makeText(getApplicationContext()," Latitude "+latitude+"Longitude"+longitude,Toast.LENGTH_SHORT).show();
                    mref.child("Location").child("User1").child("latitude").setValue(latitude);
                    mref.child("Location").child("User1").child("longitude").setValue(longitude);
//                    setmarkers(latitude,longitude);
                }
            };
        };
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

//    public void setmarkers(Double lat,Double lng){
//        LatLng lt = new LatLng(lat,lng);
//        Toast.makeText(getApplicationContext(), " Update Check!!!"+" "+count+"\n"+lat+" "+lng, Toast.LENGTH_SHORT).show();
//        mMap.clear();
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt,16));
//        mMap.addMarker(new MarkerOptions().position(lt).title(" Position updated " + count));
//    }


//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.d("TAG"," Status Changed ");
////        Toast.makeText(this, "Status Changed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Log.d("TAG"," Provider Enabled ");
////        Toast.makeText(this, "Provider Enabled", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Log.d("TAG"," Provider Disabled ");
////        Toast.makeText(this, "Provider Disabled", Toast.LENGTH_SHORT).show();
//    }


}
