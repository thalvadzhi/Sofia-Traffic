package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.ResultsAdapter;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.LineTimes;

import java.util.ArrayList;
import java.util.Iterator;


public class TimeResultsFragment extends Fragment {
    private static final String TIMES = "param2";

    private RecyclerView resultsView;
    private String stationName;
    private ArrayList<LineTimes> lineTimes;
    private ResultsAdapter resultsAdapter;


    public TimeResultsFragment() {
        // Required empty public constructor
    }

    public static TimeResultsFragment newInstance(ArrayList<LineTimes> vt) {
        TimeResultsFragment fragment = new TimeResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(TIMES, vt);
        fragment.setArguments(args);
        return fragment;
    }

    public void addTimeSchedule(com.bearenterprises.sofiatraffic.restClient.second.Line line, ArrayList<Time> times){
        synchronized (lineTimes){
            int idx = 0;
            if(times != null){
                for(LineTimes vt : lineTimes){
                    if(vt.getLine().getName().equals(line.getName())){
                        vt.setVehicleTimes(times);
                        this.resultsAdapter.notifyItemChanged(idx);
                    }
                    idx ++;
                }
            }else {
                Iterator<LineTimes> i = lineTimes.iterator();
                while (i.hasNext()) {
                    LineTimes vt = i.next();
                    if (vt.getLine().getName().equals(line.getName())) {
                        i.remove();
                        this.resultsAdapter.notifyItemRemoved(idx);
                    }
                    idx++;
                }
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            stationName = getArguments().getString(STATION_NAME);
            lineTimes = (ArrayList<LineTimes>)getArguments().getSerializable(TIMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        resultsView = (RecyclerView) view.findViewById(R.id.station_times);
        resultsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean enable = false;
                if(recyclerView.getAdapter().getItemCount() == 0){
                    ((MainActivity)getActivity()).setEnablednessRefreshLayout(true);
                    return;
                }
                if(recyclerView != null && recyclerView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    boolean firstItemVisible = manager.findFirstVisibleItemPosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = manager.findFirstCompletelyVisibleItemPosition() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                ((MainActivity)getActivity()).setEnablednessRefreshLayout(enable);
            }
        });
        resultsAdapter = new ResultsAdapter(getContext(), lineTimes);
        resultsView.setAdapter(resultsAdapter);
        resultsView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

}
