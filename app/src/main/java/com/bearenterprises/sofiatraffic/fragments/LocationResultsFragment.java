package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.ClosestStationsAdapter;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;


public class LocationResultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String STATIONS = "param1";

    // TODO: Rename and change types of parameters
    private ArrayList<Station> stations;
    private MainActivity activity;


    public LocationResultsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LocationResultsFragment newInstance(ArrayList<Station> stations) {
        LocationResultsFragment fragment = new LocationResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(STATIONS, stations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stations = (ArrayList<Station>)getArguments().getSerializable(STATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_results, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewClosestStations);
        ClosestStationsAdapter adapter = new ClosestStationsAdapter(this.stations, getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        return v;
    }

}
