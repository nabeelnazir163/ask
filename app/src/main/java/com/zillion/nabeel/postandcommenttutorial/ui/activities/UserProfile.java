package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.models.Post;
import com.zillion.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.zillion.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.zillion.nabeel.postandcommenttutorial.utils.Constants;
import com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lid.lib.LabelTextView;

import java.util.ArrayList;

public class UserProfile extends BaseActivity {

    private ImageView mUserProfile_Iv;

    private LabelTextView mUserProfile_name_tv;
    private TextView mUser_add;
    private TextView fiqah;
    private TextView mFollowtv;
    private TextView munfollowtv;
    private TextView mMessage;
    private TextView update_Profile;
    private TextView institute_userprofile;
    private TextView about_alim;

    String mUserEmail;
    String email_user;
    String mEmail;
    String userType_;
    String imageuri;
    int userType;

    private String fiqah_string;
    private String image_url;
    private String name;

    String description = "";
    String institute_name = "";

    private RecyclerView userProfile_question;

    private SwipeRefreshLayout mSwipeRef_userProfile;

    private LinearLayout institute_layout;
    private LinearLayout description_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        mUserProfile_Iv = (ImageView) findViewById(R.id.userprofile_displayImage);

        mUserProfile_name_tv = (LabelTextView) findViewById(R.id.userprofile_display_name);
        mUser_add = (TextView) findViewById(R.id.userprofile_add);
        fiqah = (TextView) findViewById(R.id.fiqahinuserprofile);
        mFollowtv = (TextView) findViewById(R.id.followText);
        munfollowtv = (TextView) findViewById(R.id.unfollowText);
        update_Profile = (TextView) findViewById(R.id.update_profile);
        institute_userprofile = (TextView) findViewById(R.id.institute_textview);
        about_alim = (TextView) findViewById(R.id.description_tv);

        institute_layout = (LinearLayout) findViewById(R.id.institute_layout);
        description_layout = (LinearLayout) findViewById(R.id.description_layout);

        mMessage = (TextView) findViewById(R.id.chat);

        mUserEmail = getIntent().getExtras().getString("email");
//        mPostKey = getIntent().getExtras().getString("postkey");

        email_user = FirebaseUtils.getCurrentUser().getEmail();
        mEmail = mUserEmail.replace(".",",");

        FirebaseUtils.getUserRef(mEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageuri = dataSnapshot.child("image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSwipeRef_userProfile = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_userProfile);

        userProfile_question = (RecyclerView) findViewById(R.id.recyclerview_userprofile);
        userProfile_question.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        userProfile_question.setLayoutManager(mLayoutManager);

        update_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent edit_intent = (new Intent(UserProfile.this, edit_profile.class));
                startActivity(edit_intent);
            }
        });

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent message_intent = new Intent(UserProfile.this , Chat.class);
                message_intent.putExtra("emailforchat", mEmail);
                message_intent.putExtra("msg_id", "");
                message_intent.putExtra("image_url", imageuri);
                startActivity(message_intent);

            }
        });


        FirebaseUtils.getUserRef(mEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_profile_image = (String) dataSnapshot.child("image").getValue();
                String user_name = (String) dataSnapshot.child("name").getValue();
                String country = (String) dataSnapshot.child("country").getValue();
                String fiqh = dataSnapshot.child("fiqah").getValue().toString();
                String city = dataSnapshot.child("city").getValue().toString();
                userType_ = dataSnapshot.child("userType").getValue().toString();
//                String country = dataSnapshot.child("country").getValue().toString();

                if(dataSnapshot.hasChild("institute")) {
                    institute_name = dataSnapshot.child("institute").getValue().toString();
                }

                if(dataSnapshot.hasChild("about_alim")){
                    description = dataSnapshot.child("about_alim").getValue().toString();
                }
                if(userType_.equals("Alim")){

                    mUserProfile_name_tv.setLabelText("Alim");
                    institute_layout.setVisibility(View.VISIBLE);
                    description_layout.setVisibility(View.VISIBLE);

                    setupAdapterProfileAlim(); // NabEel's task

                    mSwipeRef_userProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setupAdapterProfileAlim();
                                    mSwipeRef_userProfile.setRefreshing(false);
                                }
                            },5000);
                        }
                    });

                } else if (userType_.equals("User")){

                    mUserProfile_name_tv.setLabelText("User");
                    institute_layout.setVisibility(View.GONE);
                    description_layout.setVisibility(View.GONE);

                    setupAdapterProfile();

                    mSwipeRef_userProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setupAdapterProfile();
                                    mSwipeRef_userProfile.setRefreshing(false);
                                }
                            },5000);
                        }
                    });

                }

                Glide.with(getApplicationContext())
                        .load(user_profile_image)
                        .into(mUserProfile_Iv);

                mUserProfile_name_tv.setText(user_name);

                if(!TextUtils.isEmpty(country) && !TextUtils.isEmpty(city)) {

                    mUser_add.setText(city + ", " + country);

                }

                fiqah.setText(fiqh);

                if(!description.equals("")){
                    about_alim.setText(description);
                } else {
                    description_layout.setVisibility(View.GONE);
                }

                if(!institute_name.equals("")){
                    institute_userprofile.setText(institute_name);
                } else {

                    institute_layout.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        userType = userType_sp.getInt("UserType", 0);

        if(mUserEmail.equals(email_user) ) {

            mFollowtv.setVisibility(View.GONE);
            mMessage.setVisibility(View.GONE);


        } else if (!mUserEmail.equals(email_user)){

            mFollowtv.setVisibility(View.VISIBLE);
            update_Profile.setVisibility(View.GONE);

        }

        if(userType == 3){

            mFollowtv.setVisibility(View.GONE);
            munfollowtv.setVisibility(View.GONE);
            mMessage.setVisibility(View.GONE);

        } else if (userType != 3){

            isFollowing();
        }



            mFollowtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUtils.getFollowers().child(email_user.replace(".",","))
                            .child(mEmail).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            followProcess();

                        }
                    });

                }
            });

        munfollowtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUtils.getFollowers().child(email_user.replace(".",","))
                        .child(mEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        unfollowprocess();

                    }
                });

            }
        });
    }

    private void setupAdapterProfileAlim() {

        FirebaseRecyclerAdapter<Post, UP_viewholder> mUP_Adapter = new FirebaseIndexRecyclerAdapter<Post, UP_viewholder>(
                Post.class,
                R.layout.row_post,
                UP_viewholder.class,
                FirebaseUtils.getmyanswerReference().child(mEmail),
                FirebaseUtils.getPostRef()
        ) {
            @Override
            protected void populateViewHolder(final UP_viewholder viewHolder, final Post model, int position) {

//                final String post_key = getRef(position).getKey();

                SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

                final int userType = userType_sp.getInt("UserType", 0);

                if(userType != 3) {

                    FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(model.getPostId())) {

                                        viewHolder.bookmark_imageview.setVisibility(View.GONE);
                                        viewHolder.after_bookmark_iv.setVisibility(View.VISIBLE);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

                FirebaseUtils.getUserRef(model.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        fiqah_string = dataSnapshot.child("fiqah").getValue().toString();
                        image_url = dataSnapshot.child("image").getValue().toString();
                        name = dataSnapshot.child("name").getValue().toString();

                        if (!TextUtils.isEmpty(fiqah_string)) {

                            viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                            viewHolder.setFiqahOfAlim(fiqah_string);
                        }

                        viewHolder.setUsername(name);

                        if (!TextUtils.isEmpty(image_url)) {

                            Glide.with(UserProfile.this)
                                    .load(image_url)
                                    .into(viewHolder.postOwnerDisplayImageView);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*if(model.getUser().getFiqah() != null){

                    viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                    viewHolder.setFiqahOfAlim(model.getUser().getFiqah());

                }*/


                if(userType == 2){

                    viewHolder.newanswers.setVisibility(View.GONE);

                } else if( userType == 3){

                    viewHolder.bookmark_imageview.setVisibility(View.GONE);
                    viewHolder.after_bookmark_iv.setVisibility(View.GONE);
                    viewHolder.menu_imageview.setVisibility(View.GONE);
                    viewHolder.newanswers.setVisibility(View.GONE);
                }

                viewHolder.setNumCOmments(String.valueOf(model.getNumComments()));
                viewHolder.setNumAnswers(String.valueOf(model.getNumAnswers()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
//                viewHolder.setUsername(model.getUser().getName());
                viewHolder.setPostText(model.getPostText());
                viewHolder.setnumberSeen(String.valueOf(model.getNumSeen()));

                viewHolder.postTextTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(viewHolder.postTextTextView.getLineCount() <= 3){

                            viewHolder.readmore_rel_layout.setVisibility(View.GONE);

                        } else if(viewHolder.postTextTextView.getLineCount() >3){

                            viewHolder.readmore_rel_layout.setVisibility((View.VISIBLE));

                        }
                    }
                });

                viewHolder.readmore_rel_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.postTextTextView.setMaxLines(Integer.MAX_VALUE);
                        viewHolder.readmore_rel_layout.setVisibility(View.GONE);

                    }
                });


                /*Glide.with(UserProfile.this)
                        .load(model.getUser().getImage())
                        .into(viewHolder.postOwnerDisplayImageView);*/


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


                viewHolder.postDisplayImageVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(UserProfile.this, android.R.style.Animation);
                        dialog.setContentView(R.layout.dialogpostimage_layout);
                        ImageView myImage = (ImageView) dialog.findViewById(R.id.i);


                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReference(model.getPostImageUrl());

                        Glide.with(UserProfile.this)
                                .using(new FirebaseImageLoader()).load(storageReference).into(myImage);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }



                        dialog.show();
                    }
                });

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.CommentLayout_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.tv_seenPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.postTextTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }

                    }
                });

                viewHolder.newanswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent answers_inteent = new Intent(UserProfile.this , postNewAnswer.class);
                        answers_inteent.putExtra(Constants.EXTRA_POST , model);
                        startActivity(answers_inteent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.menu_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final PopupMenu popupMenu = new PopupMenu(UserProfile.this, viewHolder.menu_imageview);

                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                        if(!model.getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))) {

                            Menu m = popupMenu.getMenu();
                            m.removeItem((R.id.delete));

                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override

                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();

                                if(id == R.id.delete){

                                    if(model.getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))){

                                        Toast.makeText(UserProfile.this , "Delete", Toast.LENGTH_SHORT).show();

                                        DeletePost(model.getPostId());

                                    }

                                } else  if ( id == R.id.report){

                                    Toast.makeText(UserProfile.this , "Report", Toast.LENGTH_SHORT).show();

                                }

                                return  true;
                            }
                        });

                        popupMenu.show();
                    }
                });



                DatabaseReference mDatabaseposts = FirebaseDatabase.getInstance().getReference().child(Constants.POST_KEY).child(model.getPostId());

                mDatabaseposts.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.bookmark_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BookmarkPost(model.getPostId() , viewHolder);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.after_bookmark_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        remove_bookmark(model.getPostId(), viewHolder);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });
            }
        };

        userProfile_question.setAdapter(mUP_Adapter);

    }

    private void isFollowing() {

        FirebaseUtils.getFollowers().child(email_user.replace(".",",")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mEmail.replace(".",","))){

                    followProcess();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void unfollowprocess() {

                mFollowtv.setVisibility(View.VISIBLE);
                munfollowtv.setVisibility(View.GONE);
    }

    private void followProcess() {

                mFollowtv.setVisibility(View.GONE);
                munfollowtv.setVisibility(View.VISIBLE);

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
            protected void populateViewHolder(final UP_viewholder viewHolder, final Post model, int position) {

//                final String post_key = getRef(position).getKey();

                SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

//                userType = userType_sp.getInt("UserType", 0);

                if(userType != 3) {

                    FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(model.getPostId())) {

                                        viewHolder.bookmark_imageview.setVisibility(View.GONE);
                                        viewHolder.after_bookmark_iv.setVisibility(View.VISIBLE);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

                FirebaseUtils.getUserRef(model.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        fiqah_string = dataSnapshot.child("fiqah").getValue().toString();
                        image_url = dataSnapshot.child("image").getValue().toString();
                        name = dataSnapshot.child("name").getValue().toString();

                        if(!TextUtils.isEmpty(fiqah_string)){

                            viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                            viewHolder.setFiqahOfAlim(fiqah_string);
                        }

                        viewHolder.setUsername(name);

                        Glide.with(UserProfile.this)
                                .load(image_url)
                                .into(viewHolder.postOwnerDisplayImageView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*if(model.getUser().getFiqah() != null){

                    viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                    viewHolder.setFiqahOfAlim(model.getUser().getFiqah());

                }*/


                if(userType == 2){

                    viewHolder.newanswers.setVisibility(View.GONE);

                } else if( userType == 3){

                    viewHolder.bookmark_imageview.setVisibility(View.GONE);
                    viewHolder.after_bookmark_iv.setVisibility(View.GONE);
                    viewHolder.menu_imageview.setVisibility(View.GONE);
                    viewHolder.newanswers.setVisibility(View.GONE);
                }

                viewHolder.setNumCOmments(String.valueOf(model.getNumComments()));
                viewHolder.setNumAnswers(String.valueOf(model.getNumAnswers()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
//                viewHolder.setUsername(model.getUser().getName());
                viewHolder.setPostText(model.getPostText());
                viewHolder.setnumberSeen(String.valueOf(model.getNumSeen()));

                viewHolder.postTextTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(viewHolder.postTextTextView.getLineCount() <= 3){

                            viewHolder.readmore_rel_layout.setVisibility(View.GONE);

                        } else if(viewHolder.postTextTextView.getLineCount() >3){

                            viewHolder.readmore_rel_layout.setVisibility((View.VISIBLE));

                        }
                    }
                });

                viewHolder.readmore_rel_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.postTextTextView.setMaxLines(Integer.MAX_VALUE);
                        viewHolder.readmore_rel_layout.setVisibility(View.GONE);

                    }
                });


               /* Glide.with(UserProfile.this)
                        .load(model.getUser().getImage())
                        .into(viewHolder.postOwnerDisplayImageView);*/


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


                viewHolder.postDisplayImageVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(UserProfile.this, android.R.style.Animation);
                        dialog.setContentView(R.layout.dialogpostimage_layout);
                        ImageView myImage = (ImageView) dialog.findViewById(R.id.i);


                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReference(model.getPostImageUrl());

                        Glide.with(UserProfile.this)
                                .using(new FirebaseImageLoader()).load(storageReference).into(myImage);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }



                        dialog.show();
                    }
                });

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.CommentLayout_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.tv_seenPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.postTextTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(UserProfile.this, PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }

                    }
                });

                viewHolder.newanswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent answers_inteent = new Intent(UserProfile.this , postNewAnswer.class);
                        answers_inteent.putExtra(Constants.EXTRA_POST , model);
                        startActivity(answers_inteent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.menu_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final PopupMenu popupMenu = new PopupMenu(UserProfile.this, viewHolder.menu_imageview);

                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                        if(!model.getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))) {

                            Menu m = popupMenu.getMenu();
                            m.removeItem((R.id.delete));

                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override

                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();

                                if(id == R.id.delete){

                                    if(model.getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))){

                                        Toast.makeText(UserProfile.this , "Delete", Toast.LENGTH_SHORT).show();

                                        DeletePost(model.getPostId());

                                    }

                                } else  if ( id == R.id.report){

                                    Toast.makeText(UserProfile.this , "Report", Toast.LENGTH_SHORT).show();

                                }

                                return  true;
                            }
                        });
                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }

                        popupMenu.show();
                    }
                });



                DatabaseReference mDatabaseposts = FirebaseDatabase.getInstance().getReference().child(Constants.POST_KEY).child(model.getPostId());

                mDatabaseposts.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.bookmark_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BookmarkPost(model.getPostId() , viewHolder);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.after_bookmark_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        remove_bookmark(model.getPostId(), viewHolder);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });
            }
        };

        userProfile_question.setAdapter(mUP_Adapter);

    }

    private void remove_bookmark(String postId, final UP_viewholder view) {

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser()
                .getEmail().replace(".",","))
                .child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(UserProfile.this , "Remove Post as bookmark", Toast.LENGTH_SHORT).show();

                view.bookmark_imageview.setVisibility(View.VISIBLE);
                view.after_bookmark_iv.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UserProfile.this , "Unable to Remove Post as bookmark", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void BookmarkPost(final String post_id, final UP_viewholder view) {

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(UserProfile.this , "Post saved", Toast.LENGTH_SHORT).show();

                view.bookmark_imageview.setVisibility(View.GONE);
                view.after_bookmark_iv.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UserProfile.this , "Unable to Bookmark Your post try agian later", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void DeletePost(final String postId) {

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).child(postId).removeValue();
        FirebaseUtils.getMyPostRef().child(postId).removeValue();
        FirebaseUtils.getPostLikedRef(postId).removeValue();
        FirebaseUtils.getAnswerRef().child(postId).removeValue();
        FirebaseDatabase.getInstance().getReference().child(Constants.ANSWER_LIKED_KEY).
                child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(postId).removeValue();
        FirebaseUtils.getCommentRef(postId).removeValue();
        FirebaseDatabase.getInstance().getReference().child(Constants.COMMENT_REPLY)
                .child(postId).removeValue();

        FirebaseUtils.getPostRef().child(postId).removeValue();
        FirebaseUtils.postViewRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(postId).removeValue();

    }

    public static class UP_viewholder extends RecyclerView.ViewHolder{

        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageVIew;
        TextView postTextTextView;
        LinearLayout postCommentLayout;
        LinearLayout CommentLayout_post;
        TextView postNumAnswersTextView;
        TextView postNumCommentsTextView;
        LinearLayout mPostView;
        ImageView menu_imageview;
        ImageView bookmark_imageview;
        ImageView after_bookmark_iv;
        LinearLayout newanswers;
        TextView fiqahOfAlim;
        RelativeLayout readmore_rel_layout;
        LinearLayout seenLayout;
        TextView tv_seenPost;

        View mView;

        public UP_viewholder(View itemView) {
            super(itemView);

            mView = itemView;

            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.iv_post_owner_display);
            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.tv_post_username);
            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.tv_time);
            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.iv_post_display);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.answer_layout);
            CommentLayout_post = (LinearLayout) itemView.findViewById(R.id.comment_layout);
            postNumAnswersTextView = (TextView) itemView.findViewById(R.id.tv_answers);
            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.tv_comments);
            postTextTextView = (TextView) itemView.findViewById(R.id.tv_post_text);
            mPostView = (LinearLayout) itemView.findViewById(R.id.postview);
            menu_imageview = (ImageView) itemView.findViewById(R.id.menuPopup_imageview);
            bookmark_imageview = (ImageView) itemView.findViewById(R.id.bookmark_iv);
            after_bookmark_iv = (ImageView) itemView.findViewById(R.id.bookmark_iv_after);
            newanswers = (LinearLayout) itemView.findViewById(R.id.newanswer_layout_post);
            fiqahOfAlim = (TextView) itemView.findViewById(R.id.tv_post_userfiqah);
            readmore_rel_layout = (RelativeLayout) itemView.findViewById(R.id.readmore_relLayout);
            seenLayout = (LinearLayout) itemView.findViewById(R.id.seen_layout);
            tv_seenPost = (TextView) itemView.findViewById(R.id.tv_seen);

        }


        public void setUsername(String username) {
            postOwnerUsernameTextView.setText(username);
        }

        public void setTIme(CharSequence time) {
            postTimeCreatedTextView.setText(time);
        }

        public void setNumAnswers(String numAnswers) { postNumAnswersTextView.setText(numAnswers); }

        public void setNumCOmments(String numCOmments){ postNumCommentsTextView.setText(numCOmments); }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }

        public void setFiqahOfAlim(String fiqah){
            fiqahOfAlim.setText(fiqah);
        }

        public void setnumberSeen(String seencount){ tv_seenPost.setText(seencount); }

    }

    private void PostSeen(final String postID) {

        FirebaseUtils.postViewRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){

                    FirebaseUtils.getPostRef().child(postID).child("numSeen").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            return null;
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });

                } else {

                    FirebaseUtils.getPostRef().child(postID).child("numSeen").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            long num = (long) mutableData.getValue();
                            mutableData.setValue(num + 1);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            FirebaseUtils.postViewRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                                    .child(postID).setValue(true);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        if(userType == 3){

            mAuth.signOut();
            startActivity(new Intent(UserProfile.this , RegisterActivity.class));
            finish();

        }
    }*/
}
