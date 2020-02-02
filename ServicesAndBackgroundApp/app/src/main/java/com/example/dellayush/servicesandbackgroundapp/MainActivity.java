package com.example.dellayush.servicesandbackgroundapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String finalMessage = "", contact = "";
    String contactName="", contactNumber="", name="", finalContactNumber ="", finalContactName ="";
    private static final int PERSON_NAME = 1;
    private static final int MESSAGE_CONTENT = 2;
    private static final int CONFIRM_PERSON_OPTIONS = 10;
    private static final int FINAL_CONFIRMATION_OF_CONTACT_NAME = 100;
    private static final int CONFIRMATION_FINAL_MESSAGE_TO_SEND = 200;
    SmsManager smsManager;
    TextToSpeech speaker;
    HashMap<String,String> contactsWithTheName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsManager = smsManager.getDefault();
        contactsWithTheName = new HashMap<String,String>();
        speaker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status != TextToSpeech.ERROR) {
                speaker.setLanguage(Locale.ENGLISH);
                askForContactName();
            }
        }
        });
    }

    private void displaySpeechRecognizer(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, code);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case PERSON_NAME:
                try{
                    if (resultCode == RESULT_OK) {
                        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        contact = results.get(0);
                        contact = toFirstLetterCapital(contact);
                        call(contact);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case CONFIRM_PERSON_OPTIONS:
                try{
                    if(resultCode == RESULT_OK){
                        List<String> results = data != null ? data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) : null;
                        finalContactName = toFirstLetterCapital(results.get(0));
                        if(contactsWithTheName.containsKey(finalContactName)){
//                            speaker.speak(" Hello " + contact, TextToSpeech.QUEUE_ADD, null);
//                            pause(3000);
                            speaker.speak(" Are you sure you want to send the message to " + finalContactName, TextToSpeech.QUEUE_ADD, null);
                            pauseAndCallSTT(100,3000);
                        }else{
                            speaker.speak(" Sorry, but that was not  " + contact, TextToSpeech.QUEUE_ADD, null);
                            speaker.speak(" Try again ", TextToSpeech.QUEUE_ADD, null);
                            pause(8000);
                            speakContacts(contactsWithTheName);
                        }
                    }
                }catch (Exception e){

                }
                break;

            case FINAL_CONFIRMATION_OF_CONTACT_NAME:
                try {
                    if (resultCode == RESULT_OK) {
                        assert data != null;
                        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String resultMessage = null;
                        if (results != null) {
                            resultMessage = results.get(0);
                        }
                        if(resultMessage.equalsIgnoreCase("yes")||resultMessage.equalsIgnoreCase("yeah")|resultMessage.equalsIgnoreCase("okay")){
                            finalContactNumber = contactsWithTheName.get(finalContactName);
                            Toast.makeText(getApplicationContext()," Messaging Number: " + finalContactNumber,Toast.LENGTH_LONG).show();
                            askForTheMessage();
                        }else{
                            speaker.speak(" Ending the message thread for messaging" + contact, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                }catch (Exception e){

                }
                break;

            case MESSAGE_CONTENT:
                try{
                    if (resultCode == RESULT_OK) {
                        assert data != null;
                        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null) {
                            finalMessage = results.get(0);
                        }
                        speaker.speak(" Are you sure you want to send the message ", TextToSpeech.QUEUE_FLUSH, null);
                        pause(50);
                        speaker.speak(finalMessage, TextToSpeech.QUEUE_ADD, null);
                        pause(50);
                        speaker.speak(" to " + finalContactName, TextToSpeech.QUEUE_ADD, null);
                        pauseAndCallSTT(CONFIRMATION_FINAL_MESSAGE_TO_SEND,((finalMessage.split(" ")).length*1000)+5000);
                    }
                }catch (Exception e){

                }
                break;

            case CONFIRMATION_FINAL_MESSAGE_TO_SEND:
                try{
                    if (resultCode == RESULT_OK) {
                        List<String> results = data != null ? data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) : null;
                        String resultContact = null;
                        if (results != null) {
                            resultContact = results.get(0);
                        }
                        if(resultContact.equalsIgnoreCase("yes")||resultContact.equalsIgnoreCase("yeah")|resultContact.equalsIgnoreCase("okay")){
                            sendSMS(finalContactNumber,finalMessage);
                        }else{
                            askForTheMessage();
                        }
                    }
                }catch (Exception e){

                }
                break;



            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void call(String personData) {
        if (personData.startsWith("9")||personData.startsWith("7")||personData.startsWith("8")) {
            if(personData.length()==10){
                finalContactNumber = personData;
                askForTheMessage();
            }else{
                speaker.speak( " Sorry, couldn't understand you! Try again. ",TextToSpeech.QUEUE_ADD,null);
                pauseAndCallSTT(1,3000);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},   //request specific permission from user
                        10);
                return;
            }
        } else {
            Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like '%" + personData + "%'";
//            Toast.makeText(getApplicationContext()," Contact Name is "+personData,Toast.LENGTH_LONG).show();
            Cursor people = getContentResolver().query(uri, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
            if(people.getCount()>0){
                people.moveToFirst();
                try {
                    Log.d("TAG","Total Rows = "+people.getCount());
                    for(int i=0;i<people.getCount();i++){
                        name = people.getString(0);
                        Log.d("TAG",(i+1)+" "+name+" "+personData+" "+validName(name,personData));
                        if(validName(name,personData)){
                            contactNumber = people.getString(1);
                            if (contactNumber.contains("+91")) {
                                contactNumber = contactNumber.replace("+91", "");
                            }
                            if(contactNumber.contains(" ")){
                                String temp[] = contactNumber.split(" ");
                                contactNumber = "";
                                for(String elem: temp){
                                    contactNumber+=elem;
                                }
                            }
                            if(!contactsWithTheName.containsKey(name)) {
                                contactsWithTheName.put(name, contactNumber);
                            }
                            people.moveToNext();
                        }else{
                            people.moveToNext();
                            continue;
                        }
                    }
                    printHashMap(contactsWithTheName);
                }catch (Exception ex){
                    speaker.speak("Kindly try again !",TextToSpeech.QUEUE_ADD,null);
                    Toast.makeText(this,"Sorry no such contact",Toast.LENGTH_SHORT);
                }
            }else{
                speaker.speak(" Sorry, no such contact found ! ",TextToSpeech.QUEUE_ADD,null);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void printHashMap(final HashMap<String, String> hm){
        Log.d("TAG","Total contacts with the name: "+hm.size());
        Iterator hmIterator = hm.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            Log.d("TAG","Details -> "+mapElement.getKey() + " : " + mapElement.getValue());
        }
        if(hm.size()>1){
            speaker.speak("Which " + contact + " ?",TextToSpeech.QUEUE_ADD,null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speakContacts(hm);
                }
            },2000);
        }else if(hm.size()==1){
            Log.d("TAG"," 1 contact name only ");
            hmIterator = hm.entrySet().iterator();
            finalContactName = (String) ((Map.Entry)hmIterator.next()).getKey();
            Toast.makeText(this,finalContactName +"'s" + " number: " + contactsWithTheName.get(finalContactName),Toast.LENGTH_SHORT).show();
            speaker.speak(" Are you sure, you want to message " + finalContactName + " ?",TextToSpeech.QUEUE_ADD,null);
            pauseAndCallSTT(FINAL_CONFIRMATION_OF_CONTACT_NAME,4000);
        }else{
            speaker.speak(" Kindly try again with correct first name ! ",TextToSpeech.QUEUE_ADD,null);
            askForContactName();
        }
    }

    public void speakContacts(HashMap<String,String> h){
        int flag = 0;
        Iterator hmIterator = h.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            if (flag == 0) {
                speaker.speak("Do you want to message "+mapElement.getKey()+" ?",TextToSpeech.QUEUE_ADD,null);
                flag = 1;
            } else {
                speaker.speak("Or "+mapElement.getKey()+" ?",TextToSpeech.QUEUE_ADD,null);
            }
        }
        pauseAndCallSTT(CONFIRM_PERSON_OPTIONS,((h.size())*1000)+3000);
    }

    public String toFirstLetterCapital(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        String name = "";
        String a[] = s.split(" ");
        for(int i=0;i<a.length;i++){
            if(i==a.length-1){
                name += Character.toUpperCase(a[i].charAt(0))+a[i].substring(1);
            }else{
                name += Character.toUpperCase(a[i].charAt(0))+a[i].substring(1)+" ";
            }
        }
        Toast.makeText(this," Contact name is: "+name,Toast.LENGTH_SHORT).show();
        return name;
    }

    public boolean validName(String n, String mName){
        Log.d("TAG","Checking: "+n+" "+mName+"\n");
        if(n.contains(" ")){
            String temp[] = n.split(" ");
            if(temp[0].equalsIgnoreCase(mName)||n.equalsIgnoreCase(mName)){
                return true;
            }
        }else if(n.length()==mName.length()||n.equalsIgnoreCase(mName)){
            return true;
        }
        return false;
    }


    public void sendSMS(String cont, String mess){
        speaker.speak(" Sending your message to " + finalContactName ,TextToSpeech.QUEUE_FLUSH,null);
        smsManager.sendTextMessage(cont,null, mess, null, null);
        Toast.makeText(this,"Message sent successfully!",Toast.LENGTH_SHORT).show();
    }

    public void askForContactName(){
        speaker.speak(" Whom do you want to send the Message? ",TextToSpeech.QUEUE_ADD,null);
        pauseAndCallSTT(1,4000);
    }

    public void askForTheMessage(){
        speaker.speak(" What is the message ? ",TextToSpeech.QUEUE_FLUSH,null);
        pauseAndCallSTT(MESSAGE_CONTENT,3000);
    }

    public void pause(long duration){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        },duration);
    }

    public void pauseAndCallSTT(final int code, long duration){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displaySpeechRecognizer(code);
            }
        },duration);
    }

}
