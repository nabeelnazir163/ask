package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.zillion.nabeel.postandcommenttutorial.utils.Constants;
import com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class edit_profile extends BaseActivity {

    private ImageView chnage_image;

    private EditText chnage_name;
    private EditText institute;
    private EditText Description;
    private EditText phone_number;
    private EditText address;
    private EditText date_of_birth;
    private EditText newPass;
    private EditText confirm_newPass;
    private EditText old_pwd_et;

    private Button done;
    private Button change_password;
    private Button done_resettinG_pwd;

    private static final int GALLERY_REQ_2 = 2;

    private Uri new_image_uri;
    private Uri old_image_uri;

    private StorageReference mSignup_Stor_ref_user;
    FirebaseUser user;

    private String institute_name;
    private String description;
    private String old_name;
    private String phone_number_string;
    private String addresss_string;
    private String dob_string;

    private TextInputLayout institute_layout_editProfile;
    private TextInputLayout description_layout_editProfile;

    Dialog reset_pass_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        chnage_image = (ImageView) findViewById(R.id.edit_image_iv);

        chnage_name = (EditText) findViewById(R.id.edit_name_edit_text);
        institute = (EditText) findViewById(R.id.institute_edittext);
        Description = (EditText) findViewById(R.id.desc_editProfile);
        address = (EditText) findViewById(R.id.edit_address_edit_text);
        phone_number = (EditText) findViewById(R.id.edit_phone_number_edit_text);
        date_of_birth = (EditText) findViewById(R.id.edit_date_of_birth_edit_text);

        done = (Button) findViewById(R.id.done_button_edit_profile);

        institute_layout_editProfile = (TextInputLayout) findViewById(R.id.institute_layout_editprofile);
        description_layout_editProfile = (TextInputLayout) findViewById(R.id.description_layout_edit_profile);

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("institute")){
                            institute_name = dataSnapshot.child("institute").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("about_alim")){
                            description = dataSnapshot.child("about_alim").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("phone")){
                            phone_number_string = dataSnapshot.child("phone").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("dateOfBirth")){
                            dob_string = dataSnapshot.child("dateOfBirth").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("address")){
                            addresss_string = dataSnapshot.child("address").getValue().toString();
                        }


                        if (!TextUtils.isEmpty(institute_name)){
                            institute.setText(institute_name);
                        }
                        if(!TextUtils.isEmpty(description)){
                            Description.setText(description);
                        }
                        if(!TextUtils.isEmpty(addresss_string)){
                            address.setText(addresss_string);
                        }
                        if(!TextUtils.isEmpty(phone_number_string)){
                            phone_number.setText(phone_number_string);
                        }
                        if(!TextUtils.isEmpty(dob_string)){
                            date_of_birth.setText(dob_string);
                        }

                        String userType = dataSnapshot.child("userType").getValue().toString();

                        if(userType.equals("Alim")){
                            institute_layout_editProfile.setVisibility(View.VISIBLE);
                            description_layout_editProfile.setVisibility(View.VISIBLE);
                        } else if( userType.equals("User")){
                            institute_layout_editProfile.setVisibility(View.GONE);
                            description_layout_editProfile.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        change_password = (Button) findViewById(R.id.chnage_pass_textview);

        mSignup_Stor_ref_user = FirebaseStorage.getInstance().getReference().child("profile_images/");

        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        old_image_uri = Uri.parse(dataSnapshot.child("image").getValue().toString());
                        old_name = dataSnapshot.child("name").getValue().toString();

                        chnage_name.setText(old_name);
                        Glide.with(getApplicationContext()).load(old_image_uri).into(chnage_image);

                        new_image_uri = old_image_uri;
//                        Toast.makeText(getApplicationContext(), "new uri" + new_image_uri, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        chnage_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent , GALLERY_REQ_2);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(address.getText().toString())) {

                    if(!address.getText().toString().equals(addresss_string))
                    {
                        final ProgressDialog progressDialog_address = new ProgressDialog(edit_profile.this);
                        progressDialog_address.setMessage("Updating your address");
                        progressDialog_address.setCancelable(false);
                        progressDialog_address.show();
                        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                                .child("address")
                                .setValue(address.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog_address.dismiss();
                                Toast.makeText(edit_profile.this, "address Updated", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }

                if(!TextUtils.isEmpty(phone_number.getText().toString())) {

                    if(!phone_number.getText().toString().equals(phone_number_string))
                    {
                        final ProgressDialog progressDialog_nmbr = new ProgressDialog(edit_profile.this);
                        progressDialog_nmbr.setMessage("Updating your Phone number");
                        progressDialog_nmbr.setCancelable(false);
                        progressDialog_nmbr.show();
                        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                                .child("phone")
                                .setValue(phone_number.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog_nmbr.dismiss();
                                Toast.makeText(edit_profile.this, "Number Updated", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }

                if(!TextUtils.isEmpty(date_of_birth.getText().toString())) {

                    if(!date_of_birth.getText().toString().equals(dob_string))
                    {
                        final ProgressDialog progressDialog_dob = new ProgressDialog(edit_profile.this);
                        progressDialog_dob.setMessage("Updating date of birth");
                        progressDialog_dob.setCancelable(false);
                        progressDialog_dob.show();
                        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                                .child("dateOfBirth")
                                .setValue(date_of_birth.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog_dob.dismiss();
                                Toast.makeText(edit_profile.this, "Date of birth Updated", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }

                if(!TextUtils.isEmpty(chnage_name.getText().toString())) {

                    if(!chnage_name.getText().toString().equals(old_name))
                    {
                        final ProgressDialog progressDialog_name = new ProgressDialog(edit_profile.this);
                        progressDialog_name.setMessage("Updating your name");
                        progressDialog_name.setCancelable(false);
                        progressDialog_name.show();
                        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                                .child("name")
                                .setValue(chnage_name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog_name.dismiss();
                                Toast.makeText(edit_profile.this, "Name Updated", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }




                if(!TextUtils.isEmpty(institute.getText().toString())) {

                    if ((institute.getText().toString()).length() > 5 && (institute.getText().toString()).length() < 100) {

                        if(!institute.getText().toString().equals(institute_name))
                        {
                            final ProgressDialog progressDialog_institute = new ProgressDialog(edit_profile.this);
                            progressDialog_institute.setMessage("Updating your institute name");
                            progressDialog_institute.setCancelable(false);
                            progressDialog_institute.show();
                            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                                    .child("institute")
                                    .setValue(institute.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressDialog_institute.dismiss();
                                    Toast.makeText(edit_profile.this, "Institute Updated", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    } else {

                        Toast.makeText(edit_profile.this, "Institute name should be between 5 to 100 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!TextUtils.isEmpty(Description.getText().toString())) {

                    if ((Description.getText().toString()).length() > 5 && (institute.getText().toString()).length() < 300) {

                      if(!Description.getText().toString().equals(description)){

                          final ProgressDialog progressDialog_Desc = new ProgressDialog(edit_profile.this);
                          progressDialog_Desc.setMessage("Updating your description");
                          progressDialog_Desc.setCancelable(false);
                          progressDialog_Desc.show();

                          FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ","))
                                  .child("about_alim")
                                  .setValue(Description.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {

                                  progressDialog_Desc.dismiss();
                                  Toast.makeText(edit_profile.this, "Description Updated", Toast.LENGTH_SHORT).show();

                              }
                          });
                      }

                    } else {

                        Toast.makeText(edit_profile.this, "Description should be between 5 to 300 characters", Toast.LENGTH_SHORT).show();
                    }

                }
                if (!new_image_uri.equals(old_image_uri)) {

                    final ProgressDialog progressDialog = new ProgressDialog(edit_profile.this);
                    progressDialog.setMessage("Updating Image");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    StorageReference filepath = mSignup_Stor_ref_user.child(new_image_uri.getLastPathSegment());

                    filepath.putFile(new_image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String downloaduri = taskSnapshot.getDownloadUrl().toString();
                            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                                    .child("image").setValue(downloaduri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(edit_profile.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reset_pass_dialog = new Dialog(edit_profile.this, android.R.style.Theme_Holo_NoActionBar_TranslucentDecor);
                reset_pass_dialog.setTitle("Reset Password");
                reset_pass_dialog.setCancelable(true);
                reset_pass_dialog.setContentView(R.layout.change_password_dialog);
                old_pwd_et = (EditText) reset_pass_dialog.findViewById(R.id.old_password_et);
                newPass = (EditText) reset_pass_dialog.findViewById(R.id.new_password_et);
                confirm_newPass = (EditText) reset_pass_dialog.findViewById(R.id.confirm_new_password_et);
                done_resettinG_pwd = (Button) reset_pass_dialog.findViewById(R.id.done_reseting_pwd);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(reset_pass_dialog.getWindow().getAttributes());
                reset_pass_dialog.setCancelable(true);
                reset_pass_dialog.getWindow().setAttributes(lp);

                user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){

                    done_resettinG_pwd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(!(TextUtils.isEmpty(old_pwd_et.getText().toString()))){

                            AuthCredential credentials = EmailAuthProvider
                                    .getCredential(FirebaseUtils.getCurrentUser().getEmail(), old_pwd_et.getText().toString());

                            if (!TextUtils.isEmpty(old_pwd_et.getText().toString())) {

                                if (!TextUtils.isEmpty((newPass.getText().toString()))) {

                                    if (!TextUtils.isEmpty((confirm_newPass.getText().toString()))) {

                                        if ((newPass.getText().toString()).equals(confirm_newPass.getText().toString())) {

                                            user.reauthenticate(credentials).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        user.updatePassword(newPass.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {

                                                                            Toast.makeText(edit_profile.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                                            reset_pass_dialog.dismiss();
                                                                        } else {

                                                                            Toast.makeText(edit_profile.this, "Unable to update password", Toast.LENGTH_SHORT).show();
//                                                                            dialog.dismiss();
                                                                        }

                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(edit_profile.this, "Try Again with correct old password", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });


                                        } else {

                                            Toast.makeText(edit_profile.this, "Fields doesnt match", Toast.LENGTH_SHORT).show();

                                        }

                                    } else {

                                        Toast.makeText(edit_profile.this, "Enter something in Confirm Password Field", Toast.LENGTH_SHORT).show();

                                    }

                                } else {

                                    Toast.makeText(edit_profile.this, "Enter something in new Password Field", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(edit_profile.this, "Enter Old Password", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                                reset_pass_dialog.dismiss();
                            }
                        }
                    });

                }

                reset_pass_dialog.show();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ_2) {
            if (resultCode == RESULT_OK) {
                new_image_uri = data.getData();
                chnage_image.setImageURI(new_image_uri);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)

            finish();

        return super.onOptionsItemSelected(item);
    }

}
