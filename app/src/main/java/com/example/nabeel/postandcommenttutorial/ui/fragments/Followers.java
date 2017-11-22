package com.example.nabeel.postandcommenttutorial.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class Followers extends Fragment {

    Post mPost;
    View mRootview;
    RecyclerView follower_recyc_view;
    String emails;
    String keys;
    String ids;

    public Followers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         mRootview = inflater.inflate(R.layout.fragment_followers, container, false);
        mPost = new Post();

        follower_recyc_view = (RecyclerView) mRootview.findViewById(R.id.followers_recyclerview);
        follower_recyc_view.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        follower_recyc_view.setLayoutManager(mLayoutManager);

        initialize();

        return mRootview;
    }

    private void initialize() {
        FirebaseUtils.getFollowers().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                    emails = childDataSnapshot.getKey();

                    setupadapter(emails);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setupadapter(String emails) {

        FirebaseIndexRecyclerAdapter<Post, followerViewHolder> adapter = new FirebaseIndexRecyclerAdapter<Post, followerViewHolder>(
                Post.class,
                R.layout.row_post,
                followerViewHolder.class,
                FirebaseUtils.getmypostReference().child(emails),
                FirebaseUtils.getPostRef().orderByKey()
                ) {

            @Override
            protected void populateViewHolder(final followerViewHolder viewHolder, final Post model, int position) {

                FirebaseUtils.getPostRef().addValueEventListener(new ValueEventListener() {
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
                });
            }
        };

        follower_recyc_view.setAdapter(adapter);
//        initialize();
    }

    public static class followerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageVIew;
        TextView postTextTextView;
        LinearLayout postCommentLayout;
        TextView postNumCommentsTextView;
        LinearLayout mPostView;
        ImageView menu_imageview;

        public followerViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.iv_post_owner_display);
            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.tv_post_username);
            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.tv_time);
            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.iv_post_display);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.answer_layout);
            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.tv_answers);
            postTextTextView = (TextView) itemView.findViewById(R.id.tv_post_text);
            mPostView = (LinearLayout) itemView.findViewById(R.id.postview);
            menu_imageview = (ImageView) itemView.findViewById(R.id.menuPopup_imageview);
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

}
