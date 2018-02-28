package com.zillion.nabeel.postandcommenttutorial.ui.activities.RegisterActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.ui.activities.MainActivity;
import com.zillion.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class SignupScreen_For_User extends BaseActivity {

    private EditText mSignupNameUser;
    private EditText mSignupEmailUser;
    private EditText mSignuppassUser;
    private EditText mSignupConf_passUser;
    private EditText mSignupPhoneUser;
    private EditText mSignupStateUser;
    private EditText mSignupCityUser;
    private EditText mSignupAdressUser;
    private EditText mSignupUsernameUser;
    private EditText mConfirmEmailField;

    private Button mSignupSubBtnUser;

    String mNameFieldUser;
    String mEmailFieldUser;
    String mPassFieldUser;
    String phonenumberUser;
    String select_gender_user;
    String mCon_pass_user;
    String mUsername_User;
    String CEmail;

    private Spinner mFiqah_spinner_user;
    private Spinner mCountry_user_spinner;

    private ImageView mProfile_user_iv;

    private final static int GALLERY_REQ = 1;

    Uri mImageUri_user = null;

    private StorageReference mSignup_Stor_ref_user;

    SharedPreferences userType_sp;
    SharedPreferences.Editor userType_sh_editor;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen__for__user);

        /*
         * getting the fcm token here
         */
        token = FirebaseInstanceId.getInstance().getToken();

        mSignupNameUser = (EditText) findViewById(R.id.Signup_user_namefield);
        mSignupEmailUser = (EditText) findViewById(R.id.Signup_user_emailfield);
        mConfirmEmailField = (EditText) findViewById(R.id.Signup_user_emailfield_confirm);
        mSignuppassUser = (EditText) findViewById(R.id.Signup_user_passwordfield);
        mSignupConf_passUser = (EditText) findViewById(R.id.Signup_user_Confirmpasswordfield);
        mSignupPhoneUser = (EditText) findViewById(R.id.Signup_user_PhoneField);
        mSignupStateUser = (EditText) findViewById(R.id.Signup_state_tv_user);
        mSignupCityUser = (EditText) findViewById(R.id.Signup_city_tv_user);
        mSignupAdressUser = (EditText) findViewById(R.id.Signup_address_tv_user);
        mSignupUsernameUser = (EditText) findViewById(R.id.Signup_username_namefield);

        mSignupSubBtnUser = (Button) findViewById(R.id.finish_Signup_user);

        mFiqah_spinner_user = (Spinner) findViewById(R.id.Signup_fiqah_spinner_user);
        mCountry_user_spinner = (Spinner) findViewById(R.id.signup_country_spinner_user);

        mProfile_user_iv = (ImageView) findViewById(R.id.signup_user_profile_iv) ;

        mSignup_Stor_ref_user = FirebaseStorage.getInstance().getReference().child("profile_images/");

        Locale[] locales_user = Locale.getAvailableLocales();
        ArrayList<String> countries_user = new ArrayList<String>();

        ArrayAdapter<String> fiqah_adapter_user = new ArrayAdapter<String>(SignupScreen_For_User.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.fiqah_spinner_name));

        fiqah_adapter_user.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Locale locale : locales_user) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries_user.contains(country)) {
                countries_user.add(country);
            }
        }

        Collections.sort(countries_user);
        for (String country : countries_user) {
            System.out.println(country);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries_user);


        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set the ArrayAdapter to the spinner
        mCountry_user_spinner.setAdapter(dataAdapter);
//        mCountry_user_spinner.setSelection(166);

        System.out.println("# countries found: " + countries_user.size());

        mFiqah_spinner_user.setAdapter(fiqah_adapter_user);
        //mFiqah_spinner_user.setSelection(1);

        mProfile_user_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent, GALLERY_REQ);

            }
        });

        mSignupSubBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mNameFieldUser = mSignupNameUser.getText().toString();
                mEmailFieldUser = mSignupEmailUser.getText().toString();
                mPassFieldUser = mSignuppassUser.getText().toString();
                mCon_pass_user = mSignupConf_passUser.getText().toString();
                phonenumberUser = mSignupPhoneUser.getText().toString();
                mUsername_User = mSignupUsernameUser.getText().toString();
                CEmail = mConfirmEmailField.getText().toString();


                if(!TextUtils.isEmpty(mNameFieldUser) &&
//                        !TextUtils.isEmpty(mEmailFieldUser)&&
                            !TextUtils.isEmpty(mPassFieldUser) &&
                                !TextUtils.isEmpty(mCon_pass_user) &&
                                    !TextUtils.isEmpty(phonenumberUser) &&
                                        !TextUtils.isEmpty(select_gender_user)){


                    if(mEmailFieldUser.equals(CEmail)){

                    if(mPassFieldUser.equals(mCon_pass_user)){

                        if(mPassFieldUser.length() > 6){

                                if(!TextUtils.isEmpty(mEmailFieldUser)) {

                                    if(isValidEmail(mEmailFieldUser)) {

                                        StartRegistering();

                                    } else {

                                        Toast.makeText(getApplicationContext() , "Enter Valid Email Address" , Toast.LENGTH_SHORT).show();

                                    }

                                } else if (TextUtils.isEmpty(mEmailFieldUser)){

                                    if(!TextUtils.isEmpty(mUsername_User)) {

                                        mEmailFieldUser = mUsername_User.toLowerCase().trim() + "@askaalim.com";
                                        StartRegistering();

                                    } else {

                                        Toast.makeText(SignupScreen_For_User.this, "Enter Email Address or userid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {

                            Toast.makeText(getApplicationContext(), "Password Length must be greater than 6 chars", Toast.LENGTH_LONG).show();

                        }

                     }else {

                        Toast.makeText(getApplicationContext(), "Password and ConfirmPassword Fields doesn't match", Toast.LENGTH_LONG).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(),"Email fields Doesn't Match", Toast.LENGTH_LONG).show();

                }
                }else {

                    if(TextUtils.isEmpty(mNameFieldUser)){

                        Toast.makeText(getApplicationContext(),"Name field is empty" , Toast.LENGTH_LONG).show();

                    } else if ( TextUtils.isEmpty(mPassFieldUser)){

                        Toast.makeText(getApplicationContext(),"Password field is empty" , Toast.LENGTH_LONG).show();

                    } else if( TextUtils.isEmpty(mCon_pass_user)){

                        Toast.makeText(getApplicationContext(),"Confirm password field is empty" , Toast.LENGTH_LONG).show();

                    } else if ( TextUtils.isEmpty(phonenumberUser)){

                        Toast.makeText(getApplicationContext(),"Phone number is not provided" , Toast.LENGTH_LONG).show();

                    } else if ( TextUtils.isEmpty(select_gender_user)){

                        Toast.makeText(getApplicationContext(),"Gender is not selected" , Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

    }

    public void SelectGenderUser(View view){

        boolean checked_gender = ((RadioButton) view).isChecked();

        switch (view.getId()){

            case R.id.signup_User_male_R_btn:
                if(checked_gender){

                    select_gender_user = "Male";
                    break;
                }

            case R.id.signup_user_Female_R_btn:
                if(checked_gender){

                    select_gender_user = "Female";
                    break;
                }
        }

    }

    private void StartRegistering() {

        final ProgressDialog progressDialog = new ProgressDialog(SignupScreen_For_User.this);
        progressDialog.setMessage("Signing up as User");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        final String email = mEmailFieldUser.replace(".", ",");

        final String countryname_user = mCountry_user_spinner.getSelectedItem().toString();
        final String State_user = mSignupStateUser.getText().toString();
        final String City_user = mSignupCityUser.getText().toString();
        final String street_address_user = mSignupAdressUser.getText().toString();
        final String selected_fiqah = mFiqah_spinner_user.getSelectedItem().toString();



        if(!TextUtils.isEmpty(mNameFieldUser) && !TextUtils.isEmpty(mEmailFieldUser) && !TextUtils.isEmpty(mPassFieldUser) && mImageUri_user != null){

            mAuth.createUserWithEmailAndPassword(mEmailFieldUser , mPassFieldUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        FirebaseUtils.getUserRef(email).child("name").setValue(mNameFieldUser);
                        FirebaseUtils.getUserRef(email).child("gender").setValue(select_gender_user);
                        FirebaseUtils.getUserRef(email).child("phone").setValue(phonenumberUser);
                        FirebaseUtils.getUserRef(email).child("country").setValue(countryname_user);
                        FirebaseUtils.getUserRef(email).child("fiqah").setValue(selected_fiqah);
                        FirebaseUtils.getUserRef(email).child("userType").setValue("User");
                        FirebaseUtils.getUserRef(email).child("uid").setValue(FirebaseUtils.getCurrentUser().getUid());
                        FirebaseUtils.getUserRef(email).child("fcmtoken").setValue(token);

                        StorageReference filepath = mSignup_Stor_ref_user.child(mImageUri_user.getLastPathSegment());

                        filepath.putFile(mImageUri_user).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String downloaduri = taskSnapshot.getDownloadUrl().toString();
                                FirebaseUtils.getUserRef(email).child("image").setValue(downloaduri);

                            }
                        });


                        if(!TextUtils.isEmpty(State_user)){

                            FirebaseUtils.getUserRef(email).child("state").setValue(State_user);

                        }

                        if (!TextUtils.isEmpty(City_user)){

                            FirebaseUtils.getUserRef(email).child("city").setValue(City_user);

                        }

                        if(!TextUtils.isEmpty(street_address_user)){

                            FirebaseUtils.getUserRef(email).child("address").setValue(street_address_user);

                        }

                        FirebaseUtils.getUserRef(email).child("email").setValue(FirebaseUtils.getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog.dismiss();

                                userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
                                userType_sh_editor = userType_sp.edit();

                                userType_sh_editor.putInt("UserType", 2);
                                userType_sh_editor.apply();

                                Intent main_intent = new Intent(SignupScreen_For_User.this , MainActivity.class);
                                main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(main_intent);

                            }
                        });

                    } else{

                        Toast.makeText(getApplicationContext(), "Unable to Signup" , Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Check your internet connection and try again" , Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            });

        }  else if ( mImageUri_user == null){
            Toast.makeText(getApplicationContext(), "Profile Image is not attached" , Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }

    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {
            Uri imageuri = data.getData();

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri_user = result.getUri();

                mProfile_user_iv.setImageURI(mImageUri_user);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
