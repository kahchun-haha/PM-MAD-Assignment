package com.example.horapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
 
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Objects;

public class TryService extends Service {
private boolean isStarted = false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isStarted){
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users/notification");
            mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    if(!data.getKey().equals("Email")){
                        if(Objects.equals(data.child("email").getValue(String.class), currentUserEmail)){

                            Intent intent = new Intent(getApplicationContext(),Chat.class);
                            intent.putExtra("otherUser",data.child("from").getValue(String.class));

                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                            final String CHANNELID = "Push Notification";
                            NotificationChannel channel = new NotificationChannel(
                                    CHANNELID,
                                    CHANNELID,
                                    NotificationManager.IMPORTANCE_HIGH
                            );

                            getSystemService(NotificationManager.class).createNotificationChannel(channel);
                            Notification.Builder notification = new Notification.Builder(getApplicationContext(), CHANNELID)
                                    .setContentText(data.child("message").getValue(String.class))
                                    .setContentTitle(data.child("from").getValue(String.class))
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.mipmap.peace_icon_foreground)
                                    .setColor(getApplicationContext().getColor(R.color.white));


                            startForeground(1001, notification.build());

                           data.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(1001, notification.build());
            System.out.println("only once");
        isStarted = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("u die");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
