package com.bearenterprises.sofiatraffic.fragments;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Transport;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.mohan.location.locationtrack.LocationProvider;
import com.mohan.location.locationtrack.LocationSettings;
import com.mohan.location.locationtrack.LocationTrack;
import com.mohan.location.locationtrack.LocationUpdateListener;
import com.mohan.location.locationtrack.Priority;
import com.mohan.location.locationtrack.providers.FusedLocationProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;

public class LocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private MainActivity activity;
    private ArrayList<Station> mStations;
    private Location location;
    private List<String> transportationTypes;
    private Spinner transportationType;
    private Spinner lineId;
    private ArrayAdapter<String> lineNamesAdapter;
    private ArrayList<String> lineNames;
    public LocationFragment() {
        // Required empty public constructor
    }


    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    public List<String> getTypesOfTransportation(){
        return new ArrayList<>(Arrays.asList("Вид транспорт","Трамвай", "Автобус", "Тройлей"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        mStations = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        transportationType = (Spinner) v.findViewById(R.id.transportationType);
        lineId = (Spinner) v.findViewById(R.id.lineNumber);
        lineId.setEnabled(false);
//        transportationType.

        transportationTypes = getTypesOfTransportation();

        ArrayAdapter<String> transportationTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, transportationTypes);
        transportationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportationType.setAdapter(transportationTypeAdapter);
        lineNames = new ArrayList<>();
        lineNamesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lineNames);
        lineNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineId.setAdapter(lineNamesAdapter);




        transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getSelectedItem();
                if (!selection.equals("Вид транспорт")){
                    int idx = transportationTypes.indexOf(selection);
                    idx -= 1;
                    LineGetter lineGetter = new LineGetter();
                    lineGetter.execute(idx);
                }else{
                    lineId.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        transportationType.setSelection(0);
        final Button locate = (Button) v.findViewById(R.id.buttonLocate);
        Button showOnMap = (Button) v.findViewById(R.id.buttonShowOnMap);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Selection", (String)lineId.getSelectedItem());
//
//                activity.tracker.startUpdatesButtonHandler();
//                activity.changeFragmentLoading(R.id.location_container);
//                StationsGetter stationsGetter = new StationsGetter(activity);
//                stationsGetter.start();
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

    private class LineGetter extends AsyncTask<Integer, Integer, List<String>>{

        @Override
        protected List<String> doInBackground(Integer... idxs) {
            SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
            Call<List<Transport>> lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
            try {
                List<Transport> transports = lines.execute().body();
                List<String> lineNames = new ArrayList<>();
                for (Transport tr : transports){
                    lineNames.add(tr.getName());
                }
                return lineNames;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<String> result) {
            lineNames.clear();
            lineNames.addAll(result);
            lineNamesAdapter.notifyDataSetChanged();
            lineId.setEnabled(true);
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
        Log.i("Location!", loc.getLatitude()+" "+ loc.getLongitude());
//            StationsLocator locator = new StationsLocator(loc, 5, 300000, getContext());
//            ArrayList<Station> stations =  locator.getClosestStations();
//            mStations = stations;
            return new ArrayList<>();//stations;
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
//                    activity.tracker.stopUpdatesButtonHandler();
                }
            };
            Thread t = new Thread(r);
            t.start();

        }



    }










}
