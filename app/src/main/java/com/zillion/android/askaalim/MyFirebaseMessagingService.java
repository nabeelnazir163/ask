package com.zillion.android.askaalim;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.zillion.android.askaalim.ui.activities.Chat;
import com.zillion.android.askaalim.ui.activities.MainActivity;
import com.zillion.android.askaalim.ui.activities.inbox;
import com.zillion.android.askaalim.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zillion.android.askaalim.utils.FirebaseUtils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notification_Title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();

        Uri snotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user = remoteMessage.getData().get("emailforchat");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo24x24)
                .setContentTitle(notification_Title)
                .setSound(snotification)
                .setContentText(notification_body);

        Intent result_intent = new Intent(click_action);

        result_intent.putExtra("emailforchat", from_user);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                result_intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);

        int notificationID = (int) System.currentTimeMillis();

        NotificationManager mNotifyMngr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMngr.notify(notificationID, mBuilder.build());

//        long[] vPattern = {0, 100, 1000};
//
//        //for setting default notification sound as notification sound
//        Uri snotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        if (FirebaseUtils.getCurrentUser() != null) {
//
//            String title = remoteMessage.getNotification().getTitle();
//            String message = remoteMessage.getNotification().getBody();
//            String postID = remoteMessage.getData().get("postID");
//
//            assert message != null;
//            if (message.contains("message")) {
//
//                intent = new Intent(getApplicationContext(), inbox.class);
//
//            } else {
//
//                intent = new Intent(getApplicationContext(), MainActivity.class);
//
//            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//            intent.putExtra(Constants.EXTRA_POST, postID);
//            notificationBuilder.setContentTitle(title);
//            notificationBuilder.setContentText(message);
//            notificationBuilder.setSmallIcon(R.drawable.logo24x24);
//            notificationBuilder.setAutoCancel(true);
//            notificationBuilder.setVibrate(vPattern);
////            notificationBuilder.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.snotification));
//            notificationBuilder.setSound(snotification);
//            notificationBuilder.setContentIntent(pendingIntent);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(0, notificationBuilder.build());
//
//        }
    }

}
