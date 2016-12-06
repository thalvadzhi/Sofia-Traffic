package com.bearenterprises.sofiatraffic.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        class SearchQuery extends AsyncTask<Void, Void, ArrayList<Line>>{
            private String code;
            private FragmentManager m;
            private LoadingFragment l;
            private MainActivity a;
            private ResultsFragment resultsFragment;
            private SofiaTransportApi sofiaTransportApi;
            private AtomicInteger integer;
            private long start;
            public SearchQuery(String code, LoadingFragment l, FragmentManager m, MainActivity a){
                this.code = code;
                this.m = m;
                this.l = l;
                this.a = a;
                integer = new AtomicInteger(0);

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
                    e.printStackTrace();
                }


                final ArrayList<VehicleTimes> vehicleTimes = new ArrayList<>();
                ArrayList<TimeGetter> timeGetterThreads = new ArrayList<>();
                for(Line line : station.getLines()){
                    vehicleTimes.add(new VehicleTimes(line.getName(), Integer.toString(line.getType())));
//                    TimeGetter getter = new TimeGetter(line, sofiaTransportApi, code);
//                    getter.start();
//                    TimeGetter1 geter = new TimeGetter1(line, code, sofiaTransportApi, i);
//                    timeGetterThreads.add(getter);
                }
                resultsFragment = ResultsFragment.newInstance(vehicleTimes);
                ((MainActivity)getActivity()).changeFragment(R.id.result_container, resultsFragment);
                start = System.currentTimeMillis();
                return station.getLines();


//                for(TimeGetter t : timeGetterThreads){
//                    try {
//                        t.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(t.getTimes() != null){
//                        vehicleTimes.add(new VehicleTimes(t.getLine().getName(), Integer.toString(t.getLine().getType()), null, t.getTimes()));
//                    }
//                }
//
//                if(vehicleTimes.size() == 0){
//                    m.beginTransaction().detach(l).commit();
//                    a.makeSnackbar("Няма информация");
//                }else{
//                    ResultsFragment f = ResultsFragment.newInstance(vehicleTimes);
//                    m.beginTransaction().replace(R.id.result_container, f).commit();
//                }
//                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Line> lines){
                for(int i = 0; i < lines.size(); i++){
//                    new TimeGetter1(lines.get(i), code, sofiaTransportApi, i, resultsFragment).execute();
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

        class TimeGetter1 extends AsyncTask<Void, Void, List<Time>>{

            private Line line;
            private String code;
            private SofiaTransportApi api;
            private int index;
            private ResultsFragment fragment;

            public TimeGetter1(Line line, String code, SofiaTransportApi api, int index, ResultsFragment fragment) {
                this.line = line;
                this.code = code;
//                this.api = api;
                this.index = index;
                this.fragment = fragment;
                this.api = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
            }

            @Override
            protected List<Time> doInBackground(Void... voids) {
                Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
                Call<List<Time>> call = this.api.getTimes(code, Integer.toString(line.getId()));
                try {
                    return call.execute().body();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Time> times){
                List<Time> timesToSend;
//                if (times == null) {
//                    timesToSend = new ArrayList<>();
//                }else{
//                    timesToSend = times;
//                }
                ((MainActivity) getActivity()).addTimes(fragment, line, times);
            }
        }

        public void getTimes(String code, FragmentManager manager){
            LoadingFragment l = LoadingFragment.newInstance();
            manager.beginTransaction().replace(R.id.result_container, l).commit();
            SearchQuery query = new SearchQuery(code,l , manager, (MainActivity) getActivity());
            query.execute();
        }


    }

