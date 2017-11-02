package com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities;

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

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.ui.activities.MainActivity;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils.getCurrentUser;

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

    private TextView mChosePP_Tv;
    private TextView mGender_tv;
    private TextView mCertificate_tv;
    private TextView mKnownLang_tv;
    private TextView mFiqah_tv;
    private TextView mSpec_Cat_tv;
    private TextView mSelectCountry_tv;

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

    private String final_speci_cat;
    private String final_language;

    private StorageReference mSignup_Stor_ref;
    private StorageReference mCertificate_stor_ref;

    SharedPreferences userType_sp;
    SharedPreferences.Editor userType_sh_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen_for_alim);

        mSignupNameAlim = (EditText) findViewById(R.id.Signup_Alim_namefield);
        mSignupEmailAlim = (EditText) findViewById(R.id.Signup_Alim_emailfield);
        mSignupPassAlim = (EditText) findViewById(R.id.Aignup_Alim_passwordfield);
        mSignupC_PassAlim = (EditText) findViewById(R.id.Signup_alim_Confirmpasswordfield);
        mSignupPhoneAlim = (EditText) findViewById(R.id.Signup_alim_PhoneField);
        mSignupStateAlim = (EditText) findViewById(R.id.Signup_state_et_Alim);
        mSignupCityAlim = (EditText) findViewById(R.id.Signup_city_et_alim);
        mSignupAddressAlim = (EditText) findViewById(R.id.Signup_address_et_alim);
        mSignupQualificationAlim = (EditText) findViewById(R.id.Signup_et_qualification_alim);

        mChosePP_Tv = (TextView) findViewById(R.id.chose_profile_tv);
        mGender_tv = (TextView) findViewById(R.id.signup_alim_gender_tv);
        mCertificate_tv = (TextView) findViewById(R.id.select_certificate_tv);
        mKnownLang_tv = (TextView) findViewById(R.id.known_lang_tv);
        mFiqah_tv = (TextView) findViewById(R.id.fiqah_tv);
        mSpec_Cat_tv = (TextView) findViewById(R.id.chose_spec_cat_tv);
        mSelectCountry_tv = (TextView) findViewById(R.id.slct_cntry_tv);

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
        mCountrySpinnerAlim.setSelection(153);

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

                if(!TextUtils.isEmpty(mNameFieldAlim) &&
                        !TextUtils.isEmpty(mEmailFieldAlim)&&
                        !TextUtils.isEmpty(mPassFieldAlim) &&
                        !TextUtils.isEmpty(mCon_pass_Alim) &&
                        !TextUtils.isEmpty(phonenumberAlim) &&
                        !TextUtils.isEmpty(select_gender_alim)){


                    if(mPassFieldAlim.equals(mCon_pass_Alim)){

                        //after checking the password and confirm passwrod, check the length of the password here

                        if(mPassFieldAlim.length() > 6){

                            if(!isValidEmail(mEmailFieldAlim)){

                                if(mEmailFieldAlim != null) {

                                    mEmailFieldAlim = mEmailFieldAlim.toLowerCase().trim() + "@askalim.com";

                                    StartRegistering();

                                } else if (mEmailFieldAlim == null){

                                    mEmailFieldAlim = mNameFieldAlim.toLowerCase().trim() + "@askalim.com";

                                    StartRegistering();
                                }
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "Password Length must be greater than 6 characters", Toast.LENGTH_LONG).show();

                        }

                    }else {

                        Toast.makeText(getApplicationContext(), "Password and ConfirmPassword Fields doesn't match", Toast.LENGTH_LONG).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(),"Some Required Fields are empty" , Toast.LENGTH_LONG).show();

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

            mChosePP_Tv.setText(R.string.urdu_choose_dp);
            mGender_tv.setText(R.string.urdu_gender);
            mCertificate_tv.setText(R.string.urdu_certificate);
            mKnownLang_tv.setText(R.string.urdu_known_lang);
            mFiqah_tv.setText(R.string.urdu_fiqah);
            mSpec_Cat_tv.setText(R.string.urdu_spec_cat);
            mSelectCountry_tv.setText(R.string.urdu_select_country);

            mLanEng.setText(R.string.urdu_eng);
            mLanUrdu.setText(R.string.urdu_urdu);
            mLanArabic.setText(R.string.urdu_arabic);
            mSpec_khuwab.setText(R.string.urdu_khuwab);
            mSpec_ist.setText(R.string.urdu_istekhara);
            mSpec_waz.setText(R.string.urdu_wazaif);

            mMale.setText(R.string.urdu_male);
            mFemale.setText(R.string.urdu_female);

            mFinishBtn.setText(R.string.urdu_finish);

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
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        //check if user is signed in (non null) and update UI accordingly
//        FirebaseUser currentUser = (FirebaseUser)getCurrentUser();
//        if (currentUser != null) {
//            Toast.makeText(getApplicationContext(),"FACEBOOK USER IS STILL LOGGED IN",Toast.LENGTH_LONG).show();
//        }
//        else{
//            Toast.makeText(getApplicationContext(),"FACEBOOK USER IS NOT LOGGED IN",Toast.LENGTH_LONG).show();
//        }
//    }

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

                        }
                    });


                    if(task.isSuccessful()){

                        FirebaseUtils.getUserRef(email).child("name").setValue(mNameFieldAlim);
                        FirebaseUtils.getUserRef(email).child("email").setValue(getCurrentUser().getEmail());
                        FirebaseUtils.getUserRef(email).child("gender").setValue(select_gender_alim);
                        FirebaseUtils.getUserRef(email).child("phone").setValue(phonenumberAlim);
                        FirebaseUtils.getUserRef(email).child("country").setValue(countryname_Alim);
                        FirebaseUtils.getUserRef(email).child("fiqah").setValue(selected_fiqah);
                        FirebaseUtils.getUserRef(email).child("userType").setValue("Alim");
                        FirebaseUtils.getUserRef(email).child("qualification").setValue(qualification);
                        FirebaseUtils.getUserRef(email).child("Interested_in_catrgoires").setValue(final_speci_cat);
                        FirebaseUtils.getUserRef(email).child("known_languages").setValue(final_language);

                        if(!TextUtils.isEmpty(State_Alim)){

                            FirebaseUtils.getUserRef(email).child("state").setValue(State_Alim);

                        }

                        if (!TextUtils.isEmpty(City_alim)){

                            FirebaseUtils.getUserRef(email).child("city").setValue(City_alim);

                        }

                        if(!TextUtils.isEmpty(street_address_alim)){

                            FirebaseUtils.getUserRef(email).child("address").setValue(street_address_alim);

                        }


                        progressDialog.dismiss();

                        userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);
                        userType_sh_editor = userType_sp.edit();

                        userType_sh_editor.putInt("UserType", 1);
                        userType_sh_editor.apply();

                        Intent main_intent = new Intent(signupScreenForAlim.this , MainActivity.class);
                        main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(main_intent);

                    } else{

                        Toast.makeText(getApplicationContext(), "Unable to Signup" , Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }
            });

        } else{

            Toast.makeText(getApplicationContext(), "Check Your Fields and Try Again" , Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    public void selectCategories(View v){

        ArrayList<String> cat_cb_selection = new ArrayList<>();

        boolean cat_check = ((CheckBox) v).isChecked();

        switch (v.getId()){

            case R.id.cat_khuwab:

                if(cat_check){

                    cat_cb_selection.add("Khuwab");

                } else {

                    cat_cb_selection.remove("Khuwab");
                }

                break;

            case R.id.cat_istekhara:

                if(cat_check){

                    cat_cb_selection.add("Istekhara");

                } else {

                    cat_cb_selection.remove("Istekhara");
                }

                break;
            case R.id.cat_wazaif:

                if(cat_check){

                    cat_cb_selection.add("Wazaif");

                } else {

                    cat_cb_selection.remove("Wazaif");
                }

                break;
        }


        final_speci_cat = "";

        for(String selections : cat_cb_selection){

            final_speci_cat = final_speci_cat + selections + " ,";

        }

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

        if (requestCode == GALLERY_REQ_2){

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

}
