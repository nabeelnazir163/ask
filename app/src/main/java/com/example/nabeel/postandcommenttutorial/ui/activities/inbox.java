package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class inbox extends AppCompatActivity {

//    private FirebaseRecyclerAdapter<Post, PostHolder> inboxAdapter;
//
//    private RecyclerView mInboxRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Inbox");
    }
}
