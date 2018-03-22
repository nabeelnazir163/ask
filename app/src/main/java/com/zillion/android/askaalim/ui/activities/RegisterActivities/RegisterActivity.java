package com.zillion.android.askaalim.ui.activities.RegisterActivities;


import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.ui.activities.MainActivity;
import com.zillion.android.askaalim.ui.activities.ResetPassword;
import com.zillion.android.askaalim.utils.BaseActivity;
import com.zillion.android.askaalim.utils.Constants;
import com.zillion.android.askaalim.utils.FirebaseUtils;
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

public class RegisterActivity extends BaseActivity{

    private TextView mSign_up;
    private TextView mForgot_pass;
    private Button mLogin_tv;
    private TextView mGuestLogin;

    private DatabaseReference mDataBaseUsers;

    private EditText memailfield;
    private EditText mpasswordfield;

    boolean doubleBackToExitPressedOnce = false;

//    AdView madView;

//// ----

    int check_user_type_from_radio_btn;

    SharedPreferences userType_sp;
    SharedPreferences.Editor userType_sh_editor;
    String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Displaying Ads

//        madView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//        madView.loadAd(adRequest);

        //get fcm token here
        token = FirebaseInstanceId.getInstance().getToken();
        //the fcm token of the user/alim has to be updated at the time of login

        mSign_up = (TextView) findViewById(R.id.signup_tv);
        mForgot_pass = (TextView) findViewById(R.id.forgotPass_tv);
        mLogin_tv = (Button) findViewById(R.id.login_tv);
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

                    *//*Intent inte
                    * nt = new Intent(RegisterActivity.this , MainActivity.class);

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

                if(!TextUtils.isEmpty(memailfield.getText().toString())
                        && !TextUtils.isEmpty(mpasswordfield.getText().toString())){

                        startLogging();

                } else {

                    Toast.makeText(RegisterActivity.this , "Empty Email or Password Field", Toast.LENGTH_SHORT).show();
//
//                    mpasswordfield.setError("Invalid");
//                    mLogin_tv.setError("Check");
                }
            }
        });


        /*
         * SIGNING UP WITH FACEBOOK
         */
        /*loginButton = (LoginButton)findViewById(R.id.facebookLoginBtn);
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

                        if(email != null){

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(email.replace(".",","))){

                                        SharedPreferences userInfo_SP = getSharedPreferences("userTypeInfo", Context.MODE_PRIVATE);
                                        final SharedPreferences.Editor sh_editor = userInfo_SP.edit();
//                                            Toast.makeText(RegisterActivity.this , "Successful", Toast.LENGTH_SHORT).show();
                                        sh_editor.putInt("usertype", 2);
                                        sh_editor.apply();

                                        Intent login_intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(login_intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(),signupScreenForAlim.class);
                                        intent.putExtra("json",object.toString());
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

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
        });*/
        //SIGNING UP WORK TILL HERE
    }

    public void signInAnonymously(){

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Logging-In as Guest");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(getApplicationContext(),"SUCCESSFUL GUEST",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    userType_sh_editor.putInt("UserType", 3);
                    userType_sh_editor.apply();
                    finish();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"UNSUCCESSFUL",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
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

                    /*sh_editor.putInt("usertype", 1);
                    sh_editor.apply();*/
                    userType_sh_editor.putInt("UserType", 1);
                    userType_sh_editor.apply();

                    check_user_type_from_radio_btn = 2;

                    break;
                }

            case R.id.radiobtn_user:
                if(checke_usertype){

                    /*sh_editor.putInt("usertype", 2);
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
        signup_altert_dlg.setTitle("Signup As :");
        signup_altert_dlg.setCancelable(true);

        signup_altert_dlg.setPositiveButton("Aalim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);

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
        progressDialog.setMessage("Logging in");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        final String email = memailfield.getText().toString();
        String password = mpasswordfield.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        if(check_user_type_from_radio_btn == 1 || check_user_type_from_radio_btn == 2) {

                            if(!TextUtils.isEmpty(token)){
                                FirebaseUtils.getUserRef(email.replace(".",",")).child("fcmtoken").setValue(token);
                            }

                            checkUserExist();

                            progressDialog.dismiss();
                        } else {

                            Toast.makeText(RegisterActivity.this, "Check type of user", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }

                        progressDialog.dismiss();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(RegisterActivity.this, e.getMessage() , Toast.LENGTH_LONG).show();
//                    mAuth.signOut();
                    progressDialog.dismiss();

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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit application", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }
}
