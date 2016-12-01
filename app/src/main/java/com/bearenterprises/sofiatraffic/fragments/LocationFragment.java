package com.bearenterprises.sofiatraffic.fragments;

import android.database.Cursor;
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
import com.bearenterprises.sofiatraffic.adapters.LineNamesAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.LineRoute;
import com.bearenterprises.sofiatraffic.restClient.second.Route;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Transport;
import com.bearenterprises.sofiatraffic.restClient.second.Routes;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
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
    private LineNamesAdapter lineNamesAdapter;
    private ArrayList<String> lineNames;
    private ArrayList<Transport> lines;
    private int currentlySelectedType;
//    private ArrayList<ArrayList<Station>> routes;
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
        lines = new ArrayList<>();
        transportationTypes = getTypesOfTransportation();

        ArrayAdapter<String> transportationTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, transportationTypes);
        transportationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportationType.setAdapter(transportationTypeAdapter);
        lineNames = new ArrayList<>();
//        lineNamesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lineNames);
//        lineNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineNamesAdapter = new LineNamesAdapter(getContext(), lines);
        lineId.setAdapter(lineNamesAdapter);




        transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getSelectedItem();
                if (!selection.equals("Вид транспорт")){
                    currentlySelectedType = transportationTypes.indexOf(selection);
                    currentlySelectedType -= 1;
                    LineGetter lineGetter = new LineGetter();
                    lineGetter.execute(currentlySelectedType);
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
//                Log.i("Selection", (String)lineId.getSelectedItem());
                String lineType = (String)transportationType.getSelectedItem();
                int idx = transportationTypes.indexOf(lineType);
                idx -= 1;
                String id = Integer.toString(((Transport)lineId.getSelectedItem()).getId());
                Log.i("id", id);
//                getRoutes(lineType, id);
                RouteGetter getter = new RouteGetter();
                getter.execute(Integer.toString(idx), id);

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
    private class RouteGetter extends AsyncTask<String, Void, Routes>{

        protected void onPreExecute(){
            LoadingFragment fragment = LoadingFragment.newInstance();
            getActivity().getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.location_container, fragment).
                    commit();
        }
        @Override
        protected Routes doInBackground(String... strings) {
            String lineType = strings[0];
            String lineId = strings[1];
            Log.i("GGGG", lineType + " " + lineId);
            SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
            Call<Routes> routes = sofiaTransportApi.getRoutes(lineType, lineId);
            try {
                Routes route = routes.execute().body();
                return route;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(Routes result) {
            RouteShower routeShower = new RouteShower();
            routeShower.execute(result);
        }
    }

    private class RouteShower extends AsyncTask<Routes, Void, Void>{

        @Override
        protected Void doInBackground(Routes... routes) {

            Routes result = routes[0];
            DbManipulator manipulator = new DbManipulator(getContext());

            ArrayList<ArrayList<Station>> stations = new ArrayList<>();
            String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE code=?";
            for(Route route : result.getRoutes()){
                ArrayList<Station> routeStations = new ArrayList<>();
                for (Stop stop : route.getStops()){
                    Cursor c = manipulator.readRawQuery(query, new String[]{Integer.toString(stop.getCode())});

                    c.moveToFirst();
                    String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                    String stationCode = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                    String latitude = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                    String longtitude = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
                    routeStations.add(new Station(stationName, stationCode, latitude, longtitude));
                }
                stations.add(routeStations);

            }
            manipulator.closeDb();
            String type = null;
            switch (currentlySelectedType){
                case 0:
                    type = Constants.TRAM;
                    break;
                case 1:
                    type = Constants.BUS;
                    break;
                case 2:
                    type = Constants.TROLLEY;
                    break;

            }
            RoutesFragment f = RoutesFragment.newInstance(stations, type);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.location_container, f).commit();

            return null;
        }
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

    private class LineGetter extends AsyncTask<Integer, Integer, List<Transport>>{

        @Override
        protected List<Transport> doInBackground(Integer... idxs) {
            SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
            Call<List<Transport>> lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
            try {
                List<Transport> transports = lines.execute().body();

                return transports;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<Transport> result) {
            if(result != null) {
                lines.clear();
                lines.addAll(result);
                lineNamesAdapter.notifyDataSetChanged();
                lineId.setEnabled(true);
            }else{
                ((MainActivity)getActivity()).makeSnackbar("Няма информация за този маршрут.");
            }
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
