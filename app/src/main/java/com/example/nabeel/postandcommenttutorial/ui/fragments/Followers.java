package com.example.nabeel.postandcommenttutorial.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.activities.UserProfile;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Followers extends Fragment {

    View mRootview;
    RecyclerView follower_recyc_view;
    String emails;
    private SwipeRefreshLayout mSwipeRef__followers;

    public Followers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootview = inflater.inflate(R.layout.fragment_followers, container, false);
        mSwipeRef__followers = (SwipeRefreshLayout) mRootview.findViewById(R.id.swipe_refresh_layout_followers);

        mSwipeRef__followers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setupadapter();
                        mSwipeRef__followers.setRefreshing(false);
                    }
                },5000);
            }
        });


        follower_recyc_view = (RecyclerView) mRootview.findViewById(R.id.followers_recyclerview);
        follower_recyc_view.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        follower_recyc_view.setLayoutManager(mLayoutManager);


//        initialize();
        setupadapter();

        return mRootview;
    }

    /*private void initialize() {
        FirebaseUtils.getFollowers().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    emails = childDataSnapshot.getKey();



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    private void setupadapter() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_KEY);

        FirebaseIndexRecyclerAdapter<User, followerViewHolder> adapter = new FirebaseIndexRecyclerAdapter<User, followerViewHolder>(
                User.class,
                R.layout.layout_user_listenitem,
                followerViewHolder.class,
                FirebaseUtils.getFollowers().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")),
                db
                ) {

            @Override
            protected void populateViewHolder(final followerViewHolder viewHolder, final User model, final int position) {


                viewHolder.email.setText(model.getEmail());
                viewHolder.username.setText(model.getName());

                Glide.with(getActivity())
                        .load(model.getImage())
                        .into(viewHolder.profileimage);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent user_profile = new Intent(getContext() , UserProfile.class);
                        user_profile.putExtra("email", model.getEmail());
                        startActivity(user_profile);

                    }
                });

                /*FirebaseUtils.getPostRef().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        viewHolder.setPostText(model.getPostText());

                        viewHolder.setPostText(model.getPostText());
                        viewHolder.setUsername(model.getUser().getName());
                        viewHolder.setNumCOmments(String.valueOf(model.getNumAnswers()));
                        viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                        Glide.with(getActivity())
                                .load(model.getUser().getImage())
                                .into(viewHolder.postOwnerDisplayImageView);

                        if (model.getPostImageUrl() != null) {
                            viewHolder.postDisplayImageVIew.setVisibility(View.VISIBLE);
                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReference(model.getPostImageUrl());
                            Glide.with(getActivity())
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .into(viewHolder.postDisplayImageVIew);
                        } else {
                            viewHolder.postDisplayImageVIew.setImageBitmap(null);
                            viewHolder.postDisplayImageVIew.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }
        };

        follower_recyc_view.setAdapter(adapter);
//        initialize();
    }

    public static class followerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        TextView username, email;
        CircleImageView profileimage;
//        ImageView postOwnerDisplayImageView;
//        TextView postOwnerUsernameTextView;
//        TextView postTimeCreatedTextView;
//        ImageView postDisplayImageVIew;
//        TextView postTextTextView;
//        LinearLayout postCommentLayout;
//        TextView postNumCommentsTextView;
//        LinearLayout mPostView;
//        ImageView menu_imageview;

        public followerViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            username = (TextView) itemView.findViewById(R.id.username_search);
            email = (TextView) itemView.findViewById(R.id.email_search);

            profileimage = (CircleImageView) itemView.findViewById(R.id.profile_image);

//            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.iv_post_owner_display);
//            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.tv_post_username);
//            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.tv_time);
//            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.iv_post_display);
//            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.answer_layout);
//            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.tv_answers);
//            postTextTextView = (TextView) itemView.findViewById(R.id.tv_post_text);
//            mPostView = (LinearLayout) itemView.findViewById(R.id.postview);
//            menu_imageview = (ImageView) itemView.findViewById(R.id.menuPopup_imageview);
        }

//        public void setUsername(String username) {
//            postOwnerUsernameTextView.setText(username);
//        }
//
//        public void setTIme(CharSequence time) {
//            postTimeCreatedTextView.setText(time);
//        }
//
//        public void setNumCOmments(String numComments) {
//            postNumCommentsTextView.setText(numComments);
//        }
//
//        public void setPostText(String text) {
//            postTextTextView.setText(text);
//        }
    }

}
