package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.ResultsAdapter;
import com.bearenterprises.sofiatraffic.adapters.ResultsRecyclerAdapter;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ResultsFragment extends Fragment {
    private static final String TIMES = "param2";

    private RecyclerView resultsView;
    private String stationName;
    private ArrayList<VehicleTimes> vehicleTimes;
    private ResultsAdapter adapter;
    private ResultsRecyclerAdapter resultsRecyclerAdapter;


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
            int idx = 0;
            if(times != null){
                for(VehicleTimes vt : vehicleTimes){
                    if(vt.getLine().equals(line.getName())){
                        vt.setVehicleTimes(times);
                        this.resultsRecyclerAdapter.notifyItemChanged(idx);
                    }
                    idx ++;
                }
            }else {
                Iterator<VehicleTimes> i = vehicleTimes.iterator();
                while (i.hasNext()) {
                    VehicleTimes vt = i.next();
                    if (vt.getLine().equals(line.getName())) {
                        i.remove();
                        this.resultsRecyclerAdapter.notifyItemRemoved(idx);
                    }
                    idx++;
                }
            }

//            if(times == null){
//                vehicleTimes.remove(index);
//            }else{
//                vehicleTimes.get(index).setVehicleTimes(times);
//            }
//            this.adapter.notifyDataSetChanged();
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
        resultsView = (RecyclerView) view.findViewById(R.id.station_times);
//        adapter = new ResultsAdapter(getActivity(), vehicleTimes);
        resultsRecyclerAdapter = new ResultsRecyclerAdapter(getContext(), vehicleTimes);
        resultsView.setAdapter(resultsRecyclerAdapter);
        resultsView.setLayoutManager(new LinearLayoutManager(getContext()));
//        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
//        swingBottomInAnimationAdapter.setAbsListView(resultsView);
//        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
//        resultsView.setAdapter(swingBottomInAnimationAdapter);

        return view;
    }

}
