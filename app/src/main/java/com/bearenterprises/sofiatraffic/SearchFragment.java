package com.bearenterprises.sofiatraffic;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.bearenterprises.sofiatraffic.utilities.Utility.toastOnUiThread;
import com.bearenterprises.sofiatraffic.Constants.Constants;
import com.bearenterprises.sofiatraffic.stations.Line;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;

import java.util.ArrayList;

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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button b = (Button) rootView.findViewById(R.id.button_search);
            final EditText t = (EditText) rootView.findViewById(R.id.station_code);
            Button save = (Button) rootView.findViewById(R.id.button_favourite);
            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String code = t.getText().toString();
                    getData(code, getFragmentManager(), (MainActivity)getActivity());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String code = t.getText().toString();
                    FavouriteSaver.save(code, getContext());
                    ((MainActivity)getActivity()).notifyDatasetChanged();
                }
            });

            return rootView;
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
                ArrayList<VehicleTimes> vt  = new ArrayList<>();
                String stationName = null;
                try {
                    String jsonLines =  RESTClient.getHTML(String.format(Constants.REST_QUERY_LINES, code));
                    ArrayList<Line> lines = JSONParser.getLines(jsonLines);
                    stationName = JSONParser.getStation(jsonLines);
                    for(Line line : lines){
                        try {
                            String timesJson = RESTClient.getHTML(String.format(Constants.REST_QUERY_TIMES, code, line.getId()));
                            String times = JSONParser.getLineTimes(timesJson);
                            vt.add(new VehicleTimes(line.getName(), line.getType(), times));
                        }catch (Exception e){
//                           Toast.makeText(getActivity(), "No information for line: " + message, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                    ArrayList<VehicleTimes> vt  = new ArrayList<>();
//                    vt.add(new VehicleTimes("10TM", "0", "11:20 20:30 11:20 20:30 11:20 20:30 11:20 20:30 11:20 20:30"));
//                    vt.add(new VehicleTimes("98", "1", "10:20 20:30"));
//                    vt.add(new VehicleTimes("11", "2", "10:20 20:30"));
//                    String stationName = "Мусагеница";
                    if(vt.size() == 0){
                        m.detachFragment(l);
                        toastOnUiThread("Няма информация", m);
                    }else{
                        m.changeFragment(vt, stationName);
                    }


            }
        }

        public void removeFragment(LoadingFragment l){
            android.support.v4.app.FragmentManager manager = getFragmentManager();
            if(l == null){
                Log.i("GGG", "WHYY");
            }
            manager.beginTransaction().detach(l).commit();
        }

        public void changeFragment(ArrayList<VehicleTimes> vt, String stationName){
            ResultsFragment f = ResultsFragment.newInstance(stationName, vt);
            getFragmentManager().beginTransaction().replace(R.id.result_container, f).commit();
        }

        public void getData(String code, android.support.v4.app.FragmentManager manager, MainActivity m){
            LoadingFragment l = LoadingFragment.newInstance("", "");
            manager.beginTransaction().replace(R.id.result_container, l).commit();
            Thread t = new Thread(new SearchQuery(code, l, m));
            t.start();
        }


    }

