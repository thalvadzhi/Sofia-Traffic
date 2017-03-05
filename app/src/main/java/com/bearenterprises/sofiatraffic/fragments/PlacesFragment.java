package com.bearenterprises.sofiatraffic.fragments;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
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
        Utility.changeFragment(R.id.places_search_container, placeAutocompleteFragment, (MainActivity)getActivity());
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                StationsLocator locator = new StationsLocator(loc, 10, 1000, getContext());
                ArrayList<Stop> closestStations = locator.getClosestStations();
                closestStations.add(new Stop(-1, (String)place.getName(), Double.toString(latLng.latitude), Double.toString(latLng.longitude)));
                if(closestStations.size() != 0){
                    CommunicationUtility.showOnMap(closestStations, (MainActivity)getActivity());
                }else{
                    Utility.makeSnackbar("Няма спирки в близост до това място", (MainActivity)getActivity());
                }
            }

            @Override
            public void onError(Status status) {

            }
        });
        return v;
    }

}
