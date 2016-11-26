package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.utilities.RESTClient;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.bearenterprises.sofiatraffic.utilities.JSONParser;
import com.bearenterprises.sofiatraffic.utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by thalv on 02-Jul-16.
 */

    public class SearchFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public SearchFragment() {


        }


        private CoordinatorLayout coordinatorLayout;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SearchFragment newInstance(int sectionNumber) {
            SearchFragment fragment = new SearchFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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
                    StationNameFragment stationNameFragment = StationNameFragment.newInstance(code);
                    getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.station_name_fragment, stationNameFragment).commit();
                    getData(code, getFragmentManager(), (MainActivity) getActivity());
                }
            });

            return rootView;
        }

        private Station getStation(String code, SofiaTransportApi sofiaTransportApi) throws IOException {
            Call<Station> call = sofiaTransportApi.getStation(code);
            return call.execute().body();
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
                Call<List<Time>> call = sofiaTransportApi.getTimes(code, Integer.toString(line.getId()));
                try {
                    this.times = (ArrayList<Time>)call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        class SearchQuery implements Runnable{
            private String code;
            private LoadingFragment l;
            private MainActivity m;
            public SearchQuery(String code, LoadingFragment l, MainActivity m){
                this.code = code;
                this.l = l;
                this.m = m;
            }

            @Override
            public void run(){
                SofiaTransportApi sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
                Station station= null;
                try {
                    station = getStation(code, sofiaTransportApi);
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
                        m.detachFragment(l);
                        Utility.makeSnackbar("Няма информация", coordinatorLayout);
                    }else{
                        m.changeFragmentTimes(vehicleTimes);
                    }
            }

        }

        public void getData(String code, android.support.v4.app.FragmentManager manager, MainActivity m){
            LoadingFragment l = LoadingFragment.newInstance("", "");
            manager.beginTransaction().replace(R.id.result_container, l).commit();
            Thread t = new Thread(new SearchQuery(code, l, m));
            t.start();
        }


    }

