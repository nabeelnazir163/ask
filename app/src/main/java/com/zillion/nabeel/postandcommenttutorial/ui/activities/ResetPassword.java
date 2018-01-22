package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    EditText mEmailEditText;
    Button mResetButton;
    TextView mBackTextView;
    String emailAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mEmailEditText = (EditText)findViewById(R.id.resetEmail_tv);
        mResetButton = (Button)findViewById(R.id.resetPassword_btn);
        mBackTextView = (TextView)findViewById(R.id.resetBack_btn);


        mBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAddress = mEmailEditText.getText().toString();
                if(emailAddress.equals("")){
                    Toast.makeText(ResetPassword.this,"ENTER EMAIL ADDRESS",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(emailAddress!= "" && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()==false){
                        Toast.makeText(ResetPassword.this,"ENTER VALID EMAIL ADDRESS",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(emailAddress.contains("@askaalim.com")){
                            Toast.makeText(ResetPassword.this, "YOU CAN NOT RESET PASSWORD AS IT IS DEFINED IN THE TERMS AND CONDITIONS", Toast.LENGTH_LONG).show();
                        }
                        else{
                            sendResetEmail();
                        }
                    }
                }
            }
        });
    }
    public void sendResetEmail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String TAG = "ResetPassword";
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(ResetPassword.this,"Check email to reset your password",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(ResetPassword.this,"Failed to reset password. Please try again.",Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}
