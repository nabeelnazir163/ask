package com.zillion.android.askaalim;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.zillion.android.askaalim.NotificationReceiever.Notification_receiver_hadees;
import com.zillion.android.askaalim.NotificationReceiever.Notification_receiver_hijriEvent;
import com.zillion.android.askaalim.NotificationReceiever.Notification_receiver_prayerTime;
import com.zillion.android.askaalim.NotificationReceiever.Notification_receiver_unAnsweredQues;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Date;

public class AskApplication extends Application {

    //int userType;
    SharedPreferences hadeesPrefs , calPrefs , prayerTimePrefs , unAnsweredQuestionPrefs ;

    public static final String HADEES_PREFS = "hadeesPrefs";
    public static final String CAL_PREFS = "calPrefs";
    public static final String PRAYER_TIME_PREFS = "prayertimePrefs";
    public static final String UNANSWERED_QUES_PREFS = "unAnsweredQuestionPrefs";

    boolean hadeesState;
    boolean calState;
    boolean prayertimeState;
    boolean unAnsweredState;

    SharedPreferences userType_sp;
    SharedPreferences.Editor userType_sh_editor;

    FirebaseAuth mAuth;

    Calendar rightNow;
//    int currentHour;
//    int current_min;

    @Override
    public void onCreate() {
        super.onCreate();
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        rightNow = Calendar.getInstance();
//        currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
//        current_min = rightNow.get(Calendar.MINUTE);

//        if(rightNow.getTime().compareTo(new Date()) < 0)
//            rightNow.add(Calendar.DAY_OF_MONTH, 1);
//
//        Toast.makeText(AskApplication.this, rightNow.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();

        hadeesPrefs = getSharedPreferences(HADEES_PREFS, 0);
        hadeesState = hadeesPrefs.getBoolean("hadees", false);

        calPrefs = getSharedPreferences(CAL_PREFS, 0);
        calState = calPrefs.getBoolean("calender", false);

        prayerTimePrefs = getSharedPreferences(PRAYER_TIME_PREFS, 0);
        prayertimeState = prayerTimePrefs.getBoolean("prayertime", false);

        unAnsweredQuestionPrefs = getSharedPreferences(UNANSWERED_QUES_PREFS, 0);
        unAnsweredState = unAnsweredQuestionPrefs.getBoolean("unAnsweredQues", false);

        if (hadeesState) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 10);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_hadees.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            if(calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        if (calState) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 12);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_hijriEvent.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        if (prayertimeState) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 14);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_prayerTime.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        if (unAnsweredState) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 16);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_unAnsweredQues.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        if (mAuth.getCurrentUser() != null) {

            if (mAuth.getCurrentUser().getEmail() != null) {

                String currentemail = mAuth.getCurrentUser().getEmail().replace(".", ",");

                userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
                userType_sh_editor = userType_sp.edit();

                if(!TextUtils.isEmpty(currentemail)){

                    FirebaseUtils.getUserRef(currentemail).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild("userType")){

                                String userType = dataSnapshot.child("userType").getValue().toString();

                            if (userType.equals("Alim")) {

                                userType_sh_editor.putInt("UserType", 1);
                                userType_sh_editor.apply();

                            } else if (userType.equals("User")) {

                                userType_sh_editor.putInt("UserType", 2);
                                userType_sh_editor.apply();

                            }
                        }
                    }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        }
    }
}