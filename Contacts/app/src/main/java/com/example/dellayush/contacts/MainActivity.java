package com.example.dellayush.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, CharSequence {

    private static final int CALL_CODE = 1;
    private static final int CONFIRM_PHONE_CALL_TO_PERSON = 2;
    private static final int CONFIRMATION = 100;
    private static final int TTS_INSTALL_CODE = 1000;
    private TextToSpeech speaker;
    String contactNumber="", name="", contactName="", exactFinalContactName="";
    HashMap<String, String> contactsWithTheName;
    Intent callIntent;
    TextView tv;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.textview);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, TTS_INSTALL_CODE);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            speaker.setLanguage(Locale.UK);
            speaker.setSpeechRate((float)0.8);
            speaker.speak("Say call and a person name or their contact to connect to a person.",TextToSpeech.QUEUE_ADD,null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displaySpeechRecognizer(1);
                }
            },5000);
            //displaySpeechRecognizer(1);
            contactsWithTheName = new HashMap<>();
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Text To Speech Not Activated", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TTS_INSTALL_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = new TextToSpeech(this, this);

            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        if (requestCode == CALL_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            String choiceOfContact[] = spokenText.split(" ");
            if (choiceOfContact[0].equalsIgnoreCase("call")) {
                for (int i = 1; i < choiceOfContact.length; i++) {
                    contactName += choiceOfContact[i] + " ";
                }
                contactName = toFirstLetterCapital(contactName);
                Toast.makeText(getApplicationContext(), " " + contactName, Toast.LENGTH_LONG).show();
                if (!contactName.equalsIgnoreCase("")) {
                    call();
                } else {
                    speaker.speak(" Try calling with the name ",TextToSpeech.QUEUE_ADD,null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            displaySpeechRecognizer(1);
                        }
                    },4000);
                }
            }
//            else if((choiceOfContact.length==1)&&(choiceOfContact[0].equalsIgnoreCase("call"))){
//                speaker.speak(" Try calling with the name ",TextToSpeech.QUEUE_ADD,null);
//            }

        }

        if (requestCode == CONFIRM_PHONE_CALL_TO_PERSON && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            exactFinalContactName = toFirstLetterCapital(spokenText);
            if(contactsWithTheName.containsKey(exactFinalContactName)){
                speaker.speak(" Are you sure, you want to call " + exactFinalContactName + " ?",TextToSpeech.QUEUE_ADD,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displaySpeechRecognizer(100);
                    }
                },4000);
            }else{
                speaker.speak(" Sorry, but that wasn't " + contactName + " !",TextToSpeech.QUEUE_ADD,null);
                speaker.speak(" Try again ! ",TextToSpeech.QUEUE_ADD,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speakContacts(contactsWithTheName);
                    }
                },5000);
            }
        }

        if (requestCode == CONFIRMATION && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if(spokenText.equalsIgnoreCase("Yes")||spokenText.equalsIgnoreCase("okay")||spokenText.equalsIgnoreCase("Yeah")){
                callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactsWithTheName.get(toFirstLetterCapital(exactFinalContactName))));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if(contactsWithTheName.get(toFirstLetterCapital(exactFinalContactName))!=null){
                    speaker.speak("Calling "+exactFinalContactName,TextToSpeech.QUEUE_ADD,null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(callIntent);
                        }
                    },3000);
                }else{
                    speaker.speak(" Kindly tell me which "+exactFinalContactName+" ?",TextToSpeech.QUEUE_ADD,null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            displaySpeechRecognizer(100);
                        }
                    },3000);
                }
            } else{
                speaker.speak(" Thank you !",TextToSpeech.QUEUE_ADD,null);
            }
        }
    }

    private void displaySpeechRecognizer(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, code);
    }


    public void speakContacts(HashMap<String,String> h){
        int flag = 0;
        Iterator hmIterator = h.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            if (flag == 0) {
                speaker.speak("Do you want to call "+mapElement.getKey()+" ?",TextToSpeech.QUEUE_ADD,null);
                flag = 1;
            } else {
                speaker.speak("Or "+mapElement.getKey()+" ?",TextToSpeech.QUEUE_ADD,null);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(getApplicationContext(),"All possible options waala tts",Toast.LENGTH_SHORT).show();
                displaySpeechRecognizer(2);
            }
        },9000);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void call() {
        Log.d("TAG"," Call called");
        if (contactName.startsWith("9")||contactName.startsWith("7")||contactName.startsWith("8")) {
            callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contactName));
            startActivity(callIntent);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                        10);
                return;
            }
        } else {
            Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
//            contactName = toFirstLetterCapital(contactName);
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like '%" + contactName + "%'";
            Toast.makeText(getApplicationContext()," Contact Name is "+contactName,Toast.LENGTH_LONG).show();
            Cursor people = getContentResolver().query(uri, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
            if(people.getCount()>0){
                people.moveToFirst();
                try {
                    Log.d("TAG","Total Rows = "+people.getCount());
                    for(int i=0;i<people.getCount();i++){
                        name = people.getString(0);
                        Log.d("TAG",(i+1)+" "+name+" "+contactName+" "+validName(name));
                        if(validName(name)){
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
//                        if(people!=null) {
//                            Log.d("TAG",i+" "+"Contact "+name+" "+number+" "+name.substring(0,contactName.length()).equalsIgnoreCase(contactName));
//                        }
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
            speaker.speak("Which "+contactName+" ?",TextToSpeech.QUEUE_ADD,null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speakContacts(hm);
                }
            },2000);
        }else if(hm.size()==1){
            hmIterator = hm.entrySet().iterator();
            exactFinalContactName = (String) ((Map.Entry)hmIterator.next()).getKey();
            speaker.speak(" Are you sure, you want to call " + exactFinalContactName + " ?",TextToSpeech.QUEUE_ADD,null);
            Log.d("TAG"," 1 contact name only ");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displaySpeechRecognizer(100);
                }
            },4000);
        }else{
            speaker.speak(" Kindly try again with correct first name ? ",TextToSpeech.QUEUE_ADD,null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displaySpeechRecognizer(1);
                }
            },7000);
        }
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

    public boolean validName(String n){
//        Log.d("TAG","Checking: "+n+" "+contactName+"\n");
        if(n.contains(" ")){
            String temp[] = n.split(" ");
            if(temp[0].equalsIgnoreCase(contactName)||n.equalsIgnoreCase(contactName)){
                return true;
            }
        }else if(n.length()==contactName.length()){
            return true;
        }
        return false;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}
