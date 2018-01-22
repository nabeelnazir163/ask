package com.zillion.nabeel.postandcommenttutorial.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.models.Post;
import com.zillion.nabeel.postandcommenttutorial.models.User;
import com.zillion.nabeel.postandcommenttutorial.ui.adapter.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zillion.nabeel.postandcommenttutorial.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class search_user_for_newMessage extends AppCompatActivity {

    private ListView mListview;

    // vars
    private List<User> mUsersList;
    private UserListAdapter mAdapter;

    String askfor;

    EditText mSearchParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_for_new_message);

        mListview = (ListView) findViewById(R.id.search_user_for_message_listview);
        mSearchParams = (EditText) findViewById(R.id.search_user_for_message);

        Intent intent = getIntent();

        if(intent.getStringExtra("askFor") != null) {

            askfor = intent.getStringExtra("askFor").toString();

        }

        mSearchParams.setHint("Search User to ask for "+ askfor);

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

        mListview.setVisibility(View.VISIBLE);
        mAdapter = new UserListAdapter(this , R.layout.layout_user_listenitem, mUsersList);

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(mUsersList != null) {

                    Intent profile_intent = new Intent(search_user_for_newMessage.this, Chat.class);
                    profile_intent.putExtra("emailforchat", mUsersList.get(i).getEmail());
                    profile_intent.putExtra("msg_id", "");
                    profile_intent.putExtra("image_url", mUsersList.get(i).getImage().toString());
                    finish();
                    startActivity(profile_intent);

                }

//                mSearchParams.setText(mUsersList.get(i).getEmail());
//                hideSoftkeyboard();

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

                String text = mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mListview.setVisibility(View.GONE);
                /*String text = mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);*/

            }
        });
    }

    private void searchforMatch(String keyword){

        mUsersList.clear();

        if(keyword.length() == 0){
            mUsersList.clear();
        }
        else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("users").orderByChild("email").startAt(keyword).endAt(keyword + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

//                        mUsersList.clear();
                        if(singlesnapshot.hasChild(askfor)) {
                            mUsersList.add(singlesnapshot.getValue(User.class));
                            updateUsersListview();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
