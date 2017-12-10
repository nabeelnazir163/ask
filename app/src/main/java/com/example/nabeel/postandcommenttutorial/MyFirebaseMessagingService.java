package com.example.nabeel.postandcommenttutorial;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.nabeel.postandcommenttutorial.ui.activities.PostActivity;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        long[] vPattern = {0,100,1000};
        //for setting default notification sound as notification sound
//        Uri snotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        String postID = remoteMessage.getData().get("postID");
        Intent intent = new Intent(getApplicationContext(), homeFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        intent.putExtra(Constants.EXTRA_POST,postID);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setSmallIcon(R.drawable.logo24x24);
        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setSound(snotification);
        notificationBuilder.setVibrate(vPattern);
        notificationBuilder.setSound(Uri.parse("android.resource://"+ getApplicationContext().getPackageName()+"/"+R.raw.snotification));
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
}