package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.adapters.RoutesAdapter;
import com.bearenterprises.sofiatraffic.adapters.RoutesAdapterSchedules;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.Direction;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.DirectionSchedules;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROUTES = "routesSchedules";
    private static final String SCHEDULE_ROUTES = "schedule_routes";

    private static final String TR_TYPE = "tr_type";
    private static final String ST_CODE = "st_code";

    // TODO: Rename and change types of parameters
    private String transportationType;
    private ArrayList<ArrayList<Stop>> routes;
    private ArrayList<ArrayList<Stop>> scheduleRoutes;
    private RecyclerView routesRecyclerView;
    private Integer stopCode;
    private ExpandableRecyclerAdapter adapter;


    public RoutesFragment() {
        // Required empty public constructor
    }


    public static RoutesFragment newInstance(ArrayList<ArrayList<Stop>> routes, String type, int stopCode) {
        RoutesFragment fragment = new RoutesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ROUTES, routes);
        args.putString(TR_TYPE, type);
        args.putInt(ST_CODE, stopCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static RoutesFragment newInstanceSchedule(ArrayList<ArrayList<Stop>> routes, String type, int stopCode) {
        RoutesFragment fragment = new RoutesFragment();
        Bundle args = new Bundle();
        args.putSerializable(SCHEDULE_ROUTES, routes);
        args.putString(TR_TYPE, type);
        args.putInt(ST_CODE, stopCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routes = (ArrayList<ArrayList<Stop>>) getArguments().getSerializable(ROUTES);
            scheduleRoutes = (ArrayList<ArrayList<Stop>>) getArguments().getSerializable(SCHEDULE_ROUTES);
            transportationType = getArguments().getString(TR_TYPE);
            stopCode = getArguments().getInt(ST_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        routesRecyclerView =  view.findViewById(R.id.recyclerView_routes);
        List<Direction> directions = new ArrayList<>();
        List<DirectionSchedules> directionSchedules = new ArrayList<>();
        if(routes != null){
            for (ArrayList<Stop> route : routes){
                directions.add(new Direction(route, transportationType));
            }
            adapter = new RoutesAdapter(directions, getContext());

        }else if(scheduleRoutes != null){
            for (ArrayList<Stop> route : scheduleRoutes){

                directions.add(new Direction(route, transportationType));
            }
            adapter = new RoutesAdapter(directions, getContext());
        }
        routesRecyclerView.setAdapter(adapter);
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (stopCode != null && stopCode != -1){
            expandGroup(stopCode);
            scrollToChild(stopCode);
        }
        return view;
    }

    /**
     * Expands the group that the stop with stopCode code is in
     * @param stopCode
     */
    public void expandGroup(Integer stopCode){
        if(stopCode == null){
            return;
        }
        adapter.collapseAllParents();
        int i = 0;
        Integer directionToExpand  = null;
        if(routes != null){
            //TODO fix repetitive code
            for (ArrayList<Stop> route : routes){
                if(stopCode != null){
                    for(Stop st : route){
                        if(st.getCode().equals(stopCode)){
                            directionToExpand = i;
                            break;
                        }
                    }
                    i++;
                }
            }
        }else{
            for (ArrayList<Stop> route : scheduleRoutes){
                if(stopCode != null){
                    for(Stop st : route){
                        if(st.getCode().equals(stopCode)){
                            directionToExpand = i;
                            break;
                        }
                    }
                    i++;
                }
            }
        }
        if(directionToExpand != null){
            adapter.expandParent(directionToExpand);
        }
    }

    /**
     * Scrolls to position where the stop with stopCode code is.
     * @param stopCode
     */
    public void scrollToChild(Integer stopCode){
        if(stopCode == null){
            return;
        }
        Integer childPosition = null;
        Integer groupPosition = null;
        int group = -1;
        if (routes != null){
            //TODO fix repetitve code
            for (ArrayList<Stop> route : routes){
                int i = 0;
                group++;
                if(stopCode != null){
                    for(Stop st : route){
                        if(st.getCode().equals(stopCode)){
                            childPosition = i;
                            groupPosition = group;
                            break;
                        }
                        i++;
                    }
                }
            }
        }else{
            for (ArrayList<Stop> route : scheduleRoutes){
                int i = 0;
                group++;
                if(stopCode != null){
                    for(Stop st : route){
                        if(st.getCode().equals(stopCode)){
                            childPosition = i;
                            groupPosition = group;
                            break;
                        }
                        i++;
                    }
                }
            }
        }
        LinearLayoutManager lm = (LinearLayoutManager)routesRecyclerView.getLayoutManager();
        if(lm == null || childPosition == null || groupPosition == null){
            return;
        }
        lm.scrollToPositionWithOffset(childPosition + 1 + groupPosition, 0);
        ((RoutesAdapter)adapter).setHighlightedPosition(groupPosition, childPosition);
        adapter.notifyChildChanged(groupPosition, childPosition);

    }
}
