package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.ui.adapter.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewallsearchesForUser extends BaseActivity {

    private ListView mListview;

    // vars
    private List<User> mUsersList;
    private UserListAdapter mAdapter;

    String testtoSearch;

    int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewallsearches_for_user);

        mListview = (ListView) findViewById(R.id.listview_search_Activity_user_Forall);
        mUsersList = new ArrayList<>();

        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        userType = userType_sp.getInt("UserType", 0);

        testtoSearch = getIntent().getExtras().getString("textTomatch").toString();

        hideSoftkeyboard();
        searchforMatch(testtoSearch);

//        Toast.makeText(getApplicationContext(), testtoSearch , Toast.LENGTH_SHORT).show();
    }

    private void hideSoftkeyboard(){

        if( getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

    }

    private void updateUsersListview(){

        mAdapter = new UserListAdapter(viewallsearchesForUser.this , R.layout.layout_user_listenitem, mUsersList);

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent profile_intent = new Intent(viewallsearchesForUser.this , UserProfile.class);
                profile_intent.putExtra("email", mUsersList.get(i).getEmail());
                startActivity(profile_intent);

            }
        });

    }

    private void searchforMatch(String keyword){

        mUsersList.clear();

        if(keyword.length() != 0)
         {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("users").orderByChild("email").startAt(keyword).endAt(keyword + "\uf8ff").limitToFirst(5);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

//                        mUsersList.clear();
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

   /* @Override
    protected void onDestroy() {
        super.onDestroy();

        if(userType == 3){

            mAuth.signOut();
            startActivity(new Intent(viewallsearchesForUser.this , RegisterActivity.class));
            finish();

        }
    }*/
}
