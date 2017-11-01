package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.app.ActionBar;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.utils.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Context mContext = SearchActivity.this;

    //Widget
    private EditText mSearchParams;
    private ListView mListview;
    private ImageView backbutton;

    // vars
    private List<User> mUsersList;
    private UserListAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.hide();

        }

        mSearchParams = (EditText) findViewById(R.id.search);
        backbutton = (ImageView) findViewById(R.id.back_button);
        mListview = (ListView) findViewById(R.id.listview_search_Activity);

        mSearchParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backbutton.setVisibility(View.VISIBLE);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        hideSoftkeyboard();
        initTextListener();

    }


    private void hideSoftkeyboard(){

       if( getCurrentFocus() != null){

           InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
           imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

       }

    }

    private void updateUsersListview(){

        mAdapter = new UserListAdapter(mContext , R.layout.layout_user_listenitem, mUsersList);

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent profile_intent = new Intent(mContext , UserProfile.class);
                profile_intent.putExtra("email", mUsersList.get(i).getEmail());
                finish();
                startActivity(profile_intent);

            }
        });

    }

    private void initTextListener(){

        mUsersList = new ArrayList<>();

        mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                backbutton.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String text = mSearchParams.getText().toString();
                searchforMatch(text);

            }
        });

    }

    private void searchforMatch(String keyword){

        mUsersList.clear();

        if(keyword.length() == 0){}
        else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("users").orderByChild("name").startAt(keyword).endAt(keyword + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

                        mUsersList.add(singlesnapshot.getValue(User.class));
                        updateUsersListview();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}
