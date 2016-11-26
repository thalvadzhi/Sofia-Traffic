package com.bearenterprises.sofiatraffic.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;

public class LocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private MainActivity activity;
    private ArrayList<Station> mStations;
    private Location location;
    public LocationFragment() {
        // Required empty public constructor
    }


    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        mStations = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        Button locate = (Button) v.findViewById(R.id.buttonLocate);
        Button showOnMap = (Button) v.findViewById(R.id.buttonShowOnMap);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.tracker.startUpdatesButtonHandler();
                activity.changeFragmentLoading(R.id.location_container);
                StationsGetter stationsGetter = new StationsGetter(activity);
                stationsGetter.start();
            }
        });

        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.tracker.startUpdatesButtonHandler();
                MapShower shower = new MapShower(mStations, (MainActivity) getActivity());
                shower.start();
            }
        });
        return v;
    }

    private class MapShower extends Thread{
        private ArrayList<Station> stations;
        private MainActivity activity;
        public MapShower(ArrayList<Station> stations, MainActivity activity){
            this.stations = stations;
            this.activity = activity;
        }

        @Override
        public void run(){
            MapFragment f = MapFragment.newInstance(this.stations, location);
            this.activity.getSupportFragmentManager().
                    beginTransaction().
                    addToBackStack("MapFragment").
                    setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).
                    replace(R.id.location_container, f).
                    commit();
        }
    }

    private class StationsGetter extends Thread{
        private MainActivity activity;

        public StationsGetter(MainActivity activity){
            this.activity = activity;
        }



        private ArrayList<Station> getStations(){
            Location loc;
            while(true){
                loc = activity.tracker.getLocation();
                if(loc != null){
                    if(loc.getAccuracy() <= 40){ //Constants.MINIMUM_ACCURACY){
                        location = loc;
                        break;
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//        Log.i("Location!", loc.getLatitude()+" "+ loc.getLongitude());
            StationsLocator locator = new StationsLocator(loc, 5, 300000, getContext());
            ArrayList<Station> stations =  locator.getClosestStations();
            mStations = stations;
            return stations;
        }

        @Override
        public void run(){
            ArrayList<Station> stations = getStations();
//            this.activity.changeFragmentLocation(stations);
//            ArrayList<Station> stations = new ArrayList<>();
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
//            stations.add(new Station("NAME", "asd", "42", "23"));
            final LocationResultsFragment f = LocationResultsFragment.newInstance(stations);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.location_container, f).commit();
                    activity.tracker.stopUpdatesButtonHandler();
                }
            };
            Thread t = new Thread(r);
            t.start();

        }



    }










}
