package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.models.inbox_model;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class inbox extends AppCompatActivity {

//    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

//        mDatabase = FirebaseDatabase.getInstance().getReference().child("message").child("inbox");

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Inbox");

        initinboxSection();

    }

    private void initinboxSection() {

        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.inbox_rv);
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
                        startActivity(chatintent);

                    }
                });

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

}
