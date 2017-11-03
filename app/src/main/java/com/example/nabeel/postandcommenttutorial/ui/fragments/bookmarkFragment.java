package com.example.nabeel.postandcommenttutorial.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.nabeel.postandcommenttutorial.models.Bookmark;
import com.example.nabeel.postandcommenttutorial.ui.activities.BookMark;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class bookmarkFragment extends Fragment {

    View mRootView;

    RecyclerView bookmark_recyc_view;
    TextView bookmarktextview;

    public bookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmark_recyc_view = (RecyclerView) mRootView.findViewById(R.id.bookmark_recyclerview);
        bookmarktextview = (TextView) mRootView.findViewById(R.id.null_bookmark_tv);

        bookmark_recyc_view.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        bookmark_recyc_view.setLayoutManager(mLayoutManager);

        if(FirebaseUtils.getCurrentUser() != null) {

            setupadapter();

        }
        return mRootView;
    }

    private void setupadapter() {

        FirebaseRecyclerAdapter<Bookmark, bookmarkViewHolder> bookmarkadapter = new FirebaseRecyclerAdapter<Bookmark, bookmarkViewHolder>(
                Bookmark.class,
                R.layout.row_post,
                bookmarkViewHolder.class,
                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
        ) {

            @Override
            protected void populateViewHolder(final bookmarkViewHolder viewHolder, final Bookmark model, int position) {

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(model.getPostId())){

                                    viewHolder.bookmark_imageview.setVisibility(View.GONE);
                                    viewHolder.after_bookmark_iv.setVisibility(View.VISIBLE);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                viewHolder.setPostText(model.getPostText());
                viewHolder.setUsername(model.getUserName());
                viewHolder.setNumCOmments(String.valueOf(model.getNumAnswers()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                Glide.with(getActivity())
                        .load(model.getUserImage())
                        .into(viewHolder.postOwnerDisplayImageView);

                if (model.getPostImageUrl() != null) {
                    viewHolder.postDisplayImageVIew.setVisibility(View.VISIBLE);
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(model.getPostImageUrl());
                    Glide.with(getContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.postDisplayImageVIew);
                } else {
                    viewHolder.postDisplayImageVIew.setImageBitmap(null);
                    viewHolder.postDisplayImageVIew.setVisibility(View.GONE);
                }

                viewHolder.bookmark_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BookmarkPost(model.getPostId() , viewHolder);

                    }
                });

                viewHolder.after_bookmark_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getContext() , "Delete From Bookmarks", Toast.LENGTH_SHORT).show();
                        remove_bookmark(model.getPostId(), viewHolder);

                    }
                });

            }
        };

        bookmark_recyc_view.setAdapter(bookmarkadapter);
    }

    private void remove_bookmark(String postId, final bookmarkViewHolder view) {

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser()
                .getEmail().replace(".",","))
                .child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getContext() , "Remove Post as bookmark", Toast.LENGTH_SHORT).show();

                view.bookmark_imageview.setVisibility(View.VISIBLE);
                view.after_bookmark_iv.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext() , "Unable to Remove Post as bookmark", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void BookmarkPost(final String post_id, final bookmarkViewHolder view) {

        FirebaseUtils.getPostRef().child(post_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postid = (String) dataSnapshot.child("postId").getValue();
                String postText = (String) dataSnapshot.child("postText").getValue();
                long timeCreated = (long) dataSnapshot.child("timeCreated").getValue();
                long numLikes = (long) dataSnapshot.child(Constants.NUM_LIKES_KEY).getValue();
                long numOfCmnt = (long) dataSnapshot.child(Constants.NUM_COMMENTS_KEY).getValue();
                long numOfAns = (long ) dataSnapshot.child(Constants.NUM_ANSWWERS_KEY).getValue();
                String userName = (String) dataSnapshot.child("user").child("name").getValue();
                String userImg = (String) dataSnapshot.child("user").child("image").getValue();


                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("postId").setValue(postid);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("postText").setValue(postText);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("timeCreated").setValue(timeCreated);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child(Constants.NUM_LIKES_KEY).setValue(numLikes);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child(Constants.NUM_ANSWWERS_KEY).setValue(numOfAns);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child(Constants.NUM_COMMENTS_KEY).setValue(numOfCmnt);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("userImage").setValue(userImg);

                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("userName").setValue(userName).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getContext() , "Save Post", Toast.LENGTH_SHORT).show();

                        view.bookmark_imageview.setVisibility(View.GONE);
                        view.after_bookmark_iv.setVisibility(View.VISIBLE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext() , "Unable to Bookmark Your post try agian later", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class bookmarkViewHolder extends RecyclerView.ViewHolder{

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
        ImageView bookmark_imageview;
        ImageView after_bookmark_iv;

        public bookmarkViewHolder(View itemView) {
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
            bookmark_imageview = (ImageView) itemView.findViewById(R.id.bookmark_iv);
            after_bookmark_iv = (ImageView) itemView.findViewById(R.id.bookmark_iv_after);
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
