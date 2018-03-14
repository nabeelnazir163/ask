package com.zillion.android.askaalim.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.zillion.android.askaalim.ui.activities.CreateNewPostActivity;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.ui.activities.MainActivity;
import com.zillion.android.askaalim.ui.activities.PostActivity;
import com.zillion.android.askaalim.ui.activities.UserProfile;
import com.zillion.android.askaalim.models.Post;
import com.zillion.android.askaalim.ui.activities.RegisterActivities.RegisterActivity;
import com.zillion.android.askaalim.ui.activities.postNewAnswer;
import com.zillion.android.askaalim.utils.Constants;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class homeFragment extends Fragment {
    private View mRootVIew;

    FirebaseRecyclerAdapter<Post, PostHolder> mPostAdapter;

    private RecyclerView mPostRecyclerView;

//    String Current_User;

    String photo_url;
    private FirebaseAuth mAuth;

    private ImageView m_current_user_display_image;

    private TextView mAsk_Ques;
    private TextView m_current_user_display_name;
    private SwipeRefreshLayout mSwipeRef_home;
    FloatingActionButton fab;
    SharedPreferences userType_sp;
    private int userType;

    private String fiqah;
    private String image_url;
    private String name;

    private Animation anim;

    LayoutInflater toast_inflater;
    View toast_layout;
    TextView toast_text;
    ImageView toast_imageView;

    private ShimmerFrameLayout mShimmerViewContainer;

    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootVIew = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();

        mShimmerViewContainer = mRootVIew.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();

        FirebaseUtils.getPostRef().keepSynced(true);

        userType_sp = getActivity().getSharedPreferences("UserType", Context.MODE_PRIVATE);

        userType = userType_sp.getInt("UserType", 0);

        anim = AnimationUtils.loadAnimation(getContext() , R.anim.toast_anim);

        toast_inflater = getLayoutInflater();
        toast_layout = toast_inflater.inflate(R.layout.toast_layout,
                (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

        toast_text = (TextView) toast_layout.findViewById(R.id.text);
        toast_imageView = (ImageView) toast_layout.findViewById(R.id.image);

        m_current_user_display_image = (ImageView)mRootVIew.findViewById(R.id.current_user_display_image);
        m_current_user_display_name = (TextView) mRootVIew.findViewById(R.id.current_user_display_name);
        mSwipeRef_home = (SwipeRefreshLayout) mRootVIew.findViewById(R.id.swipe_refresh_layout);

        mSwipeRef_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init();

                        init_home();
                        mSwipeRef_home.setRefreshing(false);
                    }
                },2000);
            }
        });

        mAsk_Ques = (TextView) mRootVIew.findViewById(R.id.ask_ques_tv);

        if (mAuth.getCurrentUser() == null) {

            Intent loginintent =  new Intent(getContext(), RegisterActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginintent);

        }
        else if(mAuth.getCurrentUser().getEmail() != null) {

            FirebaseUtils.getPostRef().keepSynced(true);

            init_home();

            FirebaseUtils.getNotificationRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .orderByChild("status")
                .equalTo("seen")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        if(count != 0) {
                            MainActivity.notification_tv.setText(count + "");
                        } else if(count == 0){

                            MainActivity.notification_tv.setVisibility(View.GONE);

                        }
//                            Toast.makeText(getContext(), (int) count, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        }



        mAsk_Ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent post_intent = new Intent(getContext(), CreateNewPostActivity.class);
                startActivity(post_intent);

            }
        });

        fab = (FloatingActionButton) mRootVIew.findViewById(R.id.fab);

        if(userType == 3){

            fab.setVisibility(View.GONE);

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent post_intent = new Intent(getContext(), CreateNewPostActivity.class);
                startActivity(post_intent);

            }
        });


        init();

        return mRootVIew;
    }

    private void init_home() {
        if(userType != 3){

            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = (String) dataSnapshot.child("name").getValue();
                    photo_url = (String) dataSnapshot.child("image").getValue();

                    if(!TextUtils.isEmpty(photo_url)) {

                        Glide.with(m_current_user_display_image.getContext().getApplicationContext()).load(photo_url).into(m_current_user_display_image);
//                    Toast.makeText(getContext(), photo_url, Toast.LENGTH_SHORT).show();
                        Log.i("Imageurl", photo_url);
                        Log.d("Imageurl", photo_url);
                    }else {

                        Glide.with(getActivity()).load(photo_url).placeholder(R.drawable.facebook).into(m_current_user_display_image);

                    }
                    m_current_user_display_name.setText(name);



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mShimmerViewContainer.startShimmerAnimation();
//    }
//
//    @Override
//    public void onPause() {
//        mShimmerViewContainer.stopShimmerAnimation();
//        super.onPause();
//    }

    private void init() {

        getActivity().invalidateOptionsMenu();

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

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        if(isVisibleToUser){
//
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
//
//        }
//    }

    private void setupAdapter() {
        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.row_post,
                PostHolder.class,
                FirebaseUtils.getPostRef()
        ) {
            @Override
            protected void populateViewHolder(final PostHolder viewHolder, final Post model, int position) {

//                final String post_key = getRef(position).getKey();

                ;
                if(model == null){
                    mShimmerViewContainer.setVisibility(View.GONE);
                }

                mShimmerViewContainer.setVisibility(View.GONE);

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
                        if(dataSnapshot.hasChild("fiqah")) {
                            fiqah = dataSnapshot.child("fiqah").getValue().toString();
                        }

                        if(dataSnapshot.hasChild("image")) {
                            image_url = dataSnapshot.child("image").getValue().toString();
                        }

                        if(dataSnapshot.hasChild("name")) {
                            name = dataSnapshot.child("name").getValue().toString();
                        }

                        if(!TextUtils.isEmpty(fiqah)){

                            viewHolder.fiqahOfAlim.setVisibility(View.VISIBLE);
                            viewHolder.setFiqahOfAlim(fiqah);
                        }

                        if( !TextUtils.isEmpty(name))
                        viewHolder.setUsername(name);

                        if( !TextUtils.isEmpty(image_url)) {
                            Glide.with(getContext()).load(image_url).into(viewHolder.postOwnerDisplayImageView);
//                            Picasso.with(getActivity()).load(image_url).into(viewHolder.postOwnerDisplayImageView);
//                            Toast.makeText(getActivity(), image_url, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(userType == 2){

                    viewHolder.newanswers.setVisibility(View.GONE);

                } else if( userType == 3){

                    m_current_user_display_name.setText("Guest User");
                    viewHolder.bookmark_imageview.setVisibility(View.GONE);
                    viewHolder.after_bookmark_iv.setVisibility(View.GONE);
                    viewHolder.menu_imageview.setVisibility(View.GONE);
                    viewHolder.newanswers.setVisibility(View.GONE);
                    mAsk_Ques.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("ALERT");
                            alert.setMessage("You cannot Post New Question, you have to login first for posting new question");

                            alert.show();

                        }
                    });
                }

                viewHolder.setNumCOmments(String.valueOf(model.getNumComments()));
                viewHolder.setNumAnswers(String.valueOf(model.getNumAnswers()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

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


                if (model.getPostImageUrl() != null) {
//                    viewHolder.postDisplayImageVIew.setVisibility(View.VISIBLE);
//                    StorageReference storageReference = FirebaseStorage.getInstance()
//                            .getReference(model.getPostImageUrl());
//                    Glide.with(getContext())
//                            .load(model.getPostImageUrl())
//                            .into(viewHolder.postDisplayImageVIew);

                    viewHolder.postDisplayImageVIew.setVisibility(View.VISIBLE);
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(model.getPostImageUrl());
                    Glide.with(getActivity())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.postDisplayImageVIew);

//                    Picasso.with(getContext()).load(model.getPostImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.postDisplayImageVIew, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError() {
//
//                            Picasso.with(getContext()).load(model.getPostImageUrl()).into(viewHolder.postDisplayImageVIew);
//
//                        }
//                    });
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

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }



                        dialog.show();
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

                viewHolder.CommentLayout_post.setOnClickListener(new View.OnClickListener() {
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

                viewHolder.menu_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final PopupMenu popupMenu = new PopupMenu(getContext(), viewHolder.menu_imageview);

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

//                                        Toast.makeText(getContext() , "Delete", Toast.LENGTH_SHORT).show();

                                        toast_imageView.setVisibility(View.GONE);
                                        toast_text.setText("Delete");
                                        Toast toast = new Toast(getContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(toast_layout);
                                        toast.show();

                                        DeletePost(model.getPostId());

                                    }

                                } else  if ( id == R.id.report){

//                                    Toast.makeText(getContext() , "Report", Toast.LENGTH_SHORT).show();

                                    toast_imageView.setVisibility(View.GONE);
                                    toast_text.setText("Report");
                                    Toast toast = new Toast(getContext());
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(toast_layout);
                                    toast.show();
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
                viewHolder.postOwnerDisplayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent user_profile = new Intent(getContext() , UserProfile.class);
                        user_profile.putExtra("email", model.getEmail());
                        startActivity(user_profile);

                        if(userType != 3) {

                            PostSeen(model.getPostId());
                        }

                    }
                });

                viewHolder.postOwnerUsernameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent user_profile = new Intent(getContext() , UserProfile.class);
                        user_profile.putExtra("email", model.getEmail());
                        startActivity(user_profile);

                        if(userType != 3){

                            PostSeen(model.getPostId());

                        }
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
    }

    private void remove_bookmark(String postId, final PostHolder view) {

        FirebaseUtils.getBookmarksRef().child(FirebaseUtils.getCurrentUser()
                .getEmail().replace(".",","))
                .child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

//                Toast.makeText(getContext() , "Remove Post as bookmark", Toast.LENGTH_SHORT).show();

                toast_imageView.setVisibility(View.GONE);
                toast_text.setText("Removed Post from bookmark");
                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toast_layout);
                toast.show();

                view.bookmark_imageview.setVisibility(View.VISIBLE);
                view.after_bookmark_iv.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

//                Toast.makeText(getContext() , "Unable to Remove Post as bookmark", Toast.LENGTH_SHORT).show();

                toast_imageView.setVisibility(View.GONE);
                toast_text.setText("Unable to Remove Post from bookmark");
                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toast_layout);
                toast.show();
            }
        });

    }

    private void BookmarkPost(final String post_id, final PostHolder view) {

        FirebaseUtils.getBookmarksRef().child(mAuth.getCurrentUser().getEmail().replace(".",","))
                .child(post_id).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                toast_imageView.setAnimation(anim);
                toast_imageView.setVisibility(View.VISIBLE);
                toast_text.setText("Post saved");
                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toast_layout);
                toast.show();

                view.bookmark_imageview.setVisibility(View.GONE);
                view.after_bookmark_iv.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

//                        Toast.makeText(getContext() , "Unable to Bookmark Your post try agian later", Toast.LENGTH_SHORT).show();

                        toast_imageView.setVisibility(View.GONE);
                        toast_text.setText("Unable to Bookmark Your post try agian later");
                        Toast toast = new Toast(getContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(toast_layout);
                        toast.show();
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

    public static class PostHolder extends RecyclerView.ViewHolder {
        View mView;
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

        public PostHolder(View itemView) {
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
}
