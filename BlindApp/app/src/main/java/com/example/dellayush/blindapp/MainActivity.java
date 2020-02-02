package com.example.dellayush.blindapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnInitListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

//    boolean start = false;
    BluetoothAdapter connectToBluetooth;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech speaker;
    private GestureDetector gestureDetector;
    TextView textView;
    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        this.gestureDetector = new GestureDetector(this,this);
        gestureDetector.setOnDoubleTapListener(this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            speaker.setLanguage(Locale.ENGLISH);
//            speaker.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//
//                @Override
//                public void onStart(String utteranceId) {
//                    Toast.makeText(MainActivity.this,"Speaker Started",Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
//                    if(!start){
//                        gestureDetector = new GestureDetector(MainActivity.this,MainActivity.this);
//                        gestureDetector.setOnDoubleTapListener(MainActivity.this);
//                        start = true;
//                    }
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//
//                }
//            });
            speakWords();
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Text To Speech Not Activated", Toast.LENGTH_LONG).show();
        }
    }

    private void speakWords() {
        speaker.setSpeechRate((float) 0.5);
        speaker.speak("Welcome to the Blind App", TextToSpeech.QUEUE_ADD, null);
        speaker.speak("Double Tap connect to the paired devices using Bluetooth", TextToSpeech.QUEUE_ADD, null);
        speaker.speak("Fling for Emergency Service", TextToSpeech.QUEUE_ADD, null);
        speaker.speak("Single Tap to stop", TextToSpeech.QUEUE_ADD, null);
        speaker.speak("To repeat the instructions, long press again", TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"Stop");
        super.onStop();
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
    }

//    @Override
//    protected void onPause() {
//        Log.d(TAG,"Pause");
//        super.onPause();
//    }
//
//    @Override
//    protected void onStart() {
//        Log.d(TAG,"Start");
//        super.onStart();
//    }
//
//    @Override
//    protected void onDestroy() {
//        Log.d(TAG,"Destroy");
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onResume() {
//        Log.d(TAG,"Resume");
//        speakWords();
//        super.onResume();
//    }
//
//    @Override
//    protected void onRestart() {
//        Log.d(TAG,"Restart");
//        super.onRestart();
//    }

    //To enable detect gestures on Touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //Actual Gestures start from here
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        speaker.stop();
        if(connectToBluetooth.isEnabled()){
            if(connectToBluetooth.disable()){
                speaker.speak("Bluetooth Turned off", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        textView.setText("Single Tapped");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        speaker.stop();
        textView.setText("Double Tapped");
        connectToBluetooth = BluetoothAdapter.getDefaultAdapter();
        connectToBluetooth.enable();
        Set<BluetoothDevice> pairedDevices = connectToBluetooth.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                speaker.speak("Connecting you to your device"+" "+deviceName, TextToSpeech.QUEUE_ADD, null);
                Toast.makeText(MainActivity.this, deviceName, Toast.LENGTH_SHORT).show();
            }
        }else{
            speaker.speak("No Bluetooth device available", TextToSpeech.QUEUE_FLUSH, null);
        }
//        if(connectToBluetooth.getProfileConnectionState(1)==1){
//            speaker.speak("Single Tap to turn off your bluetooth", TextToSpeech.QUEUE_ADD, null);
//        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        textView.setText("Down Action");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        textView.setText("Show Press ACtion");
        onDoubleTap(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        textView.setText("Single Tap Up Action");
        speaker.stop();
        if(connectToBluetooth.isEnabled()){
            if(connectToBluetooth.disable()){
                speaker.speak("Bluetooth Turned off", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
//        textView.setText("Single Tapped");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        textView.setText("Scroll Action");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        textView.setText("Long Pressed");
        speaker.stop();
        speakWords();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        textView.setText("Fling Action");
        String emergencyText = " Taking you to Emergency Page " + "\n" + " Double Tap to alert your Guardian with your Location ";
        speaker.speak(emergencyText,TextToSpeech.QUEUE_FLUSH,null);
        Intent toEmergencyMessage = new Intent(MainActivity.this,EmergencyMessage.class);
        startActivity(toEmergencyMessage);
        return false;
    }

}
