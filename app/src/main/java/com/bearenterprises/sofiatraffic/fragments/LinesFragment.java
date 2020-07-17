package com.bearenterprises.sofiatraffic.fragments;

import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.adapters.ChangeListener;
import com.bearenterprises.sofiatraffic.adapters.LineNamesAdapter;
import com.bearenterprises.sofiatraffic.adapters.TransportationTypeAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Route;
import com.bearenterprises.sofiatraffic.restClient.Routes;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleRoute;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.db.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bearenterprises.sofiatraffic.utilities.network.RetrofitUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    private HashMap<String, Integer> transportationNameToTransportationId;
    private static final String TAG = LinesFragment.class.getName();

    public LinesFragment() {
        // Required empty public constructor
    }


    public static LinesFragment newInstance() {
        LinesFragment fragment = new LinesFragment();
        return fragment;
    }

    private void populateTransportationNameToTransportationId(){
        transportationNameToTransportationId = new HashMap<>();
        transportationNameToTransportationId.put("Трамвай", 0);
        transportationNameToTransportationId.put("Автобус", 1);
        transportationNameToTransportationId.put("Тролей", 2);
        transportationNameToTransportationId.put("Метро", 10);
        transportationNameToTransportationId.put("----", -1);


    }

    public List<String> getTypesOfTransportation(){
        List<String> types = Arrays.asList("----", "Трамвай", "Автобус", "Тролей", "Метро");
        return types;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        populateTransportationNameToTransportationId();
        loadingFragment = LoadingFragment.newInstance();
        listener = new LineInfoLoadedListener();
        cond = new NotifyLineInfoLoaded();
        
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        transportationType =  v.findViewById(R.id.transportationType);
        lineId = v.findViewById(R.id.lineNumber);
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
//                int selectionIdx = transportationTypes.indexOf(selection);
                lineId.setSelection(0);
                lineId.setEnabled(false);

                currentlySelectedType = transportationNameToTransportationId.get(selection);

                if(currentlySelectedType < 0){
                    return;
                }
                LineGetter lineGetter = new LineGetter();
                lineGetter.execute(currentlySelectedType);
//                if (selectionIdx >= 1 && selectionIdx <= 3) {
//                    currentlySelectedType = transportationTypes.indexOf(selection);
//                    currentlySelectedType -= 1;
//                    LineGetter lineGetter = new LineGetter();
//                    lineGetter.execute(currentlySelectedType);
//                }else if (selectionIdx == 4){
//                    LineGetter lineGetter = new LineGetter();
//                    lineGetter.execute(10);
//                }else{
//                    lineId.setEnabled(false);
//                }

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

//                int idx = transportationTypes.indexOf(lineType);
//                idx -= 1;
                Line transportSchedule = null;
                Line transport = null;
                try{
                    transportSchedule = (Line) lineId.getSelectedItem();
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
                try{
                    transport = (Line) lineId.getSelectedItem();
                }catch (ClassCastException e){
                    e.printStackTrace();
                }

                if(transport == null && transportSchedule == null){
                    return;
                }

                if(transport != null &&(transport.getName().equals(Constants.LINE_ID_DEFAULT)) ){
                    return;
                }

                if (!transportSchedule.isSchedule()) {
                    //means selected item is indeed scheduleStop
                    RouteGetter routeGetter = new RouteGetter(new Utility.RouteGettingFunction<String, String, RouteShowerArguments>() {
                        @Override
                        public RouteShowerArguments getRoutes(SofiaTransportApi sofiaTransportApi, String lineType, String lineId) throws IOException {
                            Call<Routes> routes = sofiaTransportApi.getRoutes(lineType, lineId);
                            try {
                                Routes route = routes.execute().body();
                                RouteShowerArguments rsa = new RouteShowerArguments(route, lineType);
                                return rsa;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                    });
                    String id = Integer.toString(transport.getId());
                    routeGetter.execute(Integer.toString(currentlySelectedType), id);
                } else {
                    RouteGetter routeGetter = new RouteGetter(new Utility.RouteGettingFunction<String, String, RouteShowerArguments>() {
                        @Override
                        public RouteShowerArguments getRoutes(SofiaTransportApi sofiaTransportApi, String lineType, String lineId) throws IOException {
                            Call<List<ScheduleRoute>> routes = sofiaTransportApi.getScheduleRoutes(lineType, lineId);
                            try {
                                List<ScheduleRoute> route = routes.execute().body();
                                RouteShowerArguments rsa = new RouteShowerArguments(route, lineType);
                                return rsa;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                    });
                    routeGetter.execute(Integer.toString(currentlySelectedType), transportSchedule.getName());
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
        public void onChange(String lineName, int trType) {
            selectLineId(trType, lineName);
        }
    }

    /**
     * Sets the selection to the line with ID {@code lineID}
     */
    private int selectLineId(int trType, String lineName){
        int pos = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getType() == trType && lines.get(i).getName().equals(lineName)) {
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
     *
     */
    public void showRoute(Line line, Integer stopCode){
        currentStopCode = stopCode;
        cond.setLineID(String.valueOf(line.getId()));
        cond.setLineName(line.getName());
        cond.setTrType(line.getType());
        cond.setListener(listener);
        int currentPosition = transportationType.getSelectedItemPosition();

        int targetPoisiton = line.getType() + 1;
        int currentLineIdPosition = this.lineId.getSelectedItemPosition();
        if(currentPosition == targetPoisiton){
            int pos = selectLineId(line.getType(), line.getName());
            if(currentLineIdPosition == pos){
                RoutesFragment f = null;
                try {
                    f = ((RoutesFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.location_container));
                }catch (ClassCastException e){
                    Log.d(TAG, "Strange thing", e);
                    return;
                }
                if(f == null){
                    return;
                }
                f.expandGroup(stopCode);
                f.scrollToChild(stopCode);
            }
        }else{
            transportationType.setSelection(targetPoisiton);
        }
    }


    private class RouteGetter extends AsyncTask<String, Void, RouteShowerArguments>{
        private Utility.RouteGettingFunction<String, String, RouteShowerArguments> routeGettingFunction;

        public RouteGetter(Utility.RouteGettingFunction<String, String, RouteShowerArguments> f){
            routeGettingFunction = f;
        }

        @Override
        protected RouteShowerArguments doInBackground(String... strings) {
            String lineType = strings[0];
            //could be either id or name
            String lineIdentification = strings[1];
            SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
            RouteShowerArguments routeShowerArguments;
            try {
                routeShowerArguments = routeGettingFunction.getRoutes(sofiaTransportApi, lineType, lineIdentification);
                return routeShowerArguments;
            } catch (IOException e) {
                Log.d(TAG, "Error getting route", e);
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            Utility.changeFragment(R.id.location_container, loadingFragment, (MainActivity)getActivity());
        }

        @Override
        protected void onPostExecute(RouteShowerArguments routeShowerArguments) {
            if(routeShowerArguments != null){
                final RouteShower routeShower;
                if(routeShowerArguments.routeType.equals(RouteShowerArguments.ROUTE_TYPE_SCHEDULE)){
                     routeShower = new RouteShower(new Utility.RouteStopsFunction<RouteShowerArguments>() {
                        @Override
                        public ArrayList<ArrayList<Stop>> getStops(RouteShowerArguments routeShowerArguments) {
                            ArrayList<ArrayList<Stop>> stops = new ArrayList<>();
                            String currentScheduleDayType = "";
                            String nextScheduleDayType = "";
                            String nextNextScheduleDayType = "";
                            try {
                                currentScheduleDayType = Utility.getScheduleDayType();
                                nextScheduleDayType = Utility.getNextScheduleDayType(currentScheduleDayType);
                                nextNextScheduleDayType = Utility.getNextScheduleDayType(nextScheduleDayType);
                            } catch (Exception e) {
                                Log.d(TAG, "Couldn't get the current day", e);
                            }
                            ArrayList<ArrayList<Stop>> nextStops = new ArrayList<>();
                            ArrayList<ArrayList<Stop>> nextNextStops = new ArrayList<>();

                            for(ScheduleRoute route : routeShowerArguments.routesSchedules){
                                ArrayList<Stop> routeStations = new ArrayList<>();
                                for (Stop stop : route.getStops()){
                                    Stop st = DbUtility.getStationByCode(Integer.toString(stop.getCode()), (MainActivity) getActivity());
                                    st.setDescription(route.getRouteName());
                                    if(st.getName() != null){
                                        routeStations.add(st);
                                    }else{
                                        routeStations.add(stop);
                                    }
                                }
                                if(route.getScheduleDayTypes().contains(currentScheduleDayType)){

                                    if(routeStations.size() != 0){
                                        stops.add(routeStations);
                                    }
                                }else if(route.getScheduleDayTypes().contains(nextScheduleDayType)){
                                    if(routeStations.size() != 0){
                                        nextStops.add(routeStations);
                                    }
                                }else if(route.getScheduleDayTypes().contains(nextNextScheduleDayType)){
                                    if(routeStations.size() != 0){
                                        nextNextStops.add(routeStations);
                                    }
                                }
                            }
                            if (stops.size() != 0){
                                return stops;
                            }else if(nextStops.size() != 0){
                                return nextStops;
                            }else if(nextNextStops.size() != 0){
                                return nextNextStops;
                            }
                            return stops;
                        }
                    });

                }else{
                    routeShower = new RouteShower(new Utility.RouteStopsFunction<RouteShowerArguments>() {
                        @Override
                        public ArrayList<ArrayList<Stop>> getStops(RouteShowerArguments routeShowerArguments) {
                            ArrayList<ArrayList<Stop>> stops = new ArrayList<>();
                            try{
                                for(Route route : routeShowerArguments.routesVirtualTables.getRoutes()){
                                    ArrayList<Stop> routeStations = new ArrayList<>();
                                    for (Stop stop : route.getStops()) {
                                        Stop st = DbUtility.getStationByCode(Integer.toString(stop.getCode()), (MainActivity) getActivity());
                                        if (st.getName() != null) {
                                            routeStations.add(st);
                                        }else{
                                            routeStations.add(stop);
                                        }
                                    }
                                    if (routeStations.size() != 0) {
                                        stops.add(routeStations);
                                    }

                                }
                                return stops;
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            return stops;
                        }

                    });
                }

                if(routeShower != null){
                    routeShower.execute(routeShowerArguments);
                }
            }
        }
    }

    private class RouteShower extends AsyncTask<RouteShowerArguments, Void, Void>{

        private Utility.RouteStopsFunction routeStopsFunction;
        public RouteShower(Utility.RouteStopsFunction routeStopsFunction){
            this.routeStopsFunction = routeStopsFunction;
        }
        @Override
        protected Void doInBackground(RouteShowerArguments... routeShowerArguments) {
            RouteShowerArguments rsa = routeShowerArguments[0];
            DbManipulator manipulator=null;
            try {
                manipulator = new DbManipulator(getContext());
            }catch (SQLiteDatabaseLockedException e){
                Utility.makeSnackbar("Информацията за спирките все още се обновява", (MainActivity)getActivity());
                return null;
            }

            if (rsa == null){
                manipulator.closeDb();
                Utility.detachFragment(loadingFragment, (MainActivity)getActivity());
                return null;
            }

            ArrayList<ArrayList<Stop>> stations = routeStopsFunction.getStops(rsa);

            manipulator.closeDb();
            int type = Integer.parseInt(rsa.lineType);
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


    private class RouteShowerArguments{
        public List<ScheduleRoute> routesSchedules;
        public Routes routesVirtualTables;
        public String lineType;
        public String routeType;
        public static final String ROUTE_TYPE_SCHEDULE = "schedule route";
        public static final String ROUTE_TYPE_VT = "vt route";

        RouteShowerArguments(List<ScheduleRoute> routesSchedules, String lineType) {
            this.routesSchedules = routesSchedules;
            this.lineType = lineType;
            this.routeType = this.ROUTE_TYPE_SCHEDULE;
        }

       RouteShowerArguments(Routes routesVirtualTables, String lineType){
            this.routesVirtualTables = routesVirtualTables;
            this.lineType = lineType;
            this.routeType = this.ROUTE_TYPE_VT;
        }
    }

    /**
     * This class is used to get all the lines that are of some transportation lines. For e.g. get all bus or tram lines.
     */
    private class LineGetter extends AsyncTask<Integer, Integer, List<Line>>{
        private void setIsScheduleForAllLines(List<Line> lines, boolean isSchedule){
            for(Line l : lines){
                l.setSchedule(isSchedule);
            }
        }
        private void addScheduledLines(List<Line> allLines, List<Line> scheduleLines){
            for(Line l : scheduleLines){
                if(!allLines.contains(l)){
                    allLines.add(l);
                }
            }
        }
        @Override
        protected List<Line> doInBackground(Integer... idxs) {
            SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
            //TODO maybe leave just one network call or make the call parallel
            Call<List<Line>> lines = sofiaTransportApi.getLines(Integer.toString(idxs[0]));
            Call<List<Line>> scheduleLines = sofiaTransportApi.getScheduleLines(Integer.toString(idxs[0]));
            try {
                List<Line> allLines = RetrofitUtility.handleUnauthorizedQuery(lines, (MainActivity) getActivity());
                List<Line> allScheduleLines = RetrofitUtility.handleUnauthorizedQuery(scheduleLines, (MainActivity) getActivity());
                setIsScheduleForAllLines(allScheduleLines, true);
                addScheduledLines(allLines, allScheduleLines);
                Collections.sort(allLines, new Comparator<Line>() {
                    @Override
                    public int compare(Line line, Line t1) {
                    return Utility.compareLineNames(line, t1);

                    }
                });
                return allLines;

            } catch (Exception e) {
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

        private int trType;
        private String lineName;

        public void setTrType(int trType) {
            this.trType = trType;
        }

        public void setLineName(String lineName) {
            this.lineName = lineName;
        }

        public void setLineID(String lineID){
            this.lineID = lineID;
        }

        /**
         * Every time a request for showing line info from outside the Lines tab is made the listener
         * is set. After the request has been completed the listener is unset.
         */
        public void notifyLinesLoaded(){
            if (listener != null){
                listener.onChange(lineName, trType);
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
