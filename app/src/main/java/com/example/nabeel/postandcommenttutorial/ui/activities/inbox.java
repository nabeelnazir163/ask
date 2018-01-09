package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.models.inbox_model;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class inbox extends AppCompatActivity {

//    DatabaseReference mDatabase;
    TextView newMessage;
    TextView viewSentMessages;
    TextView noNewMessage;
    private  RecyclerView commentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        newMessage = (TextView) findViewById(R.id.newMessageInbox);
        viewSentMessages = (TextView) findViewById(R.id.sentMessageInbox);
        noNewMessage = (TextView) findViewById(R.id.no_new_msg);

        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inbox.this , search_user_for_newMessage.class);
                startActivity(intent);
            }
        });

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
                FirebaseUtils.getMessageRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).child("inbox")
        ) {
            @Override
            protected void populateViewHolder(inboxHolder viewHolder, final inbox_model model, int position) {

                if(model.getMessage() != null){

                    commentRecyclerView.setVisibility(View.VISIBLE);
                    noNewMessage.setVisibility(View.GONE);
                    viewHolder.setUsername(model.getSender_name());
                    viewHolder.setMessage(model.getMessage());
                    viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getSendingtimeStamp()));

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
