package com.zillion.android.askaalim.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.Notification_model;
import com.zillion.android.askaalim.models.Post;
import com.zillion.android.askaalim.utils.Constants;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Notification extends AppCompatActivity {

//    private TextView mName_Notification , mMsg_Notification;

    private RecyclerView notification_recylerview;
    FirebaseRecyclerAdapter<Notification_model, Notification_holder> mNotificationAdapter;
    LinearLayout no_notification_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notification");

        no_notification_layout = (LinearLayout) findViewById(R.id.no_notification_layout);

        init();

        FirebaseUtils.getNotificationRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))){

                    no_notification_layout.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        notification_recylerview = (RecyclerView) findViewById(R.id.notification_rv);
        notification_recylerview.setLayoutManager(new LinearLayoutManager(this));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        notification_recylerview.setLayoutManager(mLayoutManager);
        setupAdapter();
        notification_recylerview.setAdapter(mNotificationAdapter);
    }

    private void setupAdapter() {

        mNotificationAdapter = new FirebaseRecyclerAdapter<Notification_model, Notification_holder>(
                Notification_model.class,
                R.layout.row_notification,
                Notification_holder.class,
                FirebaseUtils.getNotificationRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
        ) {


            @Override
            protected void populateViewHolder(final Notification_holder viewHolder, final Notification_model model, int position) {

//                FirebaseUtils.getNotificationRef()
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
                    viewHolder.setNotificationMesssage(model.getName() + " " + model.getNotification());

                    if(!TextUtils.isEmpty(model.getImage())) {
                        Glide.with(Notification.this).load(model.getImage()).into(viewHolder.Notification_from_iv);
                    }
                    viewHolder.Notification_time_tv.setText(DateUtils.getRelativeTimeSpanString(model.getTime()));


                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent post_intent = new Intent(Notification.this, PostActivity.class);
                        post_intent.putExtra(Constants.EXTRA_POST, model.getPost());
                        startActivity(post_intent);

                    }
                    });


//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

            }
        };

    }


    public static class Notification_holder extends RecyclerView.ViewHolder {

        View mView;

        ImageView Notification_from_iv;
        TextView Notification_message_tv;
        TextView Notification_time_tv;

        public Notification_holder(View itemView) {
            super(itemView);

            mView = itemView;

            Notification_from_iv = (ImageView) itemView.findViewById(R.id.notification_iv);
            Notification_message_tv = (TextView) itemView.findViewById(R.id.notification_to_display);
            Notification_time_tv = (TextView) itemView.findViewById(R.id.notification_time);

        }

        public void setNotificationMesssage(String message) {
            Notification_message_tv.setText(message);
        }
    }
}
