package com.example.nabeel.postandcommenttutorial.ui.dialogs;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.MotionEvent;
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
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by brad on 2017/02/05.
 */

public class PostCreateDialog extends DialogFragment implements View.OnClickListener {
    private static final int RC_PHOTO_PICKER = 1;
    private Post mPost;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;
    private static final String LOG_TAG = "recording" ;

    private ImageView mPostDisplay;
    private ImageView mAudioRecord;

    private CircleImageView m_User_display;

    private View mRootView;

    private TextView mUsername;

    private Button Start, Stop, Play;
    private MediaRecorder myAudioRecorder;
    private String OutPutFile;

    private DatabaseReference mCreateDataRef;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       /* mPost = new Post();
        mProgressDialog = new ProgressDialog(getContext());*/

        mRootView = getActivity().getLayoutInflater().inflate(R.layout.create_post_dialog, null);

       /* mPostDisplay = (ImageView) mRootView.findViewById(R.id.post_dialog_display);
        mAudioRecord = (ImageView) mRootView.findViewById(R.id.post_create_dialog_audio_Start);

        //recordbtn = (Button) mRootView.findViewById(R.id.recordingStart);

        m_User_display = (CircleImageView) mRootView.findViewById(R.id.post_create_user_iv);

        mUsername = (TextView) mRootView.findViewById(R.id.post_create_user_name_tv);

        mCreateDataRef = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_KEY);

        //OUTFILE = Environment.getExternalStorageState()+"/audiorecorder.3gpp";

        Play = (Button)mRootView.findViewById(R.id.play);
        Stop = (Button)mRootView.findViewById(R.id.stop);
        Start = (Button)mRootView.findViewById(R.id.record);

        Stop.setEnabled(false);
        Play.setEnabled(false);

        OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/recording.3gp";

        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        myAudioRecorder.setOutputFile(OutPutFile);

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File outFile = new File(OutPutFile);

                if(outFile.exists()){

                    outFile.delete();

                }

                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                Start.setEnabled(false);
                Stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                Start.setEnabled(true);
                Stop.setEnabled(false);
                Play.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording Finished", Toast.LENGTH_SHORT).show();
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(OutPutFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(getApplicationContext(), "Recording Playing", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        String Current_user_email = FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

        mCreateDataRef.child(Current_user_email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = (String) dataSnapshot.child("name").getValue();
                String photo_url = (String) dataSnapshot.child("image").getValue();

                Glide.with(getContext()).load(photo_url).into(m_User_display);

                mUsername.setText(name + " asks");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       *//* String photourl = mAuth.getCurrentUser().getPhotoUrl().toString();
        String username = mAuth.getCurrentUser().getDisplayName();



        Glide.with(getActivity())
                .load(photourl)
                .into(m_User_display);*//*

       mAudioRecord.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              LinearLayout recordLayout = (LinearLayout) mRootView.findViewById(R.id.recordingStartLayout) ;
               recordLayout.setVisibility(View.VISIBLE);
           }
       });




        *//*recordbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    try {
                        startRecording();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                    StopRecording();

                }

                return false;
            }
        });*//*

        mRootView.findViewById(R.id.post_dialog_send_imageview).setOnClickListener(this);
        mRootView.findViewById(R.id.post_dialog_select_imageview).setOnClickListener(this);*/
        builder.setView(mRootView);
        return builder.create();
    }

    /*private void StopRecording() {

        if(recorder != null) {
            recorder.stop();
            recorder.release();
        }
        
        playbackAudio();

    }

    private void playbackAudio() {
        LinearLayout mSeekbarLayout = (LinearLayout) mRootView.findViewById(R.id.seekbar_layout);
        final ImageView mStopbtn = (ImageView) mRootView.findViewById(R.id.stopAudioBtn);

        mSeekbarLayout.setVisibility(View.VISIBLE);
        mStopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStopbtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });
    }*/
/*
    private void startRecording() throws Exception {

        ditchMediaRecorder();

        File outFile = new File(OUTFILE);

        if(outFile.exists()){

            outFile.delete();

        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(OUTFILE);
        recorder.prepare();
        recorder.start();

    }

    private void ditchMediaRecorder() {

        if(recorder!=null){

            recorder.release();

        }

    }*/


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

                        User user = dataSnapshot.getValue(User.class);

                        final String postId = FirebaseUtils.getUid();

                        TextView postDialogTextView = (TextView) mRootView.findViewById(R.id.post_dialog_edittext);

                        String text = postDialogTextView.getText().toString();

                        mPost.setUser(user);
                        mPost.setNumComments(0);
                        mPost.setNumAnswers(0);
                        mPost.setTimeCreated(System.currentTimeMillis());
                        mPost.setPostId(postId);
                        mPost.setPostText(text);

                        if (mSelectedUri != null) {
                            FirebaseUtils.getImageSRef()
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri)
                                    .addOnSuccessListener(getActivity(),
                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = Constants.POST_IMAGES + "/" + mSelectedUri.getLastPathSegment();
                                                    mPost.setPostImageUrl(url);
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
                    }
                });


    }

    private void addToMyPostList(String postId) {
        FirebaseUtils.getPostRef().child(postId)
                .setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postId).setValue(true)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        dismiss();
                    }
                });

        //FirebaseUtils.addToMyRecord(Constants.POST_KEY, postId);
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