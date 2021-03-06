package com.zillion.android.askaalim.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.User;
import com.zillion.android.askaalim.ui.activities.UserProfile;
import com.zillion.android.askaalim.utils.Constants;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class Followers extends Fragment {

    View mRootview;
    RecyclerView follower_recyc_view;
    String emails;
    private SwipeRefreshLayout mSwipeRef__followers;
    private TextView follow_tv;

    public Followers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootview = inflater.inflate(R.layout.fragment_followers, container, false);

        mSwipeRef__followers = (SwipeRefreshLayout) mRootview.findViewById(R.id.swipe_refresh_layout_followers);
        follow_tv = (TextView)mRootview.findViewById(R.id.follow_tv);

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

    @Override
    public void onResume() {
        super.onResume();
        setupadapter();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){

            getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        }
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

                if(model == null){
                    follow_tv.setVisibility(View.VISIBLE);
                }else {
                    follow_tv.setVisibility(View.GONE);
                    viewHolder.username.setText(model.getName());
                }

                assert model != null;

                if(!TextUtils.isEmpty(model.getImage())) {
                    Glide.with(getActivity())
                            .load(model.getImage())
                            .into(viewHolder.profileimage);
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent user_profile = new Intent(getContext() , UserProfile.class);
                        user_profile.putExtra("email", model.getEmail());
                        startActivity(user_profile);

                    }
                });

            }
        };

        follower_recyc_view.setAdapter(adapter);
//        initialize();
    }

    public static class followerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        TextView username;
        CircleImageView profileimage;

        public followerViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            username = (TextView) itemView.findViewById(R.id.username_search);
//            email = (TextView) itemView.findViewById(R.id.email_search);

            profileimage = (CircleImageView) itemView.findViewById(R.id.profile_image);

        }
    }

}
