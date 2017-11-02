
package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Comment;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.models.reply;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class comment_reply_activity extends AppCompatActivity {

    private RecyclerView mcomment_reply_rv;
    Comment mComment;
    private reply mReply;
    private ImageView mSendReply;
    private EditText mReplyEditText;
    private Post mPost;
    private ImageView ComUserDisplayImageView;
    private TextView Com_user_Display_name;
    private TextView Comment;
    private TextView commentTime;
    private RelativeLayout readmore_for_commentin_Reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply_activity);


        Intent intent = getIntent();
        mComment = (Comment) intent.getSerializableExtra(Constants.EXTRA_REPLY);

        Intent in = getIntent();

        mPost = (Post) in.getSerializableExtra(Constants.EXTRA_POST);
        Comment = (TextView) findViewById(R.id.tv_reply);


        mSendReply = (ImageView) findViewById(R.id.iv_send_reply);

        mReplyEditText = (EditText) findViewById(R.id.edittext_reply);

        Comment.setMaxLines(Integer.MAX_VALUE);

        mSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendReply();

            }
        });

        initReply();
        initReplyAdapter();

    }

    private void sendReply() {
        final ProgressDialog progressDialog = new ProgressDialog(comment_reply_activity.this);
        progressDialog.setMessage("Sending comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mReply = new reply();

        final String Strreply = mReplyEditText.getText().toString();

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        final String uid = FirebaseUtils.getUid();

                        mReply.setUser(user);
                        mReply.setReplyId(uid);
                        mReply.setReply_text(Strreply);
                        mReply.setTimeCreated(System.currentTimeMillis());

                        FirebaseUtils.getC_ReplyRef(mPost.getPostId())
                                .child(mComment.getCommentId())
                                .child(uid)
                                .setValue(mReply);


                       /* FirebaseUtils.getCommentRef(mPost.getPostId())
                                .child(mComment.getCommentId())
                                .child(Constants.Num_REPLY)
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

                                    }
                                });
*/

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mReplyEditText.setText("");

        progressDialog.dismiss();
    }

    private void initReply() {

        ComUserDisplayImageView = (ImageView) findViewById(R.id.iv_reply_owner_display);
        Com_user_Display_name = (TextView) findViewById(R.id.tv_username_reply);

        commentTime = (TextView) findViewById(R.id.tv_time_reply);

        Com_user_Display_name.setText(mComment.getUser().getName());
        Comment.setText(mComment.getComment());
        commentTime.setText(DateUtils.getRelativeTimeSpanString(mComment.getTimeCreated()));

        Glide.with(comment_reply_activity.this)
                .load(mComment.getUser().getImage())
                .into(ComUserDisplayImageView);

    }

    private void initReplyAdapter() {

        mcomment_reply_rv = (RecyclerView) findViewById(R.id.comment_reply_rv);
        mcomment_reply_rv.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<reply , reply_holder> reply_adapter = new FirebaseRecyclerAdapter<reply, reply_holder>(
                reply.class,
                R.layout.row_reply,
                reply_holder.class,
                FirebaseUtils.getC_ReplyRef(mPost.getPostId()).child(mComment.getCommentId())
        ) {
            @Override
            protected void populateViewHolder(final reply_holder viewHolder, reply model, int position) {

                viewHolder.setComUsername(model.getUser().getName());
                viewHolder.setReply(model.getReply_text());
                viewHolder.setReplyTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                Glide.with(comment_reply_activity.this).load(model.getUser().getImage()).into(viewHolder.mCommentIv);

                viewHolder.reply_tv.post(new Runnable() {
                    @Override
                    public void run() {

                        if(viewHolder.reply_tv.getLineCount() <= 3){

                            viewHolder.readmore_rel_lay_replyactivity.setVisibility(View.GONE);

                        } else if(viewHolder.reply_tv.getLineCount() >3){

                            viewHolder.readmore_rel_lay_replyactivity.setVisibility((View.VISIBLE));

                        }

                    }
                });

                viewHolder.readmore_rel_lay_replyactivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.reply_tv.setMaxLines(Integer.MAX_VALUE);
                        viewHolder.readmore_rel_lay_replyactivity.setVisibility(View.GONE);

                    }
                });

            }
        };

        mcomment_reply_rv.setAdapter(reply_adapter);

    }

    public static class reply_holder extends RecyclerView.ViewHolder{

        View mView;
        ImageView mCommentIv;
        TextView reply_tv;
        TextView username_tv;
        TextView time_reply;
        RelativeLayout readmore_rel_lay_replyactivity;

        public reply_holder(View itemView) {
            super(itemView);

            mView = itemView;
            reply_tv = (TextView) mView.findViewById(R.id.tv_reply);
            username_tv = (TextView) mView.findViewById(R.id.tv_username_reply);
            time_reply = (TextView) mView.findViewById(R.id.tv_time_reply);
            mCommentIv = (ImageView) mView.findViewById(R.id.iv_reply_owner_display);
            readmore_rel_lay_replyactivity = (RelativeLayout) mView.findViewById(R.id.readmore_relLayout_reply_activity);
        }




        public void setReply(String reply){

            reply_tv.setText(reply);

        }

        public void setComUsername(String username){

            username_tv.setText(username);

        }

        public void setReplyTime (CharSequence time){

            time_reply.setText(time);

        }

    }
}
