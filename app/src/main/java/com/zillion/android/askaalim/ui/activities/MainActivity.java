package com.zillion.android.askaalim.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
//import com.nex3z.notificationbadge.NotificationBadge;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zillion.android.askaalim.BootCompleteReceiver;
import com.zillion.android.askaalim.MyFirebaseMessagingService;
import com.zillion.android.askaalim.Premium_Activity;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.ui.adapter.ViewPagerAdapter;
import com.zillion.android.askaalim.ui.activities.RegisterActivities.RegisterActivity;
import com.zillion.android.askaalim.ui.fragments.Followers;
import com.zillion.android.askaalim.ui.fragments.bookmarkFragment;
import com.zillion.android.askaalim.ui.fragments.homeFragment;
import com.zillion.android.askaalim.ui.fragments.unAnsweredFragment;
import com.zillion.android.askaalim.utils.BaseActivity;
import com.zillion.android.askaalim.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler {


    private BillingProcessor bp;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private ValueEventListener mUserValueEventListener;
    private DatabaseReference mUserRef;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    int userType;
    SharedPreferences userType_sp;

    DatabaseReference mDatabase;

    NavigationView nv;
    RelativeLayout notificationCount1;

    public static int cou = 0;
    Menu mMenu;

    boolean doubleBackToExitPressedOnce = false;

    private static int MY_PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static int MY_PERMISSION_ACCESS_FINE_LOCATION = 0;

    public static final String HADEES_PREFS = "hadeesPrefs";
    public static final String CAL_PREFS = "calPrefs";
    public static final String PRAYER_TIME_PREFS = "prayertimePrefs";
    public static final String UNANSWERED_QUES_PREFS = "unAnsweredQuestionPrefs";

    SharedPreferences hadeesPrefs , calPrefs , prayerTimePrefs, unAnsweredQuestionPrefs;

    SharedPreferences.Editor Hadeeseditor , CalEditor , PrayerTimeEditor , unAnsQuesEditor;

    public static TextView messagetv;
    public static TextView notification_tv;

    String photo_url;

    //Alert dialog to confirm for inAPppurchase
    AlertDialog.Builder purchaseConfirm;

    //product ID ( in app purchase )
    String product_id = "ask_aalim.premium_account_com.zillion.android.askaalim";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bp = new BillingProcessor(this,getLiscence(),this);

        notificationCount1 = (RelativeLayout) findViewById(R.id.content_main);

        getPermissions();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Starting Background service
        startService(new Intent(getBaseContext(), MyFirebaseMessagingService.class));
        ComponentName rec = new ComponentName(MainActivity.this, BootCompleteReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(rec, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginintent =  new Intent(MainActivity.this, RegisterActivity.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
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

//        FirebaseMessaging.getInstance().subscribeToTopic("ABC");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userType_sp = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        FirebaseUtils.getPostRef().keepSynced(true);

        if(mAuth.getCurrentUser() != null) {

            userType = userType_sp.getInt("UserType", 0);

        }


        viewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new homeFragment(), "Home");

        if(userType == 1) {
            viewPagerAdapter.addFragments(new bookmarkFragment(), "Bookmarks");
            viewPagerAdapter.addFragments(new unAnsweredFragment(), "Unanswered");
            viewPagerAdapter.addFragments(new Followers(), "Following");


        } else if(userType == 2) {

            viewPagerAdapter.addFragments(new bookmarkFragment(), "Bookmarks");
           // viewPagerAdapter.addFragments(new NotificationFrag(), "Notification");
            viewPagerAdapter.addFragments(new Followers(), "Following");
            tabLayout.setTabMode(TabLayout.MODE_FIXED);

        } else if (userType == 3){

            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setTabTextColors(Color.BLACK , Color.WHITE);

        nv = (NavigationView) findViewById(R.id.nav_view);

        Menu nav_Menu = nv.getMenu();
        nav_Menu.findItem(R.id.myaccount).setVisible(false);


//        mBadge = (NotificationBadge)findViewById(R.id.badge);

//        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

       /* findViewById(R.id.count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Count++;

                Notification notification = new NotificationCompat.Builder(getBaseContext())
                        .setContentTitle("Ask App")
                        .setContentText(Count +" Azan Notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();

                notificationManager.notify(0,notification);


                ImageView notification_iv = (ImageView)findViewById(R.id.notification_home);
                notification_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBadge.clear();
                        Count = 0;
                    }
                });

            }
        });*/

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

           hadeesPrefs = getSharedPreferences(HADEES_PREFS, 0);
           Hadeeseditor = hadeesPrefs.edit();
           Hadeeseditor.putBoolean("hadees", true);
           Hadeeseditor.commit();

           calPrefs = getSharedPreferences(CAL_PREFS,0);
           CalEditor = calPrefs.edit();
           CalEditor.putBoolean("calender", true);
           CalEditor.commit();

           prayerTimePrefs = getSharedPreferences(PRAYER_TIME_PREFS,0);
           PrayerTimeEditor = prayerTimePrefs.edit();
           PrayerTimeEditor.putBoolean("prayertime", true);
           PrayerTimeEditor.commit();

           unAnsweredQuestionPrefs = getSharedPreferences(UNANSWERED_QUES_PREFS,0);
           unAnsQuesEditor = unAnsweredQuestionPrefs.edit();
           unAnsQuesEditor.putBoolean("unAnsweredQues", true);
           unAnsQuesEditor.commit();


           final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Animation);
           dialog.setContentView(R.layout.dialogfirstruninstructions);
           WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
           dialog.setTitle("Confirmation Dialog");

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

        if(userType != 3) {

            init();

       }

        /*getSupportFragmentManager().beginTransaction().replace(R.id.container, new homeFragment())
                .commit();*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        assert nv != null;
        nv.setNavigationItemSelectedListener(this);
        Menu menu = nv.getMenu();
        MenuItem Ask_Wazifa = menu.findItem(R.id.askWazaif);
        MenuItem Ask_Khuwab = menu.findItem(R.id.askkhuwab);
        MenuItem Ask_istekhara = menu.findItem(R.id.askistekhara);


        //Hide Premium Items For Now
        nav_Menu.findItem(R.id.askWazaif).setVisible(false);
        nav_Menu.findItem(R.id.askkhuwab).setVisible(false);
        nav_Menu.findItem(R.id.askistekhara).setVisible(false);

        if(userType == 1) {

            Ask_Wazifa.setIcon(R.mipmap.ic_star_border_black_24dp);
            Ask_istekhara.setIcon(R.mipmap.ic_star_border_black_24dp);
            Ask_Khuwab.setIcon(R.mipmap.ic_star_border_black_24dp);

        } else if(userType == 2){

            if(bp.isPurchased(product_id))
            {
                Ask_Wazifa .setIcon(R.mipmap.ic_lock_open_black_24dp);
                Ask_Khuwab .setIcon(R.mipmap.ic_lock_open_black_24dp);
                Ask_istekhara .setIcon(R.mipmap.ic_lock_open_black_24dp);
            } else
            {
                Ask_Wazifa.setIcon(R.mipmap.ic_lock_black_24dp);
                Ask_Khuwab.setIcon(R.mipmap.ic_lock_black_24dp);
                Ask_istekhara.setIcon(R.mipmap.ic_lock_black_24dp);
            }
        } else if(userType == 3) {

            Ask_Wazifa.setIcon(R.mipmap.ic_lock_black_24dp);
            Ask_Khuwab.setIcon(R.mipmap.ic_lock_black_24dp);
            Ask_istekhara.setIcon(R.mipmap.ic_lock_black_24dp);

        }


//        MenuItem nav_logoutItem = menu.findItem(R.id.nav_input_logout);
//        nav_logoutItem .setIcon(R.drawable.ic_nav_input_logout);

        View navHeaderView = nv.getHeaderView(0);
        initNavHeader(navHeaderView);

        if(userType == 3) {

            hideItemforGuest();

        }
    }

    public static void setValue(int val){
        cou = val;
        Log.d("counter_main", "" + cou);
//        mBadge.setNumber(cou);

    }

    public void getPermissions() {
        getCoarsePermission();
        getFinePermission();
    }
    public void getCoarsePermission(){
        //check if permission is granted
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            //should we show description?
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                /*
                DESCRIBE WHY THE PERMISSION IS REQUIRED
                 */
            }else{
                //NO EXPLANATION NEEDED

                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
        }
    }
    public void getFinePermission(){
        //check if permission is granted
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){

            //should we show description?
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                /*
                DESCRIBE WHY THE PERMISSION IS REQUIRED
                 */
            }else{
                //NO EXPLANATION NEEDED
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
        // other 'case' lines to check for other
        // permissions this app might request
    }


    private void hideItemforGuest()
    {
        Menu nav_Menu = nv.getMenu();
        nav_Menu.findItem(R.id.myaccount).setVisible(false);
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

        if(userType == 3){

            mNameTextView.setText("Guest User");
            mEmailTextView.setText(null);

        } else {

            mUserValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String email = null;
                    String name = (String) dataSnapshot.child("name").getValue();
                    if(dataSnapshot.hasChild("image")) {

                        photo_url = (String) dataSnapshot.child("image").getValue();
                        Glide.with(getApplicationContext()).load(photo_url).into(mDisplayImageView);

                    }
                    if (mAuth.getCurrentUser() != null) {

                        email = mAuth.getCurrentUser().getEmail();

                    }


                    mNameTextView.setText(name);
                    mEmailTextView.setText(email);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
//            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
//            alert.setTitle("ASK ALIM");
//            alert.setMessage("Do you want exit?");
//            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                    System.exit(1);
//                }
//            });
//            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            alert.show();

                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press again to exit application", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items

//        if(userType == 2){
//
//            menu.findItem(R.id.chat_inbox).setVisible(true);
//
//        } else if (userType == 3){
//
//            menu.findItem(R.id.chat_inbox).setVisible(false);
////            menu.findItem(R.id.notification).setVisible(false);
//
//        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void goToInbox(View v){

        if(userType == 2){
            if(bp.isPurchased(product_id)){

                startActivity(new Intent(MainActivity.this, inbox.class));
                messagetv.setText(""+0);
                messagetv.setVisibility(View.GONE);

            } else {

                purchaseConfirmDialog();

            }
        } else if ( userType == 1){
            startActivity(new Intent(MainActivity.this, inbox.class));
            messagetv.setText(""+0);
            messagetv.setVisibility(View.GONE);
        } else if(userType == 3){

            Toast.makeText(MainActivity.this, "You have to login first in order to use this feature", Toast.LENGTH_SHORT).show();

        }

    }

    private void purchaseConfirmDialog(){

        purchaseConfirm = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.in_app_billing_dialog, null);
        purchaseConfirm.setView(alertView);

        final AlertDialog show_dialog = purchaseConfirm.show();

        TextView cancel_button = (TextView) alertView.findViewById(R.id.dont_purchase);
        TextView Ok_button = (TextView) alertView.findViewById(R.id.positive_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog.dismiss();
            }
        });

        Ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyPremium();
            }
        });

//        purchaseConfirm.show();

    }


    private void buyPremium() {

        bp.purchase(MainActivity.this, product_id);

    }


    public void goTosearch(View v){

        startActivity(new Intent(MainActivity.this , SearchWithTabbedActivity.class));

    }

    public void gotoNotification(View v){

        startActivity(new Intent(MainActivity.this, Notification.class));
        notification_tv.setText(""+0);
        notification_tv.setVisibility(View.GONE);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);


        MenuItem item1 = menu.findItem(R.id.action_search);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item1);

        messagetv = notificationCount1.findViewById(R.id.message_badge);
        messagetv.setVisibility(View.GONE);
        messagetv.setText(""+0);

        notification_tv = notificationCount1.findViewById(R.id.notification_badge);
        notification_tv.setVisibility(View.GONE);
        notification_tv.setText(""+0);

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*if(id == R.id.notification){

            startActivity(new Intent(MainActivity.this , Notification.class));

        } else*/ if(id == R.id.action_search){

            startActivity(new Intent(MainActivity.this , SearchWithTabbedActivity.class));

        }/* else if (id == R.id.chat_inbox){

            if(userType == 2){
                Toast.makeText(MainActivity.this, "You have to purchase our app in order to use this feature", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(MainActivity.this, inbox.class));
            }
        }*/
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

        } else if (id == R.id.rateus) {

            try{

                Uri uri1 = Uri.parse("market://details?id=" + getPackageName());
                Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(gotoMarket);

            }catch ( ActivityNotFoundException e){

                Uri uri1 = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(gotoMarket);

            }

        } else if( id == R.id.logout){

            if(userType != 3){

                FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                        .child("fcmtoken").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mAuth.signOut();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, "Unable to Signout", Toast.LENGTH_SHORT).show();
                    }
                });
            }else if(userType == 3){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this , RegisterActivity.class));
                            finish();
                        }
                    }
                });
            }

        } else if ( id == R.id.prayertime){

            Intent main_intent = new Intent(MainActivity.this , Prayer_Time_Activity.class);
            startActivity(main_intent);

        } else if(id == R.id.appinfo){

            Intent appinfo_intent = new Intent (MainActivity.this , AppInfo.class);
            startActivity(appinfo_intent);

        }else if( id == R.id.qibladirection){

//            mAuth.signOut();
            startActivity(new Intent(MainActivity.this , QiblaDirection.class));
        } else if ( id == R.id.premium){

            if(userType == 3){

                Toast.makeText(MainActivity.this, "You have to login first in order to use this feature", Toast.LENGTH_SHORT).show();

            } else if(userType == 2){

                if(!bp.isPurchased("ask_aalim.premium_account_com.zillion.android.askalim")){

                   // Toast.makeText(MainActivity.this, "Purchased", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this, Premium_Activity.class));

                } else {

                    buyPremium();
                }

            } else if(userType == 1){

                Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                main_intent.putExtra("askFor", "wazifa");
                startActivity(main_intent);

            }

        } else if ( id == R.id.askWazaif){

            if(userType == 3){

                Toast.makeText(MainActivity.this, "You have to login first in order to use this feature", Toast.LENGTH_SHORT).show();

            } else if(userType == 2){

                if(bp.isPurchased("ask_aalim.premium_account_com.zillion.android.askalim")){

                    Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                    main_intent.putExtra("askFor", "wazifa");
                    startActivity(main_intent);

                } else {

                    purchaseConfirmDialog();

                }

            } else if(userType == 1){

                Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                main_intent.putExtra("askFor", "wazifa");
                startActivity(main_intent);

            }

        } else if ( id == R.id.askkhuwab){

            if(userType == 3){

                Toast.makeText(MainActivity.this, "You have to login first in order to use this feature", Toast.LENGTH_SHORT).show();

            } else if(userType == 2){

                if(bp.isPurchased("ask_aalim.premium_account_com.zillion.android.askalim")){

                    Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                    main_intent.putExtra("askFor", "khuwab");
                    startActivity(main_intent);

                } else {

                    purchaseConfirmDialog();

                }

            } else if(userType == 1){

                Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                main_intent.putExtra("askFor", "khuwab");
                startActivity(main_intent);

            }

        } else if ( id == R.id.askistekhara){

            if(userType == 3){

                Toast.makeText(MainActivity.this, "You have to login first in order to use this feature", Toast.LENGTH_SHORT).show();

            } else if(userType == 2){

                if(bp.isPurchased("ask_aalim.premium_account_com.zillion.android.askalim")){

                    Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                    main_intent.putExtra("askFor", "istekhara");
                    startActivity(main_intent);

                } else {

                    purchaseConfirmDialog();

                }

            } else if(userType == 1){

                Intent main_intent = new Intent(MainActivity.this , search_user_for_newMessage.class);
                main_intent.putExtra("askFor", "istekhara");
                startActivity(main_intent);

            }

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


    /**
     * Methods of Billing Processor
     */

    @Override
    public void onBillingInitialized() {
    /*
    * Called when BillingProcessor was initialized and it's ready to purchase
    */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(this, "YOU ARE NOW PREMIUM!!!", Toast.LENGTH_SHORT).show();
        //ADD PREMIUM TAG TO THE DATABASE TO THE USER PROFILE
        //SET SHARED PREFERENCE AND ENABLE THE CHAT/MESSAGING FEATURE
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "UNABLE TO REQUEST PROCESS!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    /*
    * Called when purchase history was restored and the list of all owned PRODUCT ID's
    * was loaded from Google Play
    */
    //NOT OF OUR USE!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getLiscence(){
        String lKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArfANYpKd02pN7Cjmb47wUNNs/AhfLtD1vBLLAy3+JniAqdcL6Js0UvobdRmSqwaejxX6rzjO2+ibF9XoDrsC67E2U+AhIRYD6HCaUyGg+suQSqhNK3L0mERHSbCzHnpdA3w8aQy+UmaeCtglh+beEGJCR6hqEWmO62GA/XdE82uehlW89ccJPaqNCzcTg3SZyJ97xlo6nswWNunpGldkU6ZX9PZsnKzCLO+B6T1HVYS2t5LfL29NCTKVJRxIXKpnaW3gMVOFTvsHmX27EDkwWASgVkc/98OfkpHG8K0JPWdCQLNcpB+Yx6gOCrgVb8j5pbf+rPIhyHHh4rIJOAnnLwIDAQAB";
        return lKey;
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}