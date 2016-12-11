package com.bearenterprises.sofiatraffic.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by thalv on 02-Jul-16.
 */

    public class SearchFragment extends Fragment {
        private  EditText t;
        public SearchFragment() {
        }

        private CoordinatorLayout coordinatorLayout;
        public static SearchFragment newInstance() {
            SearchFragment fragment = new SearchFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public void requestFocusOnEditText(){
            if(t.requestFocus()) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            coordinatorLayout = ((MainActivity)getActivity()).getCoordinatorLayout();
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button searchButton = (Button) rootView.findViewById(R.id.button_search);
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
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        if(keyCode == KeyEvent.KEYCODE_ENTER){
                            String code = t.getText().toString();
                            showStationTimes(code);
                        }
                    }
                    return false;
                }
            });
            searchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String code = t.getText().toString();
                    showStationTimes(code);
                }
            });

            return rootView;
        }

        private Station getStation(String code, SofiaTransportApi sofiaTransportApi) throws IOException {
            Call<Station> call = sofiaTransportApi.getStation(code);
            return call.execute().body();
        }

        public void showStationTimes(String code){
            if(code != null){
                FragmentManager manager = getFragmentManager();
                StationNameFragment stationNameFragment = StationNameFragment.newInstance(code);
                manager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.station_name_fragment, stationNameFragment).commit();
                getTimes(code, manager);
            }else{
                ((MainActivity)getActivity()).makeSnackbar("Няма въведен код на спирка!");
            }
        }

        class SearchQuery extends AsyncTask<Void, Void, ArrayList<Line>>{
            private String code;
            private FragmentManager m;
            private LoadingFragment l;
            private MainActivity a;
            private ResultsFragment resultsFragment;
            private SofiaTransportApi sofiaTransportApi;
            public SearchQuery(String code, LoadingFragment l, FragmentManager m, MainActivity a){
                this.code = code;
                this.m = m;
                this.l = l;
                this.a = a;

            }

            @Override
            protected ArrayList<Line> doInBackground(Void... params) {
                sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
                Station station= null;
                try {
                    station = getStation(code, sofiaTransportApi);
                    if(station == null){
                        m.beginTransaction().detach(l).commit();
                        ((MainActivity)getActivity()).makeSnackbar("Няма информация!");
                        return null;
                    }
                } catch (IOException e) {
                    if (station == null){
                        m.beginTransaction().detach(l).commit();
                        ((MainActivity)getActivity()).makeSnackbar("Няма информация!");
                        return null;
                    }
                    e.printStackTrace();
                }


                final ArrayList<VehicleTimes> vehicleTimes = new ArrayList<>();
                for(Line line : station.getLines()){
                    vehicleTimes.add(new VehicleTimes(line, Integer.toString(line.getType())));
                }
                resultsFragment = ResultsFragment.newInstance(vehicleTimes);
                ((MainActivity)getActivity()).changeFragment(R.id.result_container, resultsFragment);
                return station.getLines();


            }

            @Override
            protected void onPostExecute(ArrayList<Line> lines){
                if(lines != null){
                    for(int i = 0; i < lines.size(); i++){
                        final Line l = lines.get(i);
                        Call<List<Time>> call = this.sofiaTransportApi.getTimes(code, Integer.toString(l.getId()));
                        call.enqueue(new Callback<List<Time>>() {
                            @Override
                            public void onResponse(Call<List<Time>> call, Response<List<Time>> response) {
                                List<Time> times = response.body();
                                ((MainActivity) getActivity()).addTimes(resultsFragment, l, times);
                            }

                            @Override
                            public void onFailure(Call<List<Time>> call, Throwable t) {

                            }
                        });

                    }
                }
            }
        }


        public void getTimes(String code, FragmentManager manager){
            LoadingFragment l = LoadingFragment.newInstance();
            manager.beginTransaction().replace(R.id.result_container, l).commit();
            SearchQuery query = new SearchQuery(code,l , manager, (MainActivity) getActivity());
            query.execute();
        }


    }

