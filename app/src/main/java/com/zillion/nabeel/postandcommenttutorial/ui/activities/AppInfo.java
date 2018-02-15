package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.zillion.nabeel.postandcommenttutorial.NotificationReceiever.Notification_receiver_hadees;
import com.zillion.nabeel.postandcommenttutorial.NotificationReceiever.Notification_receiver_hijriEvent;
import com.zillion.nabeel.postandcommenttutorial.NotificationReceiever.Notification_receiver_prayerTime;
import com.zillion.nabeel.postandcommenttutorial.NotificationReceiever.Notification_receiver_unAnsweredQues;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.utils.BaseActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

public class  AppInfo extends BaseActivity {

    Switch hadees, HijriCal, PrayerTime, unAnsweredQues;

    public static final String HADEES_PREFS = "hadeesPrefs";
    public static final String CAL_PREFS = "calPrefs";
    public static final String PRAYER_TIME_PREFS = "prayertimePrefs";
    public static final String UNANSWERED_QUES_PREFS = "unAnsweredQuestionPrefs";

    SharedPreferences hadeesPrefs , calPrefs , prayerTimePrefs, unAnsweredQuestionPrefs;

    SharedPreferences.Editor Hadeeseditor , CalEditor , PrayerTimeEditor , unAnsQuesEditor;

    SharedPreferences userType_sp;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        hadees = (Switch) findViewById(R.id.dailyhadeesSwitch);
        HijriCal = (Switch) findViewById(R.id.hijriCalUpdateswitch);
        PrayerTime = (Switch) findViewById(R.id.prayertimeswitch);
        unAnsweredQues = (Switch) findViewById(R.id.unansweredQuestion);

        userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
        userType = userType_sp.getInt("UserType", 0);

        if(userType == 3 || userType == 2){
            unAnsweredQues.setVisibility(View.GONE);
            unAnsweredQues.setChecked(false);
        }

        hadeesPrefs = getSharedPreferences(HADEES_PREFS,0);
        Hadeeseditor = hadeesPrefs.edit();
        hadees.setChecked(hadeesPrefs.getBoolean("hadees", false));

        calPrefs = getSharedPreferences(CAL_PREFS,0);
        CalEditor = calPrefs.edit();
        HijriCal.setChecked(calPrefs.getBoolean("calender", false));

        prayerTimePrefs = getSharedPreferences(PRAYER_TIME_PREFS,0);
        PrayerTimeEditor = prayerTimePrefs.edit();
        PrayerTime.setChecked(prayerTimePrefs.getBoolean("prayertime", false));

        unAnsweredQuestionPrefs = getSharedPreferences(UNANSWERED_QUES_PREFS,0);
        unAnsQuesEditor = unAnsweredQuestionPrefs.edit();
        unAnsweredQues.setChecked(unAnsweredQuestionPrefs.getBoolean("unAnsweredQues", false));

        hadees.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {

                    Hadeeseditor.putBoolean("hadees", true);
                    Hadeeseditor.commit();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 6);
                    calendar.set(Calendar.MINUTE, 0);

                    Intent intent = new Intent(getApplicationContext(), Notification_receiver_hadees.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                } else if (!isChecked){

                    Hadeeseditor.putBoolean("hadees", false);
                    Hadeeseditor.commit();

                }
            }
        });
        HijriCal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 7);
                    calendar.set(Calendar.MINUTE, 00);

                    Intent intent = new Intent(getApplicationContext(), Notification_receiver_hijriEvent.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                    CalEditor.putBoolean("calender", true);
                    CalEditor.commit();

                } else if (!isChecked){

                    CalEditor.putBoolean("calender", false);
                    CalEditor.commit();

                }

            }
        });

        PrayerTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 14);
                    calendar.set(Calendar.MINUTE, 30);

                    Intent intent = new Intent(getApplicationContext(), Notification_receiver_prayerTime.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                    PrayerTimeEditor.putBoolean("prayertime", true);
                    PrayerTimeEditor.commit();

                } else if (!isChecked){

                    PrayerTimeEditor.putBoolean("prayertime", false);
                    PrayerTimeEditor.commit();

                }
            }
        });

        unAnsweredQues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 15);
                    calendar.set(Calendar.MINUTE, 00);

                    Intent intent = new Intent(getApplicationContext(), Notification_receiver_unAnsweredQues.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                    unAnsQuesEditor.putBoolean("unAnsweredQues", true);
                    unAnsQuesEditor.commit();

                } else if (!isChecked){

                    unAnsQuesEditor.putBoolean("unAnsweredQues", false);
                    unAnsQuesEditor.commit();

                }

            }
        });

//
//        Button btn = (Button) findViewById(R.id.btn_notification);
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //sendNotification();
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.HOUR_OF_DAY,6);
//                calendar.set(Calendar.MINUTE,30);
//
//                Intent intent = new Intent(getApplicationContext(),Notification_receiver_hadees.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext() , 100 , intent , PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
//
//            }
//        });

//        hadeesPrefs = getSharedPreferences(HADEES_PREFS, 0);
//        hadeesState = hadeesPrefs.getBoolean("hadees", false);
//
//        calPrefs = getSharedPreferences(CAL_PREFS, 0);
//        calState = calPrefs.getBoolean("calender", false);
//
//        prayerTimePrefs = getSharedPreferences(PRAYER_TIME_PREFS, 0);
//        prayertimeState = prayerTimePrefs.getBoolean("prayertime", false);
//
//        unAnsweredQuestionPrefs = getSharedPreferences(UNANSWERED_QUES_PREFS, 0);
//        unAnsweredState = unAnsweredQuestionPrefs.getBoolean("unAnsweredQues", false);

        /*if (hadeesState == true) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 30);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_hadees.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        if (calState == true) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 32);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_hijriEvent.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        if (prayertimeState == true) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 34);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_prayerTime.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

        if (unAnsweredState == true) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 50);

            Intent intent = new Intent(getApplicationContext(), Notification_receiver_unAnsweredQues.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }*/
    }

    /*private void sendNotification()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (homeFragment.Loggedin_user_email.equals("nabeelnazir163@yahoo.com")) {
                        send_email = "nabeelnazir163@gmail.com";
                    } else {
                        send_email = "nabeelnazir163@yahoo.com";
                    }

//                    Toast.makeText(getApplicationContext() , "e" , Toast.LENGTH_LONG).show();

                    Log.d("AppInfo", "Checking");

                    try {


                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NTgxNWU0OTItMmIxZS00OWM5LWJhYWEtN2NhZGViNTg1OTFk");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"989e3989-b3e5-46e5-9cf9-8dae56df8bec\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"English Message\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }
}
