package com.example.nabeel.postandcommenttutorial.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.activities.UserProfile;
import com.example.nabeel.postandcommenttutorial.utils.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Nabeel on 11/7/2017.
 */

public class fragment_post_Search extends Fragment {

    private EditText mSearchParams;
    private ListView mListview;
    private ImageView backbutton;

    private List<User> mUsersList;
    private UserListAdapter mAdapter;

    View mRootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_usersearch, container , false);

        mSearchParams = (EditText) mRootview.findViewById(R.id.search);
        backbutton = (ImageView) mRootview.findViewById(R.id.back_button);
        mListview = (ListView) mRootview.findViewById(R.id.listview_search_Activity);

        hideSoftkeyboard();
        initTextListener();

        return mRootview;
    }

    private void hideSoftkeyboard(){

        if( getActivity().getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        }
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
                String text = mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                /*String text = mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);*/

            }
        });

    }

    private void searchforMatch(String keyword){

        mUsersList.clear();

        if(keyword.length() == 0){}
        else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("post").orderByChild("postText").startAt(keyword).endAt(keyword + "\uf8ff");

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

    private void updateUsersListview(){

        mAdapter = new UserListAdapter(getContext() , R.layout.layout_user_listenitem, mUsersList);

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent profile_intent = new Intent(getContext() , UserProfile.class);
                profile_intent.putExtra("email", mUsersList.get(i).getEmail());
                startActivity(profile_intent);

            }
        });

    }
}