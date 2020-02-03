package com.example.dellayush.sendliveposition;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_CHECK_SETTINGS = 1;
    String add = "";
    LocationManager locationManager;
    LocationListener locationListener;
    double finalLatitude = 0.0, finalLongitude = 0.0;
    TextView lati, longi, addr;
    int changeInLocationCounter = 0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location mCurrentLocation;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lati = findViewById(R.id.lat);
        longi = findViewById(R.id.lon);
        addr = findViewById(R.id.add);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

//        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                // All location settings are satisfied. The client can initialize
//                // location requests here.
//                // ...
//            }
//        });
//
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        resolvable.startResolutionForResult(MainActivity.this,
//                                REQUEST_CHECK_SETTINGS);
//                    } catch (IntentSender.SendIntentException sendEx) {
//                        // Ignore the error.
//                    }
//                }
//            }
//        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();


//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                changeInLocationCounter++;
////        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
//                Log.d("TAG"," Location Changed ");
//                double changedLat = location.getLatitude();
//                double changedLong = location.getLongitude();
//                String address = getAddress(changedLat,changedLong);
//                Log.d("Change in Location",changedLat+" "+changedLong);
//                lati.setText(" Latitude Changed to "+changedLat+" "+changeInLocationCounter+"th time");
//                longi.setText(" Longitude Changed to "+changedLong+" "+changeInLocationCounter+"th time");
//                addr.setText(address);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.d("TAG"," Provider Enabled ");
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.d("TAG"," Provider Disabled ");
//            }
//        };
//        Location location = getLocation();
//        if (location != null) {
//            finalLatitude = location.getLatitude();
//            finalLongitude = location.getLongitude();
//            Log.d("Coordinates",finalLatitude+" "+finalLongitude);
//            add = getAddress(finalLatitude, finalLongitude);
//            lati.setText(finalLatitude+"");
//            longi.setText(finalLongitude+"");
//            addr.setText(add);
//        }else{
//            lati.setText(finalLatitude+"");
//            longi.setText(finalLongitude+"");
//            addr.setText("Nothing");
//        }
    }

    public void perform(View view){
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

//    public Location getLocation() {
//        Location location = null;
//        try {
//            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//
//            // getting GPS status
//            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            // getting network status
//            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//            Log.d("Boolean Values", isGPSEnabled+" "+isNetworkEnabled);
//
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                add = "Location or Network Services is disabled ,please enable it and try again";
//            } else {
//
//                if (isNetworkEnabled) {
//                    Toast.makeText(getApplicationContext(),"Network",Toast.LENGTH_SHORT).show();
//                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(getApplicationContext(),"Permission",Toast.LENGTH_SHORT).show();
//                        return null;
//                    }
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListener);
//                    Toast.makeText(getApplicationContext(),locationManager+"",Toast.LENGTH_SHORT).show();
//                    Log.d("Network", "Network Enabled");
//                    if (locationManager != null) {
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//                        }
//                    }
//                }
//
////                 if GPS Enabled get lat/long using GPS Services
//                if (isGPSEnabled) {
//                    Toast.makeText(getApplicationContext(),"GPS",Toast.LENGTH_SHORT).show();
//                    if (location == null) {
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
//                        Log.d("GPS", "GPS Enabled");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                double latitude = location.getLatitude();
//                                double longitude = location.getLongitude();
//                            }
//                        }
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return location;
//    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String add = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            add = add + "\n" + obj.getSubLocality();
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + "0" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
//            tv.setText(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("TAG"," Status Changed ");
//        Toast.makeText(this, "Status Changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("TAG"," Provider Enabled ");
//        Toast.makeText(this, "Provider Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("TAG"," Provider Disabled ");
//        Toast.makeText(this, "Provider Disabled", Toast.LENGTH_SHORT).show();
    }
}

class LiveLocation extends AsyncTask<Void,Void,Void> {

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;
    private Context context;

    //Class Constructor
    public LiveLocation(Context context) {
        //Initializing variables
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        
        return null;
    }

}

