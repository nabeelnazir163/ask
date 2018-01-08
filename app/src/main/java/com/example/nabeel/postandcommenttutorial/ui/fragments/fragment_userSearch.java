package com.example.nabeel.postandcommenttutorial.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.activities.SearchWithTabbedActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.UserProfile;
import com.example.nabeel.postandcommenttutorial.ui.activities.viewallsearchesForUser;
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

public class fragment_userSearch extends Fragment {

    private ListView mListview;

    // vars
    private List<User> mUsersList;
    private UserListAdapter mAdapter;

    View mRootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_usersearch, container, false);

        mListview = (ListView) mRootview.findViewById(R.id.listview_search_Activity_user);

        mUsersList = new ArrayList<>();
        mAdapter = new UserListAdapter(getContext() , R.layout.layout_user_listenitem, mUsersList);

        mAdapter.notifyDataSetChanged();

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

    private void updateUsersListview(){

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent profile_intent = new Intent(getContext() , UserProfile.class);
                profile_intent.putExtra("email", mUsersList.get(i).getEmail());
                getActivity().finish();
                startActivity(profile_intent);

            }
        });

    }

    private void initTextListener(){

        SearchWithTabbedActivity.mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                SearchWithTabbedActivity.backbutton.setVisibility(View.VISIBLE);
                String text = SearchWithTabbedActivity.mSearchParams.getText().toString().trim().toLowerCase();
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

        if(keyword.length() != 0)
         {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("users").orderByChild("email").startAt(keyword).endAt(keyword + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

//                        mUsersList.clear();
                        mUsersList.add(singlesnapshot.getValue(User.class));
                        updateUsersListview();

//                        SearchWithTabbedActivity.mSearchParams.setOnKeyListener(new View.OnKeyListener() {
//                            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                                // If the event is a key-down event on the "enter" button
//                                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                                    // Perform action on key press
//
//                                    Intent alluserSearchIntent = new Intent(getContext(), viewallsearchesForUser.class);
//                                    alluserSearchIntent.putExtra("textTomatch" , SearchWithTabbedActivity.mSearchParams.getText().toString().trim().toLowerCase() );
//                                    startActivity(alluserSearchIntent);
//
//                                    return true;
//                                }
//                                return false;
//                            }
//                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
