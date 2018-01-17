package com.example.nabeel.postandcommenttutorial.ui.activities;

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
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.sentbox_model;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class sent_messages extends AppCompatActivity {

    TextView noNewMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_messages);

        noNewMessage = (TextView) findViewById(R.id.no_new_msg_sb);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            ActionBar ab = getSupportActionBar();
            ab.setTitle("Sentbox");

        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Sentbox");

        initinboxSection();
    }

    private void initinboxSection() {

        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.inbox_rv);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<sentbox_model, sentboxHolder> commentAdapter = new FirebaseRecyclerAdapter<sentbox_model, sentboxHolder>(
                sentbox_model.class,
                R.layout.row_inbox,
                sentboxHolder.class,
                FirebaseUtils.getMessageRef()
                        .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child("sentbox")
        ) {
            @Override
            protected void populateViewHolder(sentboxHolder viewHolder, final sentbox_model model, int position) {

                if(model.getMessage() != null) {

                    noNewMessage.setVisibility(View.GONE);
                    viewHolder.setUsername(model.getReceiver_name());
                    viewHolder.setMessage(model.getMessage());
                    viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getSending_timeStamp()));

                    Glide.with(getApplicationContext()).load(model.getReceiver_image_url()).into(viewHolder.messageSenderImageView);

                } else {

                    noNewMessage.setVisibility(View.VISIBLE);

                }
            }

        };

        commentRecyclerView.setAdapter(commentAdapter);

    }

    public static class sentboxHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView messageSenderImageView;
        TextView messageSenderusernameTextView;
        TextView SendingtimeTextView;
        TextView messageTextView;

        public sentboxHolder(View itemView) {

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
