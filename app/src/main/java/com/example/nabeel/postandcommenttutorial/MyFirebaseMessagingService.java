/*
package com.example.nabeel.postandcommenttutorial;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.nabeel.postandcommenttutorial.ui.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if(remoteMessage.getData().size() > 0){

            Toast.makeText(getApplicationContext() , "ALHAMDULLILAH" , Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(getApplicationContext() , "ALHAMDULLILAH :)" , Toast.LENGTH_LONG).show();


        }


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(this);
        notification_builder.setContentTitle("ASK ALIM");
        notification_builder.setContentText(remoteMessage.getNotification().getBody());
        notification_builder.setAutoCancel(true);
        notification_builder.setSmallIcon(R.mipmap.ic_launcher);
        notification_builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,notification_builder.build());

    }
}
*/
