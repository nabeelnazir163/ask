package com.example.nabeel.postandcommenttutorial.ui.activities;


import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.ui.adapter.ViewPagerAdapter;
import com.example.nabeel.postandcommenttutorial.ui.activities.RegisterActivities.RegisterActivity;
import com.example.nabeel.postandcommenttutorial.ui.fragments.AppInfo;
import com.example.nabeel.postandcommenttutorial.ui.fragments.Followers;
import com.example.nabeel.postandcommenttutorial.ui.fragments.bookmarkFragment;
import com.example.nabeel.postandcommenttutorial.ui.fragments.homeFragment;
import com.example.nabeel.postandcommenttutorial.ui.fragments.unAnsweredFragment;
import com.example.nabeel.postandcommenttutorial.utils.BaseActivity;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private ValueEventListener mUserValueEventListener;
    private DatabaseReference mUserRef;
    private Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    int userType;
    SharedPreferences userType_sp;

    DatabaseReference mDatabase;

    Menu mMenu;

 /*
    NotificationManager notificationManager;
    NotificationBadge mBadge;
    int Count = 0;*/


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                /*if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginintent =  new Intent(MainActivity.this, RegisterActivity.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }*/
            }
        };

/*
        if(mAuth.getCurrentUser() != null) {

            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".", ",")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild("image")) {

                    }else{


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }*/
        mDatabase = FirebaseDatabase.getInstance().getReference();


        userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        FirebaseUtils.getPostRef().keepSynced(true);

        if(mAuth.getCurrentUser() != null) {

            userType = userType_sp.getInt("UserType", 0);

        }


        viewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new homeFragment(), "home");

        viewPagerAdapter.addFragments(new bookmarkFragment(), "Bookmark");

        if(userType == 1) {
            viewPagerAdapter.addFragments(new unAnsweredFragment(), "Unanswer");
        }

        if(userType == 2) {
           // viewPagerAdapter.addFragments(new NotificationFrag(), "Notification");

        }


        viewPagerAdapter.addFragments(new Followers(), "Follower");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setTabTextColors(Color.BLACK , Color.WHITE);

        /*mBadge = (NotificationBadge)findViewById(R.id.badge);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        findViewById(R.id.count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Count++;

                Notification notification = new NotificationCompat.Builder(getBaseContext())
                        .setContentTitle("Ask App")
                        .setContentText(Count +" Azan Notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();

                notificationManager.notify(0,notification);

                mBadge.setNumber(Count);

                ImageView notification_iv = (ImageView)findViewById(R.id.notification_home);
                notification_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBadge.clear();
                        Count = 0;
                    }
                });

            }
        });

*/
       /* findViewById(R.id.set_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                */


/*
            }
        });*/

       /* findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notification notification = new NotificationCompat.Builder(getBaseContext())
                        .setContentTitle("Ask App")
                        .setContentText("Azan Notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();

                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            }
        });*/


       Boolean isFirtR = getSharedPreferences("firstRunPref",  MODE_PRIVATE).getBoolean("isFrtR", true);

       if(isFirtR){

           final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Widget_PopupWindow);
           dialog.setContentView(R.layout.dialogfirstruninstructions);
           WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

           lp.copyFrom(dialog.getWindow().getAttributes());
           lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
           lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

           dialog.setCancelable(true);
           dialog.show();
           dialog.getWindow().setAttributes(lp);

           final CheckBox dont_show = (CheckBox) dialog.findViewById(R.id.dont_show_checkbox);
           Button close = (Button) dialog.findViewById(R.id.ins_close_b);

           close.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(dont_show.isChecked()){

                       getSharedPreferences("firstRunPref",  MODE_PRIVATE).edit().putBoolean("isFrtR", false).commit();

                   }

                   dialog.dismiss();
               }
           });
       }


        init();
        /*getSupportFragmentManager().beginTransaction().replace(R.id.container, new homeFragment())
                .commit();*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        initNavHeader(navHeaderView);

    }

    private void init() {
        if (mFirebaseUser != null) {
            mUserRef = FirebaseUtils.getUserRef(mFirebaseUser.getEmail().replace(".", ","));
        }
    }

    private void initNavHeader(View view) {
        mDisplayImageView = (ImageView) view.findViewById(R.id.imageView_display);
        mNameTextView = (TextView) view.findViewById(R.id.textview_name);
        mEmailTextView = (TextView) view.findViewById(R.id.textView_email);

        mUserValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String email = null;
                String name = (String) dataSnapshot.child("name").getValue();
                String photo_url = (String) dataSnapshot.child("image").getValue();
                
                if(mAuth.getCurrentUser() != null){

                    email = mAuth.getCurrentUser().getEmail();
                    
                }

                Glide.with(getApplicationContext()).load(photo_url).into(mDisplayImageView);

                mNameTextView.setText(name);
                mEmailTextView.setText(email);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        mMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem mi = mMenu.findItem(R.id.action_search);
        /*if(mi.isActionViewExpanded()){
            mi.collapseActionView();
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchview = (SearchView) menu.findItem(R.id.action_search).getActionView();

//        ImageView closeButton = (ImageView) mSearchview.findViewById(R.id.search_close_btn);

        if (null != mSearchview) {
            mSearchview.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            mSearchview.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // This is your adapter that will be filtered

                if (!newText.equals("")) {

                    Query query = mDatabase.child("users").orderByChild("name").startAt(newText).endAt(newText + "\uf8ff");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                    //Toast.makeText(getApplicationContext(), issue.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();

                                    ArrayList<String> arrayList = new ArrayList<>();
                                    arrayList.add(issue.child("name").getValue().toString());


                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                return true;
            }

            public boolean onQueryTextSubmit(String text) {
                // **Here you can get the value "query" which is entered in the search box.**
                if (!text.equals("")) {

                    Query query = mDatabase.child("users").orderByChild("name").startAt(text).endAt(text + "\uf8ff");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                    Toast.makeText(getApplicationContext(), issue.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                return true;
            }
        };
        mSearchview.setOnQueryTextListener(queryTextListener);
*/
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.notification){

            startActivity(new Intent(MainActivity.this , Notification.class));

        } else if(id == R.id.action_search){

            startActivity(new Intent(MainActivity.this , SearchActivity.class));


        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle xnavigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            homeFragment home = new homeFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.container, home , home.getTag()).commit();

           viewPager.setCurrentItem(0);
        } else if (id == R.id.myaccount) {

            String email_user = FirebaseUtils.getCurrentUser().getEmail();

            Intent profile_intent = new Intent(MainActivity.this , UserProfile.class);
            profile_intent.putExtra("email" , email_user);
            startActivity(profile_intent);


        }  else if(id == R.id.bookmark){

            Intent bookmark_intent = new Intent(MainActivity.this, BookMark.class);
            startActivity(bookmark_intent);

        } else if( id == R.id.logout){

            mAuth.signOut();
            startActivity(new Intent(MainActivity.this , RegisterActivity.class));
            finish();

        } else if ( id == R.id.prayertime){

            Intent main_intent = new Intent(MainActivity.this , Prayer_Time_Activity.class);
            startActivity(main_intent);

        } else if(id == R.id.appinfo){

            Intent appinfo_intent = new Intent (MainActivity.this , AppInfo.class);
            startActivity(appinfo_intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        if (mUserRef != null) {
            mUserRef.addValueEventListener(mUserValueEventListener);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
        if (mUserRef != null)
            mUserRef.removeEventListener(mUserValueEventListener);
    }


    /*public void setRole(){

        // get value from sharedprefrence


        // if value is 2

        // get id of any view and hide it




    }*/


}