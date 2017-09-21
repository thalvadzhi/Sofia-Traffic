package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.adapters.RoutesAdapter;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.Direction;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.Stop;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROUTES = "routes";
    private static final String TR_TYPE = "tr_type";
    private static final String ST_CODE = "st_code";

    // TODO: Rename and change types of parameters
    private String transportationType;
    private ArrayList<ArrayList<Stop>> routes;
    private RecyclerView routesRecyclerView;
    private Integer stopCode;
    private RoutesAdapter adapter;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routes = (ArrayList<ArrayList<Stop>>) getArguments().getSerializable(ROUTES);
            transportationType = getArguments().getString(TR_TYPE);
            stopCode = getArguments().getInt(ST_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        routesRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_routes);
        List<Direction> directions = new ArrayList<>();
        for (ArrayList<Stop> route : routes){
            directions.add(new Direction(route, transportationType));
        }
        adapter = new RoutesAdapter(directions, getContext());
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
        LinearLayoutManager lm = (LinearLayoutManager)routesRecyclerView.getLayoutManager();
        if(lm == null || childPosition == null || groupPosition == null){
            return;
        }
        lm.scrollToPositionWithOffset(childPosition + 1 + groupPosition, 0);
        adapter.setHighlightedPosition(groupPosition, childPosition);
        adapter.notifyChildChanged(groupPosition, childPosition);

    }
}
