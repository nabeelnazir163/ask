package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Answer;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.example.nabeel.postandcommenttutorial.utils.sendNotification;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Scanner;

public class postNewAnswer extends AppCompatActivity implements View.OnClickListener {

    String Current_UserName,FCM_token;

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

    private StorageReference mStorage;

    Runnable runnable;

    ImageView cross_imageview;
    ImageView ref_answer_iv;
    ImageView select_image_from_gall_iv;
    ImageView post_answer_record_audio_mic;

    TextView submit_answer_tv;

    private EditText mAnswerEditText;

    String email;
    String name;

    private static final int RC_PHOTO_PICKER = 1;
    private Uri mSelectedUri;

    ProgressDialog progressDialog;

    String image_current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_answer);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.hide();

        }

        initialize();
    }

    private void initialize() {

        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);

        cross_imageview = (ImageView) findViewById(R.id.post_Answer_cross_iv);
        ref_answer_iv = (ImageView) findViewById(R.id.postanswer_reference_image);
        select_image_from_gall_iv = (ImageView) findViewById(R.id.post_Answer_select_imageview);
        post_answer_record_audio_mic = (ImageView) findViewById(R.id.postanswer_mic_iv);

        submit_answer_tv = (TextView) findViewById(R.id.post_answer_submit_tv);
        audiotv = (TextView) findViewById(R.id.audiotxt_answer);

        mAnswerEditText = (EditText) findViewById(R.id.post_ansswer_question_tv);

        mStorage = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.post_answer_submit_tv).setOnClickListener(this);
        findViewById(R.id.post_Answer_cross_iv).setOnClickListener(this);
        findViewById(R.id.postanswer_mic_iv).setOnClickListener(this);
        findViewById(R.id.post_Answer_select_imageview).setOnClickListener(this);

        progressDialog = new ProgressDialog(postNewAnswer.this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.post_Answer_cross_iv:
                finish();
                break;

            case R.id.post_answer_submit_tv:
                sendAnswer();
                uploadAudio();
                break;

            case R.id.postanswer_mic_iv:
                showRecordbuttons();
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
                ref_answer_iv.setImageURI(mSelectedUri);
            }
        }
    }

    private void uploadAudio() {

        if(OutPutFile != null){

            StorageReference filepath = mStorage.child("Audio").child("new_audio.3gp");

            Uri uri = Uri.fromFile(new File(OutPutFile));
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloaduri = taskSnapshot.getDownloadUrl().toString();

                    FirebaseUtils.getAnswerRef().child(mPost.getPostId()).child(mAnswer.getanswerId()).child("audio").setValue(downloaduri);

                }
            });

        }

    }

    private void showRecordbuttons() {

        //Toast.makeText(getApplicationContext(), "Dds" , Toast.LENGTH_SHORT).show();

        audiobtnLayout = (LinearLayout) findViewById(R.id.postanswer_record_layout);
        seekBar = (SeekBar) findViewById(R.id.post_answer_seekbar);
        seekBar.setVisibility(View.VISIBLE);
        audiobtnLayout.setVisibility(View.VISIBLE);

        Play = (Button)findViewById(R.id.post_answer_play);
        Stop = (Button)findViewById(R.id.post_answer_stop);
        Start = (Button)findViewById(R.id.post_answer_record);
        Ok = (Button) findViewById(R.id.post_answer_okay);
        cancel = (Button) findViewById(R.id.post_answer_cancel);

        Stop.setEnabled(false);
        Play.setEnabled(false);

        OutPutFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/recording.3gp";

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                Start.setEnabled(false);
                Stop.setEnabled(true);
                Ok.setEnabled(false);
                cancel.setEnabled(false);

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
                Ok.setEnabled(false);
                cancel.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Recording Finished", Toast.LENGTH_SHORT).show();
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Ok.setEnabled(true);
                cancel.setEnabled(true);

                handler = new Handler();

                //MediaPlayer mediaPlayer = MediaPlayer.create()

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

                mediaPlayer.stop();
                audiobtnLayout.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);

                File outFile = new File(OutPutFile);

                if(outFile.exists()){

                    audiotv.setText("Audio File included");
                    audiotv.setVisibility(View.VISIBLE);
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File outFile = new File(OutPutFile);

                if(outFile.exists()){

                    outFile.delete();

                    //myAudioRecorder.stop();

                }

                audiotv.setText(null);
                audiotv.setVisibility(View.GONE);
                audiobtnLayout.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);

            }
        });

    }

    private void sendAnswer() {

        progressDialog.setMessage("Sending Answer..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mAnswer = new Answer();

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        User user = dataSnapshot.getValue(User.class);
                        final String uid = FirebaseUtils.getUid();
                        String strAnswer = mAnswerEditText.getText().toString();

                        mAnswer.setUser(user);
                        mAnswer.setanswerId(uid);
                        mAnswer.setanswer(strAnswer);
                        mAnswer.setNumLikes(0);
                        mAnswer.setTimeCreated(System.currentTimeMillis());

                        if (mSelectedUri != null) {
                            FirebaseUtils.getAnswerImageSRef()
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = Constants.ANSWER_IMAGES + "/" + mSelectedUri.getLastPathSegment();
                                                    Toast.makeText(getApplicationContext() , url , Toast.LENGTH_SHORT).show();
                                                    mAnswer.setAnswerImgUrl(url);
                                                    finish();
                                                    addToMyPostList(uid);
                                                }
                                            });
                        } else {

                            addToMyPostList(uid);

                        }

                        Boolean checktoclose = getSharedPreferences("firstAnsPref",  MODE_PRIVATE).getBoolean("isFirstAnswer", true);

                        if(!checktoclose){

                            finish();

                        }


                        Boolean isFirtanswer = getSharedPreferences("firstAnsPref",  MODE_PRIVATE).getBoolean("isFirstAnswer", true);

                        if(isFirtanswer){

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
                                    if(dont_show.isChecked()){

                                        getSharedPreferences("firstAnsPref",  MODE_PRIVATE).edit().putBoolean("isFirstAnswer", false).commit();

                                    }
                                    finish();
                                    dialog_first_answer.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error while answer", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToMyPostList(String uid) {

        FirebaseUtils.getAnswerRef()
                .child(mPost.getPostId())
                .child(uid)
                .setValue(mAnswer);

        FirebaseUtils.getPostRef().child(mPost.getPostId())
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

                        progressDialog.dismiss();


                        mAnswerEditText.setText("");

                        audiotv.setText(null);

                        final String C_Current_user =FirebaseUtils.getCurrentUser().getEmail().replace(".",",");
                        FirebaseUtils.getUserRef(C_Current_user).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                image_current_user = dataSnapshot.child("image").getValue().toString();
                                name = dataSnapshot.child("name").getValue().toString();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        String postid = mPost.getPostId();

                        DatabaseReference mDatabase = FirebaseUtils.getPostRef().child(postid).child("user");

                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                email = dataSnapshot.child("email").getValue().toString();

                                String updated_email = email.replace(".",",");

                                if(!C_Current_user.equals(updated_email)){

//                                    sendNotification();

                                    /**
                                     * Send notification on answer post
                                     */


                                    final String C_Current_user =FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

                                    FirebaseUtils.getUserRef(C_Current_user).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Current_UserName = dataSnapshot.child("name").getValue().toString();

                                            FirebaseUtils.getPostRef().child(mPost.getPostId())
                                                    .child("user").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    String email = dataSnapshot.child("email").getValue().toString();

                                                    FirebaseUtils.getUserRef(email.replace(".",",")).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            FCM_token = dataSnapshot.child("fcmtoken").getValue().toString();
                                                            Current_UserName +=" answered your question";
                                                            sendNotification notify = new sendNotification(Current_UserName,mPost.getPostId(),FCM_token);

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    //notify.send(Current_UserName,postId,FCM_token);
                                                    // asy n
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

                                    /**
                                     *  NOTIFICATION WORK ENDS HERE
                                     */


//                                    /*FirebaseUtils.getNotificationRef().child(updated_email)
//                                            .child("username").setValue(C_Current_user);
//
//                                    FirebaseUtils.getNotificationRef().child(updated_email)
//                                            .child("message").setValue( " Send you a Message");
//
//                                    Toast.makeText(getApplicationContext(), C_Current_user , Toast.LENGTH_SHORT).show();*/
//
//                                    Map<String, String> map = new HashMap<String, String>();
//                                    map.put("notification", "answer on your post");
//                                    map.put("name", name);
//                                    map.put("imageurl" , image_current_user);
//                                    FirebaseUtils.getNotificationRef().child(updated_email).push().setValue(map);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



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

    private void sendNotification()
    {
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
                    /*if (homeFragment.Loggedin_user_email.equals(email)) {
                        send_email = "nabeelnazir163@gmail.com";
                    } else {
                        send_email = "nabeelnazir163@yahoo.com";
                    }*/

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
    }
}
