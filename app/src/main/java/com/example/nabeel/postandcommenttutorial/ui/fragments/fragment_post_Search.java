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
import android.widget.Toast;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.activities.PostActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.SearchWithTabbedActivity;
import com.example.nabeel.postandcommenttutorial.ui.activities.UserProfile;
import com.example.nabeel.postandcommenttutorial.ui.adapter.PostListadapter;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
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

    private ListView mListview;

    private List<Post> mUsersList;
    private PostListadapter mAdapter;

    View mRootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_postsearch, container , false);

//        mSearchParams = (EditText) mRootview.findViewById(R.id.search);
//        backbutton = (ImageView) mRootview.findViewById(R.id.back_button);
        mListview = (ListView) mRootview.findViewById(R.id.listview_search_Activity_post);

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

                /*String text = SearchWithTabbedActivity.mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);*/

            }
        });

    }

    private void searchforMatch(String keyword){

        mUsersList.clear();

        if(keyword.length() == 0){}
        else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("posts").orderByChild("postText").startAt(keyword).endAt(keyword + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

//                        Toast.makeText(getContext(), singlesnapshot.child("postText").getValue(Post.class).toString(), Toast.LENGTH_SHORT).show();

                        mUsersList.add(singlesnapshot.getValue(Post.class));
                        updateUsersListview(singlesnapshot.getValue(Post.class));

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void updateUsersListview(final Post model){

        mAdapter = new PostListadapter(getContext() , R.layout.layout_post_listenitem, mUsersList);

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getContext(), PostActivity.class);
                intent.putExtra(Constants.EXTRA_POST, model);
                startActivity(intent);

            }
        });

    }
}
