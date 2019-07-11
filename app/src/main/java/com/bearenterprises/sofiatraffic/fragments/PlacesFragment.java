package com.bearenterprises.sofiatraffic.fragments;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.libraries.places.compat.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {
        // Required empty public constructor
    }
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;
    private ImageButton button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Bounding box of Sofia
        LatLngBounds sofiaBounds = new LatLngBounds(new LatLng(42.6227,23.2108), new LatLng(42.7587, 23.4173));

        View v = inflater.inflate(R.layout.fragment_places, container, false);
        placeAutocompleteFragment = new SupportPlaceAutocompleteFragment();
        placeAutocompleteFragment.setBoundsBias(sofiaBounds);
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
                if(closestStations == null){
                    return;
                }
                closestStations.add(new Stop(-1, (String)place.getName(), Double.toString(latLng.latitude), Double.toString(latLng.longitude)));
                if(closestStations.size() != 0){
                    CommunicationUtility.showOnMap(closestStations, false, (MainActivity)getActivity());
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
