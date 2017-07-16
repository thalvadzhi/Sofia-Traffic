package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.TimeResultsAdapter;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;

import java.util.ArrayList;
import java.util.Iterator;


public class TimeResultsFragment extends Fragment {
    private static final String TIMES = "TIMES";
    private static final String STOP = "STOP";

    private RecyclerView resultsView;
    private ArrayList<LineTimes> lineTimes;
    private TimeResultsAdapter timeResultsAdapter;
    private Stop stop;


    public TimeResultsFragment() {
        // Required empty public constructor
    }

    public static TimeResultsFragment newInstance(ArrayList<LineTimes> vt, Stop stop) {
        TimeResultsFragment fragment = new TimeResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(TIMES, vt);
        args.putSerializable(STOP, stop);
        fragment.setArguments(args);
        return fragment;
    }

    public void addTimeSchedule(Line line, ArrayList<Time> times){
        if(lineTimes != null){
            synchronized (lineTimes){
                int idx = 0;
                if(times != null){
                    for(LineTimes vt : lineTimes){
                        if(vt.getLine().getId().equals(line.getId())){
                            vt.setVehicleTimes(times);
                            this.timeResultsAdapter.notifyItemChanged(idx);
                        }
                        idx ++;
                    }
                }else {
                    Iterator<LineTimes> i = lineTimes.iterator();
                    while (i.hasNext()) {
                        LineTimes vt = i.next();
                        if (vt.getLine().getId().equals(line.getId())) {
                            i.remove();
                            this.timeResultsAdapter.notifyItemRemoved(idx);
                        }
                        idx++;
                    }
                }
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lineTimes = (ArrayList<LineTimes>)getArguments().getSerializable(TIMES);
            stop = (Stop)getArguments().getSerializable(STOP);
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
                    CommunicationUtility.setEnablednessRefreshLayout(true, (MainActivity)getActivity());
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
                CommunicationUtility.setEnablednessRefreshLayout(enable, (MainActivity) getActivity());
            }
        });
        timeResultsAdapter = new TimeResultsAdapter(getContext(), lineTimes, stop);
        resultsView.setAdapter(timeResultsAdapter);
        resultsView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

}
