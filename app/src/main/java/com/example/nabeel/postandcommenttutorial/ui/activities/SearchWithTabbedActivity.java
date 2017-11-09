package com.example.nabeel.postandcommenttutorial.ui.activities;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.User;
import com.example.nabeel.postandcommenttutorial.ui.fragments.fragment_post_Search;
import com.example.nabeel.postandcommenttutorial.ui.fragments.fragment_userSearch;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.example.nabeel.postandcommenttutorial.utils.UserListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchWithTabbedActivity extends AppCompatActivity {

//    public static TextView mSearchParams;
    public static EditText mSearchParams;
    public static ImageView backbutton;


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_with_tabbed);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.hide();

        }

        mSearchParams = (EditText) findViewById(R.id.search);
        backbutton = (ImageView) findViewById(R.id.back_button);

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


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container_search);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    private void initTextListener(){

//        mUsersList = new ArrayList<>();

        mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                backbutton.setVisibility(View.VISIBLE);
                String text = mSearchParams.getText().toString().trim().toLowerCase();
//                searchforMatch(text);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                /*String text = mSearchParams.getText().toString().trim().toLowerCase();
                searchforMatch(text);*/

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_with_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search_with_tabbed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    private void hideSoftkeyboard(){

        if( getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){

                case 0:

                    /*fragment_userSearch userSearch = new fragment_userSearch();
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.container_search, userSearch , userSearch.getTag()).commit();*/

                    fragment_userSearch userSearch = new fragment_userSearch();
                    return userSearch;
                case 1:
                    fragment_post_Search post_search = new fragment_post_Search();
                    /*FragmentManager mana = getSupportFragmentManager();
                    mana.beginTransaction().replace(R.id.container_search, post_search , post_search.getTag()).commit();*/

                    return post_search;
            }
            return null;

        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
