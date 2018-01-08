package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Answer;
import com.example.nabeel.postandcommenttutorial.models.Comment;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class viewallComments extends BaseActivity {

    Post mPost;
    int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewall_comments);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        userType = userType_sp.getInt("UserType", 0);

        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);

        initCommentSection();
    }

    private void initCommentSection() {

        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_all_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(viewallComments.this));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.row_comment,
                CommentHolder.class,
                FirebaseUtils.getCommentRef(mPost.getPostId())
        ) {
            @Override
            protected void populateViewHolder(final CommentHolder viewHolder, final Comment model, int position) {

                if (model.getUser().getName() != null) {

                    viewHolder.setUsername(model.getUser().getName());

                }

                viewHolder.setComment(model.getComment());
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

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


                if (model.getUser().getImage() != null) {
                    Glide.with(viewallComments.this)
                            .load(model.getUser().getImage())
                            .into(viewHolder.commentOwnerDisplay);
                }

                viewHolder.reply_count_tv.setText(String.valueOf(model.getNumReply()));

                if((model.getNumReply()) > 1){
                    viewHolder.reply_text_tv.setText(R.string.replies);
                } else {
                    viewHolder.reply_text_tv.setText(R.string.reply);
                }


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

    public static class CommentHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;
        RelativeLayout readmore_rel_lay_postactivity;
        TextView reply_count_tv;
        TextView reply_text_tv;

        public CommentHolder(View itemView) {

            super(itemView);

            mView = itemView;


            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.iv_comment_owner_display);
            usernameTextView = (TextView) itemView.findViewById(R.id.tv_username);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            commentTextView = (TextView) itemView.findViewById(R.id.tv_comment);
            readmore_rel_lay_postactivity = (RelativeLayout) mView.findViewById(R.id.readmore_relLayout_postactivity);
            reply_count_tv = (TextView) itemView.findViewById(R.id.reply_count);
            reply_text_tv = (TextView) itemView.findViewById(R.id.reply_text);

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
            startActivity(new Intent(viewallComments.this , RegisterActivity.class));
            finish();

        }
    }*/
}
