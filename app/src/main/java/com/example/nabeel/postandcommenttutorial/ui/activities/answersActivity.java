package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Answer;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.example.nabeel.postandcommenttutorial.utils.sendNotification;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Scanner;

public class answersActivity extends BaseActivity{

    String FCM_token,Current_UserName;
    Post mPost;
    TextView question_tv;
    RelativeLayout readmore;
    int userType;

    String tosendNotifemail;

    private String image_url;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        question_tv = (TextView) findViewById(R.id.questiontext_answeractivity);
        question_tv.setMaxLines(Integer.MAX_VALUE);

        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra(Constants.EXTRA_POST);


        question_tv.setText(mPost.getPostText());

        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
        userType = userType_sp.getInt("UserType", 0);
        init();
    }

    private void init() {

        RecyclerView answerRecyclerView = (RecyclerView) findViewById(R.id.answer_recyclerview);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(answersActivity.this));


        FirebaseRecyclerAdapter<Answer, AnswerHolder> answerAdapter = new FirebaseRecyclerAdapter<Answer, AnswerHolder>(
                Answer.class,
                R.layout.rowanswer2,
                AnswerHolder.class,
                FirebaseUtils.getAnswerRef().child(mPost.getPostId())
        ) {
            @Override
            protected void populateViewHolder(final AnswerHolder viewHolder, final Answer model, int position) {

               final String audiokey = getRef(position).getKey();

                        String email = model.getEmail();

                        if(email.equals(FirebaseUtils.getCurrentUser().getEmail())){

                            viewHolder.edit_answer_iv.setVisibility(View.VISIBLE);

                        } else {

                            viewHolder.edit_answer_iv.setVisibility(View.GONE);

                        }


                FirebaseUtils.getUserRef(model.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        image_url = dataSnapshot.child("image").getValue().toString();
                        name = dataSnapshot.child("name").getValue().toString();

                        if(!TextUtils.isEmpty(name)){
                            viewHolder.setanswerUsername(name);
                        }

                        if (!TextUtils.isEmpty(image_url)) {
                            Glide.with(answersActivity.this)
                                    .load(image_url)
                                    .into(viewHolder.AnswerOwnerDisplay);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

               viewHolder.setAnswer(model.getAnswer());
               viewHolder.setAnswerTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
               viewHolder.setNumLikes(String.valueOf(model.getNumLikes()));

               viewHolder.edit_answer_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        final LinearLayout edittext_layout = (LinearLayout) findViewById(R.id.edittext_layout);
                        final EditText answer_Edittext = (EditText) findViewById(R.id.edittext_answer);
                        TextView done = (TextView) findViewById(R.id.done_textview);

                        edittext_layout.setVisibility(View.VISIBLE);
                        answer_Edittext.setText(model.getAnswer());
                        viewHolder.AnswerTextView.setVisibility(View.GONE);

                        /*FirebaseUtils.getAnswerLikedRef().child(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                                .child(mPost.getPostId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(model.getanswerId())){

                                    viewHolder.LikeAnswer_iv.setText(R.string.Downvote);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/

                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String update = answer_Edittext.getText().toString();


                                FirebaseUtils.getAnswerRef()
                                        .child(mPost.getPostId())
                                        .child(model.getAnswerId())
                                        .child("answer")
                                        .setValue(update);

                                edittext_layout.setVisibility(View.GONE);
                                viewHolder.AnswerTextView.setVisibility(View.VISIBLE);

                            }
                        });

                    }
                });

                if (model.getAnswerImgUrl() != null) {
                    viewHolder.answerDispalyImageview.setVisibility(View.VISIBLE);
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(model.getAnswerImgUrl());
                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.answerDispalyImageview);
                } else {
                    viewHolder.answerDispalyImageview.setImageBitmap(null);
                    viewHolder.answerDispalyImageview.setVisibility(View.GONE);
                }

               if(model.getAudio() != null){

                   viewHolder.playaudio.setVisibility(View.VISIBLE);

               }

               viewHolder.LikeAnswer_iv.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       if(userType != 3){

                           LikeAnswer(model.getAnswerId(), mPost.getPostId(), viewHolder, model);

                       } else if ( userType == 3 ){

                           final AlertDialog.Builder alert = new AlertDialog.Builder(answersActivity.this);
                           alert.setTitle("ALERT");
                           alert.setMessage("You cannot like the answer, you have to login first");

                           alert.show();

                       }

                   }
               });

                /*viewHolder.unLikeAnswer_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LikeAnswer(model.getanswerId(), mPost.getPostId(), viewHolder);

                    }
                });*/


                viewHolder.AnswerTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(viewHolder.AnswerTextView.getLineCount() <= 3){

                            viewHolder.readmore_rel_lay_answers_Activity.setVisibility(View.GONE);

                        } else if(viewHolder.AnswerTextView.getLineCount() >3){

                            viewHolder.readmore_rel_lay_answers_Activity.setVisibility((View.VISIBLE));

                        }
                    }
                });

               viewHolder.readmore_rel_lay_answers_Activity.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       viewHolder.AnswerTextView.setMaxLines(Integer.MAX_VALUE);
                       viewHolder.readmore_rel_lay_answers_Activity.setVisibility(View.GONE);

                   }
               });



               viewHolder.playaudio.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       //viewHolder.playaudio.setImageResource(R.drawable.ic_stop_black_24dp);

                       DatabaseReference draudio = FirebaseDatabase.getInstance().getReference().child("answers").child(mPost.getPostId());

                       draudio.child(audiokey).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {

                               MediaPlayer mediaPlayer;

                               String audiochild = (String) dataSnapshot.child("audio").getValue();


                               try {

                                   mediaPlayer = new MediaPlayer();
                                   mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                   mediaPlayer.setDataSource(audiochild);
                                   mediaPlayer.prepare();
                                   mediaPlayer.start();

//                                   if(mediaPlayer.isPlaying()){
//
//                                       viewHolder.playaudio.setImageResource(R.drawable.ic_stop_black_24dp);
//
//                                   }

                               } catch (IOException e) {
                                   e.printStackTrace();
                               }

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });

                   }
               });


            }
        };

        answerRecyclerView.setAdapter(answerAdapter);

    }

    private void LikeAnswer(final String answerId, final String postID, final AnswerHolder v, final Answer model) {

        v.LikeAnswer_iv.setClickable(false);
        v.LikeAnswer_iv.setEnabled(false);

        FirebaseUtils.getAnswerLikedRef(answerId, postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            //User liked
                            FirebaseUtils.getAnswerRef()
                                    .child(postID)
                                    .child(answerId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num - 1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getAnswerLikedRef(answerId, postID)
                                                    .setValue(null);
                                            v.LikeAnswer_iv.setEnabled(true);
                                            v.LikeAnswer_iv.setClickable(true);
//                                            v.LikeAnswer_iv.setText/(R.string.upvote);

                                        }
                                    });
                        } else {
                            FirebaseUtils.getAnswerRef()
                                    .child(postID)
                                    .child(answerId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num + 1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getAnswerLikedRef(answerId , postID)
                                                    .setValue(true);
                                            v.LikeAnswer_iv.setEnabled(true);
                                            v.LikeAnswer_iv.setClickable(true);
//                                            v.LikeAnswer_iv.setText(R.string.Downvote);

                                            /**
                                             * Send Notification here
                                             */

                                            final String C_Current_user =FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

                                            FirebaseUtils.getUserRef(C_Current_user).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Current_UserName = dataSnapshot.child("name").getValue().toString();

                                                        tosendNotifemail = model.getEmail();

                                                        FirebaseUtils.getUserRef(tosendNotifemail.replace(".",",")).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if(!C_Current_user.equals(tosendNotifemail.replace(".",","))) {

                                                                    FCM_token = dataSnapshot.child("fcmtoken").getValue().toString();
                                                                    Current_UserName += " liked your answer";
                                                                    sendNotification notify = new sendNotification(Current_UserName, postID, FCM_token);
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


                                            /**
                                             * Notification work ends here
                                             */
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.iv_send_answer:
//
//                //sendAnswer();
//                //uploadAudio();
//                //setting_num_comment();
//                break;
//
//            case R.id.activity_post_mic:
//
//                //showRecordbuttons();
//                break;
//        }
//    }

//    public void playCycle(){
//
//        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//
//        if(mediaPlayer.isPlaying()){
//
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//
//                    playCycle();
//
//                }
//            };
//
//            handler.postDelayed(runnable, 1000);
//
//        }
//
//    }

    /*private void showRecordbuttons() {

        //Toast.makeText(getApplicationContext(), "Dds" , Toast.LENGTH_SHORT).show();

        audiobtnLayout = (LinearLayout) findViewById(R.id.activity_post_record_layout);
        seekBar = (SeekBar) findViewById(R.id.A_post_seekbar);
        seekBar.setVisibility(View.VISIBLE);
        audiobtnLayout.setVisibility(View.VISIBLE);

        Play = (Button)findViewById(R.id.A_post_play);
        Stop = (Button)findViewById(R.id.A_post_stop);
        Start = (Button)findViewById(R.id.A_post_record);
        Ok = (Button) findViewById(R.id.A_post_okay);
        cancel = (Button) findViewById(R.id.A_post_cancel);

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

    }*/


    /*private void uploadAudio() {

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

    }*/
/*
    private void sendAnswer() {

        final ProgressDialog progressDialog = new ProgressDialog(answersActivity.this);
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
                        String strAnswer = mAnswerEditTextView.getText().toString();

                        mAnswer.setUser(user);
                        mAnswer.setanswerId(uid);
                        mAnswer.setanswer(strAnswer);
                        mAnswer.setNumLikes(0);
                        mAnswer.setTimeCreated(System.currentTimeMillis());

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

                                        //FirebaseUtils.addToMyRecord(Constants.ANSWERS_KEY, uid);

                                        mAnswerEditTextView.setText("");

//                                        uploadAudio();

//                                        setting_num_comment();

                                        audiotv.setText(null);

                                        final String C_Current_user =FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

                                        String postid = mPost.getPostId();

                                        DatabaseReference mDatabase = FirebaseUtils.getPostRef().child(postid).child("user");

                                        mDatabase.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                email = dataSnapshot.child("email").getValue().toString();

                                                String updated_email = email.replace(".",",");

                                                if(!C_Current_user.equals(updated_email)){

                                                    //Toast.makeText(PostActivity.this , C_Current_user + email , Toast.LENGTH_LONG).show();

                                                    sendNotification();

                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });

    }*/
//
//    private void sendNotification()
//    {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                int SDK_INT = android.os.Build.VERSION.SDK_INT;
//                if (SDK_INT > 8) {
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                            .permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    String send_email;
//
//                    //This is a Simple Logic to Send Notification different Device Programmatically....
//                    /*if (homeFragment.Loggedin_user_email.equals(email)) {
//                        send_email = "nabeelnazir163@gmail.com";
//                    } else {
//                        send_email = "nabeelnazir163@yahoo.com";
//                    }*/
//
//                    send_email = email;
//
//                    String user_name = homeFragment.login_user_name;
//
////                    Toast.makeText(getApplicationContext() , "e" , Toast.LENGTH_LONG).show();
//
//                    Log.d("AppInfo", "Checking");
//
//                    try {
//
//
//                        String jsonResponse;
//
//                        URL url = new URL("https://onesignal.com/api/v1/notifications");
//                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                        con.setUseCaches(false);
//                        con.setDoOutput(true);
//                        con.setDoInput(true);
//
//                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                        con.setRequestProperty("Authorization", "Basic NTgxNWU0OTItMmIxZS00OWM5LWJhYWEtN2NhZGViNTg1OTFk");
//                        con.setRequestMethod("POST");
//
//                        String strJsonBody = "{"
//                                + "\"app_id\": \"989e3989-b3e5-46e5-9cf9-8dae56df8bec\","
//
//                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"
//
//                                + "\"data\": {\"foo\": \"bar\"},"
//                                + "\"contents\": {\"en\":\"" + user_name + " Commented on your post\"}"
//                                + "}";
//
//
//                        System.out.println("strJsonBody:\n" + strJsonBody);
//
//                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
//                        con.setFixedLengthStreamingMode(sendBytes.length);
//
//                        OutputStream outputStream = con.getOutputStream();
//                        outputStream.write(sendBytes);
//
//                        int httpResponse = con.getResponseCode();
//                        System.out.println("httpResponse: " + httpResponse);
//
//                        if (httpResponse >= HttpURLConnection.HTTP_OK
//                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
//                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
//                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                            scanner.close();
//                        } else {
//                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
//                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                            scanner.close();
//                        }
//                        System.out.println("jsonResponse:\n" + jsonResponse);
//
//                    } catch (Throwable t) {
//                        t.printStackTrace();
//                    }
//                }
//            }
//        });
//    }


    public static class AnswerHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView AnswerOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView AnswerTextView;
        ImageView playaudio;
        TextView LikeAnswer_iv;
        TextView LikeAnswer_tv;
//        TextView unLikeAnswer_tv;
        ImageView edit_answer_iv;
        ImageView answerDispalyImageview;
        RelativeLayout readmore_rel_lay_answers_Activity;

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
//            unLikeAnswer_tv = (TextView) mView.findViewById(R.id.iv_unlike_answer);
            edit_answer_iv = (ImageView) mView.findViewById(R.id.edit_answer_iv);
            answerDispalyImageview = (ImageView) mView.findViewById(R.id.iv_answer_ref_display);
            readmore_rel_lay_answers_Activity = (RelativeLayout) mView.findViewById(R.id.readmore_relLayout_answersactivity);

        }

        public void setanswerUsername(String username) {
            usernameTextView.setText(username);
        }

        public void setAnswerTime(CharSequence time) {
            timeTextView.setText(time);
        }

        public void setAnswer(String comment) {
            AnswerTextView.setText(comment);
        }

        public void setNumLikes(String numLikes){ LikeAnswer_tv.setText(numLikes); }


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
            startActivity(new Intent(answersActivity.this , RegisterActivity.class));
            finish();

        }
    }*/
}
