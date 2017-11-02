package com.example.nabeel.postandcommenttutorial.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.ui.activities.CreateNewPostActivity;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.ui.activities.PostActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.UserProfile;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.postNewAnswer;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;

/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment {
    private View mRootVIew;

    private FirebaseRecyclerAdapter<Post, PostHolder> mPostAdapter;

    private RecyclerView mPostRecyclerView;

    String Current_User;

    private FirebaseAuth mAuth;

    private ImageView m_current_user_display_image;

    private TextView mAsk_Ques;
    private TextView m_current_user_display_name;
    static String Loggedin_user_email;
    public static String login_user_name;

    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootVIew = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();


        m_current_user_display_image = (ImageView)mRootVIew.findViewById(R.id.current_user_display_image);
        m_current_user_display_name = (TextView) mRootVIew.findViewById(R.id.current_user_display_name);

        mAsk_Ques = (TextView) mRootVIew.findViewById(R.id.ask_ques_tv);

        if (mAuth.getCurrentUser() == null) {

            Intent loginintent =  new Intent(getContext(), RegisterActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginintent);

        }
        else if(mAuth.getCurrentUser() != null) {

            OneSignal.startInit(getActivity())
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();

            Loggedin_user_email = mAuth.getCurrentUser().getEmail();
            OneSignal.sendTag("User_ID", Loggedin_user_email);

            FirebaseUtils.getUserRef(Loggedin_user_email.replace(".",",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    login_user_name = dataSnapshot.child("name").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseUtils.getPostRef().keepSynced(true);


            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = (String) dataSnapshot.child("name").getValue();
                        String photo_url = (String) dataSnapshot.child("image").getValue();

                        if(photo_url != null) {

                            Glide.with(getContext()).load(photo_url).into(m_current_user_display_image);
                        }
                        m_current_user_display_name.setText(name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        /*else{

            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = (String) dataSnapshot.child("name").getValue();
                    String photo_url = (String) dataSnapshot.child("image").getValue();

                    if(photo_url != null) {

                        Glide.with(getContext()).load(photo_url).into(m_current_user_display_image);
                    }
                    m_current_user_display_name.setText(name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }*/

        mAsk_Ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent post_intent = new Intent(getContext(), CreateNewPostActivity.class);
                startActivity(post_intent);

            }
        });





        /*FloatingActionButton fab = (FloatingActionButton) mRootVIew.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PostCreateDialog dialog = new PostCreateDialog();
//                dialog.show(getFragmentManager(), null);

                Intent post_intent = new Intent(getContext(), CreateNewPostActivity.class);
                startActivity(post_intent);

            }
        });*/


        init();

        return mRootVIew;
    }

    private void init() {

        mPostRecyclerView = (RecyclerView) mRootVIew.findViewById(R.id.recyclerview_post);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostRecyclerView.setLayoutManager(mLayoutManager);

        if(mAuth.getCurrentUser() != null) {
            setupAdapter();
        }
        mPostRecyclerView.setAdapter(mPostAdapter);

        /*mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if( mLayoutManager.getItemCount() == mLayoutManager.findFirstVisibleItemPosition()){
                    setupAdapter();

                }

            }
        });*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){

            getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        }
    }

    private void setupAdapter() {
        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.row_post,
                PostHolder.class,
                FirebaseUtils.getPostRef()
        ) {
            @Override
            protected void populateViewHolder(final PostHolder viewHolder, final Post model, int position) {

                final String post_key = getRef(position).getKey();


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


                SharedPreferences userType_sp = getActivity().getSharedPreferences("UserType", Context.MODE_PRIVATE);

                int userType = userType_sp.getInt("UserType", 0);
                if(userType == 2){

                    viewHolder.newanswers.setVisibility(View.GONE);

                } else {

                    if(model.getUser().getFiqah() != null){

                        viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                        viewHolder.setFiqahOfAlim(model.getUser().getFiqah());

                    }

                }

                viewHolder.setNumCOmments(String.valueOf(model.getNumComments()));
                viewHolder.setNumAnswers(String.valueOf(model.getNumAnswers()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                viewHolder.setUsername(model.getUser().getName());
                viewHolder.setPostText(model.getPostText());

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
                        dialog.show();
                    }
                });

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);

                    }
                });

                viewHolder.newanswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent answers_inteent = new Intent(getActivity() , postNewAnswer.class);
                        answers_inteent.putExtra(Constants.EXTRA_POST , model);
                        startActivity(answers_inteent);
                    }
                });

                viewHolder.menu_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final PopupMenu popupMenu = new PopupMenu(getContext(), viewHolder.menu_imageview);

                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

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

                                } else if ( id == R.id.edit_question){

                                    Toast.makeText(getContext() , "Edit Question", Toast.LENGTH_SHORT).show();

                                }

                                return  true;
                            }
                        });

                        popupMenu.show();
                    }
                });



                DatabaseReference mDatabaseposts = FirebaseDatabase.getInstance().getReference().child(Constants.POST_KEY).child(post_key);

                mDatabaseposts.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Current_User = (String) dataSnapshot.child("uid").getValue();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.postOwnerDisplayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseUtils.getPostRef().child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Current_User = (String) dataSnapshot.child("user").child("email").getValue();
//                                Toast.makeText(getContext() , Current_User , Toast.LENGTH_LONG).show();
//                                Toast.makeText(getContext() , "post key "+post_key , Toast.LENGTH_LONG).show();

                                Intent user_profile = new Intent(getContext() , UserProfile.class);
                                user_profile.putExtra("postkey", post_key);
                                user_profile.putExtra("email", Current_User);
                                startActivity(user_profile);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                viewHolder.bookmark_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BookmarkPost(model.getPostId() , viewHolder);

                    }
                });

                viewHolder.after_bookmark_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        remove_bookmark(model.getPostId(), viewHolder);

                    }
                });
            }
        };
    }



    private void remove_bookmark(String postId, final PostHolder view) {

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

        //FirebaseUtils.getPostRef().child(postId).removeValue();

    }

    private void BookmarkPost(final String post_id, final PostHolder view) {


//        String id = FirebaseUtils.getPostRef().child(post_id).getKey();
//        //Toast.makeText(getContext() , id , Toast.LENGTH_SHORT).show();
//        //Toast.makeText(getContext() , id , Toast.LENGTH_SHORT).show();
//
//        FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
//                .child(id).setValue("true");

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


                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child("postId").setValue(postid);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child("postText").setValue(postText);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child("timeCreated").setValue(timeCreated);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child(Constants.NUM_LIKES_KEY).setValue(numLikes);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child(Constants.NUM_ANSWWERS_KEY).setValue(numOfAns);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).child(Constants.NUM_COMMENTS_KEY).setValue(numOfCmnt);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("userImage").setValue(userImg);

                FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                        .child(post_id).child("userName").setValue(userName).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageVIew;
        TextView postTextTextView;
        LinearLayout postCommentLayout;
        TextView postNumAnswersTextView;
        TextView postNumCommentsTextView;
        LinearLayout mPostView;
        ImageView menu_imageview;
        ImageView bookmark_imageview;
        ImageView after_bookmark_iv;
        TextView newanswers;
        TextView fiqahOfAlim;
        RelativeLayout readmore_rel_layout;

        public PostHolder(View itemView) {
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
            mPostView = (LinearLayout) itemView.findViewById(R.id.postview);
            menu_imageview = (ImageView) itemView.findViewById(R.id.menuPopup_imageview);
            bookmark_imageview = (ImageView) itemView.findViewById(R.id.bookmark_iv);
            after_bookmark_iv = (ImageView) itemView.findViewById(R.id.bookmark_iv_after);
            newanswers = (TextView) itemView.findViewById(R.id.newanswer_layout_post);
            fiqahOfAlim = (TextView) itemView.findViewById(R.id.tv_post_userfiqah);
            readmore_rel_layout = (RelativeLayout) itemView.findViewById(R.id.readmore_relLayout);


        }

        public void setUsername(String username) {
            postOwnerUsernameTextView.setText(username);
        }

        public void setTIme(CharSequence time) {
            postTimeCreatedTextView.setText(time);
        }

        public void setNumAnswers(String numAnswers) {
            postNumAnswersTextView.setText(numAnswers);
        }

        public void setNumCOmments(String numCOmments){

            postNumCommentsTextView.setText(numCOmments);

        }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }

        public void setFiqahOfAlim(String fiqah){
            fiqahOfAlim.setText(fiqah);
        }

    }
}
