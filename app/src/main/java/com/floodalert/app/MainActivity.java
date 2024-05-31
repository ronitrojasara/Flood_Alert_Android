package com.floodalert.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = mDatabase.child("sensor");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            setContentView(R.layout.activity_main);
            ProgressBar progressBar = findViewById(R.id.circularSeekBar);
            ProgressBar progressBar2 = findViewById(R.id.circularSeekBar2);

            TextView temperature = findViewById(R.id.temperature);
            TextView humidity = findViewById(R.id.humidity);

            ProgressBar progressBar3 = findViewById(R.id.SeekBar);

            TextView waterLevel = findViewById(R.id.water_level);

            TextView A1 = findViewById(R.id.a1);
            TextView A2 = findViewById(R.id.a2);
            TextView B1 = findViewById(R.id.b1);
            TextView B2 = findViewById(R.id.b2);

            SeekBar gate = findViewById(R.id.gate);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Float humidity_value = snapshot.child("humidity").getValue(Float.class);
                    if (humidity_value!=null){
                    progressBar.setProgress(humidity_value.intValue());
                    humidity.setText(""+humidity_value);
                    }

                    Float temperature_value = snapshot.child("temperature").getValue(Float.class);
                    if (temperature_value!=null){
                        progressBar2.setProgress(temperature_value.intValue());
                        temperature.setText(""+temperature_value+ "°C");
                    }

                    Float waterLevel_value = snapshot.child("Level").getValue(Float.class);
                    if (waterLevel_value!=null){
                        waterLevel_value = 50 - waterLevel_value;
                        if (waterLevel_value>=0){
                            progressBar3.setProgress(waterLevel_value.intValue());
                            waterLevel.setText(""+waterLevel_value+" ft");
                        }
                    }

                    Integer A1_value = snapshot.child("TP1").getValue(Integer.class);
                    Integer A2_value = snapshot.child("TP2").getValue(Integer.class);
                    Integer B1_value = snapshot.child("TP3").getValue(Integer.class);
                    Integer B2_value = snapshot.child("TP4").getValue(Integer.class);

                    if (A1_value!=null && A2_value!=null && B1_value!=null && B2_value!=null){
                        if (A1_value==1){
                            A1.setBackgroundColor(Color.rgb(0,255,0));
                            A1.setTextColor(Color.rgb(0,0,0));
                        }else{
                            A1.setTextColor(Color.rgb(255,255,255));
                            A1.setBackgroundColor(Color.rgb(255,0,0));
                        }

                        if (A2_value==1){
                            A2.setBackgroundColor(Color.rgb(0,255,0));
                            A2.setTextColor(Color.rgb(0,0,0));
                        }else {
                            A2.setTextColor(Color.rgb(255,255,255));
                            A2.setBackgroundColor(Color.rgb(255,0,0));
                        }

                        if (B1_value==1){
                            B1.setBackgroundColor(Color.rgb(0,255,0));
                            B1.setTextColor(Color.rgb(0,0,0));
                        }else {
                            B1.setTextColor(Color.rgb(255,255,255));
                            B1.setBackgroundColor(Color.rgb(255,0,0));
                        }

                        if (B2_value==1){
                            B2.setBackgroundColor(Color.rgb(0,255,0));
                            B2.setTextColor(Color.rgb(0,0,0));
                        }else{
                            B2.setTextColor(Color.rgb(255,255,255));
                            B2.setBackgroundColor(Color.rgb(255,0,0));
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            gate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mDatabase.child("gate").setValue(seekBar.getProgress());
                }
            });
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


    }
}