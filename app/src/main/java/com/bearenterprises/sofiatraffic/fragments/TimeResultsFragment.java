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
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.StopInformationGetter;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        int indexOfLine = getIndexOfLine(line);
        if(indexOfLine != Constants.NO_SUCH_LINE){
            synchronized (lineTimes){
                LineTimes vt = lineTimes.get(indexOfLine);
                vt.setVehicleTimes(times);
                this.timeResultsAdapter.notifyItemChanged(indexOfLine);
            }
        }

    }

    public void removeLine(Line line){
        int indexOfLine = getIndexOfLine(line);
        if(indexOfLine != Constants.NO_SUCH_LINE){
            lineTimes.remove(indexOfLine);
            this.timeResultsAdapter.notifyItemRemoved(indexOfLine);
        }
    }

    private int getIndexOfLine(Line line){
        if(lineTimes != null){
            synchronized (lineTimes){
                int idx = 0;
                    for(LineTimes vt : lineTimes){
                        Line lineOriginal = vt.getLine();
                        if(lineOriginal.equals(line)){
                            return idx;
                        }
                        idx ++;
                    }
                }
            }
        return Constants.NO_SUCH_LINE;
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
        resultsView = view.findViewById(R.id.station_times);
        Collections.sort(lineTimes, new Comparator<LineTimes>() {
            @Override
            public int compare(LineTimes lineTimes, LineTimes t1) {
                Line l1 = lineTimes.getLine();
                Line l2 = t1.getLine();
                if(l1.getType() != l2.getType()){
                    return l1.getType() - l2.getType();
                }else{
                    return Utility.compareLineNames(l1, l2);
                }
            }
        });
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

    public void addTimeScheduleWithDirection(Line line, StopInformationGetter.TimesWithDirection timesWithDirection) {
        int indexOfLine = getIndexOfLine(line);
        if(indexOfLine != Constants.NO_SUCH_LINE){
            synchronized (lineTimes){
                LineTimes vt = lineTimes.get(indexOfLine);
                vt.setSchedule(true);
                vt.setVehicleTimes(timesWithDirection.getTimes());
                vt.setRouteName(timesWithDirection.getDirection());
                this.timeResultsAdapter.notifyItemChanged(indexOfLine);
            }
        }
    }
}
