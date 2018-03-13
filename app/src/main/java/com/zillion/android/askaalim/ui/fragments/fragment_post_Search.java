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
import android.widget.Toast;

import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.Post;
import com.zillion.android.askaalim.models.User;
import com.zillion.android.askaalim.ui.activities.PostActivity;
import com.zillion.android.askaalim.ui.activities.SearchWithTabbedActivity;
import com.zillion.android.askaalim.ui.activities.viewallPosts;
import com.zillion.android.askaalim.ui.adapter.PostListadapter;
import com.zillion.android.askaalim.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
//import static com.zillion.android.askaalim.ui.activities.SearchWithTabbedActivity.backbutton;

public class fragment_post_Search extends Fragment {

    private ListView mListview;

    private ArrayList<Post> mPostList;
    private PostListadapter mAdapter;
    ImageView backbutton;

    public static EditText mSearchParams;

    View mRootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_postsearch, container , false);

        mSearchParams = (EditText) mRootview.findViewById(R.id.search);
        backbutton = (ImageView) mRootview.findViewById(R.id.back_button);

        mPostList = new ArrayList<>();

        mAdapter = new PostListadapter(getContext() , R.layout.layout_post_listenitem, mPostList);

//        mAdapter.notifyDataSetChanged();

        mListview = (ListView) mRootview.findViewById(R.id.listview_search_Activity_post);
        mSearchParams = (EditText) mRootview.findViewById(R.id.search);

        hideSoftkeyboard();
        initTextListenerPost();

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

    private void initTextListenerPost(){

        mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPostList.clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

/*
                backbutton.setVisibility(View.VISIBLE);
                String text = mSearchParams.getText().toString();

                if(!TextUtils.isEmpty(text)){

                    searchforMatchingPost(text);

                } else if( TextUtils.isEmpty(text)){

                    mPostList.clear();

                }
*/

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*String text = SearchWithTabbedActivity.mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);*/

                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                }

            }
        });

    }

    private void setAdapter(final String s) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts");

        reference.orderByChild("postText").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()) {

                    String postText = singlesnapshot.child("postText").getValue().toString().toLowerCase();

                    if (postText.contains(s)) {

                        mPostList.add(singlesnapshot.getValue(Post.class));
                        updatePostListview(singlesnapshot.getValue(Post.class));

                    }else if(!postText.contains(s) && mPostList.isEmpty()){

                        mPostList.clear();
//                        noSearchFound.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), "No post found", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    private void searchforMatchingPost(String keyword){
//
//        mPostList.clear();
//
//        if(keyword.length() != 0)
//         {
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//            Query query = reference.child("posts").orderByChild("postText").startAt(keyword).endAt(keyword + "\uf8ff").limitToFirst(5);
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()){
//
//                        mPostList.add(singlesnapshot.getValue(Post.class));
//                        updatePostListview(singlesnapshot.getValue(Post.class));
//
//                        mSearchParams.setOnKeyListener(new View.OnKeyListener() {
//                            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                                // If the event is a key-down event on the "enter" button
//                                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                                    // Perform action on key press
//
//                                    Intent alluserSearchIntent = new Intent(getContext(), viewallPosts.class);
//                                    alluserSearchIntent.putExtra("textTomatch" , mSearchParams.getText().toString() );
//                                    startActivity(alluserSearchIntent);
//
////                                    Toast.makeText(getContext(), "post", Toast.LENGTH_SHORT).show();
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
//            mPostList.clear();
//
//        }
//
//    }

    private void updatePostListview(final Post model){

        mAdapter.notifyDataSetChanged();
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
