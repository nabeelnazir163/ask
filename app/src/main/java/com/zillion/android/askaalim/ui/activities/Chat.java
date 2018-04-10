package com.zillion.android.askaalim.ui.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.Chat_model;
import com.zillion.android.askaalim.ui.adapter.Message_Adapter;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zillion.android.askaalim.utils.sendNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {

    String chatWithEmail;
//    String chatWith_image_url;

    String current_user_name;
    String current_user_image;

    String chatwithName;
    String specific_message_id = "";

    //EditText to write message
    private EditText message_edittext;

    //Button to Send Message
    private ImageView sendMessageButton;

    //Recyclerview to show message
    private RecyclerView message_rv;

    //Swipe to refesh layout to load further messages onload
    private SwipeRefreshLayout mRefreshMessage;

    //list to get data from firebase and store in it
    private final List<Chat_model> messageList = new ArrayList<>();

    //LayoutManager to reverse messagelist
    private LinearLayoutManager manager;

    //start your custom Adapter
    private Message_Adapter mAdapter;

    //define static variable to set how many number of messages to show;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;

    private int mCurrentPage = 1;

    //NEW SOLUTION
    private int itemsPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        chatWithEmail = (String) getIntent().getStringExtra("emailforchat")
                .trim().replace(".",",");

        /*if(!getIntent().getStringExtra("image_url").equals("")) {

            chatWith_image_url = (String) getIntent().getStringExtra("image_url");

        }

        if(!getIntent().getStringExtra("msg_id").equals("")) {

            specific_message_id = (String) getIntent().getStringExtra("msg_id").trim();

        }*/

        message_edittext = (EditText) findViewById(R.id.messageArea);
        sendMessageButton = (ImageView) findViewById(R.id.sendButton);

        mAdapter = new Message_Adapter(messageList, Chat.this);

        message_rv = (RecyclerView) findViewById(R.id.message_list_rv);

        mRefreshMessage = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

        manager = new LinearLayoutManager(this);
        message_rv.setHasFixedSize(true);
        message_rv.setLayoutManager(manager);

        message_rv.setAdapter(mAdapter);

        loadMessages();

        final ActionBar actionBar = getSupportActionBar();

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

                current_user_name = (String) dataSnapshot.child("name").getValue();
                current_user_image = (String) dataSnapshot.child("image").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendMessage();

            }
        });

        mRefreshMessage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemsPos = 0;

                loadMoreMessages();

            }
        });

    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = FirebaseUtils.getRootRef().child("messages")
                .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(chatWithEmail);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Chat_model chat_model = dataSnapshot.getValue(Chat_model.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messageList.add(itemsPos++, chat_model);

                } else {

                    mPrevKey = mLastKey;

                }

                if(itemsPos == 1){

                    mLastKey = messageKey;

                }


                mAdapter.notifyDataSetChanged();

                mRefreshMessage.setRefreshing(false);

                manager.scrollToPositionWithOffset(10, 0);
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
        });


    }

    private void loadMessages() {

        DatabaseReference messageRef = FirebaseUtils.getRootRef().child("messages")
                .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(chatWithEmail);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Chat_model chat_model = dataSnapshot.getValue(Chat_model.class);

                itemsPos++;

                if(itemsPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messageList.add(chat_model);
                mAdapter.notifyDataSetChanged();

                message_rv.scrollToPosition(messageList.size() - 1);

//                mRefreshMessage.setRefreshing(false);

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
        });

    }

    private void SendMessage() {

        String message = message_edittext.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + FirebaseUtils.getCurrentUser().getEmail().replace(".",",")
                    + "/" + chatWithEmail;

            String chat_user_Ref = "messages/" + chatWithEmail + "/" + FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

            DatabaseReference user_message_push = FirebaseUtils.getRootRef().child("messages")
                    .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                    .child(chatWithEmail).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("sending_timeStamp", System.currentTimeMillis());
            messageMap.put("from", FirebaseUtils.getCurrentUser().getEmail().replace(".",","));
            messageMap.put("from_image_url", current_user_image);

            Map messageUserMap = new HashMap();

            messageUserMap.put(current_user_ref +"/" + push_id, messageMap );
            messageUserMap.put(chat_user_Ref + "/" + push_id, messageMap);

            message_edittext.setText("");

            FirebaseUtils.getNotificationRef().child(chatWithEmail)
                    .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).push().setValue(true);

            /* START SENDING NOTIFICATION HERE */

            /*if(!FirebaseUtils.getCurrentUser().getEmail().replace(".",",").equals(chatWithEmail)){

                FirebaseUtils.getUserRef(chatWithEmail).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("fcmtoken")) {

                            String FCM_token = (String) dataSnapshot.child("fcmtoken").getValue();
                          //  sendNotification notify = new sendNotification(current_user_name + " send you a message", "", FCM_token);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }*/

            FirebaseUtils.getRootRef().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("LOGERROR", databaseError.getMessage().toString());

                    }

                }
            });

            FirebaseUtils.getChatRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.hasChild(chatWithEmail)){

                        Map chatAdMAp = new HashMap();

                        chatAdMAp.put("sending_timeStamp", System.currentTimeMillis());
                        chatAdMAp.put("seen", true);

                        Map chatuserMap = new HashMap();

                        chatuserMap.put("chat/" + FirebaseUtils.getCurrentUser().getEmail().replace(".",",") +
                                "/" + chatWithEmail, chatAdMAp
                        );
                        chatuserMap.put("chat/" + chatWithEmail +
                                "/" + FirebaseUtils.getCurrentUser().getEmail().replace(".",","), chatAdMAp
                        );

                        FirebaseUtils.getRootRef().updateChildren(chatuserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("ChatError", databaseError.getMessage());

                                }

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
   /* private void send_Message() {

        final ProgressDialog progressDialog = new ProgressDialog(Chat.this);
        progressDialog.setMessage("Sending Message...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);

        messageText = messageArea.getText().toString();
        push_id = FirebaseUtils.getUid();

        if(!TextUtils.isEmpty(messageText)){
            progressDialog.show();

//            DatabaseReference mySendDatabase = FirebaseUtils.getMessageRef()
//                    .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
//                    .child("sentbox").push();
//
//            mySendDatabase.child("receiver_name").setValue(chatwithName);
//            mySendDatabase.child("receiver_email").setValue(chatWithEmail);
//            mySendDatabase.child("message").setValue(messageText);
//            mySendDatabase.child("receiver_image_url").setValue(chatWith_image_url);
//            mySendDatabase.child("message_id").setValue(push_id);
//            mySendDatabase.child("sending_timeStamp").setValue(System.currentTimeMillis());

            DatabaseReference sendToInboxDb = FirebaseUtils.getMessageRef().child(chatWithEmail)
                    .child(push_id);

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

    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }


}