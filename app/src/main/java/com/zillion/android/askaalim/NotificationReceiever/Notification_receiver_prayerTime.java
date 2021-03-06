package com.zillion.android.askaalim.NotificationReceiever;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.zillion.android.askaalim.ui.activities.repeatingActivities.Repeating_activity_hadees;
import com.zillion.android.askaalim.ui.activities.repeatingActivities.Repeating_activity_prayertime;

/**
 * Created by Nabeel on 10/20/2017.
 */

public class Notification_receiver_prayerTime extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_Intent =  new Intent(context , Repeating_activity_prayertime.class);
        repeating_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_Intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Ask Alim")
                .setContentText("Prayer Time Alert")
                .setAutoCancel(true);

        notificationManager.notify(100,builder.build());
    }
}
