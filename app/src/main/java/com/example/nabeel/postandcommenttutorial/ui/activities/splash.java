package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.nabeel.postandcommenttutorial.R;

public class splash extends AppCompatActivity {

    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        final int welcomeScreenDisplay = 2000;
        /** create a thread to show splash up to splash time */
        Thread welcomeThread = new Thread() {
            int wait = 0;

            @Override
            public void run() {
                try {
                    sleep(1000);
                    Intent i = new Intent(splash.this,MainActivity.class);
                    startActivity(i);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        };
        welcomeThread.start();


    }
}
