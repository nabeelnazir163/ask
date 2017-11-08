package com.example.nabeel.postandcommenttutorial.ui.activities;

/**
 * Created by Nabeel on 10/28/2017.
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Chat extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    String chatWithEmail;
    String current_user;
    String current_user_name;
    String current_user_image;
    String chatwithName;
    String push_id;
    String specific_message_id;

    String messageText;

    String message_received;

    TextView textView;
    TextView textview_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        textView = (TextView)findViewById(R.id.text_message_left);
        textview_right = (TextView) findViewById(R.id.text_message_right);

        chatWithEmail = (String) getIntent().getSerializableExtra("emailforchat").toString().trim().replace(".",",");

        specific_message_id = (String) getIntent().getSerializableExtra("msg_id").toString().trim();

        FirebaseUtils.getMessageRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child("inbox").child(specific_message_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                message_received = dataSnapshot.child("message").getValue().toString();
                textView.setText(message_received);
                textView.setBackgroundResource(R.drawable.rounded_corner1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ActionBar actionBar = getSupportActionBar();

        current_user = FirebaseUtils.getCurrentUser().getEmail().toString().replace(".",",");

        FirebaseUtils.getUserRef(chatWithEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatwithName = dataSnapshot.child("name").getValue().toString();

                actionBar.setTitle(chatwithName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseUtils.getUserRef(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                current_user_name = dataSnapshot.child("name").getValue().toString();
                current_user_image = dataSnapshot.child("image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(Chat.this);
                progressDialog.setMessage("Sending Message...");
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                messageText = messageArea.getText().toString();
                push_id = FirebaseUtils.getUid();

                if(!messageText.equals("")){

                    DatabaseReference mySendDatabase = FirebaseUtils.getMessageRef()
                            .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                            .child("sentbox").push();

                    mySendDatabase.child("sendtoName").setValue(chatwithName);
                    mySendDatabase.child("sendtoemail").setValue(chatWithEmail);
                    mySendDatabase.child("message").setValue(messageText);

//                    Toast.makeText(getApplicationContext(), current_user_image , Toast.LENGTH_SHORT).show();

                    DatabaseReference sendToInboxDb = FirebaseUtils.getMessageRef().child(chatWithEmail)
                            .child("inbox").child(push_id);

                    sendToInboxDb.child("Sender_name").setValue(current_user_name);
                    sendToInboxDb.child("sender_image_url").setValue(current_user_image);
                    sendToInboxDb.child("Sender_email").setValue(FirebaseUtils.getCurrentUser().getEmail().replace(".",","));
                    sendToInboxDb.child("message").setValue(messageText);
                    sendToInboxDb.child("message_id").setValue(push_id);
                    sendToInboxDb.child("SendingtimeStamp").setValue(System.currentTimeMillis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            textview_right.setText(messageText);
                            textview_right.setBackgroundResource(R.drawable.rounded_corner2);
                            progressDialog.dismiss();
                            finish();

                        }
                    });

                }

            }
        });
    }

       /* reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = ( Map<String, String>)dataSnapshot.getValue();
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(current_user)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox(chatwithName + ":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

//        scrollView.fullScroll(View.FOCUS_DOWN);
    }*/

    public void addMessageBox(String message, int type){
        TextView textView = (TextView)findViewById(R.id.text_message_left);
        TextView textview_right = (TextView) findViewById(R.id.text_message_right);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);


        if(type == 1) {
            textView.setText(message);
            textView.setBackgroundResource(R.drawable.rounded_corner1);

        }
        else{
            textview_right.setText(message);
            textView.setBackgroundResource(R.drawable.rounded_corner2);

        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void sendNotification(final String cu_user)
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

                    send_email = chatWithEmail;

                    //String user_name = homeFragment.login_user_name;

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
                                + "\"contents\": {\"en\":\"" + cu_user + " Send You a message\"}"
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
    }
}