package com.example.nabeel.postandcommenttutorial.ui.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.ui.activities.PostActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.UserProfile;
import com.example.nabeel.postandcommenttutorial.ui.activities.postNewAnswer;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class bookmarkFragment extends Fragment {

    View mRootView;

    RecyclerView bookmark_recyc_view;
    String Current_User;
    private SwipeRefreshLayout mSwipeRef_bookmark;

    public bookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmark_recyc_view = (RecyclerView) mRootView.findViewById(R.id.bookmark_recyclerview);
        mSwipeRef_bookmark = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout_bm);

        mSwipeRef_bookmark.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setupadapter();
                        mSwipeRef_bookmark.setRefreshing(false);
                    }
                },5000);
            }
        });

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

        FirebaseIndexRecyclerAdapter<Post, bookmarkViewHolder> bookmarkadapter = new FirebaseIndexRecyclerAdapter<Post, bookmarkViewHolder>(
                Post.class,
                R.layout.row_post,
                bookmarkViewHolder.class,
                FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")),
                FirebaseUtils.getPostRef()
        ) {

            @Override
            protected void populateViewHolder(final bookmarkViewHolder viewHolder, final Post model, int position) {

                final String post_key = getRef(position).getKey();

                SharedPreferences userType_sp = getActivity().getSharedPreferences("UserType", Context.MODE_PRIVATE);

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

                if(model.getUser().getFiqah() != null){

                    viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                    viewHolder.setFiqahOfAlim(model.getUser().getFiqah());

                }

                if(userType == 2){

                    viewHolder.newanswers.setVisibility(View.GONE);

                } else if( userType == 3){

                    viewHolder.bookmark_imageview.setVisibility(View.GONE);
                    viewHolder.after_bookmark_iv.setVisibility(View.GONE);
                    viewHolder.menu_imageview.setVisibility(View.GONE);
                    viewHolder.newanswers.setVisibility(View.GONE);
//                    mAsk_Ques.setVisibility(View.GONE);
                }

                viewHolder.setPostText(model.getPostText());
                viewHolder.setNumAnswers(String.valueOf(model.getNumAnswers()));
                viewHolder.setUsername(model.getUser().getName());
                viewHolder.setnumberSeen(String.valueOf(model.getNumSeen()));
                viewHolder.setNumCOmments(String.valueOf(model.getNumAnswers()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

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

                viewHolder.postOwnerDisplayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseUtils.getPostRef().child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Current_User = (String) dataSnapshot.child("user").child("email").getValue();

                                Intent user_profile = new Intent(getContext() , UserProfile.class);
                                user_profile.putExtra("postkey", post_key);
                                user_profile.putExtra("email", Current_User);
                                startActivity(user_profile);

                                if(userType != 3){

                                    PostSeen(model.getPostId());

                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.postTextTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }

                    }
                });

                viewHolder.postOwnerUsernameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseUtils.getPostRef().child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Current_User = (String) dataSnapshot.child("user").child("email").getValue();

                                Intent user_profile = new Intent(getContext() , UserProfile.class);
                                user_profile.putExtra("postkey", post_key);
                                user_profile.putExtra("email", Current_User);
                                startActivity(user_profile);

                                if(userType != 3){

                                    PostSeen(model.getPostId());

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                viewHolder.readmore_rel_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.postTextTextView.setMaxLines(Integer.MAX_VALUE);
                        viewHolder.readmore_rel_layout.setVisibility(View.GONE);

                    }
                });

                viewHolder.tv_seenPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
                    }
                });

                viewHolder.menu_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final PopupMenu popupMenu = new PopupMenu(getContext(), viewHolder.menu_imageview);

                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                        if(!model.getUser().getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))) {

                            Menu m = popupMenu.getMenu();
                            m.removeItem((R.id.delete));

                        }

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override

                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();

                                if(id == R.id.delete){

                                    if(model.getUser().getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))){

                                        Toast.makeText(getContext() , "Delete", Toast.LENGTH_SHORT).show();

                                        DeletePost(model.getPostId());

                                    }

                                } else  if ( id == R.id.report){

                                    Toast.makeText(getContext() , "Report", Toast.LENGTH_SHORT).show();

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

                viewHolder.postDisplayImageVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(getContext(), android.R.style.Animation);
                        dialog.setContentView(R.layout.dialogpostimage_layout);
                        ImageView myImage = (ImageView) dialog.findViewById(R.id.i);


                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReference(model.getPostImageUrl());

                        Glide.with(getContext())
                                .using(new FirebaseImageLoader()).load(storageReference).into(myImage);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }

                        dialog.show();
                    }
                });

                Glide.with(getActivity())
                        .load(model.getUser().getImage())
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

                        BookmarkPost(model.getPostId(), viewHolder);

                    }
                });

                viewHolder.newanswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent answers_inteent = new Intent(getActivity() , postNewAnswer.class);
                        answers_inteent.putExtra(Constants.EXTRA_POST , model);
                        startActivity(answers_inteent);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
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

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child("Bookmarked").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getContext() , "Post saved", Toast.LENGTH_SHORT).show();

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

    }

    public static class bookmarkViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageVIew;
        TextView postTextTextView;
        LinearLayout postCommentLayout;
        TextView postNumAnswersTextView;
        TextView postNumCommentsTextView;
//        LinearLayout mPostView;
        ImageView menu_imageview;
        ImageView bookmark_imageview;
        ImageView after_bookmark_iv;
        TextView fiqahOfAlim;
        ImageView newanswers;
        RelativeLayout readmore_rel_layout;
        TextView tv_seenPost;

        public bookmarkViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.iv_post_owner_display);
            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.tv_post_username);
            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.tv_time);
            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.iv_post_display);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.answer_layout);
            postNumAnswersTextView = (TextView) itemView.findViewById(R.id.tv_answers);
            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.tv_comments);
            postTextTextView = (TextView) itemView.findViewById(R.id.tv_post_text);
//            mPostView = (LinearLayout) itemView.findViewById(R.id.postview);
            menu_imageview = (ImageView) itemView.findViewById(R.id.menuPopup_imageview);
            bookmark_imageview = (ImageView) itemView.findViewById(R.id.bookmark_iv);
            after_bookmark_iv = (ImageView) itemView.findViewById(R.id.bookmark_iv_after);
            fiqahOfAlim = (TextView) itemView.findViewById(R.id.tv_post_userfiqah);
            newanswers = (ImageView) itemView.findViewById(R.id.newanswer_layout_post);
            readmore_rel_layout = (RelativeLayout) itemView.findViewById(R.id.readmore_relLayout);
            tv_seenPost = (TextView) itemView.findViewById(R.id.tv_seen);
        }

        public void setUsername(String username) {
            postOwnerUsernameTextView.setText(username);
        }

        public void setTIme(CharSequence time) {
            postTimeCreatedTextView.setText(time);
        }

        public void setNumCOmments(String numCOmments){ postNumCommentsTextView.setText(numCOmments); }

        public void setNumAnswers(String numAnswers) { postNumAnswersTextView.setText(numAnswers); }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }

        public void setFiqahOfAlim(String fiqah){ fiqahOfAlim.setText(fiqah); }

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

}
