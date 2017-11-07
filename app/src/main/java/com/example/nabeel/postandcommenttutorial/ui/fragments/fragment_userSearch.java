package com.example.nabeel.postandcommenttutorial.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nabeel.postandcommenttutorial.R;

/**
 * Created by Nabeel on 11/7/2017.
 */

public class fragment_userSearch extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postsearch, container, false);
    }
}
