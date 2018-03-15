package com.zillion.android.askaalim.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zillion.android.askaalim.models.inbox_model;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.utils.BaseActivity;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class inbox extends BaseActivity {

//    DatabaseReference mDatabase;
    FloatingActionButton newMessage;

    TextView noNewMessage;

    private RecyclerView inboxRecyclerView;

    //Database Reference
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    //Swipe layoutout ( LAzy loader )
    private SwipeRefreshLayout inbox_swipe_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        newMessage = (FloatingActionButton) findViewById(R.id.newMessageInbox);
        noNewMessage = (TextView) findViewById(R.id.no_new_msg);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            ActionBar ab = getSupportActionBar();
            ab.setTitle("Inbox");

        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Inbox");

        inbox_swipe_layout = (SwipeRefreshLayout) findViewById(R.id.inbox_swipe_Refresh_layout);

        inbox_swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadChats();

                inbox_swipe_layout.setRefreshing(false);

            }
        });

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("chat")
                .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","));
        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages")
                .child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","));
        mMessageDatabase.keepSynced(true);

        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inbox.this , search_user_for_newMessage.class);
                intent.putExtra("askFor", "");
                startActivity(intent);
            }
        });

        loadChats();

    }

    private void loadChats() {

        inboxRecyclerView = (RecyclerView) findViewById(R.id.inbox_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
//        inboxRecyclerView.setHasFixedSize(true);
        inboxRecyclerView.setLayoutManager(linearLayoutManager);

        Query conversationQuery = mConvDatabase.orderByChild("sending_timeStamp");

        FirebaseRecyclerAdapter<inbox_model, inboxViewHolder> firebaseConadapter = new FirebaseRecyclerAdapter<inbox_model, inboxViewHolder>(
                inbox_model.class,
                R.layout.row_inbox,
                inboxViewHolder.class,
                conversationQuery

        ) {
            @Override
            protected void populateViewHolder(final inboxViewHolder viewHolder, final inbox_model model, int i) {


                FirebaseUtils.getMessageRef().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))){

                            noNewMessage.setVisibility(View.GONE);
                        } else {
                            noNewMessage.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final String list_user_email = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_email.replace(".",",")).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = (String) dataSnapshot.child("message").getValue();
                        long time = (long) dataSnapshot.child("sending_timeStamp").getValue();

                        viewHolder.setTime(time);
                        viewHolder.setMessage(data, model.isSeen());

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

                mUsersDatabase.child(list_user_email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String username = (String) dataSnapshot.child("name").getValue();
                        String userImage = (String) dataSnapshot.child("image").getValue();


                        viewHolder.setName(username);
                        viewHolder.setUserImage(userImage, getApplicationContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent chatintent = new Intent(inbox.this , Chat.class);
                        chatintent.putExtra("emailforchat", list_user_email.replace(".",","));
                        startActivity(chatintent);

                    }
                });

            }
        };

        inboxRecyclerView.setAdapter(firebaseConadapter);
    }

    public static class inboxViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public inboxViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTime(long time){

            TextView timeTv = (TextView) mView.findViewById(R.id.inbox_timestamp);
            timeTv.setText(DateUtils.getRelativeTimeSpanString(time));

        }


        public void setMessage(String message, boolean isSeen){

            TextView userStatusview = (TextView) mView.findViewById(R.id.inbox_message_tv);
            userStatusview.setText(message);

            if(!isSeen){

                userStatusview.setTypeface(userStatusview.getTypeface(), Typeface.BOLD);

            } else {

                userStatusview.setTypeface(userStatusview.getTypeface(), Typeface.NORMAL);

            }
        }

        public void setName(String name){

            TextView username = (TextView) mView.findViewById(R.id.inbox_sender_name_tv);

            username.setText(name);

        }

        public void setUserImage(String thumbImage, Context ctx){

            CircleImageView userImage = (CircleImageView) mView.findViewById(R.id.inbox_sender_imageview);
            Glide.with(ctx)
                    .load(thumbImage)
                    .into(userImage);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }

}
