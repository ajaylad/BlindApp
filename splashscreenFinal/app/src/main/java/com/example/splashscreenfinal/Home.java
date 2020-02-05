package com.example.splashscreenfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.common.collect.Range;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {
    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference mref;
    DatabaseReference mdup;
    Double latitude=0.0;
    Double longitude=0.0;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_home);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mref = mdatabase.getReference();
        mdup=database.getReference();
        editText1=findViewById(R.id.editText1);
        editText2=findViewById(R.id.editText2);
        editText3=findViewById(R.id.editText3);
        editText4=findViewById(R.id.editText4);
        awesomeValidation.addValidation(this, R.id.editText1, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.editText3, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.editText2, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.editText4, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        if (awesomeValidation.validate()) {
            String bName=editText1.getText().toString();
            String cBlind=editText2.getText().toString();
            String gName=editText3.getText().toString();
            String gContact=editText4.getText().toString();
            Log.d("TAG",bName+" "+cBlind+" "+gName+" "+gContact);
            mref.child("Blind").child(cBlind).child("BlindName").setValue(bName);
            mref.child("Blind").child(cBlind).child("Location").child("Latitude").setValue(latitude);
            mref.child("Blind").child(cBlind).child("Location").child("Longitude").setValue(longitude);
            mref.child("Blind").child(cBlind).child("Guardian").child("Name").setValue(gName);
            mref.child("Blind").child(cBlind).child("Guardian").child("Contact").setValue(gContact);
            mref.child("Guardian").child("Guardian").child("BlindName").setValue(bName);
            mref.child("Guardian").child(gContact).child("BlindContact").setValue(cBlind);
            Intent intent=new Intent(this,ButtonSelection.class);
            startActivity(intent);
        }

    }

//    public void fetchData(View view){
//        mref.child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot d : dataSnapshot.getChildren()) {
//                    String lat = d.child("latitude").getValue(String.class);
//                    String lng = d.child("longitude").getValue(String.class);
//                    Log.d("TAG","Latitude"+ " "+latitude);
//                    Log.d("TAG", "Longitude"+" "+longitude);
//                    // setmarkers(lat, lng);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void onclick(View v){
        submitForm();
    }
}
