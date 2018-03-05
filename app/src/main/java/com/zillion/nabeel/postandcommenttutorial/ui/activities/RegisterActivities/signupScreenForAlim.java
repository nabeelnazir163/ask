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
import android.widget.CheckBox;
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
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils.getCurrentUser;

public class signupScreenForAlim extends BaseActivity {

    private EditText mSignupNameAlim;
    private EditText mSignupEmailAlim;
    private EditText mSignupPassAlim;
    private EditText mSignupC_PassAlim;
    private EditText mSignupPhoneAlim;
    private EditText mSignupStateAlim;
    private EditText mSignupCityAlim;
    private EditText mSignupAddressAlim;
    private EditText mSignupQualificationAlim;
    private EditText mSignupUsernameAlim;
    private EditText mConfirmEmailAlim;

    private TextView mChosePP_Tv;
    private TextView mGender_tv;
    private TextView mCertificate_tv;
    private TextView mKnownLang_tv;
    private TextView mFiqah_tv;
    private TextView mSpec_Cat_tv;
    private TextView mSelectCountry_tv;
    private TextView passwordHint;

    private CheckBox mLanEng;
    private CheckBox mLanUrdu;
    private CheckBox mLanArabic;
    private CheckBox mSpec_khuwab;
    private CheckBox mSpec_ist;
    private CheckBox mSpec_waz;

    private RadioButton mMale;
    private RadioButton mFemale;

    private ImageView mProfileiv;
    private ImageView mCertificateiv;

    private Spinner mFiqahSpinnerAlim;
    private Spinner mCountrySpinnerAlim;

    private Button mFinishBtn;

    private final static int GALLERY_REQ = 1;
    private static final int GALLERY_REQ_2 = 2;

    private Uri mImageUriAlim = null;
    private Uri certificate_image_uri = null;

    String mNameFieldAlim;
    String mEmailFieldAlim;
    String mPassFieldAlim;
    String phonenumberAlim;
    String select_gender_alim;
    String mCon_pass_Alim;
    String mUsernameAlim;
    String mConfirmEmail;

    private TextView mEmailValidation;

//    private String final_speci_cat;
    private String final_language;

    private boolean wazaif_cb;
    private boolean istekhara_cb;
    private boolean khuwab_cb;

    private StorageReference mSignup_Stor_ref;
    private StorageReference mCertificate_stor_ref;

    SharedPreferences userType_sp;
    SharedPreferences.Editor userType_sh_editor;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen_for_alim);

        /*
         * getting the fcm token here
         */
        token = FirebaseInstanceId.getInstance().getToken();

        mSignupNameAlim = (EditText) findViewById(R.id.Signup_Alim_namefield);
        mSignupEmailAlim = (EditText) findViewById(R.id.Signup_Alim_emailfield);
        mConfirmEmailAlim = (EditText) findViewById(R.id.Signup_Alim_emailfield_Confirm);
        mSignupPassAlim = (EditText) findViewById(R.id.Aignup_Alim_passwordfield);
        mSignupC_PassAlim = (EditText) findViewById(R.id.Signup_alim_Confirmpasswordfield);
        mSignupPhoneAlim = (EditText) findViewById(R.id.Signup_alim_PhoneField);
        mSignupStateAlim = (EditText) findViewById(R.id.Signup_state_et_Alim);
        mSignupCityAlim = (EditText) findViewById(R.id.Signup_city_et_alim);
        mSignupAddressAlim = (EditText) findViewById(R.id.Signup_address_et_alim);
        mSignupQualificationAlim = (EditText) findViewById(R.id.Signup_et_qualification_alim);
        mSignupUsernameAlim = (EditText) findViewById(R.id.Signup_Alim_Username_namefield);

        mChosePP_Tv = (TextView) findViewById(R.id.chose_profile_tv);
        mGender_tv = (TextView) findViewById(R.id.signup_alim_gender_tv);
        mCertificate_tv = (TextView) findViewById(R.id.select_certificate_tv);
        mKnownLang_tv = (TextView) findViewById(R.id.known_lang_tv);
        mFiqah_tv = (TextView) findViewById(R.id.fiqah_tv);
        mSpec_Cat_tv = (TextView) findViewById(R.id.chose_spec_cat_tv);
        mSelectCountry_tv = (TextView) findViewById(R.id.slct_cntry_tv);
        passwordHint = (TextView) findViewById(R.id.passwordHint);

        mEmailValidation = (TextView) findViewById(R.id.email_validity_statement);

        mLanEng = (CheckBox) findViewById(R.id.Signup_alim_lan_eng_cb);
        mLanUrdu = (CheckBox) findViewById(R.id.Signup_Alim_lan_urdu_cb);
        mLanArabic = (CheckBox) findViewById(R.id.Signup_alim_lan_arabic_cb);
        mSpec_khuwab = (CheckBox) findViewById(R.id.cat_khuwab);
        mSpec_ist = (CheckBox) findViewById(R.id.cat_istekhara);
        mSpec_waz = (CheckBox) findViewById(R.id.cat_wazaif);

        mMale = (RadioButton) findViewById(R.id.signup_Alim_male_R_btn);
        mFemale = (RadioButton) findViewById(R.id.signup_Alim_Female_R_btn);

        mFiqahSpinnerAlim = (Spinner) findViewById(R.id.fiqah_spinner_alim);
        mCountrySpinnerAlim = (Spinner) findViewById(R.id.setup_country_spinner_alim);

        mProfileiv = (ImageView) findViewById(R.id.profile_iv_alim);
        mCertificateiv = (ImageView) findViewById(R.id.Signup_Alim_certificate_imageView);

        mFinishBtn = (Button) findViewById(R.id.finish_setup);

        mSignup_Stor_ref = FirebaseStorage.getInstance().getReference().child("profile_images/");
        mCertificate_stor_ref = FirebaseStorage.getInstance().getReference().child("Alim_certificates/");

        Locale[] locales_user = Locale.getAvailableLocales();
        ArrayList<String> countries_user = new ArrayList<String>();

        ArrayAdapter<String> fiqah_adapter_user = new ArrayAdapter<String>(signupScreenForAlim.this,
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
        mCountrySpinnerAlim.setAdapter(dataAdapter);
//        mCountrySpinnerAlim.setSelection(166);

        System.out.println("# countries found: " + countries_user.size());

        mFiqahSpinnerAlim.setAdapter(fiqah_adapter_user);

        mProfileiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent, GALLERY_REQ);

            }
        });

        mCertificateiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent , GALLERY_REQ_2);

            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mNameFieldAlim = mSignupNameAlim.getText().toString();
                mEmailFieldAlim = mSignupEmailAlim.getText().toString();
                mPassFieldAlim = mSignupPassAlim.getText().toString();
                mCon_pass_Alim = mSignupC_PassAlim.getText().toString();
                phonenumberAlim = mSignupPhoneAlim.getText().toString();
                mUsernameAlim = mSignupUsernameAlim.getText().toString();
                mConfirmEmail = mConfirmEmailAlim.getText().toString();

                if (!TextUtils.isEmpty(mNameFieldAlim) &&
//                        !TextUtils.isEmpty(mEmailFieldAlim)&&
                            !TextUtils.isEmpty(mPassFieldAlim) &&
                                !TextUtils.isEmpty(mCon_pass_Alim) &&
                                    !TextUtils.isEmpty(phonenumberAlim) &&
                                        !TextUtils.isEmpty(select_gender_alim)) {

                    if(mEmailFieldAlim.equals(mConfirmEmail)){

                        if (mPassFieldAlim.equals(mCon_pass_Alim)) {

                            if (mPassFieldAlim.length() > 6) {

                                if (!TextUtils.isEmpty(mEmailFieldAlim)) {

                                    if (isValidEmail(mEmailFieldAlim)) {

                                        StartRegistering();
                                    } else {

                                        Toast.makeText(getApplicationContext(), "Enter Valid Email Address", Toast.LENGTH_SHORT).show();

                                    }


                                } else if (TextUtils.isEmpty(mEmailFieldAlim)) {

                                    if(!TextUtils.isEmpty(mUsernameAlim)) {

                                        mEmailFieldAlim = mUsernameAlim.toLowerCase().trim() + "@askaalim.com";
                                        StartRegistering();

                                    } else {

                                        Toast.makeText(signupScreenForAlim.this, "Enter Email Address or userid", Toast.LENGTH_SHORT).show();
                                    }

                            }

                        } else {

                            Toast.makeText(getApplicationContext(), "Password Length must be greater than 6 characters", Toast.LENGTH_LONG).show();

                        }

                    } else {

                        Toast.makeText(getApplicationContext(), "Password and ConfirmPassword Fields doesn't match", Toast.LENGTH_LONG).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(),"Email fields Doesn't Match", Toast.LENGTH_LONG).show();
                }

            }else {

                    if(TextUtils.isEmpty(mNameFieldAlim)){

                        Toast.makeText(getApplicationContext(),"Name field is empty" , Toast.LENGTH_LONG).show();

                    } else if ( TextUtils.isEmpty(mPassFieldAlim)){

                        Toast.makeText(getApplicationContext(),"Password field is empty" , Toast.LENGTH_LONG).show();

                    } else if( TextUtils.isEmpty(mCon_pass_Alim)){

                        Toast.makeText(getApplicationContext(),"Confirm password field is empty" , Toast.LENGTH_LONG).show();

                    } else if ( TextUtils.isEmpty(phonenumberAlim)){

                        Toast.makeText(getApplicationContext(),"Phone number is not provided" , Toast.LENGTH_LONG).show();

                    } else if ( TextUtils.isEmpty(select_gender_alim)){

                        Toast.makeText(getApplicationContext(),"Gender is not selected" , Toast.LENGTH_LONG).show();

                    }

                }
            }
        });


        SharedPreferences userInfo_SP = getSharedPreferences("Language", Context.MODE_PRIVATE);
        String language = userInfo_SP.getString("Language", "");

        if(language.equals("urdu")){

            mSignupNameAlim.setHint(R.string.urdu_name);
            mSignupEmailAlim.setHint(R.string.urdu_email);
            mSignupPassAlim.setHint(R.string.urdu_password);
            mSignupC_PassAlim.setHint(R.string.urdu_conf_password);
            mSignupPhoneAlim.setHint(R.string.urdu_phone);
            mSignupStateAlim.setHint(R.string.urdu_state);
            mSignupCityAlim.setHint(R.string.urdu_city);
            mSignupAddressAlim.setHint(R.string.urdu_cmplt_address);
            mSignupQualificationAlim.setHint(R.string.urdu_education);
            mConfirmEmailAlim.setHint(R.string.U_C_email);
            mSignupUsernameAlim.setHint(R.string.Urdu_username);

            mChosePP_Tv.setText(R.string.urdu_choose_dp);
            mGender_tv.setText(R.string.urdu_gender);
            mCertificate_tv.setText(R.string.urdu_certificate);
            mKnownLang_tv.setText(R.string.urdu_known_lang);
            mFiqah_tv.setText(R.string.urdu_fiqah);
            mSpec_Cat_tv.setText(R.string.urdu_spec_cat);
            mSelectCountry_tv.setText(R.string.urdu_select_country);
            passwordHint.setText(R.string.passwordhinturdu);

            mLanEng.setText(R.string.urdu_eng);
            mLanUrdu.setText(R.string.urdu_urdu);
            mLanArabic.setText(R.string.urdu_arabic);
            mSpec_khuwab.setText(R.string.urdu_khuwab);
            mSpec_ist.setText(R.string.urdu_istekhara);
            mSpec_waz.setText(R.string.urdu_wazaif);

            mMale.setText(R.string.urdu_male);
            mFemale.setText(R.string.urdu_female);

            mFinishBtn.setText(R.string.urdu_finish);
            mEmailValidation.setText(R.string.emailvalidity_urdu);

        } else if(language.equals("en")){

            mSignupNameAlim.setHint(R.string.eng_name);
            mSignupEmailAlim.setHint(R.string.eng_email);
            mSignupPassAlim.setHint(R.string.eng_pass);
            mSignupC_PassAlim.setHint(R.string.eng_con_pass);
            mSignupPhoneAlim.setHint(R.string.eng_phone);
            mSignupStateAlim.setHint(R.string.eng_state);
            mSignupCityAlim.setHint(R.string.eng_city);
            mSignupAddressAlim.setHint(R.string.eng_address);
            mSignupQualificationAlim.setHint(R.string.eng_education);

            mChosePP_Tv.setText(R.string.eng_chose_dp);
            mGender_tv.setText(R.string.eng_gender);
            mCertificate_tv.setText(R.string.eng_certificate);
            mKnownLang_tv.setText(R.string.eng_known_lan);
            mFiqah_tv.setText(R.string.eng_fiqah);
            mSpec_Cat_tv.setText(R.string.eng_spec_cat);
            mSelectCountry_tv.setText(R.string.eng_slct_country);

            mLanEng.setText(R.string.eng_Eng);
            mLanUrdu.setText(R.string.eng_urdu);
            mLanArabic.setText(R.string.eng_arabic);
            mSpec_khuwab.setText(R.string.eng_khuwab);
            mSpec_ist.setText(R.string.eng_istekhara);
            mSpec_waz.setText(R.string.eng_wazaif);

            mMale.setText(R.string.eng_male);
            mFemale.setText(R.string.eng_female);

            mFinishBtn.setText(R.string.eng_finish);

        }
    }



    //ON START OF APPLICATION CHECK IF THERE IS DATA COMING WITH THE INTENT
    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getStringExtra("json")!=null){
            try {
                JSONObject object = new JSONObject(getIntent().getStringExtra("json"));
                String fName = "", lName = "", name = "";
                fName = object.getString("first_name");
                lName = object.getString("last_name");
                name = ""+fName+" "+lName;

                mSignupNameAlim.setText(name);
                mSignupEmailAlim.setText(object.getString("email"));
                mEmailFieldAlim = object.getString("email");
                String userID = object.getString("id");
                Picasso.with(signupScreenForAlim.this).load("https://graph.facebook.com/" + userID+ "/picture?type=large").into(mProfileiv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void StartRegistering() {

        final ProgressDialog progressDialog = new ProgressDialog(signupScreenForAlim.this);
        progressDialog.setMessage("Signing up as Alim");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        //final String email = mAuth.getCurrentUser().getEmail().replace(".",",");
        final String email = mEmailFieldAlim.replace(".", ",");

        /*SharedPreferences userInfo_SP = getSharedPreferences("userTypeInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor sh_editor = userInfo_SP.edit();
*/

        final String countryname_Alim = mCountrySpinnerAlim.getSelectedItem().toString();
        final String State_Alim = mSignupStateAlim.getText().toString();
        final String City_alim = mSignupCityAlim.getText().toString();
        final String street_address_alim = mSignupAddressAlim.getText().toString();
        final String selected_fiqah = mFiqahSpinnerAlim.getSelectedItem().toString();
        final String qualification = mSignupQualificationAlim.getText().toString();


        if(!TextUtils.isEmpty(mNameFieldAlim) && !TextUtils.isEmpty(mEmailFieldAlim) && !TextUtils.isEmpty(mPassFieldAlim) && certificate_image_uri != null && mImageUriAlim!= null){

            mAuth.createUserWithEmailAndPassword(mEmailFieldAlim , mPassFieldAlim).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){

                        StorageReference certificates_filepath = mCertificate_stor_ref.child(certificate_image_uri.getLastPathSegment());

                        certificates_filepath.putFile(certificate_image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String download_certicate_uri = taskSnapshot.getDownloadUrl().toString();
                                FirebaseUtils.getUserRef(email).child("certificate").setValue(download_certicate_uri);
                            }
                        });


                        StorageReference filepath = mSignup_Stor_ref.child(mImageUriAlim.getLastPathSegment());

                        filepath.putFile(mImageUriAlim).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String downloaduri = taskSnapshot.getDownloadUrl().toString();
                                FirebaseUtils.getUserRef(email).child("image").setValue(downloaduri);

                                FirebaseUtils.getUserRef(email).child("name").setValue(mNameFieldAlim);
                                FirebaseUtils.getUserRef(email).child("gender").setValue(select_gender_alim);
                                FirebaseUtils.getUserRef(email).child("phone").setValue(phonenumberAlim);
                                FirebaseUtils.getUserRef(email).child("country").setValue(countryname_Alim);
                                FirebaseUtils.getUserRef(email).child("fiqah").setValue(selected_fiqah);
                                FirebaseUtils.getUserRef(email).child("userType").setValue("Alim");
                                FirebaseUtils.getUserRef(email).child("qualification").setValue(qualification);
                                if(wazaif_cb)
                                {
                                    FirebaseUtils.getUserRef(email).child("wazaif").setValue(true);
                                }
                                if(khuwab_cb)
                                {
                                    FirebaseUtils.getUserRef(email).child("khuwab").setValue(true);
                                }
                                if(istekhara_cb)
                                {
                                    FirebaseUtils.getUserRef(email).child("istekhara").setValue(true);
                                }
                                FirebaseUtils.getUserRef(email).child("known_languages").setValue(final_language);
                                FirebaseUtils.getUserRef(email).child("fcmtoken").setValue(token);
                                FirebaseUtils.getUserRef(email).child("uid").setValue(FirebaseUtils.getCurrentUser().getUid());

                                if(!TextUtils.isEmpty(State_Alim)){

                                    FirebaseUtils.getUserRef(email).child("state").setValue(State_Alim);

                                }

                                if (!TextUtils.isEmpty(City_alim)){

                                    FirebaseUtils.getUserRef(email).child("city").setValue(City_alim);

                                }

                                if(!TextUtils.isEmpty(street_address_alim)){

                                    FirebaseUtils.getUserRef(email).child("address").setValue(street_address_alim);

                                }

                                FirebaseUtils.getUserRef(email).child("email").setValue(getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        progressDialog.dismiss();

                                        userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
                                        userType_sh_editor = userType_sp.edit();

                                        userType_sh_editor.putInt("UserType", 1);
                                        userType_sh_editor.apply();

                                        Intent main_intent = new Intent(signupScreenForAlim.this , MainActivity.class);
                                        main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                        startActivity(main_intent);

                                    }
                                });


                            }
                        });


                    } else{

                        Toast.makeText(getApplicationContext(), "Try Again with different email" , Toast.LENGTH_LONG).show();
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

        } else if ( certificate_image_uri == null){

            Toast.makeText(getApplicationContext(), "Certificate Image is not attached" , Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if ( mImageUriAlim == null){
            Toast.makeText(getApplicationContext(), "Profile Image is not attached" , Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    public void selectCategories(View v){

        ArrayList<String> cat_cb_selection = new ArrayList<>();

        boolean cat_check = ((CheckBox) v).isChecked();

        switch (v.getId()){

            case R.id.cat_khuwab:

                if(cat_check){

//                    cat_cb_selection.add("Khuwab");

                    khuwab_cb = true;

                } else {

//                    cat_cb_selection.remove("Khuwab");
                    khuwab_cb = false;
                }

                break;

            case R.id.cat_istekhara:

                if(cat_check){

//                    cat_cb_selection.add("Istekhara");
                    istekhara_cb =  true;

                } else {

//                    cat_cb_selection.remove("Istekhara");
                    istekhara_cb = false;
                }

                break;
            case R.id.cat_wazaif:

                if(cat_check){

//                    cat_cb_selection.add("Wazaif");
                    wazaif_cb = true;

                } else {

//                    cat_cb_selection.remove("Wazaif");
                    wazaif_cb = false;
                }

                break;
        }


//        final_speci_cat = "";
//
//        for(String selections : cat_cb_selection){
//
//            final_speci_cat = final_speci_cat + selections + " ,";

        }


    public void selectLanguage(View v) {

        ArrayList<String> language_cb_selection = new ArrayList<>();

        boolean lanuage_check = ((CheckBox) v).isChecked();

        switch (v.getId()) {

            case R.id.Signup_alim_lan_eng_cb:

                if (lanuage_check) {

                    language_cb_selection.add("English");

                } else {

                    language_cb_selection.remove("English");
                }

                break;

            case R.id.Signup_Alim_lan_urdu_cb:

                if (lanuage_check) {

                    language_cb_selection.add("Urdu");

                } else {

                    language_cb_selection.remove("Urdu");
                }

                break;
            case R.id.Signup_alim_lan_arabic_cb:

                if (lanuage_check) {

                    language_cb_selection.add("Arabic");

                } else {

                    language_cb_selection.remove("Arabic");
                }

                break;
        }


        final_language = "";

        for (String lan_selections : language_cb_selection) {

            final_language = final_language + lan_selections + " ,";
        }
    }

    //FOR VALIDATING THE EMAIL ADDRESS

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
                mImageUriAlim = result.getUri();

                mProfileiv.setImageURI(mImageUriAlim);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == GALLERY_REQ_2 && resultCode == RESULT_OK){

            certificate_image_uri = data.getData();

            mCertificateiv.setImageURI(certificate_image_uri);

        }

    }

    public void SelectGenderAlim(View view){

        boolean checked_gender = ((RadioButton) view).isChecked();

        switch (view.getId()){

            case R.id.signup_Alim_male_R_btn:
                if(checked_gender){

                    select_gender_alim = "Male";
                    break;
                }

            case R.id.signup_Alim_Female_R_btn:
                if(checked_gender){

                    select_gender_alim = "Female";
                    break;
                }
        }

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }*/
}
