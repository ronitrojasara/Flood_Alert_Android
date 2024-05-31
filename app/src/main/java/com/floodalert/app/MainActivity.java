package com.floodalert.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends Activity {
//    private static final String CHANNEL_ID = "my_channel";
//    int notification_id = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = mDatabase.child("sensor");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            requestNotificationPermission(this);
            try{
                Intent serviceIntent = new Intent(this, MyForegroundService.class);
                startForegroundService(serviceIntent);
            }catch (Exception e){
                Toast.makeText(this, "Service not running", Toast.LENGTH_SHORT).show();
            }

            setContentView(R.layout.activity_main);

//            createNotificationChannel();

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

                    Float waterLevel_value = 50 - snapshot.child("Level").getValue(Float.class);
                        if (waterLevel_value>=0){
                            progressBar3.setProgress(waterLevel_value.intValue());
                            waterLevel.setText(""+waterLevel_value+" ft");
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

//                        if (!(A1_value==1 && A2_value==1 && B1_value==1 && B2_value==1)||waterLevel_value>40){
//                            showNotification("Flood Alert", "Evacuate to higher ground now");
//                        }
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
    public static void requestNotificationPermission(Context context) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            showPermissionDialog(context);
        }
    }

    private static void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Notification Permission Required");
        builder.setMessage("This app requires notification permission. Please enable it in the app settings.");

        builder.setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings(context));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private static void openAppSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

//    private void createNotificationChannel() {
//        CharSequence name = "My Channel";
//        String description = "Channel Description";
//        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//        channel.setDescription(description);
//
//        // Customize channel settings if needed
//        channel.enableLights(true);
//        channel.setLightColor(Color.RED);
//        channel.enableVibration(true);
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//    }
//    private void showNotification(String title, String content) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.priority_high_24px)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        // Optionally, you can set additional properties for the notification, such as actions, intent, etc.
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(++notification_id, builder.build());
//    }

//    @Override
//    protected void onDestroy() {
//        Intent serviceIntent = new Intent(this, MyForegroundService.class);
//        startForegroundService(serviceIntent);
//        super.onDestroy();
//    }


}