package com.zillion.nabeel.postandcommenttutorial;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zillion.nabeel.postandcommenttutorial.ui.activities.MainActivity;
import com.zillion.nabeel.postandcommenttutorial.ui.activities.PostActivity;
import com.zillion.nabeel.postandcommenttutorial.ui.activities.inbox;
import com.zillion.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.zillion.nabeel.postandcommenttutorial.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Intent intent;
//    public int counter = 0;

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        long[] vPattern = {0,100,1000};
        //for setting default notification sound as notification sound
//        Uri snotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        String postID = remoteMessage.getData().get("postID");

        if(message.contains("message")){
            intent = new Intent(getApplicationContext(), inbox.class);
//            counter++;
//            MainActivity m = new MainActivity();
//            MainActivity.setValue(counter);
//            Log.d("counter", "" + counter);
//            MainActivity.tv.setText(""+2);
        }else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
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

    /*
     @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
     */
}
