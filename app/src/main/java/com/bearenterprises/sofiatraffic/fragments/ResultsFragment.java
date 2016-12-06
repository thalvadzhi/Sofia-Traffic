package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.ResultsAdapter;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ResultsFragment extends Fragment {
    private static final String TIMES = "param2";

    private ListView resultsView;
    private String stationName;
    private ArrayList<VehicleTimes> vehicleTimes;
    private ResultsAdapter adapter;


    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(ArrayList<VehicleTimes> vt) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(TIMES, vt);
//        args.putString(STATION_NAME, stationName);
        fragment.setArguments(args);
        return fragment;
    }

    public void addTimeSchedule(Line line, ArrayList<Time> times){
        synchronized (vehicleTimes){

            if(times != null){
                for(VehicleTimes vt : vehicleTimes){
                    if(vt.getLine().equals(line.getName())){
                        vt.setVehicleTimes(times);
                    }
                }
            }else {
                Iterator<VehicleTimes> i = vehicleTimes.iterator();
                while (i.hasNext()) {
                    VehicleTimes vt = i.next();
                    if (vt.getLine().equals(line.getName())) {
                        i.remove();
                    }
                }
            }

//            if(times == null){
//                vehicleTimes.remove(index);
//            }else{
//                vehicleTimes.get(index).setVehicleTimes(times);
//            }
            this.adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            stationName = getArguments().getString(STATION_NAME);
            vehicleTimes = (ArrayList<VehicleTimes>)getArguments().getSerializable(TIMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        resultsView = (ListView) view.findViewById(R.id.station_times);
        adapter = new ResultsAdapter(getActivity(), vehicleTimes);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
        swingBottomInAnimationAdapter.setAbsListView(resultsView);
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
        resultsView.setAdapter(swingBottomInAnimationAdapter);

        return view;
    }

}
