package com.zillion.android.askaalim.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.Answer;
import com.zillion.android.askaalim.models.Post;
import com.zillion.android.askaalim.models.User;
import com.zillion.android.askaalim.ui.fragments.homeFragment;
import com.zillion.android.askaalim.utils.Constants;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.zillion.android.askaalim.utils.sendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class postNewAnswer extends AppCompatActivity implements View.OnClickListener {

    String Current_UserName,FCM_token;
    String Current_userImage;

    Answer mAnswer;
    Post mPost;

    LinearLayout audiobtnLayout;

    SeekBar seekBar;

    private Button Start, Stop, Play, Ok, cancel;

    private String OutPutFile;

    private MediaRecorder myAudioRecorder;

    Handler handler;

    MediaPlayer mediaPlayer;

    TextView audiotv;
    TextView question;

    private StorageReference mStorage;

    Runnable runnable;

//    ImageView cross_imageview;
    ImageView ref_answer_iv;
    ImageView select_image_from_gall_iv;
    ImageView post_answer_record_audio_mic;

//    TextView submit_answer_tv;

    private EditText mAnswerEditText;

    public static final int RECORD_AUDIO = 0;
    public static final int WRITE_EXTERNAL_STORAGE = 10;

    String email;
    String name;

    Dialog dialog;

    private static final int RC_PHOTO_PICKER = 1;
    private Uri mSelectedUri;

    ProgressDialog progressDialog;

    String image_current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_answer);

        ActionBar actionBar = getSupportActionBar();

        handler = new Handler();

        //MediaPlayer mediaPlayer = MediaPlayer.create()

        mediaPlayer = new MediaPlayer();

        if(actionBar != null){

            actionBar.hide();

        }

        if (ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                 ) {
//            if ( ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(postNewAnswer.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        RECORD_AUDIO);

//            } else if ( ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
//
//                ActivityCompat.requestPermissions(postNewAnswer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        WRITE_EXTERNAL_STORAGE );
//            }

        }

        initialize();
    }

    private void initialize() {

        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);

        ref_answer_iv = (ImageView) findViewById(R.id.postanswer_reference_image);
        select_image_from_gall_iv = (ImageView) findViewById(R.id.post_Answer_select_imageview);
        post_answer_record_audio_mic = (ImageView) findViewById(R.id.postanswer_mic_iv);

        audiotv = (TextView) findViewById(R.id.audiotxt_answer);
        question = (TextView) findViewById(R.id.questiontext_new_answer);
        question.setText(mPost.getPostText());

        mAnswerEditText = (EditText) findViewById(R.id.post_ansswer_question_tv);

        mStorage = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.post_dialog_send_imageview).setOnClickListener(this);
        findViewById(R.id.post_question_cross_iv).setOnClickListener(this);
        findViewById(R.id.postanswer_mic_iv).setOnClickListener(this);
        findViewById(R.id.post_Answer_select_imageview).setOnClickListener(this);

        progressDialog = new ProgressDialog(postNewAnswer.this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.post_question_cross_iv:
                finish();
                break;

            case R.id.post_dialog_send_imageview:

                sendAnswer();

                break;

            case R.id.postanswer_mic_iv:
//                showRecordbuttons();

                if (ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ) {

                                ActivityCompat.requestPermissions(postNewAnswer.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        RECORD_AUDIO);

                }
                else if ( ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(postNewAnswer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        ) {
                    dialog = new Dialog(postNewAnswer.this, android.R.style.Widget_PopupWindow);
                    dialog.setTitle("Record Audio");
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_for_audio);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialog.setCancelable(true);
                    dialog.getWindow().setAttributes(lp);

                    showRecordbuttons();

                    dialog.show();
                } else {

                    ActivityCompat.requestPermissions(postNewAnswer.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RECORD_AUDIO);

                }
                break;

            case R.id.post_Answer_select_imageview:
                selectImage();
                break;
        }

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
                ref_answer_iv.setVisibility(View.VISIBLE);
                ref_answer_iv.setImageURI(mSelectedUri);
            }
        }
    }

    private void uploadAudio() {

        String SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        if(OutPutFile != null){

            StorageReference filepath = mStorage.child("Audio").child("audio" + saltStr +".3gp");

            Uri uri = Uri.fromFile(new File(OutPutFile));
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloaduri = taskSnapshot.getDownloadUrl().toString();

                    FirebaseUtils.getAnswerRef().child(mPost.getPostId()).child(mAnswer.getAnswerId()).child("audio").setValue(downloaduri);

                }
            });
        }

    }

    private void showRecordbuttons() {

        audiobtnLayout = (LinearLayout) dialog.findViewById(R.id.postanswer_record_layout);
        seekBar = (SeekBar) dialog.findViewById(R.id.post_answer_seekbar);
        seekBar.setVisibility(View.VISIBLE);
        audiobtnLayout.setVisibility(View.VISIBLE);

        Play = (Button) dialog.findViewById(R.id.post_answer_play);
        Stop = (Button) dialog.findViewById(R.id.post_answer_stop);
        Start = (Button) dialog.findViewById(R.id.post_answer_record);
        Ok = (Button) dialog.findViewById(R.id.post_answer_okay);
        cancel = (Button) dialog.findViewById(R.id.post_answer_cancel);

        Stop.setVisibility(View.GONE);
        Play.setVisibility(View.GONE);
        Ok.setVisibility(View.GONE);

        OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + System.currentTimeMillis() + ".3gp";

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                seekBar.setProgress(0);

                myAudioRecorder = new MediaRecorder();

                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                myAudioRecorder.setOutputFile(OutPutFile);

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

                Start.setVisibility(View.GONE);
                Stop.setVisibility(View.VISIBLE);
                Ok.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;

                Start.setVisibility(View.GONE);
                Stop.setVisibility(View.GONE);
                Play.setVisibility(View.VISIBLE);
                Ok.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                Ok.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Recording Finished", Toast.LENGTH_SHORT).show();
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancel.setVisibility(View.VISIBLE);

                handler = new Handler();

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(OutPutFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    seekBar.setMax(mediaPlayer.getDuration());

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {

                            playCycle();
                            mediaPlayer.start();

                        }
                    });


                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {

                            if(input){

                                mediaPlayer.seekTo(progress);

                            }

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    Toast.makeText(getApplicationContext(), "Recording Playing", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mediaPlayer.isPlaying()){

                    mediaPlayer.stop();

                }

                audiobtnLayout.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);

                File outFile = new File(OutPutFile);

                if(outFile.exists()){

                    audiotv.setText("Audio File included");
                    audiotv.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File outFile = new File(OutPutFile);

                if(outFile.exists()){

                    outFile.delete();

                }

                audiotv.setText(null);
                audiotv.setVisibility(View.GONE);
                audiobtnLayout.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

    }

    private void sendAnswer() {

        progressDialog.setMessage("Sending Answer..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        mAnswer = new Answer();

        final String strAnswer = mAnswerEditText.getText().toString();

        if(strAnswer.length() >= 10) {

//                        mAnswer.setUser(user);

            progressDialog.show();

            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


//                        User user = dataSnapshot.getValue(User.class);
                            final String uid = FirebaseUtils.getUid();


                            mAnswer.setEmail(FirebaseUtils.getCurrentUser().getEmail());
                            mAnswer.setAnswerId(uid);
                            mAnswer.setAnswer(strAnswer);
                            mAnswer.setNumLikes(0);
                            mAnswer.setTimeCreated(System.currentTimeMillis());

                           /* Boolean isFirtanswer = getSharedPreferences("firstAnsPref", MODE_PRIVATE).getBoolean("isFirstAnswer", true);

                            if (isFirtanswer) {

                                final Dialog dialog_first_answer = new Dialog(postNewAnswer.this, android.R.style.Widget_PopupWindow);
                                dialog_first_answer.setContentView(R.layout.dialogfirstanswerinstructions);

                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                                lp.copyFrom(dialog_first_answer.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                dialog_first_answer.setCancelable(true);
                                dialog_first_answer.show();
                                dialog_first_answer.getWindow().setAttributes(lp);

                                final CheckBox dont_show = (CheckBox) dialog_first_answer.findViewById(R.id.dont_show_checkbox_answer);
                                Button close = (Button) dialog_first_answer.findViewById(R.id.ins_close_b_answer);

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (dont_show.isChecked()) {

                                            getSharedPreferences("firstAnsPref", MODE_PRIVATE).edit().putBoolean("isFirstAnswer", false).commit();

                                        }
                                        dialog_first_answer.dismiss();
                                    }
                                });*/
//                            }

                            if (mSelectedUri != null) {
                                FirebaseUtils.getAnswerImageSRef()
                                        .child(mSelectedUri.getLastPathSegment())
                                        .putFile(mSelectedUri)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        String url = Constants.ANSWER_IMAGES + "/" + mSelectedUri.getLastPathSegment();
//                                                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                                                        mAnswer.setAnswerImgUrl(url);
                                                        addToMyPostList(uid);
                                                    }
                                                });
                            } else {

                                addToMyPostList(uid);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Error while answer", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(postNewAnswer.this, "Your answer must have atleast 10 characters", Toast.LENGTH_LONG).show();
        }
    }

    private void addToMyPostList(String uid) {

        FirebaseUtils.getAnswerRef()
                .child(mPost.getPostId())
                .child(uid)
                .setValue(mAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadAudio();
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(postNewAnswer.this, "Unable to post answer", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUtils.getMyAnsRef().child(mPost.getPostId()).setValue(true)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

        FirebaseUtils.getPostRef()
                .child(mPost.getPostId())
                .child(Constants.NUM_ANSWWERS_KEY)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        mutableData.setValue(num + 1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        mAnswerEditText.setText("");
                        ref_answer_iv.setVisibility(View.GONE);
                        mSelectedUri = null;

                        audiotv.setText(null);

                        final String C_Current_user =FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

                            String updated_email = mPost.getEmail().replace(".",",");

                            if(!C_Current_user.equals(updated_email)){


                                /*
                                 * Send notification on answer post
                                 */

                                FirebaseUtils.getUserRef(C_Current_user).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Current_UserName = (String) dataSnapshot.child("name").getValue();
                                        Current_userImage = (String) dataSnapshot.child("image").getValue();

                                        FirebaseUtils.getPostRef().child(mPost.getPostId()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                final String email = (String) dataSnapshot.child("email").getValue();

                                                FirebaseUtils.getUserRef(email.replace(".",",")).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        String pushId = FirebaseUtils.getUid();

                                                        FirebaseUtils.getNotificationRef().child(email.replace(".",",")).child(pushId).child("name").setValue(Current_UserName);
                                                        FirebaseUtils.getNotificationRef().child(email.replace(".",",")).child(pushId).child("notification").setValue("answered your post");
                                                        FirebaseUtils.getNotificationRef().child(email.replace(".",",")).child(pushId).child("post").setValue(mPost);
                                                        FirebaseUtils.getNotificationRef().child(email.replace(".",",")).child(pushId).child("time").setValue(System.currentTimeMillis());
                                                        FirebaseUtils.getNotificationRef().child(email.replace(".",",")).child(pushId).child("image").setValue(Current_userImage);


                                                        if(dataSnapshot.hasChild("fcmtoken")) {

                                                            FCM_token = dataSnapshot.child("fcmtoken").getValue().toString();
                                                            sendNotification notify = new sendNotification(Current_UserName + " answered your question", mPost.getPostId(), FCM_token);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                /*
                                 *  NOTIFICATION WORK ENDS HERE
                                 */

                            }

                    }
                });

    }

    public void playCycle(){

        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()){

            runnable = new Runnable() {
                @Override
                public void run() {

                    playCycle();

                }
            };

            handler.postDelayed(runnable, 1000);

        }

    }

    /*private void sendNotification() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    *//*if (homeFragment.Loggedin_user_email.equals(email)) {
                        send_email = "nabeelnazir163@gmail.com";
                    } else {
                        send_email = "nabeelnazir163@yahoo.com";
                    }*//*

                    send_email = email;

                    String user_name = homeFragment.login_user_name;

//                    Toast.makeText(getApplicationContext() , "e" , Toast.LENGTH_LONG).show();

                    Log.d("AppInfo", "Checking");

                    try {


                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NTgxNWU0OTItMmIxZS00OWM5LWJhYWEtN2NhZGViNTg1OTFk");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"989e3989-b3e5-46e5-9cf9-8dae56df8bec\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\":\"" + user_name + " Commented on your post\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }*/
}
