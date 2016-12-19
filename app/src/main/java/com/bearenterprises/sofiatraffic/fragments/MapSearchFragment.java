package com.bearenterprises.sofiatraffic.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;

public class MapSearchFragment extends android.support.v4.app.Fragment {

    public MapSearchFragment() {
        // Required empty public constructor
    }

    private MapFragment mapFragment;

    public MapFragment getMapFragment(){
        return mapFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_search, container, false);
        mapFragment = MapFragment.newInstance(null, null);
        ((MainActivity)getActivity()).changeFragment(R.id.mapContainer, mapFragment);
        PlacesFragment placesFragment = new PlacesFragment();
        ((MainActivity)getActivity()).changeFragment(R.id.placeSearchBarContainer, placesFragment);

        return v;
    }

}
