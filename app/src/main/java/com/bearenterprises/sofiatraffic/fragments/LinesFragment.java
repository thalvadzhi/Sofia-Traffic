package com.bearenterprises.sofiatraffic.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;
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
import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.restClient.second.Route;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.second.Routes;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.ParseApiError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class LinesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private MainActivity activity;
    private ArrayList<Stop> mStations;
    private List<String> transportationTypes;
    private Spinner transportationType;
    private Spinner lineId;
    private LineNamesAdapter lineNamesAdapter;
    private ArrayList<String> lineNames;
    private ArrayList<Line> lines;
    private int currentlySelectedType;
    private Location location;
    private LoadingFragment loadingFragment;
    private PlacesFragment placesFragment;
//    private ArrayList<ArrayList<Station>> routes;
    private boolean dontActivateListener;
    public LinesFragment() {
        // Required empty public constructor
    }


    public static LinesFragment newInstance() {
        LinesFragment fragment = new LinesFragment();
        return fragment;
    }

    public List<String> getTypesOfTransportation(){
        List<String> types = Arrays.asList("----", "Трамвай", "Автобус", "Тролей");
//        types.addAll(Constants.TRANSPORTATION_TYPES);
        return types;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
            loadingFragment = LoadingFragment.newInstance();
//        }
        dontActivateListener = false;

        activity = (MainActivity) getActivity();
        mStations = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        transportationType = (Spinner) v.findViewById(R.id.transportationType);
        lineId = (Spinner) v.findViewById(R.id.lineNumber);
        lineId.setEnabled(false);

        lines = new ArrayList<>();
        transportationTypes = getTypesOfTransportation();
        final TransportationTypeAdapter transportationTypeAdapter = new TransportationTypeAdapter(getContext(), transportationTypes);
        transportationType.setAdapter(transportationTypeAdapter);
        lineNames = new ArrayList<>();

        lineNamesAdapter = new LineNamesAdapter(getContext(), lines);
        lineId.setAdapter(lineNamesAdapter);

        placesFragment = new PlacesFragment();


        transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getSelectedItem();
                int selectionIdx = transportationTypes.indexOf(selection);
                if (selectionIdx >= 1 && selectionIdx <= 3) {
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

        lineId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String lineType = (String)transportationType.getSelectedItem();
                int idx = transportationTypes.indexOf(lineType);
                idx -= 1;
                Line transport = (Line) lineId.getSelectedItem();
                if (!(transport.getName().equals(Constants.LINE_ID_DEFAULT))){
                    String id = Integer.toString(transport.getId());
                    RouteGetter getter = new RouteGetter();
                    getter.execute(Integer.toString(idx), id);
                }
                lineId.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        return v;
    }

    public void showRoute(String transportationTypeId, String lineId){
        RouteGetter getter = new RouteGetter();
        getter.execute(transportationTypeId, lineId);
    }

    private class RouteGetter extends AsyncTask<String, Void, Routes>{

        protected void onPreExecute(){
            ((MainActivity)getActivity()).changeFragment(R.id.location_container, loadingFragment);
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
            DbManipulator manipulator=null;
            try {
               manipulator = new DbManipulator(getContext());
            }catch (SQLiteDatabaseLockedException e){
                ((MainActivity)getContext()).makeSnackbar("Информацията за спирките все още се обновява");
                return null;
            }

            ArrayList<ArrayList<Stop>> stations = new ArrayList<>();
            String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE code=?";
            //TODO fix when result is null
            if (result == null){
                manipulator.closeDb();
                ((MainActivity)getActivity()).detachFragment(loadingFragment);
                return null;
            }
            for(Route route : result.getRoutes()){
                ArrayList<Stop> routeStations = new ArrayList<>();
                for (Stop stop : route.getStops()){
                    Cursor c = manipulator.readRawQuery(query, new String[]{Integer.toString(stop.getCode())});
                    if(c.getCount() != 0) {
                        c.moveToFirst();
                        String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                        String stationCode = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                        String latitude = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                        String longtitude = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
                        String description = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION));
                        routeStations.add(new Stop(Integer.parseInt(stationCode), stationName, latitude, longtitude, description));
                    }
                }
                if(routeStations.size() != 0){
                    stations.add(routeStations);
                }

            }
            manipulator.closeDb();
            int type = result.getLine().getType();
            String typeString = null;
            switch (type){
                case 0:
                    typeString = Constants.TRAM;
                    break;
                case 1:
                    typeString = Constants.BUS;
                    break;
                case 2:
                    typeString = Constants.TROLLEY;
                    break;
            }

            if (stations.size() != 0){
                RoutesFragment f = RoutesFragment.newInstance(stations, typeString);
                ((MainActivity)getActivity()).changeFragment(R.id.location_container, f);
            }else{
                ((MainActivity)getActivity()).detachFragment(loadingFragment);
            }

            return null;
        }


    }



    private class LineGetter extends AsyncTask<Integer, Integer, List<Line>>{

        @Override
        protected List<Line> doInBackground(Integer... idxs) {
            SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
            Call<List<Line>> lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
            try {
                Response<List<Line>> response = lines.execute();
                if(!response.isSuccessful()){
                    ApiError error = ParseApiError.parseError(response);
                    if(error.getCode().equals(Constants.UNAUTHOROZIED_USER_ID)){
                        ((MainActivity)getActivity()).removeRegistration();
                        lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
                        response = lines.execute();
                    }

                }

                return response.body();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<Line> result) {
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

    private class StationGetter extends AsyncTask<Void, Void, ArrayList<Stop>>{
        private MainActivity activity;

        public StationGetter(MainActivity activity){
            this.activity = activity;
        }

        @Override
        protected void onPreExecute(){
            ((MainActivity)getActivity()).changeFragment(R.id.location_container, loadingFragment);
            activity.tracker.startUpdatesButtonHandler();
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

                    if((System.currentTimeMillis() - lastUpdated) <= Constants.FIVE_SECONDS_MS &&loc.getAccuracy() <= Constants.MINIMUM_ACCURACY){
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
        protected ArrayList<Stop> doInBackground(Void... voids) {
            Location loc = getLocation();
            if (loc != null){
                StationsLocator locator = new StationsLocator(loc, 10, 1000, getContext());
                ArrayList<Stop> closestStations = locator.getClosestStations();
                return closestStations;
            }
            return null;
//            return getLocation();
        }

        @Override
        protected void onPostExecute(ArrayList<Stop> stations){
            activity.tracker.stopUpdatesButtonHandler();
            if (stations != null && stations.size() != 0){
                //this is so as to be compatible with route fragment
                ArrayList<ArrayList<Stop>> stationsGroup = new ArrayList<>();
                stationsGroup.add(stations);

                RoutesFragment locationResults = RoutesFragment.newInstance(stationsGroup, null);
                ((MainActivity)getActivity()).changeFragment(R.id.location_container, locationResults);
            }else{
                ((MainActivity)getActivity()).detachFragment(loadingFragment);
//                ((MainActivity) getActivity()).makeSnackbar("Няма спирки в радиус от 200 метра");
            }
        }



    }


}
