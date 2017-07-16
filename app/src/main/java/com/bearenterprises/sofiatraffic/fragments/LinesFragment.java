package com.bearenterprises.sofiatraffic.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.ChangeListener;
import com.bearenterprises.sofiatraffic.adapters.LineNamesAdapter;
import com.bearenterprises.sofiatraffic.adapters.TransportationTypeAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Route;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Routes;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.db.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.db.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.network.RetrofitUtility;
import com.bearenterprises.sofiatraffic.utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class LinesFragment extends Fragment {
    private List<String> transportationTypes;
    private Spinner transportationType;
    private Spinner lineId;
    private LineNamesAdapter lineNamesAdapter;
    private ArrayList<Line> lines;
    private int currentlySelectedType;
    private LoadingFragment loadingFragment;
    private NotifyLineInfoLoaded cond;
    private LineInfoLoadedListener listener;
    private Integer currentStopCode;

    public LinesFragment() {
        // Required empty public constructor
    }


    public static LinesFragment newInstance() {
        LinesFragment fragment = new LinesFragment();
        return fragment;
    }

    public List<String> getTypesOfTransportation(){
        List<String> types = Arrays.asList("----", "Трамвай", "Автобус", "Тролей");
        return types;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingFragment = LoadingFragment.newInstance();
        listener = new LineInfoLoadedListener();
        cond = new NotifyLineInfoLoaded();
        
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        transportationType = (Spinner) v.findViewById(R.id.transportationType);
        lineId = (Spinner) v.findViewById(R.id.lineNumber);
        lineId.setEnabled(false);

        lines = new ArrayList<>();
        transportationTypes = getTypesOfTransportation();
        final TransportationTypeAdapter transportationTypeAdapter = new TransportationTypeAdapter(getContext(), transportationTypes);
        transportationType.setAdapter(transportationTypeAdapter);

        lineNamesAdapter = new LineNamesAdapter(getContext(), lines);
        lineId.setAdapter(lineNamesAdapter);

        currentStopCode = null;

        // after tr type is selected, start getting all the lines relative to that tr. type
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

    /**
     * This class is used to signal that after selecting a transportation type(bus, tram etc), the list of
     * lines for the corresponding tr. type has been loaded. When that happens onChange is called
     */
    private class LineInfoLoadedListener implements ChangeListener {
        @Override
        public void onChange(String lineID) {
            selectLineId(lineID);
        }
    }

    /**
     * Sets the selection to the line with ID {@code lineID}
     * @param lineID
     */
    private int selectLineId(String lineID){
        int pos = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getId() == Integer.parseInt(lineID)) {
                lineId.setSelection(i + 1);
                pos = i + 1;
                break;
            }
        }
        cond.removeListener();
        return pos;
    }

    /**
     * This method is for external use. For example when someone clicks on the line indicator in the Time Result card
     * this method is called to show the information in the Line tab.
     * @param transportationTypeId
     * @param lineId
     */
    public void showRoute(String transportationTypeId, String lineId, Integer stopCode){
        currentStopCode = stopCode;
        cond.setLineID(lineId);
        cond.setListener(listener);
        int currentPosition = transportationType.getSelectedItemPosition();
        int targetPoisiton = Integer.parseInt(transportationTypeId) + 1;
        int currentLineIdPosition = this.lineId.getSelectedItemPosition();
        if(currentPosition == targetPoisiton){
            int pos = selectLineId(lineId);
            if(currentLineIdPosition == pos){
                RoutesFragment f =((RoutesFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.location_container));
                f.expandGroup(stopCode);
                f.scrollToChild(stopCode);
            }
        }else{
            transportationType.setSelection(targetPoisiton);
        }
    }


    /**
     * A class to get the routes from the Ivkos API
     * Typically there are two routes: From stop A to stop B, and from stop B to stop A
     */
    private class RouteGetter extends AsyncTask<String, Void, Routes>{

        protected void onPreExecute(){
            Utility.changeFragment(R.id.location_container, loadingFragment, (MainActivity)getActivity());
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

        /**
         * Now that the stops information has been received we can show it
         * @param result
         */
        protected void onPostExecute(Routes result) {
            RouteShower routeShower = new RouteShower();
            routeShower.execute(result);
        }
    }

    /**
     * This class is used to show the already received information from the ivkos API.
     * It also polls the database in order to complete the data with location and description information.
     */
    private class RouteShower extends AsyncTask<Routes, Void, Void>{

        @Override
        protected Void doInBackground(Routes... routes) {

            Routes result = routes[0];
            DbManipulator manipulator=null;
            try {
               manipulator = new DbManipulator(getContext());
            }catch (SQLiteDatabaseLockedException e){
                Utility.makeSnackbar("Информацията за спирките все още се обновява", (MainActivity)getActivity());
                return null;
            }

            ArrayList<ArrayList<Stop>> stations = new ArrayList<>();
            String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS + " WHERE code=?";
            //TODO fix when result is null
            if (result == null){
                manipulator.closeDb();
                Utility.detachFragment(loadingFragment, (MainActivity)getActivity());
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
                int stCode = -1;
                if(currentStopCode != null){
                    stCode = currentStopCode.intValue();
                }
                RoutesFragment f = RoutesFragment.newInstance(stations, typeString, stCode);
                Utility.changeFragment(R.id.location_container, f, (MainActivity)getActivity());
                currentStopCode = null;
            }else{
                Utility.detachFragment(loadingFragment, (MainActivity) getActivity());
            }

            return null;
        }


    }

    /**
     * This class is used to get all the lines that are of some transportation lines. For e.g. get all bus or tram lines.
     */
    private class LineGetter extends AsyncTask<Integer, Integer, List<Line>>{

        @Override
        protected List<Line> doInBackground(Integer... idxs) {
            SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
            Call<List<Line>> lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
            try {
                return RetrofitUtility.handleUnauthorizedQuery(lines, (MainActivity) getActivity());

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
                    //now that lines have been loaded we can notify the listener
                    cond.notifyLinesLoaded();
                }

            }else{
                Utility.makeSnackbar("Няма информация за този маршрут.", (MainActivity)getActivity());
            }
        }
    }

    /**
     * This class is used to notify that the lines have been loaded so that the wanted one can be selected.
     * It is used when a line must be shown form outside the lines tab. For e.g. if someone presses the line
     * indicator on a result card.
     */
    public class NotifyLineInfoLoaded {
        private ChangeListener listener;
        private String lineID;


        public void setLineID(String lineID){
            this.lineID = lineID;
        }

        /**
         * Every time a request for showing line info from outside the Lines tab is made the listener
         * is set. After the request has been completed the listener is unset.
         */
        public void notifyLinesLoaded(){
            if (listener != null){
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

    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).hideSoftKeyboad();
    }


}
