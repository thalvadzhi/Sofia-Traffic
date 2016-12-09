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
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {
        // Required empty public constructor
    }
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private ImageButton button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        placeAutocompleteFragment = new PlaceAutocompleteFragment();
        ((MainActivity)getActivity()).changeFragmentNotSupport(R.id.places_search_container, placeAutocompleteFragment);
        button = (ImageButton) v.findViewById(R.id.set_place_button);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                StationsLocator locator = new StationsLocator(loc, 10, 1000, getContext());
                ArrayList<Station> closestStations = locator.getClosestStations();
                if(closestStations.size() != 0){
                    MapFragment f = MapFragment.newInstance(closestStations, null);
                    ((MainActivity)getActivity()).changeFragmentAddBackStack(R.id.location_container, f);
                }else{
                    ((MainActivity)getActivity()).makeSnackbar("Няма спирки в близост до това място");
                }
            }

            @Override
            public void onError(Status status) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment f = MapFragment.newInstance(null, null);
                ((MainActivity)getActivity()).changeFragmentAddBackStack(R.id.location_container, f);
            }
        });
        return v;
    }

}
