package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.zillion.nabeel.postandcommenttutorial.utils.sendNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    String chatWithEmail;
    String chatWith_image_url;
//    String current_user;
    String current_user_name;
    String current_user_image;
    String chatwithName;
    String push_id;
    String specific_message_id = "";

    String messageText;

    String message_received;

    TextView textView;
    TextView textview_right;

    String FCM_token,Current_UserName;
    String postID = null;
    DatabaseReference firebaseDatabase1;
    DatabaseReference firebaseDatabase2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        textView = (TextView)findViewById(R.id.text_message_left);
        textview_right = (TextView) findViewById(R.id.text_message_right);

        chatWithEmail = (String) getIntent().getSerializableExtra("emailforchat")
                .toString().trim().replace(".",",");

        if(!getIntent().getSerializableExtra("image_url").toString().equals("")) {

            chatWith_image_url = (String) getIntent().getSerializableExtra("image_url")
                    .toString();

        }

        if(!getIntent().getSerializableExtra("msg_id").toString().equals("")) {

            specific_message_id = (String) getIntent().getSerializableExtra("msg_id").toString().trim();

        }

        if(!specific_message_id.equals("")) {

            FirebaseUtils.getMessageRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                    .child("inbox").child(specific_message_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    message_received = dataSnapshot.child("message").getValue().toString();
                    textView.setText("\n" + message_received + "\n");
                    textView.setBackgroundResource(R.drawable.rounded_corner1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        final ActionBar actionBar = getSupportActionBar();

//        current_user = FirebaseUtils.getCurrentUser().getEmail().toString().replace(".",",");

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

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                current_user_name = dataSnapshot.child("name").getValue().toString();
                current_user_image = dataSnapshot.child("image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*messageArea.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                // If the event is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    send_Message();

                    return true;
                }
                return false;
            }
        }); */


        layout = (LinearLayout) findViewById(R.id.layout1);
//        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send_Message();

            }
        });

/*        firebaseDatabase1 = FirebaseDatabase.getInstance().getReference().child("message")
                .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).child(chatWithEmail.replace(".",","));
        firebaseDatabase2 = FirebaseDatabase.getInstance().getReference().child("message")
                .child(chatWithEmail.replace(".",",")).child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map map = new HashMap();
                    map.put("message", messageText);
                    map.put("Sender_name", current_user_name);
                    map.put("receiver_name", chatwithName );
                    map.put("receiver_email",chatWithEmail);
                    map.put("sender_image_url", current_user_image);
                    map.put("receiver_image_uri", chatWith_image_url);
                    map.put("sender_email", FirebaseUtils.getCurrentUser().getEmail());
                    map.put("sending_timeStamp", System.currentTimeMillis());

                    if(!FirebaseUtils.getCurrentUser().getEmail().replace(".",",").equals(chatWithEmail.replace(".",","))) {
                        firebaseDatabase1.push().setValue(map);
                        firebaseDatabase2.push().setValue(map);
                    }else {
                        firebaseDatabase1.push().setValue(map);
                    }
                    messageArea.setText("");
                }
            }
        });

        firebaseDatabase1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = (Map) dataSnapshot.getValue();
                String message = map.get("message").toString();
                String userName = map.get("Sender_name").toString();

//                Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();

                if(userName.equals(FirebaseUtils.getCurrentUser().getEmail())){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox(chatWithEmail+ ":-\n" + message, 2);
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
            public void onCancelled(DatabaseError databaseError) {

            }

        });*/

    }

    /*public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setPadding(15,15,15,15);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
        }
        else{
            lp2.gravity = Gravity.LEFT;
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }*/

    private void send_Message() {

        final ProgressDialog progressDialog = new ProgressDialog(Chat.this);
        progressDialog.setMessage("Sending Message...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);

        messageText = messageArea.getText().toString();
        push_id = FirebaseUtils.getUid();

        if(!TextUtils.isEmpty(messageText)){
            progressDialog.show();

            DatabaseReference mySendDatabase = FirebaseUtils.getMessageRef()
                    .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                    .child("sentbox").push();

            mySendDatabase.child("receiver_name").setValue(chatwithName);
            mySendDatabase.child("receiver_email").setValue(chatWithEmail);
            mySendDatabase.child("message").setValue(messageText);
            mySendDatabase.child("receiver_image_url").setValue(chatWith_image_url);
            mySendDatabase.child("message_id").setValue(push_id);
            mySendDatabase.child("sending_timeStamp").setValue(System.currentTimeMillis());

            DatabaseReference sendToInboxDb = FirebaseUtils.getMessageRef().child(chatWithEmail)
                    .child("inbox").child(push_id);

            sendToInboxDb.child("Sender_name").setValue(current_user_name);
            sendToInboxDb.child("sender_image_url").setValue(current_user_image);
            sendToInboxDb.child("sender_email").setValue(FirebaseUtils.getCurrentUser().getEmail().replace(".",","));
            sendToInboxDb.child("message").setValue(messageText);
            sendToInboxDb.child("message_id").setValue(push_id);
            sendToInboxDb.child("sending_timeStamp").setValue(System.currentTimeMillis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    textview_right.append("\n"+messageText+"\n");
                    textview_right.setBackgroundResource(R.drawable.rounded_corner2);
                    progressDialog.dismiss();
                    messageArea.setText("");

                    final String C_Current_user =FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

                    FirebaseUtils.getUserRef(C_Current_user).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Current_UserName = dataSnapshot.child("name").getValue().toString();

                            FirebaseUtils.getUserRef(chatWithEmail.replace(".",",")).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(!chatWithEmail.replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))) {

                                        if (dataSnapshot.hasChild("fcmtoken")) {

                                            FCM_token = dataSnapshot.child("fcmtoken").getValue().toString();

                                            sendNotification notify = new sendNotification(Current_UserName + " send you a message", postID, FCM_token);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }


}