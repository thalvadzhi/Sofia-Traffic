package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PlacesFragment extends android.support.v4.app.Fragment {

    public PlacesFragment() {
        // Required empty public constructor
    }
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;
    private ImageButton button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        placeAutocompleteFragment = new SupportPlaceAutocompleteFragment();
        ((MainActivity)getActivity()).changeFragment(R.id.places_search_container, placeAutocompleteFragment);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                StationsLocator locator = new StationsLocator(loc, 10, 1000, getContext());
                ArrayList<Station> closestStations = locator.getClosestStations();
                closestStations.add(new Station((String)place.getName(), "", Double.toString(latLng.latitude), Double.toString(latLng.longitude)));
                if(closestStations.size() != 0){
                    ((MainActivity)getActivity()).showOnMap(closestStations);
                }else{
                    ((MainActivity)getActivity()).makeSnackbar("Няма спирки в близост до това място");
                }
            }

            @Override
            public void onError(Status status) {

            }
        });
        return v;
    }

}
