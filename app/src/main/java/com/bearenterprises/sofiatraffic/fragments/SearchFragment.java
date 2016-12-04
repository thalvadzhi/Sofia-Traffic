package com.bearenterprises.sofiatraffic.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import retrofit2.Call;


/**
 * Created by thalv on 02-Jul-16.
 */

    public class SearchFragment extends Fragment {

        public SearchFragment() {
        }

        private CoordinatorLayout coordinatorLayout;
        public static SearchFragment newInstance() {
            SearchFragment fragment = new SearchFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            coordinatorLayout = ((MainActivity)getActivity()).getCoordinatorLayout();
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button searchButton = (Button) rootView.findViewById(R.id.button_search);
            final EditText t = (EditText) rootView.findViewById(R.id.station_code);
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

        class TimeGetter extends Thread{
            private Line line;
            private ArrayList<Time> times;
            private SofiaTransportApi sofiaTransportApi;
            private String code;

            public TimeGetter(Line line, SofiaTransportApi sofiaTransportApi, String code) {
                this.line = line;
                this.sofiaTransportApi = sofiaTransportApi;
                this.code = code;
            }

            public ArrayList<Time> getTimes() {
                return times;
            }

            public Line getLine(){
                return line;
            }

            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Call<List<Time>> call = sofiaTransportApi.getTimes(code, Integer.toString(line.getId()));
                try {
                    this.times = (ArrayList<Time>)call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        class SearchQuery extends AsyncTask<Void, Void, Void>{
            private String code;
            private FragmentManager m;
            private LoadingFragment l;
            private MainActivity a;
            public SearchQuery(String code, LoadingFragment l, FragmentManager m, MainActivity a){
                this.code = code;
                this.m = m;
                this.l = l;
                this.a = a;
            }

            @Override
            protected Void doInBackground(Void... params) {
                SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
                Station station= null;
                try {
                    station = getStation(code, sofiaTransportApi);
                    if(station == null){
                        m.beginTransaction().detach(l).commit();
                        ((MainActivity)getActivity()).makeSnackbar("Няма информация!");
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final ArrayList<VehicleTimes> vehicleTimes = new ArrayList<>();
                ArrayList<TimeGetter> timeGetterThreads = new ArrayList<>();
                for(Line line : station.getLines()){
                    TimeGetter getter = new TimeGetter(line, sofiaTransportApi, code);
                    getter.start();
                    timeGetterThreads.add(getter);
                }

                for(TimeGetter t : timeGetterThreads){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(t.getTimes() != null){
                        vehicleTimes.add(new VehicleTimes(t.getLine().getName(), Integer.toString(t.getLine().getType()), null, t.getTimes()));
                    }
                }

                if(vehicleTimes.size() == 0){
                    m.beginTransaction().detach(l).commit();
                    a.makeSnackbar("Няма информация");
                }else{
                    ResultsFragment f = ResultsFragment.newInstance(vehicleTimes);
                    m.beginTransaction().replace(R.id.result_container, f).commit();
                }
                return null;
            }
        }

        public void getTimes(String code, FragmentManager manager){
            LoadingFragment l = LoadingFragment.newInstance();
            manager.beginTransaction().replace(R.id.result_container, l).commit();
            SearchQuery query = new SearchQuery(code,l , manager, (MainActivity) getActivity());
            query.execute();
        }


    }

