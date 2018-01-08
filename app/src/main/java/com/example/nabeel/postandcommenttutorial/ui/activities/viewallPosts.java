package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.example.nabeel.postandcommenttutorial.ui.adapter.PostListadapter;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewallPosts extends BaseActivity {

    private ListView mListview;

    private List<Post> mUsersList;
    private PostListadapter mAdapter;

    String textToSearch;

    int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewall_posts);

        mListview = (ListView) findViewById(R.id.listview_search_Activity_post_allPosts);
        mUsersList = new ArrayList<>();

        SharedPreferences userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        userType = userType_sp.getInt("UserType", 0);

        textToSearch = getIntent().getExtras().getString("textTomatch").toString();

        hideSoftkeyboard();
        searchforMatch(textToSearch);
    }

    private void hideSoftkeyboard(){

        if( getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
    }

    private void searchforMatch(String keyword){

        mUsersList.clear();

        if(keyword.length() != 0)
         {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("posts").orderByChild("postText").startAt(keyword).endAt(keyword + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){

//                        Toast.makeText(getContext(), singlesnapshot.child("postText").getValue(Post.class).toString(), Toast.LENGTH_SHORT).show();
//                        mUsersList.clear();
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

        mAdapter = new PostListadapter(viewallPosts.this , R.layout.layout_post_listenitem, mUsersList);

        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(viewallPosts.this, PostActivity.class);
                intent.putExtra(Constants.EXTRA_POST, model);
                startActivity(intent);

            }
        });

    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        if(userType == 3){

            mAuth.signOut();
            startActivity(new Intent(viewallPosts.this , RegisterActivity.class));
            finish();

        }
    }*/

}
