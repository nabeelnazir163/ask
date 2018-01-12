package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CreateNewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_PHOTO_PICKER = 1;
    private Post mPost;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;

    private ImageView mPostDisplay;
    private ImageView mCross;

    private CircleImageView m_User_display;

    private TextView mUsername;

    private DatabaseReference mCreateDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.hide();
        }

        mPost = new Post();
        mProgressDialog = new ProgressDialog(CreateNewPostActivity.this);

        ///mRootView = getApplicationContext().getLayoutInflater().inflate(R.layout.create_post_dialog, null);

        mPostDisplay = (ImageView) findViewById(R.id.post_dialog_display);

        mCross = (ImageView) findViewById(R.id.post_question_cross_iv);

        m_User_display = (CircleImageView) findViewById(R.id.post_create_user_iv);

        mUsername = (TextView) findViewById(R.id.post_create_user_name_tv);

        mCreateDataRef = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_KEY);

        String Current_user_email = FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

        mCreateDataRef.child(Current_user_email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = (String) dataSnapshot.child("name").getValue();
                String photo_url = (String) dataSnapshot.child("image").getValue();

                Glide.with(getApplicationContext()).load(photo_url).into(m_User_display);

                mUsername.setText(name + " asks");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       /* String photourl = mAuth.getCurrentUser().getPhotoUrl().toString();
        String username = mAuth.getCurrentUser().getDisplayName();



        Glide.with(getActivity())
                .load(photourl)
                .into(m_User_display);*/

        /*mAudioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout recordLayout = (LinearLayout) findViewById(R.id.recordingStartLayout) ;
                recordLayout.setVisibility(View.VISIBLE);
            }
        });
*/



        findViewById(R.id.post_dialog_send_imageview).setOnClickListener(this);
        findViewById(R.id.post_dialog_select_imageview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_dialog_send_imageview:

                sendPost();
                break;
            case R.id.post_dialog_select_imageview:
                selectImage();
                break;
        }
    }

    private void sendPost() {
        mProgressDialog.setMessage("Sending post...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        User user = dataSnapshot.getValue(User.class);

                        final String postId = FirebaseUtils.getUid();

                        TextView postDialogTextView = (TextView) findViewById(R.id.post_dialog_edittext);

                        String text = postDialogTextView.getText().toString();

                        mPost.setEmail(FirebaseUtils.getCurrentUser().getEmail());
                        mPost.setNumComments(0);
                        mPost.setNumAnswers(0);
                        mPost.setNumSeen(0);
                        mPost.setTimeCreated(System.currentTimeMillis());
                        mPost.setPostId(postId);
                        mPost.setPostText(text);

                        if (mSelectedUri != null) {
                            FirebaseUtils.getImageSRef()
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = Constants.POST_IMAGES + "/" + mSelectedUri.getLastPathSegment();
                                                    mPost.setPostImageUrl(url);
                                                    finish();
                                                    addToMyPostList(postId);
                                                }
                                            });
                        } else {
                            addToMyPostList(postId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                        finish();
                    }
                });


    }

    private void addToMyPostList(String postId) {
        FirebaseUtils.getPostRef().child(postId)
                .setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postId).setValue(true)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        finish();
                    }
                });

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                mSelectedUri = data.getData();
                mPostDisplay.setImageURI(mSelectedUri);
            }
        }
    }
}
