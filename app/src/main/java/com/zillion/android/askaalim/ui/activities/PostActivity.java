package com.zillion.android.askaalim.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.Answer;
import com.zillion.android.askaalim.models.Comment;
import com.zillion.android.askaalim.models.Post;
import com.zillion.android.askaalim.models.User;
import com.zillion.android.askaalim.ui.activities.RegisterActivities.RegisterActivity;
import com.zillion.android.askaalim.ui.fragments.homeFragment;
import com.zillion.android.askaalim.utils.BaseActivity;
import com.zillion.android.askaalim.utils.Constants;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.zillion.android.askaalim.utils.sendNotification;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PostActivity extends BaseActivity implements View.OnClickListener {

    private static final String BUNDLE_COMMENT = "comment";
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mComment;
    int userType;
//    String email;

    ImageView mostLikedanswer_iv;
    TextView mostlikedanswer_name_tv;
    TextView mostlikedanswer_time_tv;
    TextView mostLikedanswer_text_tv;
    LinearLayout answers_tv;

    TextView viewallanswers_tv;
    CardView viewallComments_tv;

    CardView max_layout;
    CardView comment_cardview;


    ImageView postOwnerDisplayImageView;
    TextView postOwnerUsernameTextView;
    TextView postTimeCreatedTextView;
    ImageView postDisplayImageView;
    TextView postTextTextView;
    TextView postNumCommentsTextView;
    TextView fiqahOfAlim;
    LinearLayout postCommentLayout;
    ImageView bookmark_iv;
    ImageView bookmark_After_iv;
    ImageView menu_imageview;
    RelativeLayout readmore_Rel_layout;
    TextView tv_seenPost;

    String Current_User;
    String Current_UserName;
    String Current_UserImage;
    String FCM_token;
    PopupMenu popupMenu;

    private String fiqah;
    private String image_url;
    private String name;

    private String image_url_ans;
    private String name_ans;

    private String image_url_comment;
    private String name_comment;

    RelativeLayout readmore_Rel_layout_mostLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        mostLikedanswer_iv = (ImageView) findViewById(R.id.iv_mostlikedanswer_owner_display);
        mostlikedanswer_name_tv = (TextView) findViewById(R.id.mostliked_answer_username);
        mostlikedanswer_time_tv = (TextView) findViewById(R.id.tv_mostlikedtime_answer);
        tv_seenPost = (TextView) findViewById(R.id.tv_seen);
        mostLikedanswer_text_tv = (TextView) findViewById(R.id.tv_mostlikedanswer_text);
        viewallanswers_tv = (TextView) findViewById(R.id.viewallanswer);
        viewallComments_tv = (CardView) findViewById(R.id.viewallcomments);

        readmore_Rel_layout_mostLiked = (RelativeLayout) findViewById(R.id.readmore_relLayout_mostLiked);

        max_layout =  (CardView)findViewById(R.id.answerlayoutpostactivity);
        comment_cardview = (CardView) findViewById(R.id.cardview_for_comment);

        answers_tv = (LinearLayout) findViewById(R.id.newanswer_layout_post);
        bookmark_iv = (ImageView) findViewById(R.id.bookmark_iv);
        bookmark_After_iv = (ImageView) findViewById(R.id.bookmark_iv_after);
        menu_imageview = (ImageView) findViewById(R.id.menuPopup_imageview);

        Intent intent = getIntent();

        if(intent.getSerializableExtra(Constants.EXTRA_POST) != null) {

            mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);

        } /*else if ( postString != null ){

            postString = intent.getSerializableExtra(Constants.EXTRA_POST);

        }*/

        if(mPost.getNumComments() < 3){

            viewallComments_tv.setVisibility(View.GONE);

        } else {

            viewallComments_tv.setVisibility(View.VISIBLE);

        }

        userType = userType_sp.getInt("UserType", 0);

        if(userType == 2){

            answers_tv.setVisibility(View.GONE);

        } else if (userType == 3){

            answers_tv.setVisibility(View.GONE);
            menu_imageview.setVisibility(View.GONE);
            bookmark_iv.setVisibility(View.GONE);
            bookmark_After_iv.setVisibility(View.GONE);
            comment_cardview.setVisibility(View.GONE);

        }

        if(userType != 3) {

            FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(mPost.getPostId())) {

                                bookmark_iv.setVisibility(View.GONE);
                                bookmark_After_iv.setVisibility(View.VISIBLE);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        bookmark_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BookmarkPost(mPost.getPostId());

            }
        });

        bookmark_After_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                remove_bookmark(mPost.getPostId());

            }
        });


        FirebaseUtils.getAnswerRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(mPost.getPostId())){

                    max_layout.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        init();
        initPost();
        initAnswerSection();
        initCommentSection();

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.getAnswerRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(mPost.getPostId())){

                    max_layout.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setnumberSeen(String seencount){ tv_seenPost.setText(seencount); }

    private void remove_bookmark(String postId) {

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser()
                .getEmail().replace(".",","))
                .child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext() , "Remove Post as bookmark", Toast.LENGTH_SHORT).show();

                bookmark_iv.setVisibility(View.VISIBLE);
                bookmark_After_iv.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext() , "Unable to Remove Post as bookmark", Toast.LENGTH_SHORT).show();

            }
        });

    }

        private void BookmarkPost(final String post_id) {

            FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                    .child(post_id).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(getApplicationContext() , "Post saved", Toast.LENGTH_SHORT).show();

                    bookmark_iv.setVisibility(View.GONE);
                    bookmark_After_iv.setVisibility(View.VISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext() , "Unable to Bookmark Your post try agian later", Toast.LENGTH_SHORT).show();

                }
            });

        }


    private void initAnswerSection() {

        RecyclerView answerRecyclerView = (RecyclerView) findViewById(R.id.post_activity_answer_recyclerview);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

       FirebaseRecyclerAdapter<Answer, AnswerHolder> answerAdapter = new FirebaseRecyclerAdapter<Answer, AnswerHolder>(
               Answer.class,
               R.layout.row_answer,
               AnswerHolder.class,
               FirebaseUtils.getAnswerRef().child(mPost.getPostId()).orderByChild("numLikes").limitToLast(1)
       ) {
           @Override
           protected void populateViewHolder(final AnswerHolder viewHolder, final Answer model, int position) {

                   /*mostLikedanseer(model.getanswer());
                   setMostlikedanswer_name_tv(model.getUser().getName());
                   setMostlikedanswer_time_tv(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                   if (model.getUser().getImage() != null) {
                       Glide.with(PostActivity.this)
                               .load(model.getUser().getImage())
                               .into(mostLikedanswer_iv);
                   }*/

               FirebaseUtils.getUserRef(model.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {

                       if(dataSnapshot.hasChild("image")) {

                           image_url_ans = (String) dataSnapshot.child("image").getValue();

                           Glide.with(PostActivity.this)
                                   .load(image_url_ans)
                                   .into(mostLikedanswer_iv);

                       }
                       name_ans = (String) dataSnapshot.child("name").getValue();
                       mostLikedanseer(model.getAnswer());
                       setMostlikedanswer_time_tv(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                       if(!TextUtils.isEmpty(name_ans)){
                           mostlikedanswer_name_tv.setText(name_ans);

                       }

                       mostLikedanswer_text_tv.post(new Runnable() {
                           @Override
                           public void run() {
                               if(mostLikedanswer_text_tv.getLineCount() <= 5){

                                   readmore_Rel_layout_mostLiked.setVisibility(View.GONE);

                               } else if(mostLikedanswer_text_tv.getLineCount() >5){

                                   readmore_Rel_layout_mostLiked.setVisibility((View.VISIBLE));

                               }
                           }
                       });

                       readmore_Rel_layout_mostLiked.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {

                               mostLikedanswer_text_tv.setMaxLines(Integer.MAX_VALUE);
                               readmore_Rel_layout_mostLiked.setVisibility(View.GONE);

                           }
                       });

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
           }
       };

        answerRecyclerView.setAdapter(answerAdapter);
    }

    private void initCommentSection() {

        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.row_comment,
                CommentHolder.class,
                FirebaseUtils.getCommentRef(mPost.getPostId()).limitToLast(3)
        ) {
            @Override
            protected void populateViewHolder(final CommentHolder viewHolder, final Comment model, int position) {

            FirebaseUtils.getUserRef(model.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild("image")){

                        image_url_comment = (String) dataSnapshot.child("image").getValue();

                        Glide.with(PostActivity.this)
                                .load(image_url_comment)
                                .into(viewHolder.commentOwnerDisplay);
                    }
                    name_comment = (String) dataSnapshot.child("name").getValue();

                    viewHolder.setUsername(name_comment);

                    viewHolder.setComment(model.getComment());
                    viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            viewHolder.commentTextView.post(new Runnable() {
                @Override
                public void run() {
                    if(viewHolder.commentTextView.getLineCount() <= 3){

                        viewHolder.readmore_rel_lay_postactivity.setVisibility(View.GONE);

                    } else if(viewHolder.commentTextView.getLineCount() >3){

                        viewHolder.readmore_rel_lay_postactivity.setVisibility((View.VISIBLE));

                    }
                }
            });

            viewHolder.readmore_rel_lay_postactivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    viewHolder.commentTextView.setMaxLines(Integer.MAX_VALUE);
                    viewHolder.readmore_rel_lay_postactivity.setVisibility(View.GONE);

                }
            });

            viewHolder.reply_count_tv.setText(String.valueOf(model.getNumReply()));

            if((model.getNumReply()) > 1){
                viewHolder.reply_text_tv.setText(R.string.replies);
            } else {
                viewHolder.reply_text_tv.setText(R.string.reply);
            }

            viewHolder.commentOwnerDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent user_profile_intent = new Intent(PostActivity.this, UserProfile.class);
                    user_profile_intent.putExtra("email", model.getEmail());
                    startActivity(user_profile_intent);

                }
            });

            viewHolder.usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent user_profile_intent = new Intent(PostActivity.this, UserProfile.class);
                    user_profile_intent.putExtra("email", model.getEmail());
                    startActivity(user_profile_intent);

                }
            });

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), comment_reply_activity.class);
                    intent.putExtra(Constants.EXTRA_REPLY, model);
                    intent.putExtra(Constants.EXTRA_POST, mPost);
                    startActivity(intent);

                }
            });
            }
        };
        commentRecyclerView.setAdapter(commentAdapter);

    }

    private void initPost() {
        postOwnerDisplayImageView = (ImageView) findViewById(R.id.iv_post_owner_display);
        postOwnerUsernameTextView = (TextView) findViewById(R.id.tv_post_username);
        postTimeCreatedTextView = (TextView) findViewById(R.id.tv_time);
        postDisplayImageView = (ImageView) findViewById(R.id.iv_post_display);
        postCommentLayout = (LinearLayout) findViewById(R.id.answer_layout);
        postNumCommentsTextView = (TextView) findViewById(R.id.tv_answers);
        postTextTextView = (TextView) findViewById(R.id.tv_post_text);
        fiqahOfAlim = (TextView) findViewById(R.id.tv_post_userfiqah);
        final String post_key = mPost.getPostId();

        readmore_Rel_layout = (RelativeLayout) findViewById(R.id.readmore_relLayout);

        setnumberSeen(String.valueOf(mPost.getNumSeen()));

       postTextTextView.post(new Runnable() {
            @Override
            public void run() {
                if(postTextTextView.getLineCount() <= 3){

                    readmore_Rel_layout.setVisibility(View.GONE);

                } else if(postTextTextView.getLineCount() >3){

                    readmore_Rel_layout.setVisibility((View.VISIBLE));

                }
            }
        });

        FirebaseUtils.getUserRef(mPost.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fiqah = (String) dataSnapshot.child("fiqah").getValue();

                if(dataSnapshot.hasChild("image")) {

                    image_url = (String) dataSnapshot.child("image").getValue();

                    Glide.with(PostActivity.this)
                            .load(image_url)
                            .into(postOwnerDisplayImageView);

                }
                name = (String) dataSnapshot.child("name").getValue();

                if(!TextUtils.isEmpty(fiqah)){

                    fiqahOfAlim.setVisibility(View.VISIBLE);
                    fiqahOfAlim.setText(fiqah);
                }

                postOwnerUsernameTextView.setText(name);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

           /*if(mPost.getUser().getFiqah() != null){

               fiqahOfAlim.setVisibility(View.VISIBLE);
               fiqahOfAlim.setText(mPost.getUser().getFiqah());
       }*/

//        postOwnerUsernameTextView.setText(mPost.getUser().getName());
        postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(mPost.getTimeCreated()));
        postTextTextView.setText(mPost.getPostText());
        postNumCommentsTextView.setText(String.valueOf(mPost.getNumAnswers()));

//        Glide.with(PostActivity.this)
//                .load(mPost.getUser().getImage())
//                .into(postOwnerDisplayImageView);

        postOwnerDisplayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUtils.getPostRef().child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Current_User = (String) dataSnapshot.child("email").getValue();

                        Intent user_profile = new Intent(getApplicationContext() , UserProfile.class);
                        user_profile.putExtra("postkey", post_key);
                        user_profile.putExtra("email", Current_User);
                        startActivity(user_profile);

                        if(userType != 3){

                            PostSeen(mPost.getPostId());

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        postTextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userType != 3){

                    PostSeen(mPost.getPostId());

                }

            }
        });


        postCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType != 3){

                    PostSeen(mPost.getPostId());

                }
            }
        });


        tv_seenPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType != 3){

                    PostSeen(mPost.getPostId());

                }
            }
        });

        postDisplayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(PostActivity.this, android.R.style.Animation);
                dialog.setContentView(R.layout.dialogpostimage_layout);
                ImageView myImage = (ImageView) dialog.findViewById(R.id.i);


                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference(mPost.getPostImageUrl());

                Glide.with(PostActivity.this)
                        .using(new FirebaseImageLoader()).load(storageReference).into(myImage);

                if(userType != 3){

                    PostSeen(mPost.getPostId());

                }



                dialog.show();
            }
        });

        postOwnerUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUtils.getPostRef().child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Current_User = (String) dataSnapshot.child("email").getValue();

                        Intent user_profile = new Intent(getApplicationContext() , UserProfile.class);
                        user_profile.putExtra("postkey", post_key);
                        user_profile.putExtra("email", Current_User);
                        startActivity(user_profile);

                        if(userType != 3){

                            PostSeen(mPost.getPostId());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


        if (mPost.getPostImageUrl() != null) {
            postDisplayImageView.setVisibility(View.VISIBLE);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(mPost.getPostImageUrl());

            Glide.with(PostActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(postDisplayImageView);
        } else {
            postDisplayImageView.setImageBitmap(null);
            postDisplayImageView.setVisibility(View.GONE);
        }

        viewallComments_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent answers_intent = new Intent(PostActivity.this, viewallComments.class);
                    answers_intent.putExtra(Constants.EXTRA_POST, mPost);
                    startActivity(answers_intent);

            }
        });

        viewallanswers_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent answers_intent = new Intent(PostActivity.this , answersActivity.class);
                answers_intent.putExtra(Constants.EXTRA_POST , mPost);
                startActivity(answers_intent);

            }
        });

        readmore_Rel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postTextTextView.setMaxLines(Integer.MAX_VALUE);
                readmore_Rel_layout.setVisibility(View.GONE);

            }
        });

        answers_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent answers_inteent = new Intent(PostActivity.this , postNewAnswer.class);
                answers_inteent.putExtra(Constants.EXTRA_POST , mPost);
                startActivity(answers_inteent);

            }
        });


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

    private void init() {
        setting_num_answer();
        setting_num_comment();

        mCommentEditTextView = (EditText) findViewById(R.id.et_comment);

        findViewById(R.id.iv_send_comment).setOnClickListener(this);

//        findViewById(R.id.menuPopup_imageview).setOnClickListener(this);

        menu_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupMenu = new PopupMenu(PostActivity.this, menu_imageview);

                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                /*FirebaseUtils.getPostRef().child(mPost.getPostId()).child("user").addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String owner_of_post = dataSnapshot.child("email").getValue().toString();

                        if(!owner_of_post.equals(FirebaseUtils.getCurrentUser().getEmail())){



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                if(!mPost.getEmail().replace(".",",")
                        .equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))) {

//                    Menu m = popupMenu.getMenu();
//                    m.removeItem((R.id.delete));

                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);

                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();

                        if(id == R.id.delete){

                            if(mPost.getEmail().replace(".",",").equals(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))){

                                Toast.makeText(getApplicationContext() , "Delete", Toast.LENGTH_SHORT).show();

                                DeletePost(mPost.getPostId());

                            }

                        } else  if ( id == R.id.report){

                            Toast.makeText(PostActivity.this , "Report", Toast.LENGTH_SHORT).show();

                        }
                        return  true;
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send_comment:

                sendComment();
                setting_num_comment();
                break;

        }
    }

    private void setting_num_comment() {

        FirebaseUtils.getPostRef().child(mPost.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView postNumCommentsTextView = (TextView) findViewById(R.id.tv_comments);

                Long numcmnt = (Long) dataSnapshot.child(Constants.NUM_COMMENTS_KEY).getValue();

                String numLike = String.valueOf(numcmnt);

                postNumCommentsTextView.setText(numLike);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void sendComment() {
        final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Sending comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mComment = new Comment();

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    final String uid = FirebaseUtils.getUid();
                    String strComment = mCommentEditTextView.getText().toString();

                    mComment.setEmail(FirebaseUtils.getCurrentUser().getEmail());
                    mComment.setCommentId(uid);
                    mComment.setComment(strComment);
                    mComment.setTimeCreated(System.currentTimeMillis());
                    mComment.setNumReply(0);

                    FirebaseUtils.getCommentRef(mPost.getPostId())
                            .child(uid)
                            .setValue(mComment);

                    if(mPost.getNumComments() < 3){

                        viewallComments_tv.setVisibility(View.GONE);

                    } else {

                        viewallComments_tv.setVisibility(View.VISIBLE);

                    }

                    FirebaseUtils.getPostRef().child(mPost.getPostId())
                        .child(Constants.NUM_COMMENTS_KEY)
                        .runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long num = (long) mutableData.getValue();
                                mutableData.setValue(num + 1);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                progressDialog.dismiss();

                                //FirebaseUtils.addToMyRecord(Constants.COMMENTS_KEY, uid);

                                mCommentEditTextView.setText("");

                                final String C_Current_user = FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

                                String updated_email = mPost.getEmail().replace(".",",");

                                if(!C_Current_user.equals(updated_email)){

                                    String pushId = FirebaseUtils.getUid();

                                    FirebaseUtils.getNotificationRef().child("notifications").child(updated_email.replace(".",","))
                                            .child(pushId).child("fromUser").setValue(FirebaseUtils.getCurrentUser().getEmail().replace(".",","));

                                }
                            }
                        });



                    Boolean isFirtComment = getSharedPreferences("firstCommentPref",  MODE_PRIVATE).getBoolean("isFrtComment", true);

                    if(isFirtComment){

                        final Dialog dialog = new Dialog(PostActivity.this, android.R.style.Widget_PopupWindow);
                        dialog.setContentView(R.layout.dialogfirstcommentinstructions);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        dialog.setCancelable(true);
                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                        final CheckBox dont_show = (CheckBox) dialog.findViewById(R.id.dont_show_checkbox_comment);
                        Button close = (Button) dialog.findViewById(R.id.ins_close_b_comment);

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(dont_show.isChecked()){

                                    getSharedPreferences("firstCommentPref",  MODE_PRIVATE).edit().putBoolean("isFrtComment", false).commit();

                                }

                                dialog.dismiss();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
    }

    private void setting_num_answer() {

        FirebaseUtils.getPostRef().child(mPost.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView postNumAnswersTextView = (TextView) findViewById(R.id.tv_answers);

                Long numanswers = (Long) dataSnapshot.child(Constants.NUM_ANSWWERS_KEY).getValue();

                String numLike = String.valueOf(numanswers);

                postNumAnswersTextView.setText(numLike);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void mostLikedanseer(String mostliked) {
        mostLikedanswer_text_tv.setText(mostliked);
    }

    public void setMostlikedanswer_name_tv(String name){

        mostlikedanswer_name_tv.setText(name);

    }

    public void setMostlikedanswer_time_tv(CharSequence time){
        mostlikedanswer_time_tv.setText(time);

    }

    public static class AnswerHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView AnswerOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView AnswerTextView;
        ImageView playaudio;
        TextView LikeAnswer_iv;
        TextView LikeAnswer_tv;

        public AnswerHolder(View itemView) {

            super(itemView);

            mView = itemView;


            AnswerOwnerDisplay = (ImageView) itemView.findViewById(R.id.iv_answer_owner_display);
            usernameTextView = (TextView) itemView.findViewById(R.id.tv_username_answer);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time_answer);
            AnswerTextView = (TextView) itemView.findViewById(R.id.tv_answer);
            playaudio = (ImageView) mView.findViewById(R.id.fab_answer);
            LikeAnswer_iv = (TextView) mView.findViewById(R.id.iv_like_answer);
            LikeAnswer_tv = (TextView) mView.findViewById(R.id.tv_likes_answer);


        }

    }


    public static class CommentHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView commentOwnerDisplay;

        TextView reply_text_tv;
        TextView reply_count_tv;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        RelativeLayout readmore_rel_lay_postactivity;

//        LinearLayout reply_layout;

        public CommentHolder(View itemView) {

            super(itemView);

            mView = itemView;

            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.iv_comment_owner_display);

            usernameTextView = (TextView) itemView.findViewById(R.id.tv_username);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            commentTextView = (TextView) itemView.findViewById(R.id.tv_comment);
            reply_text_tv = (TextView) itemView.findViewById(R.id.reply_text);
            reply_count_tv = (TextView) itemView.findViewById(R.id.reply_count);

            readmore_rel_lay_postactivity = (RelativeLayout) itemView.findViewById(R.id.readmore_relLayout_postactivity);

//            reply_layout = (LinearLayout) itemView.findViewById(R.id.reply_layout);

        }

        public void setUsername(String username) {
            usernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            timeTextView.setText(time);
        }

        public void setComment(String comment) {
            commentTextView.setText(comment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }

    /*public static void sendNotification(String user_id,String message,String description,String type){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(user_id);
        String pushKey = databaseReference.push().getKey();

        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setMessage(message);
        notification.setUser_id(user_id);
        notification.setType(type);

        Map<String, Object> forumValues = notification.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(pushKey, forumValues);
        databaseReference.setPriority(ServerValue.TIMESTAMP);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){

                }
            }
        });
    }*/

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        if(userType == 3){

            mAuth.signOut();
            startActivity(new Intent(PostActivity.this , RegisterActivity.class));
            finish();

        }
    }*/
}