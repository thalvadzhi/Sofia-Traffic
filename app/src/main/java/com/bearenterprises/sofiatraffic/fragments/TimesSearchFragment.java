package com.bearenterprises.sofiatraffic.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.StopBuilder;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.db.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.network.RetrofitUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by thalv on 02-Jul-16.
 */

public class TimesSearchFragment extends Fragment {
    private EditText t;
    private SwipeRefreshLayout refreshLayout;
    private TimeResultsFragment timeResultsFragment;
    private SegmentedGroup codeNameSwitch;
    private ViewSwitcher searchBoxesContainer;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> stopNames;

    public TimesSearchFragment() {
    }

    private CoordinatorLayout coordinatorLayout;

    public static TimesSearchFragment newInstance() {
        TimesSearchFragment fragment = new TimesSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void requestFocusOnEditText() {
        if (t.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stopNames = new ArrayList<>();
        coordinatorLayout = ((MainActivity) getActivity()).getCoordinatorLayout();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button searchButton = (Button) rootView.findViewById(R.id.button_search);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        codeNameSwitch = (SegmentedGroup) rootView.findViewById(R.id.segmentedGroupCodeNameSwitch);
        searchBoxesContainer = (ViewSwitcher) rootView.findViewById(R.id.switcher_code_name_entry_box);
        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete_text_view_station_name);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stopNames);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Stop> stops = readByQueryString(s.toString(), true);
                stopNames.clear();
                for (Stop stop : stops) {
                    stopNames.add(stop.getName());
                }
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stopNames);
                autoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setSearchMethod();
        codeNameSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                searchBoxesContainer.showNext();
                if (checkedId == R.id.toggleStateCode) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String code = t.getText().toString();
                showStationTimes(code);
                refreshLayout.setRefreshing(false);
            }
        });
        t = (EditText) rootView.findViewById(R.id.station_code);
        t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        t.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String code = t.getText().toString();
                        showStationTimes(code);
                    }
                }
                return false;
            }
        });

        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String query = autoCompleteTextView.getText().toString();
                        showNameSearchResults(query);
                    }
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (codeNameSwitch.getCheckedRadioButtonId() == R.id.toggleStateCode) {
                    String code = t.getText().toString();
                    showStationTimes(code);
                } else {
                    String query = autoCompleteTextView.getText().toString();
                    showNameSearchResults(query);
                }

            }
        });

        return rootView;
    }

    public void nextInCodeName(){
        if (codeNameSwitch.getCheckedRadioButtonId() == R.id.toggleStateCode){
            codeNameSwitch.check(R.id.toggleStateName);
        }else{
            codeNameSwitch.check(R.id.toggleStateCode);
        }
    }

    public void checkCodeNameSwitch(int id){
        codeNameSwitch.check(id);
    }
    private void setSearchMethod() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String searchMethod = sharedPref.getString(getResources().getString(R.string.key_choose_search_method), getResources().getString(R.string.search_method_code_value));
        if (searchMethod.equals(getResources().getString(R.string.search_method_code_value))) {
            codeNameSwitch.check(R.id.toggleStateCode);
        } else {
            codeNameSwitch.check(R.id.toggleStateName);
            searchBoxesContainer.showNext();
        }
    }

    public void showNameSearchResults(String query) {
        ((MainActivity) getActivity()).hideSoftKeyboad();
        List<Stop> stops = readByQueryString(query, false);
        NameResultsFragment f = NameResultsFragment.newInstance((ArrayList<Stop>) stops);
        ShowAllOnMapFragment showAllOnMapFragment = ShowAllOnMapFragment.newInstance((ArrayList<Stop>) stops);
        Utility.changeFragmentSlideIn(R.id.station_name_fragment, showAllOnMapFragment, (MainActivity) getActivity());
        Utility.changeFragment(R.id.result_container, f, (MainActivity) getActivity());
    }

    public void setEnablednessRefreshLayout(boolean enable) {
        refreshLayout.setEnabled(enable);
    }

    private Stop getStop(String code, SofiaTransportApi sofiaTransportApi) throws IOException {
        Call<Stop> call = sofiaTransportApi.getStop(code);
        return RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) getActivity());
    }

    /**
     * This uses the fast method(parsed from mobile site)
     */
    private Stop getStopFast(String code, SofiaTransportApi sofiaTransportApi) throws IOException {
        Call<Stop> call = sofiaTransportApi.getStopWithTimes(code);
        return RetrofitUtility.handleUnauthorizedQuery(call, (MainActivity) getActivity());
    }

    public void showStationTimes(String code) {
        if (code != null) {
            //hide soft keyboard
            ((MainActivity) getActivity()).hideSoftKeyboad();

            refreshLayout.setEnabled(true);
            FragmentManager manager = getFragmentManager();
            StationNameFragment stationNameFragment = StationNameFragment.newInstance(code);
            Utility.changeFragmentSlideIn(R.id.station_name_fragment, stationNameFragment, (MainActivity) getActivity());
            t.setText(code);
            t.setSelection(t.getText().length());
            getTimes(code, manager);
        } else {
            Utility.makeSnackbar("Няма въведен код на спирка!", (MainActivity) getActivity());
        }
    }

    /**
     * @param queryString
     * @param distinct    determines whether station with the same name will be extracted from the DB. For e.g. if
     *                    distinct is True only one result for query string "Плиска" will be returned.
     * @return
     */
    public List<Stop> readByQueryString(String queryString, boolean distinct) {
        List<Stop> stops = new ArrayList<>();
        if ("".equals(queryString) || " ".equals(queryString)) {
            return stops;
        }

        Cursor c;
        DbManipulator manipulator = new DbManipulator(getContext());
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //for some reason LIKE is case sensitive on older than LOLLIPOP software
            //so we have to get everything from the DB and manually find the stops
            String sqlStations = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS;
            c = manipulator.readRawQuery(sqlStations, null);
        }else{
            String sqlStations = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS;
            sqlStations += " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME + " LIKE ?";
            if (distinct) {
                sqlStations += "GROUP BY " + DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME;
            }
            c = manipulator.readRawQuery(sqlStations, new String[]{"%"+queryString+"%"});
        }
        try {
            if (c.moveToFirst()) {
                do {
                    StopBuilder builder = new StopBuilder();
                    String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if(!stationName.toLowerCase(new Locale("bg")).contains(queryString.toLowerCase(new Locale("bg")))){
                            continue;
                        }
                    }
                    String code = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
                    String lat = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
                    String lon = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));

                    builder.setName(stationName)
                            .setCode(Integer.parseInt(code))
                            .setLatitude(lat)
                            .setLongtitude(lon);

                    Stop stop = builder.build();
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && distinct) {
                        boolean shouldAdd = true;
                        for(Stop st : stops){
                            if(st.getName().equals(stop.getName())){
                               shouldAdd = false;
                            }
                        }
                        if(shouldAdd){
                            stops.add(stop);
                        }
                    }else{
                        stops.add(stop);
                    }

                } while (c.moveToNext());
            }
        } finally {
            manipulator.closeDb();
        }
        return stops;
    }

    class SearchQuery extends AsyncTask<Void, Void, Stop> {
        private String code;
        private FragmentManager m;
        private LoadingFragment l;
        private MainActivity a;
        //        private TimeResultsFragment timeResultsFragment;
        private SofiaTransportApi sofiaTransportApi;
        private String queryMethod;

        public SearchQuery(String code, LoadingFragment l, FragmentManager m, MainActivity a) {
            this.code = code;
            this.m = m;
            this.l = l;
            this.a = a;

        }

        @Override
        protected Stop doInBackground(Void... params) {
            sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
            Stop stop = null;
            try {
                queryMethod = ((MainActivity) getActivity()).getQueryMethod();
                if (queryMethod.equals(Constants.QUERY_METHOD_SLOW)) {
                    stop = getStop(code, sofiaTransportApi);
                } else if (queryMethod.equals(Constants.QUERY_METHOD_FAST)) {
                    stop = getStopFast(code, sofiaTransportApi);
                }
                if (stop == null) {
                    Utility.detachFragment(l, (MainActivity) getActivity());

                    Utility.makeSnackbar("Няма информация!", (MainActivity) getActivity());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (stop == null) {
                    Utility.detachFragment(l, (MainActivity) getActivity());
                    Utility.makeSnackbar("Няма информация!", (MainActivity) getActivity());
                    return null;
                }

            }


            final ArrayList<LineTimes> lineTimes = new ArrayList<>();
            for (Line line : stop.getLines()) {
                lineTimes.add(new LineTimes(line, Integer.toString(line.getType())));
            }
            timeResultsFragment = TimeResultsFragment.newInstance(lineTimes, stop);
            Utility.changeFragment(R.id.result_container, timeResultsFragment, (MainActivity) getActivity());
            return stop;


        }

        @Override
        protected void onPostExecute(Stop stop) {
            if (queryMethod.equals(Constants.QUERY_METHOD_SLOW)) {
                updateLineInfoSlow(stop);
            } else if (queryMethod.equals(Constants.QUERY_METHOD_FAST)) {
                updateLineInfoFast(stop);
            }
        }

    }

    private void updateLineInfoFast(Stop stop) {
        //if station was gotten the fast way, it should contain time info
        if (stop != null) {
            ArrayList<Line> lines = stop.getLines();
            if (lines != null) {
                for (Line line : lines) {
                    List<Time> times = line.getTimes();
                    CommunicationUtility.addTimes(timeResultsFragment, line, times);
                }
            }
        }
    }

    /*
        Update the line information by using the slow method that outputs many arrival times
    */
    private void updateLineInfoSlow(Stop stop) {
        if (stop != null) {
            ArrayList<Line> lines = stop.getLines();
            updateLineInfoSlowForSelectLines(stop, lines);
        }
    }

    public void updateLineInfoSlowForSelectLines(Stop stop, ArrayList<Line> lines) {
        SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                final Line l = lines.get(i);
                Call<List<Time>> call = sofiaTransportApi.getTimes(Integer.toString(stop.getCode()), Integer.toString(l.getId()));
                call.enqueue(new Callback<List<Time>>() {
                    @Override
                    public void onResponse(Call<List<Time>> call, Response<List<Time>> response) {
                        List<Time> times = response.body();
                        CommunicationUtility.addTimes(timeResultsFragment, l, times);
                    }

                    @Override
                    public void onFailure(Call<List<Time>> call, Throwable t) {

                    }
                });

            }
        }
    }

    public void getTimes(String code, FragmentManager manager) {
        LoadingFragment l = LoadingFragment.newInstance();
        manager.beginTransaction().replace(R.id.result_container, l).commit();
        SearchQuery query = new SearchQuery(code, l, manager, (MainActivity) getActivity());
        query.execute();
    }


}

