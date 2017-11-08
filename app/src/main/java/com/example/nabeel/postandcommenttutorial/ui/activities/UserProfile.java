package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private ImageView mUserProfile_Iv;

    private TextView mUserProfile_name_tv;
    private TextView mUser_add;
    private TextView mUserProfile_desc_tv;
    private TextView mFollowtv;
    private TextView munfollowtv;
    private TextView mMessage;

    private DatabaseReference mDatabase;
    String mUserEmail;
    String mPostKey;

    String email_user;
    String mEmail;

    ArrayList<String> postids = new ArrayList<>();

    private RecyclerView userProfile_question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.MY_POSTS);

        mUserProfile_Iv = (ImageView) findViewById(R.id.userprofile_displayImage);

        mUserProfile_name_tv = (TextView) findViewById(R.id.userprofile_display_name);
        mUser_add = (TextView) findViewById(R.id.userprofile_add);
        mUserProfile_desc_tv = (TextView) findViewById(R.id.userprofile_desc);
        mFollowtv = (TextView) findViewById(R.id.followText);
        munfollowtv = (TextView) findViewById(R.id.unfollowText);

        mMessage = (TextView) findViewById(R.id.chat);

        mUserEmail = getIntent().getExtras().getString("email");
        mPostKey = getIntent().getExtras().getString("postkey");

        email_user = FirebaseUtils.getCurrentUser().getEmail();
        mEmail = mUserEmail.replace(".",",");

        userProfile_question = (RecyclerView) findViewById(R.id.recyclerview_userprofile);
        userProfile_question.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        userProfile_question.setLayoutManager(mLayoutManager);

        setupAdapterProfile();

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent message_intent = new Intent(UserProfile.this , Chat.class);
                message_intent.putExtra("emailforchat", mEmail);
                startActivity(message_intent);

            }
        });


        FirebaseUtils.getUserRef(mEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_profile_image = (String) dataSnapshot.child("image").getValue();
                String user_name = (String) dataSnapshot.child("name").getValue();
                String address = (String) dataSnapshot.child("address").getValue();

                Glide.with(getApplicationContext())
                        .load(user_profile_image)
                        .into(mUserProfile_Iv);

                mUserProfile_name_tv.setText(user_name);

                if(!TextUtils.isEmpty(address)) {

                    mUser_add.setText(address);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        int userType = userType_sp.getInt("UserType", 0);


        if(mUserEmail.equals(email_user) ) {

            mFollowtv.setVisibility(View.GONE);
            mMessage.setVisibility(View.GONE);


        } else if (!mUserEmail.equals(email_user)){

            mFollowtv.setVisibility(View.VISIBLE);

        }

        if(userType == 3){

            mFollowtv.setVisibility(View.GONE);
            munfollowtv.setVisibility(View.GONE);
            mMessage.setVisibility(View.GONE);

        }




            mFollowtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUtils.getFollowers().child(email_user.replace(".",",")).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                followProcess();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        munfollowtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUtils.getFollowers().child(email_user.replace(".",",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                            unfollowprocess();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void unfollowprocess() {

        FirebaseUtils.getFollowers().child(email_user.replace(".",","))
                .child(mEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFollowtv.setVisibility(View.VISIBLE);
                munfollowtv.setVisibility(View.GONE);

            }
        });

    }


    private void followProcess() {

        FirebaseUtils.getFollowers().child(email_user.replace(".",","))
                .child(mEmail).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFollowtv.setVisibility(View.GONE);
                munfollowtv.setVisibility(View.VISIBLE);

            }
        });


    }

    private void initadapter() {


    }

    private void setupAdapterProfile() {

//        Toast.makeText(getApplicationContext(), mEmail , Toast.LENGTH_SHORT).show();

        FirebaseRecyclerAdapter<Post, UP_viewholder> mUP_Adapter = new FirebaseIndexRecyclerAdapter<Post, UP_viewholder>(
                Post.class,
                R.layout.row_post,
                UP_viewholder.class,
                FirebaseUtils.getmypostReference().child(mEmail),
                FirebaseUtils.getPostRef()
        ) {
            @Override
            protected void populateViewHolder(UP_viewholder viewHolder, final Post model, int position) {


                       /* String User_email = mUserEmail.replace(".", ",");

                        mDatabase.child(User_email).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {

                               for(DataSnapshot ds : dataSnapshot.getChildren()) {

                                   String name = ds.getKey().toString();
                                   postids.add(name);
                               }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                viewHolder.setNumCOmments(String.valueOf(model.getNumComments()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                viewHolder.setUsername(model.getUser().getName());
                viewHolder.setPostText(model.getPostText());

                Glide.with(UserProfile.this)
                        .load(model.getUser().getImage())
                        .into(viewHolder.postOwnerDisplayImageView);

                if (model.getPostImageUrl() != null) {
                    viewHolder.postDisplayImageVIew.setVisibility(View.VISIBLE);
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(model.getPostImageUrl());
                    Glide.with(UserProfile.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.postDisplayImageVIew);
                } else {
                    viewHolder.postDisplayImageVIew.setImageBitmap(null);
                    viewHolder.postDisplayImageVIew.setVisibility(View.GONE);
                }

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);
                    }
                });

            }
        };

        userProfile_question.setAdapter(mUP_Adapter);

    }


    public static class UP_viewholder extends RecyclerView.ViewHolder{

        TextView postTextTextView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        TextView postNumCommentsTextView;
        ImageView postOwnerDisplayImageView;
        ImageView postDisplayImageVIew;
        LinearLayout postCommentLayout;



        View mView;

        public UP_viewholder(View itemView) {
            super(itemView);

            mView = itemView;

            postTextTextView = (TextView) itemView.findViewById(R.id.tv_post_text);
            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.tv_post_username);
            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.tv_time);
            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.tv_answers);
            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.iv_post_owner_display);
            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.iv_post_display);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.answer_layout);

        }


        public void setUsername(String username) {
            postOwnerUsernameTextView.setText(username);
        }

        public void setTIme(CharSequence time) {
            postTimeCreatedTextView.setText(time);
        }

        public void setNumCOmments(String numComments) {
            postNumCommentsTextView.setText(numComments);
        }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }
}
