package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import androidx.annotation.IdRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.StopInformationGetter;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import info.hoang8f.android.segmented.SegmentedGroup;


/**
 * Created by thalv on 02-Jul-16.
 */

public class TimesSearchFragment extends Fragment {
    private EditText editTextSearchByCodeOrName;
    private SwipeRefreshLayout refreshLayout;
    private TimeResultsFragment timeResultsFragment;
    private SegmentedGroup codeNameSwitch;
    private ViewSwitcher searchBoxesContainer;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> stopNames;
    private LoadingFragment loadingFragment;

    public TimesSearchFragment() {
    }

    private CoordinatorLayout coordinatorLayout;

    public static TimesSearchFragment newInstance() {
        TimesSearchFragment fragment = new TimesSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EditText getEditTextSearchByCodeOrName() {
        return editTextSearchByCodeOrName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingFragment = LoadingFragment.newInstance();
        stopNames = new ArrayList<>();
        coordinatorLayout = ((MainActivity) getActivity()).getCoordinatorLayout();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button searchButton = rootView.findViewById(R.id.button_search);
        refreshLayout = rootView.findViewById(R.id.swiperefresh);
        codeNameSwitch = rootView.findViewById(R.id.segmentedGroupCodeNameSwitch);
        searchBoxesContainer = rootView.findViewById(R.id.switcher_code_name_entry_box);
        autoCompleteTextView = rootView.findViewById(R.id.autocomplete_text_view_station_name);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stopNames);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Stop> stops = readByQueryString(s.toString(), true);
                if(stops == null){
                    return;
                }
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
                String code = editTextSearchByCodeOrName.getText().toString();
                showStationTimes(code);
                refreshLayout.setRefreshing(false);
            }
        });
        editTextSearchByCodeOrName = rootView.findViewById(R.id.station_code);

        editTextSearchByCodeOrName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getEditTextSearchByCodeOrName(), 0);
                }
            }
        });
        editTextSearchByCodeOrName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String code = editTextSearchByCodeOrName.getText().toString();
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
                    String code = editTextSearchByCodeOrName.getText().toString();

                    showStationTimes(code);
                } else {
                    String query = autoCompleteTextView.getText().toString();
                    showNameSearchResults(query);
                }

            }
        });

        return rootView;
    }

    public void nextInCodeName() {
        if (codeNameSwitch.getCheckedRadioButtonId() == R.id.toggleStateCode) {
            codeNameSwitch.check(R.id.toggleStateName);
        } else {
            codeNameSwitch.check(R.id.toggleStateCode);
        }
    }

    public void checkCodeNameSwitch(int id) {
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


    public void showStationTimes(String code) {
        if("".equals(code) || " ".equals(code)){
            return;
        }
        if (code != null) {
            //hide soft keyboard
            ((MainActivity) getActivity()).hideSoftKeyboad();

            refreshLayout.setEnabled(true);
            FragmentManager manager = getFragmentManager();
            StationNameFragment stationNameFragment = StationNameFragment.newInstance(code);
            Utility.changeFragmentSlideIn(R.id.station_name_fragment, stationNameFragment, (MainActivity) getActivity());
            editTextSearchByCodeOrName.setText(code);
            editTextSearchByCodeOrName.setSelection(editTextSearchByCodeOrName.getText().length());
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

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //for some reason LIKE is case sensitive on older than LOLLIPOP software
            //so we have to get everything from the DB and manually find the stops
            String sqlStations = getQueryStringPreLollipop();
            ArrayList<Stop> stationsFromDatabase = DbUtility.getStationsFromDatabase(sqlStations, null, (MainActivity) getActivity());
            TreeSet<Stop> outputStations = new TreeSet<>();
            ArrayList<Stop> outputStationsWithRepetition = new ArrayList<>();
            for (Stop stop : stationsFromDatabase) {
                if (stop.getName().toLowerCase(new Locale("bg")).contains(queryString.toLowerCase(new Locale("bg")))) {
                    if(distinct){
                        outputStationsWithRepetition.add(stop);
                    }else{
                        outputStations.add(stop);
                    }
                }
            }
            if(outputStationsWithRepetition.size() == 0){
                return new ArrayList<>(outputStations);
            }else{
                return outputStationsWithRepetition;
            }
        } else {
            String sqlStations = getQueryStringPostLollipop(distinct);
            ArrayList<Stop> stationsFromDatabase = DbUtility.getStationsFromDatabase(sqlStations, new String[]{"%" + queryString + "%"}, (MainActivity) getActivity());
            return stationsFromDatabase;
        }

    }

    private String getQueryStringPreLollipop() {
        //for some reason LIKE is case sensitive on older than LOLLIPOP software
        //so we have to get everything from the DB and manually find the stops
        return "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS;
    }

    public static String getQueryStringPostLollipop(boolean distinct) {
        String sqlStations = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS;
        sqlStations += " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME + " LIKE ?";
        if (distinct) {
            sqlStations += " GROUP BY " + DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME;
        }
        return sqlStations;
    }

    private void detachLoadingFragment() {
        Utility.detachFragment(loadingFragment, (MainActivity) getActivity());
    }

    public void getTimes(String code, FragmentManager manager) {
        manager.beginTransaction().replace(R.id.result_container, loadingFragment).commit();
        SearchQuery query = new SearchQuery(code);
        query.execute();
    }

    class SearchQuery extends AsyncTask<Void, Void, Stop> {
        private String stopCode;
        private String queryMethod;
        private StopInformationGetter stopInformationGetter;
        private final String TAG = SearchQuery.class.getName();


        public SearchQuery(String code) {
            this.stopCode = code;
            stopInformationGetter = new StopInformationGetter(Integer.parseInt(code), getContext());
            timeResultsFragment = null;
        }

        @Override
        protected Stop doInBackground(Void... params) {
            stopInformationGetter.setOnScheduleLinesReceived(new StopInformationGetter.OnScheduleLinesReceived(){

                @Override
                public void scheduleReceived(Line line) {
                    try {
                        if (!CommunicationUtility.checkIfTimesAlreadySet(timeResultsFragment, line)){
                            CommunicationUtility.addScheduleTime(timeResultsFragment, line);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void allLinesProcessed() {
                    CommunicationUtility.removeAllUnpopulatedLines(timeResultsFragment);
                }
            });

            Stop scheduleStop = null;
            Stop stop = null;
            Stop mergedStop = null;
            queryMethod = Constants.QUERY_METHOD_SLOW;
            try {
//                scheduleStop = stopInformationGetter.getScheduleStop(stopCode);
                stop = stopInformationGetter.getStopSlow(stopCode);

                mergedStop = stop;//Utility.mergeStops(stop, scheduleStop);
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (mergedStop == null) {
                detachLoadingFragment();
                Utility.makeSnackbar("Няма информация!", (MainActivity) getActivity());
                return null;
            }

            final ArrayList<LineTimes> lineTimes = new ArrayList<>();
            for (Line line : mergedStop.getLines()) {
                LineTimes lt = new LineTimes(line, Integer.toString(line.getType()), (ArrayList<Time>) line.getTimes());
                lt.setRouteName(line.getRouteName().toUpperCase());
                lineTimes.add(lt);
            }

            timeResultsFragment = TimeResultsFragment.newInstance(lineTimes, mergedStop);
            // it's important that this is called after creating the timeresultsfragment

            try {
                // this is so the station name animation doesn't stutter
                // since when adding the result fragment the station name animation is still taking place
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Utility.changeFragment(R.id.result_container, timeResultsFragment, (MainActivity) getActivity());
//            stopInformationGetter.getScheduleTimesAsync();

            return mergedStop;
        }

    }
}
