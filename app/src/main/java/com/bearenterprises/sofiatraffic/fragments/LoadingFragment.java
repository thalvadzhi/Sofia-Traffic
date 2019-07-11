package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bearenterprises.sofiatraffic.R;


public class LoadingFragment extends Fragment {
    public LoadingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }
}
