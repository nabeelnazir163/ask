package com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities;


import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.inApp_purchaser;
import com.example.nabeel.postandcommenttutorial.ui.activities.MainActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.ResetPassword;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Share;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity{

    private TextView mSign_up;
    private TextView mForgot_pass;
    private TextView mLogin_tv;
    private TextView mGuestLogin;

    private DatabaseReference mDataBaseUsers;

    private EditText memailfield;
    private EditText mpasswordfield;

    //FOR FACEBOOK INTEGRATION

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String email;

//// ----

    int check_user_type_from_radio_btn;

    SharedPreferences userType_sp;
    SharedPreferences.Editor userType_sh_editor;
    String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get fcm token here
        token = FirebaseInstanceId.getInstance().getToken();
        //the fcm token of the user/alim has to be updated at the time of login

        mSign_up = (TextView) findViewById(R.id.signup_tv);
        mForgot_pass = (TextView) findViewById(R.id.forgotPass_tv);
        mLogin_tv = (TextView) findViewById(R.id.login_tv);
        mGuestLogin = (TextView) findViewById(R.id.guest_login);

        memailfield = (EditText) findViewById(R.id.login_email);
        mpasswordfield = (EditText) findViewById(R.id.login_pass);
        mDataBaseUsers = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_KEY);
        mDataBaseUsers.keepSynced(true);

       /* ActionBar ab = getSupportActionBar();
        ab.hide();*/



//        initializeRadioButton(RegisterActivity.this);

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){

                    *//*Intent intent = new Intent(RegisterActivity.this , MainActivity.class);

                    startActivity(intent);*//*

                }

            }
        };*/

        userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
        userType_sh_editor = userType_sp.edit();

//        mGuestLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent inApppurchaser_intent = new Intent(RegisterActivity.this , inApp_purchaser.class);
//                startActivity(inApppurchaser_intent);
//
//            }
//        });

        mGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInAnonymously();
            }
        });

        mSign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateConfirmationDialog();

            }
        });

        mForgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoResetPassword();
            }
        });


        mLogin_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startLogging();

            }
        });


        /**
         * SIGNING UP WITH FACEBOOK
         */
        loginButton = (LoginButton)findViewById(R.id.facebookLoginBtn);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email","public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        try {
                            email = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(!email.equals(null)){

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(email.replace(".",","))){

                                        SharedPreferences userInfo_SP = getSharedPreferences("userTypeInfo", Context.MODE_PRIVATE);
                                        final SharedPreferences.Editor sh_editor = userInfo_SP.edit();

                                        sh_editor.putInt("usertype", 2);
                                        sh_editor.apply();

                                        Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(login_intent);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else if(!email.equals(null)) {

                            Intent intent = new Intent(getApplicationContext(), signupScreenForAlim.class);
                            intent.putExtra("json", object.toString());
                            startActivity(intent);
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name, last_name, email, gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"PROCESS CANCELLED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"AUTHENTICATION FAILED",Toast.LENGTH_SHORT).show();
            }
        });
        //SIGNING UP WORK TILL HERE
    }

    public void signInAnonymously(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(getApplicationContext(),"SUCCESSFUL GUEST",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    userType_sh_editor.putInt("UserType", 3);
                    userType_sh_editor.apply();
                    finish();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"UNSUCCESSFUL",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //ADDITIONAL FB FUNCTIOANLITY
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void gotoResetPassword(){
        Intent intent = new Intent(RegisterActivity.this, ResetPassword.class);
        startActivity(intent);
    }


    private void initializeRadioButton(View v) {

        boolean checke_usertype = ((RadioButton) v).isChecked();

        switch (v.getId()){

            case R.id.radiobtn_alim:
                if(checke_usertype){

                   /* sh_editor.putInt("usertype", 1);
                    sh_editor.apply();*/
                    userType_sh_editor.putInt("UserType", 1);
                    userType_sh_editor.apply();

                    check_user_type_from_radio_btn = 2;

                    break;
                }

            case R.id.radiobtn_user:
                if(checke_usertype){

                   /* sh_editor.putInt("usertype", 2);
                    sh_editor.apply();*/

                    userType_sh_editor.putInt("UserType", 2);
                    userType_sh_editor.apply();

                    check_user_type_from_radio_btn = 1;

                    break;
                }
        }
    }

    private void CreateConfirmationDialog() {
        AlertDialog.Builder signup_altert_dlg = new AlertDialog.Builder(this);
        signup_altert_dlg.setTitle("SignUp As ?");
        signup_altert_dlg.setMessage("You Want to Signup as ?");
        signup_altert_dlg.setCancelable(true);

        signup_altert_dlg.setPositiveButton("Alim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);

               /* Intent alim_SU_intent = new Intent(RegisterActivity.this, signupScreenForAlim.class);
                startActivity(alim_SU_intent);*/

                SharedPreferences Lang_sp = getSharedPreferences("Language", Context.MODE_PRIVATE);
                final SharedPreferences.Editor lan_sh_editor = Lang_sp.edit();

                alertDialog.setTitle("Language");
                alertDialog.setMessage("Choose Language");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("English", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        lan_sh_editor.putString("Language", "en");
                        lan_sh_editor.apply();

                        Intent intent = new Intent(RegisterActivity.this, signupScreenForAlim.class );
                        startActivity(intent);

                    }
                });

                alertDialog.setNegativeButton("Urdu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        lan_sh_editor.putString("Language", "urdu");
                        lan_sh_editor.apply();

                        Intent intent = new Intent(RegisterActivity.this, signupScreenForAlim.class );
                        startActivity(intent);

                    }
                });

                alertDialog.show();

            }
        });

        signup_altert_dlg.setNegativeButton("User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent user_SU_intent = new Intent(RegisterActivity.this , SignupScreen_For_User.class);
                startActivity(user_SU_intent);

            }
        });

        signup_altert_dlg.show();
    }


    public void selectUserType(View view){

        /*SharedPreferences userInfo_SP = getSharedPreferences("userTypeInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor sh_editor = userInfo_SP.edit();
*/
        boolean checke_usertype = ((RadioButton) view).isChecked();

        switch (view.getId()){

            case R.id.radiobtn_alim:
                if(checke_usertype){

                   /* sh_editor.putInt("usertype", 1);
                    sh_editor.apply();*/
                    userType_sh_editor.putInt("UserType", 1);
                    userType_sh_editor.apply();

                    check_user_type_from_radio_btn = 2;

                    break;
                }

            case R.id.radiobtn_user:
                if(checke_usertype){

                   /* sh_editor.putInt("usertype", 2);
                    sh_editor.apply();*/

                    userType_sh_editor.putInt("UserType", 2);
                    userType_sh_editor.apply();

                    check_user_type_from_radio_btn = 1;

                    break;
                }
            }
    }

    private void startLogging() {

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Logging-In");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        final String email = memailfield.getText().toString();
        String password = mpasswordfield.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        if(check_user_type_from_radio_btn == 1 || check_user_type_from_radio_btn == 2) {

                            checkUserExist();

                            FirebaseUtils.getUserRef(email.replace(".",",")).child("FCM_TOKEN").setValue(token);

                            progressDialog.dismiss();
                        } else {

                            Toast.makeText(RegisterActivity.this, "Check type of user", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }

                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

   /* @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }*/


    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_email = mAuth.getCurrentUser().getEmail().replace(".",",");

            mDataBaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_email)) {

                        SharedPreferences userInfo_SP = getSharedPreferences("userTypeInfo", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor sh_editor = userInfo_SP.edit();

                        String usertype_from_database = dataSnapshot.child(user_email).child("userType").getValue().toString();

                        //Toast.makeText(getApplicationContext() , check_user_type_from_radio_btn + "and" + usertype_from_database , Toast.LENGTH_LONG).show();


                        if(check_user_type_from_radio_btn == 2 &&
                                usertype_from_database.equals("Alim")
                                ){

                           // Toast.makeText(getApplicationContext() , check_user_type_from_radio_btn + "and" + usertype_from_database , Toast.LENGTH_LONG).show();

                            sh_editor.putInt("usertype", 2);
                            sh_editor.apply();

                            Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(login_intent);

                        } else if( check_user_type_from_radio_btn == 2 &&
                                        !usertype_from_database.equals("Alim") ){

                            Toast.makeText(getApplicationContext() ,"You are not signed up as alim" , Toast.LENGTH_LONG).show();
                            mAuth.signOut();


                        } else if ( check_user_type_from_radio_btn == 1 &&
                                        usertype_from_database.equals("User")
                                    ){
                            sh_editor.putInt("usertype", 1);
                            sh_editor.apply();

                            Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(login_intent);

                        } else if( check_user_type_from_radio_btn == 1 &&
                                       !usertype_from_database.equals("User")
                                ){

                            Toast.makeText(getApplicationContext() ,"You are not signed up as User" , Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }


                   /*     SharedPreferences userInfo_SP = getSharedPreferences("userTypeInfo", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor sh_editor = userInfo_SP.edit();

                        String check_user_type = dataSnapshot.child("userType").getValue().toString();

                        if(check_user_type == "Alim"){

                            sh_editor.putInt("usertype", 2);
                            sh_editor.apply();

                            Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(login_intent);

                        } else if(check_user_type == "User"){

                            sh_editor.putInt("usertype", 1);
                            sh_editor.apply();

                            Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(login_intent);

                        }*/

                       /* Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                        login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login_intent);
*/
                    } else {

                        Toast.makeText(getApplicationContext() , "You Need to Signup again with different email", Toast.LENGTH_LONG).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
