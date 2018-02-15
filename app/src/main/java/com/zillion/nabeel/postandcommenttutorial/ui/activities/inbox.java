package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zillion.nabeel.postandcommenttutorial.models.inbox_model;

import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.ui.adapter.UserListAdapter;
import com.zillion.nabeel.postandcommenttutorial.ui.adapter.inbox_adapter;
import com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.ArrayList;

public class inbox extends AppCompatActivity {

//    DatabaseReference mDatabase;
    TextView newMessage;
    TextView viewSentMessages;
    TextView noNewMessage;

    private inbox_adapter mAdapter;

    ArrayList<inbox_model> inbox_array_message;
    private RecyclerView commentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        newMessage = (TextView) findViewById(R.id.newMessageInbox);
        viewSentMessages = (TextView) findViewById(R.id.sentMessageInbox);
        noNewMessage = (TextView) findViewById(R.id.no_new_msg);


//        commentRecyclerView = (ListView) findViewById(R.id.inbox_rv);
//        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
//        commentRecyclerView.setLayoutManager(mLayoutManager);

//        inbox_array_message = new ArrayList<>();

        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inbox.this , search_user_for_newMessage.class);
                intent.putExtra("askFor", "");
                startActivity(intent);
//                inbox_array_message.clear();
            }
        });

//        mAdapter = new inbox_adapter(inbox.this, R.layout.row_inbox, inbox_array_message);

//        mAdapter.notifyDataSetChanged();

        viewSentMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inbox.this , sent_messages.class);
                startActivity(intent);
            }
        });

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            ActionBar ab = getSupportActionBar();
            ab.setTitle("Inbox");

        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Inbox");

//        FirebaseDatabase.getInstance().getReference().child("message")
//                .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    for (final DataSnapshot singlesnap : snapshot.getChildren()) {
//
//                        if(!TextUtils.isEmpty(singlesnap.child("message").getValue().toString())){
//
//                            Toast.makeText(getApplicationContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//
//                            commentRecyclerView.setVisibility(View.VISIBLE);
//                            noNewMessage.setVisibility(View.GONE);
//
//                            if(singlesnap.getValue(inbox_model.class) != null) {
//                                if(inbox_array_message.contains(singlesnap.child("Sender_name").getValue().toString())) {
//                                    inbox_array_message.add(singlesnap.getValue(inbox_model.class));
//                                }
//                            }
//                            commentRecyclerView.setAdapter(mAdapter);
//
//                            commentRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                                    Intent chatintent = new Intent(inbox.this , Chat.class);
//                                    chatintent.putExtra("emailforchat", singlesnap.child("sender_email").getValue().toString());
//                                    chatintent.putExtra("image_url", singlesnap.child("sender_image_url").getValue().toString());
//                                    startActivity(chatintent);
//
//                                }
//                            });
//
////                                       viewHolder.setUsername(singlesnap.child("receiver_name").getValue().toString());
////                                       viewHolder.setMessage(singlesnap.child("message").getValue().toString());
////                                       Glide.with(inbox.this).load(singlesnap.child("sender_image_url").getValue().toString())
////                                               .into(viewHolder.messageSenderImageView);
////                                       viewHolder.setTime(DateUtils.getRelativeTimeSpanString(Long.parseLong(singlesnap.child("sending_timeStamp").getValue().toString())));
//
//                            /*viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                    Intent chatintent = new Intent(inbox.this , Chat.class);
//                                    chatintent.putExtra("emailforchat", singlesnap.child("sender_email").getValue().toString());
//                                    chatintent.putExtra("image_url", singlesnap.child("sender_image_url").getValue().toString());
//                                    startActivity(chatintent);
//
//                                }
//                            });*/
//
//                        }  else {
//                            commentRecyclerView.setVisibility(View.GONE);
//                            noNewMessage.setVisibility(View.VISIBLE);
//                        }
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        initinboxSection();

    }

    private void initinboxSection() {

        commentRecyclerView = (RecyclerView) findViewById(R.id.inbox_rv);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<inbox_model, inboxHolder> commentAdapter = new FirebaseRecyclerAdapter<inbox_model, inboxHolder>(
                inbox_model.class,
                R.layout.row_inbox,
                inboxHolder.class,
                FirebaseDatabase.getInstance().getReference().child("message")
                        .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).child("inbox")
        ) {
            @Override
            protected void populateViewHolder(final inboxHolder viewHolder, final inbox_model model, int position) {

                  /*  FirebaseDatabase.getInstance().getReference().child("message")
                            .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                for (final DataSnapshot singlesnap : snapshot.getChildren()) {

                                   if(!TextUtils.isEmpty(singlesnap.child("message").getValue().toString())){

                                       commentRecyclerView.setVisibility(View.VISIBLE);
                                       noNewMessage.setVisibility(View.GONE);

                                       inbox_array_message.add(singlesnap.getValue(inbox_model.class));


//                                       viewHolder.setUsername(singlesnap.child("receiver_name").getValue().toString());
//                                       viewHolder.setMessage(singlesnap.child("message").getValue().toString());
//                                       Glide.with(inbox.this).load(singlesnap.child("sender_image_url").getValue().toString())
//                                               .into(viewHolder.messageSenderImageView);
//                                       viewHolder.setTime(DateUtils.getRelativeTimeSpanString(Long.parseLong(singlesnap.child("sending_timeStamp").getValue().toString())));

                                       viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {

                                               Intent chatintent = new Intent(inbox.this , Chat.class);
                                               chatintent.putExtra("emailforchat", singlesnap.child("sender_email").getValue().toString());
                                               chatintent.putExtra("image_url", singlesnap.child("sender_image_url").getValue().toString());
                                               startActivity(chatintent);

                                           }
                                       });

                                   }  else {
                                       commentRecyclerView.setVisibility(View.GONE);
                                       noNewMessage.setVisibility(View.VISIBLE);
                                   }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
*/
                  if(model.getMessage() != null){
                    commentRecyclerView.setVisibility(View.VISIBLE);
                    noNewMessage.setVisibility(View.GONE);
                    viewHolder.setUsername(model.getSender_name());
                    viewHolder.setMessage(model.getMessage());
                    viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getSending_timeStamp()));

                    Glide.with(getApplicationContext()).load(model.getSender_image_url()).into(viewHolder.messageSenderImageView);

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent chatintent = new Intent(inbox.this , Chat.class);
                            chatintent.putExtra("emailforchat", model.getSender_email());
                            chatintent.putExtra("msg_id", model.getMessage_id());
                            chatintent.putExtra("image_url", model.getSender_image_url());
                            startActivity(chatintent);

                        }
                    });
            } else if (model == null) {
                    commentRecyclerView.setVisibility(View.GONE);
                    noNewMessage.setVisibility(View.VISIBLE);
                }

            }

        };

        commentRecyclerView.setAdapter(commentAdapter);

    }


    public static class inboxHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView messageSenderImageView;
        TextView messageSenderusernameTextView;
        TextView SendingtimeTextView;
        TextView messageTextView;

        public inboxHolder(View itemView) {

            super(itemView);

            mView = itemView;

            messageSenderImageView = (ImageView) itemView.findViewById(R.id.inbox_sender_imageview);
            messageSenderusernameTextView = (TextView) itemView.findViewById(R.id.inbox_sender_name_tv);
            SendingtimeTextView = (TextView) itemView.findViewById(R.id.inbox_timestamp);
            messageTextView = (TextView) itemView.findViewById(R.id.inbox_message_tv);

        }

        public void setUsername(String username) {
            messageSenderusernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            SendingtimeTextView.setText(time);
        }

        public void setMessage(String comment) {
            messageTextView.setText(comment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }

}
