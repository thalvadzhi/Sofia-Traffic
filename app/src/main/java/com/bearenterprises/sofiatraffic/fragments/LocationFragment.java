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
import android.widget.Spinner;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.LineNamesAdapter;
import com.bearenterprises.sofiatraffic.adapters.TransportationTypeAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.second.Route;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Transport;
import com.bearenterprises.sofiatraffic.restClient.second.Routes;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class LocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private MainActivity activity;
    private ArrayList<Station> mStations;
    private List<String> transportationTypes;
    private Spinner transportationType;
    private Spinner lineId;
    private LineNamesAdapter lineNamesAdapter;
    private ArrayList<String> lineNames;
    private ArrayList<Transport> lines;
    private int currentlySelectedType;
    private Location location;
//    private ArrayList<ArrayList<Station>> routes;
    public LocationFragment() {
        // Required empty public constructor
    }


    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    public List<String> getTypesOfTransportation(){
        return new ArrayList<>(Arrays.asList("----","Трамвай", "Автобус", "Тройлей", "Около мен"));
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

        lines = new ArrayList<>();
        transportationTypes = getTypesOfTransportation();
        TransportationTypeAdapter transportationTypeAdapter = new TransportationTypeAdapter(getContext(), transportationTypes);
        transportationType.setAdapter(transportationTypeAdapter);
        lineNames = new ArrayList<>();

        lineNamesAdapter = new LineNamesAdapter(getContext(), lines);
        lineId.setAdapter(lineNamesAdapter);


        transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getSelectedItem();
                if (!selection.equals(transportationTypes.get(0)) && !selection.equals(transportationTypes.get(4))) {
                    currentlySelectedType = transportationTypes.indexOf(selection);
                    currentlySelectedType -= 1;
                    LineGetter lineGetter = new LineGetter();
                    lineGetter.execute(currentlySelectedType);
//                    lineId.setEnabled(true);
                }else if(selection.equals(transportationTypes.get(4))){
                    transportationType.setSelection(0);
                    lineId.setEnabled(false);
                    StationGetter locationGetter = new StationGetter((MainActivity) getActivity());
                    locationGetter.execute();

                }else{
                    lineId.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        transportationType.setSelection(0);
        lineId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String lineType = (String)transportationType.getSelectedItem();
                int idx = transportationTypes.indexOf(lineType);
                idx -= 1;
                Transport transport = (Transport) lineId.getSelectedItem();
                if (!(transport.getName().equals(Constants.LINE_ID_DEFAULT))){
                    String id = Integer.toString(transport.getId());
                    RouteGetter getter = new RouteGetter();
                    getter.execute(Integer.toString(idx), id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            ((MainActivity)getActivity()).changeFragment(R.id.location_container, f);

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

    private class StationGetter extends AsyncTask<Void, Void, ArrayList<Station>>{
        private MainActivity activity;

        public StationGetter(MainActivity activity){
            this.activity = activity;
        }

        @Override
        protected void onPreExecute(){
            LoadingFragment l = LoadingFragment.newInstance();
            getActivity().getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.location_container, l).
                    commit();
        }


        private Location getLocation(){
            Location loc;
            long startTime = System.currentTimeMillis();
            long endTime;
            long lastUpdated;
            while(true){
                loc = activity.tracker.getLocation();

                lastUpdated = activity.tracker.getLastUpdateTime();
                if(loc != null){

                    if((System.currentTimeMillis() - lastUpdated) < Constants.FIVE_SECONDS_MS && loc.getAccuracy() <= Constants.MINIMUM_ACCURACY){
                        location = loc;
                        break;
                    }
                }
                endTime = System.currentTimeMillis();
                if(endTime - startTime > Constants.MAXIMUM_TIME_GPS_LOCK){
                    ((MainActivity)getActivity()).makeSnackbar("Определянето на местоположението отнема твърде много време.");
                    return null;
                }
            }
            return loc;
        }


        @Override
        protected ArrayList<Station> doInBackground(Void... voids) {
            Location loc = getLocation();
            if (loc != null){
                StationsLocator locator = new StationsLocator(loc, 10, 1000, getContext());
                ArrayList<Station> closestStations = locator.getClosestStations();
                return closestStations;
            }
            return null;
//            return getLocation();
        }

        @Override
        protected void onPostExecute(ArrayList<Station> stations){
            if (stations != null && stations.size() != 0){
                //this is so as to be compatible with route fragment
                ArrayList<ArrayList<Station>> stationsGroup = new ArrayList<>();
                stationsGroup.add(stations);

                RoutesFragment locationResults = RoutesFragment.newInstance(stationsGroup, null);
                ((MainActivity)getActivity()).changeFragment(R.id.location_container, locationResults);
            }else{
                ((MainActivity) getActivity()).makeSnackbar("Няма спирки в радиус от 200 метра");
            }
        }



    }


}
