package com.example.dellayush.blindapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class EmergencyMessage extends AppCompatActivity implements TextToSpeech.OnInitListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int MY_PERMISSIONS_FOR_LOCATION = 101 ;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech speaker;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView latitude, longitude, gesture;
    private GestureDetector gestureDetector;
    SmsManager smsManager;
    String emergencyMessage;
    String lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_message);
        this.gestureDetector = new GestureDetector(this,this);
        gestureDetector.setOnDoubleTapListener(this);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        gesture = findViewById(R.id.gesture);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        smsManager = SmsManager.getDefault();
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            speaker.setLanguage(Locale.ENGLISH);
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, " Text To Speech Not Activated ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    //To enable detect gestures on Touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        gesture.setText("Single Tapped");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        gesture.setText("Double Tapped");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EmergencyMessage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_FOR_LOCATION);

        }
        Task task = fusedLocationClient.getLastLocation();
        Toast.makeText(EmergencyMessage.this,task.toString(),Toast.LENGTH_LONG).show();
        task.addOnSuccessListener(EmergencyMessage.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    latitude.setText("Latitide = "+location.getLatitude());
                    longitude.setText("Longitude = "+location.getLongitude());
                    lastLocation = lastLocation + location.getLatitude() + "," + location.getLongitude();
                    lastLocation = "https://maps.google.com/?q="+location.getLatitude()+","+location.getLongitude();
                    emergencyMessage = "I am in trouble. Help me out!" + "\n" + "My live location is: "  +"\n"+ lastLocation;
                    smsManager.sendTextMessage("9321293999",null,emergencyMessage,null,null);
                    Toast.makeText(EmergencyMessage.this,"Last Known Location Sent",Toast.LENGTH_LONG).show();
                }else{
                    latitude.setText("Latitide = NULL");
                    longitude.setText("Longitude = NULL");
                }
            }
        });
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_FOR_LOCATION) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        gesture.setText("onDown Performed");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        gesture.setText("onShowPress Performed");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        gesture.setText("Single Tapped Up");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        gesture.setText("Scrolled");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        gesture.setText("Long Pressed");
        Intent toMainActivity = new Intent(EmergencyMessage.this,MainActivity.class);
        startActivity(toMainActivity);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        gesture.setText("Flinged");
        return false;
    }
}
