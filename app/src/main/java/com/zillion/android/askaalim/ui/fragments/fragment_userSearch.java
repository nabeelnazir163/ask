package com.zillion.android.askaalim.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.User;
import com.zillion.android.askaalim.ui.activities.UserProfile;
import com.zillion.android.askaalim.ui.activities.viewallsearchesForUser;
import com.zillion.android.askaalim.ui.adapter.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class fragment_userSearch extends Fragment {

    private ListView mListview;
    public static EditText mSearchParams;
    ImageView backbutton;
//    private TextView noSearchFound;

    // vars
    private ArrayList<User> mUsersList;
    private UserListAdapter mAdapter;

    View mRootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_usersearch, container, false);

        mListview = (ListView) mRootview.findViewById(R.id.listview_search_Activity_user);
        mSearchParams = (EditText) mRootview.findViewById(R.id.search);
        backbutton = (ImageView) mRootview.findViewById(R.id.back_button);
//        noSearchFound = mRootview.findViewById(R.id.noUserfound);

        mUsersList = new ArrayList<>();
        mAdapter = new UserListAdapter(getContext() , R.layout.layout_user_listenitem, mUsersList);

        hideSoftkeyboard();
        initTextListener();

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return mRootview;
    }

    private void hideSoftkeyboard(){

        if( getActivity().getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        }

    }

    private void updateUsersListview(){

        mAdapter.notifyDataSetChanged();
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

//        mUsersList.clear();

        mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUsersList.clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                backbutton.setVisibility(View.VISIBLE);
               /* String text = mSearchParams.getText().toString();
                if(!TextUtils.isEmpty(text)){

                    searchforMatch(text);

                } else if( TextUtils.isEmpty(text)){

                    mUsersList.clear();

                }
*/
//               mListview.setVisibility(View.VISIBLE);
//               noSearchFound.setVisibility(View.GONE);


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                }
            }
        });

    }

    private void setAdapter(final String s) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.orderByChild("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

                    String email = singlesnapshot.child("email").getValue().toString().toLowerCase();
                    String name = singlesnapshot.child("name").getValue().toString().toLowerCase();

                    if (email.contains(s)) {

//                        Toast.makeText(getContext(), "No user" + mUsersList.size(), Toast.LENGTH_SHORT).show();
                        mUsersList.add(singlesnapshot.getValue(User.class));
                        updateUsersListview();


                    } else if( name.contains(s)){

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

//    private void searchforMatch(String keyword){
//
//        mUsersList.clear();
//
//        if(keyword.length() != 0)
//         {
//
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//            Query query = reference.child("users").orderByChild("email").startAt(keyword).endAt(keyword + "\uf8ff").limitToFirst(5);
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){
//
//                        mUsersList.add(singlesnapshot.getValue(User.class));
//                        updateUsersListview();
//
//                        mSearchParams.setOnKeyListener(new View.OnKeyListener() {
//                            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                                // If the event is a key-down event on the "enter" button
//                                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                                    // Perform action on key press
//
//                                    Intent alluserSearchIntent = new Intent(getContext(), viewallsearchesForUser.class);
//                                    alluserSearchIntent.putExtra("textTomatch" , mSearchParams.getText().toString().trim().toLowerCase() );
//                                    startActivity(alluserSearchIntent);
////                                    Toast.makeText(getContext(), "User", Toast.LENGTH_SHORT).show();
//
//                                    return true;
//                                }
//                                return false;
//                            }
//                        });
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        } else {
//
//            mUsersList.clear();
//
//        }
//
//    }
}
