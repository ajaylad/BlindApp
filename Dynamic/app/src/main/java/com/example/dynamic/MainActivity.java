package com.example.dynamic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener  {
    TextToSpeech t1;
    private GestureDetector gestureDetector;
    int temp=0;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    String modules[] = new String[6];
    int flagAll;
    int count;
    HashMap<String,String> h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.gestureDetector = new GestureDetector(this, this);
        gestureDetector.setOnDoubleTapListener(this);
        count = 0;
        h = new HashMap<>();
        modules[0]="Email";
        modules[1]="Automatic Bluetooth Connectivity";
        modules[2]="Clock";
        modules[3]="Call";
        modules[4]="Nearby Places";
        modules[5]="Emergency Service";
//        {"Email","Automatic Bluetooth Connectivity","Clock","Call","Emergency","Nearby Places"};
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                    startFuntionSetup();
                }
            }
        });

    }
    private void speakwords(String s) {
        t1.speak(" Which gesture do you want to set for " + s + " module? ",TextToSpeech.QUEUE_ADD,null);
    }

    public void startFuntionSetup(){
        t1.speak(" This is Gesture Setting module. ",TextToSpeech.QUEUE_ADD,null);
        speakwords(modules[count]);
    }

    @Override
    public void onInit(int status) {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }




    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(count==999){
            performAction("Single Tap");
        } else if(count<=5){
            if(h.containsKey("Single Tap")){
                t1.speak(" Sorry, but you've already set single tap for "+h.get("Single Tap"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Single Tap",modules[count]);
                t1.speak(" Single Tap is now set for "+h.get("Single Tap"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else {
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(count==999){
            performAction("Double Tap");
        }else if(count<=5){
            if(h.containsKey("Double Tap")){
                t1.speak(" Sorry, but you've already set double tap for "+h.get("Double Tap"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Double Tap",modules[count]);
                t1.speak(" Double Tap is now set for "+h.get("Double Tap"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else{
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if(count==999){
            performAction("Long Press");
        } else if(count<=5){
            if(h.containsKey("Long Press")){
                t1.speak(" Sorry, but you've already set long press for "+h.get("Long Press"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Long Press",modules[count]);
                t1.speak(" Long Press is now set for "+h.get("Long Press"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else {
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    private void onSwipeRight() {
        if(count==999){
            performAction("Right Swipe");
        } else if(count<=5){
            if(h.containsKey("Right Swipe")){
                t1.speak(" Sorry, but you've already set Right Swipe for "+h.get("Right Swipe"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Right Swipe",modules[count]);
                t1.speak(" Right Swipe is now set for "+h.get("Right Swipe"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else{
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
    }

    private void onSwipeLeft() {
        if(count==999){
            performAction("Left Swipe");
        } else if(count<=5){
            if(h.containsKey("Left Swipe")){
                t1.speak(" Sorry, but you've already set Left Swipe for "+h.get("Left Swipe"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Left Swipe",modules[count]);
                t1.speak(" Left Swipe is now set for "+h.get("Left Swipe"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else{
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
    }

    private void onSwipeTop() {
        if(count==999){
            performAction("Swipe Up");
        }else if(count<=5){
            if(h.containsKey("Swipe Up")){
                t1.speak(" Sorry, but you've already set Swipe Up for "+h.get("Swipe Up"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Swipe Up",modules[count]);
                t1.speak(" Swipe Up is now set for "+h.get("Swipe Up"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else{
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
    }

    private void onSwipeBottom() {
        if(count==999){
            performAction("Swipe Down");
        } else if(count<=5){
            if(h.containsKey("Swipe Down")){
                t1.speak(" Sorry, but you've already set Down Swipe for "+h.get("Swipe Down"),TextToSpeech.QUEUE_ADD,null);
                speakwords(modules[count]);
            }else{
                h.put("Swipe Down",modules[count]);
                t1.speak(" Swipe Down is now set for "+h.get("Swipe Down"),TextToSpeech.QUEUE_ADD,null);
                count++;
                if(count<=5){
                    speakwords(modules[count]);
                }else{
                    count=999;
                }
            }
        }else{
            count = 999;
            Toast.makeText(this,"All Gestures Set",Toast.LENGTH_LONG).show();
        }
    }

    public void performAction(String s){
        String action = h.get(s);
        Intent i;
        switch(action){
            case "Email":
                i = new Intent(this, SecondActivity.class);
                startActivity(i);
                break;
            case "Automatic Bluetooth Connectivity":
                i = new Intent(this, SecondActivity.class);
                startActivity(i);
                break;
            case "Clock":
                i = new Intent(this, SecondActivity.class);
                startActivity(i);
                break;
            case "Call":
                i = new Intent(this, SecondActivity.class);
                startActivity(i);
                break;
            case "Nearby Places":
                i = new Intent(this, SecondActivity.class);
                startActivity(i);
                break;
            case "Emergency Service":
                i = new Intent(this, SecondActivity.class);
                startActivity(i);
                break;
            default:
                Toast.makeText(getApplicationContext(),"No Action Detected",Toast.LENGTH_LONG).show();
                break;
        }
    }

}

