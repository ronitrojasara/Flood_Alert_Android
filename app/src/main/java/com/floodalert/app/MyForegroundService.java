package com.floodalert.app;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    int notification_id = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        createNotificationChannel0();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Your background logic goes here
        // For simplicity, let's just show a notification
        showNotification("Flood Alert", "We will notify you immediately if flooding is detected in your area.");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("sensor");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("TP1").getValue(Integer.class)==0
                        ||dataSnapshot.child("TP2").getValue(Integer.class)==0
                        ||dataSnapshot.child("TP3").getValue(Integer.class)==0
                        ||dataSnapshot.child("TP4").getValue(Integer.class)==0||dataSnapshot.child("Level").getValue(Integer.class)<10){
                    showNotification0("Flood Alert", "Evacuate to higher ground now");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error","cancelled");
            }
        };
        mDatabase.addValueEventListener(postListener);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private void showNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_shield_24)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(1, builder.build());
    }
    private void showNotification0(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "my_channel")
                .setSmallIcon(R.drawable.priority_high_24px)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Optionally, you can set additional properties for the notification, such as actions, intent, etc.

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(++notification_id, builder.build());
    }
    private void createNotificationChannel0() {
        CharSequence name = "My Channel 2";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("my_channel2", name, importance);
        channel.setDescription(description);

        // Customize channel settings if needed
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}