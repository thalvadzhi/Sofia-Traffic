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
import com.bearenterprises.sofiatraffic.adapters.ChangeListener;
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
    private volatile boolean canSelectLineId;
    private ShowLineFromTimeResult cond;
//    private ArrayList<ArrayList<Station>> routes;
    private boolean dontActivateListener;
    private ShouldShowLineInfoFromTimeResultsListener listener;
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
        listener = new ShouldShowLineInfoFromTimeResultsListener();
        cond = new ShowLineFromTimeResult();

        dontActivateListener = false;
        canSelectLineId = false;
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
                lineId.setSelection(0);
                lineId.setEnabled(false);
                if (selectionIdx >= 1 && selectionIdx <= 3) {
                    currentlySelectedType = transportationTypes.indexOf(selection);
                    currentlySelectedType -= 1;
                    canSelectLineId = false;
                    cond.setBoo(false);
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return v;
    }
    private class ShouldShowLineInfoFromTimeResultsListener implements ChangeListener {
        @Override
        public void onChange(String lineID) {

            selectLineId(lineID);
        }
    }

    private void selectLineId(String lineID){
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getId() == Integer.parseInt(lineID)) {
                lineId.setSelection(i + 1);
                break;
            }
        }
        cond.removeListener();
    }

    public void showRoute(String transportationTypeId, String lineId){
        cond.setLineID(lineId);
        cond.setListener(listener);
        int currentPosition = transportationType.getSelectedItemPosition();
        int targetPoisiton = Integer.parseInt(transportationTypeId) + 1;
        if(currentPosition == targetPoisiton){
           selectLineId(lineId);
        }else{
            transportationType.setSelection(targetPoisiton);
        }
    }


    private class RouteGetter extends AsyncTask<String, Void, Routes>{

        protected void onPreExecute(){
            ((MainActivity)getActivity()).changeFragment(R.id.location_container, loadingFragment);
        }
        @Override
        protected Routes doInBackground(String... strings) {
            String lineType = strings[0];
            String lineId = strings[1];
            SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
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
            SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
            Call<List<Line>> lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
            try {
                return ((MainActivity)getActivity()).handleUnauthorizedQuery(lines);

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
                synchronized (cond){
                    cond.setBoo(true);
                }

            }else{
                ((MainActivity)getActivity()).makeSnackbar("Няма информация за този маршрут.");
            }
        }
    }

    public class ShowLineFromTimeResult {
        private boolean boo = false;
        private ChangeListener listener;
        private String lineID;


        public void setLineID(String lineID){
            this.lineID = lineID;
        }

        public void setBoo(boolean boo) {
            this.boo = boo;
            if (listener != null && boo == true){
                listener.onChange(lineID);
            }
        }

        public void removeListener(){
            listener = null;
        }

        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }

    }


}
