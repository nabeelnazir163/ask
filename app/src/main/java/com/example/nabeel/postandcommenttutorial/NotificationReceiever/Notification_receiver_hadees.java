package com.example.nabeel.postandcommenttutorial.NotificationReceiever;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.nabeel.postandcommenttutorial.ui.activities.repeatingActivities.Repeating_activity_hadees;

public class Notification_receiver_hadees extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_Intent =  new Intent(context , Repeating_activity_hadees.class);
        repeating_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_Intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("Ask Alim")
                .setContentText("Daily Hadees")
                .setAutoCancel(true);

        notificationManager.notify(100,builder.build());
    }
}
