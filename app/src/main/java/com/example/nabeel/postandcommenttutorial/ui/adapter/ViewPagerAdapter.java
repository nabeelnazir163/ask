package com.example.nabeel.postandcommenttutorial.ui.adapter;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tab_titles = new ArrayList<>();

    public void addFragments(Fragment fragments, String tab_titles){

        this.fragments.add(fragments);
        this.tab_titles.add(tab_titles);

    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
      /*  if (position ==0) {
            return new homeFragment();
        } else if (position == 1) {
            return new NotificationFrag();
        } else
            return new NewQuestions();*/
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles.get(position);
    }
}
