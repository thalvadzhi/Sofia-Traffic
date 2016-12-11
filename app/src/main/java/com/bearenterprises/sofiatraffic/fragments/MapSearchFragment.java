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
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MapSearchFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static MapSearchFragment newInstance(String param1, String param2) {
//        MapSearchFragment fragment = new MapSearchFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

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
