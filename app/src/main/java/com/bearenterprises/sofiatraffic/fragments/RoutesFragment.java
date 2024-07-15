package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.adapters.RoutesAdapter;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.Direction;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROUTES = "routes_accurate";
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

        Gson gson = new Gson();
        String routesJson = gson.toJson(routes);
        args.putString(ROUTES, routesJson);

//        args.putSerializable(ROUTES, routes);
        args.putString(TR_TYPE, type);
        args.putInt(ST_CODE, stopCode);
        fragment.setArguments(args);
        return fragment;
    }

//    public static RoutesFragment newInstanceSchedule(ArrayList<ArrayList<Stop>> routes, String type, int stopCode) {
//        RoutesFragment fragment = new RoutesFragment();
//        Bundle args = new Bundle();
//        Parcelable routesParcel = Parcels.wrap(routes);
//        args.putParcelable(SCHEDULE_ROUTES, routesParcel);
//        args.putString(TR_TYPE, type);
//        args.putInt(ST_CODE, stopCode);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState){
//        super.onActivityCreated(savedInstanceState);
//        Log.i("Maina", "Activity created");
//        if (savedInstanceState != null){
//            Log.i("Maina", "Activity created non null instance state");
//
//            routes = (ArrayList<ArrayList<Stop>>) savedInstanceState.getSerializable(ROUTES);
//            scheduleRoutes = (ArrayList<ArrayList<Stop>>) savedInstanceState.getSerializable(SCHEDULE_ROUTES);
//            transportationType = savedInstanceState.getString(TR_TYPE);
//            stopCode = savedInstanceState.getInt(ST_CODE);
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            Type routesType = new TypeToken<ArrayList<ArrayList<Stop>>>(){}.getType();

            routes = gson.fromJson(getArguments().getString(ROUTES), routesType);
            scheduleRoutes = gson.fromJson(getArguments().getString(SCHEDULE_ROUTES), routesType);
            transportationType = getArguments().getString(TR_TYPE);
            stopCode = getArguments().getInt(ST_CODE);
        }

        if (savedInstanceState != null){
            Gson gson = new Gson();
            Type routesType = new TypeToken<ArrayList<ArrayList<Stop>>>(){}.getType();

            routes = gson.fromJson(savedInstanceState.getString(ROUTES), routesType);
            scheduleRoutes = gson.fromJson(savedInstanceState.getString(SCHEDULE_ROUTES), routesType);
            transportationType = savedInstanceState.getString(TR_TYPE);
            stopCode = savedInstanceState.getInt(ST_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
//        Bundle args = new Bundle();
        Gson gson = new Gson();
        String routesJson = gson.toJson(routes);
        savedInstanceState.putString(ROUTES, routesJson);
        savedInstanceState.putString(TR_TYPE, transportationType);
        savedInstanceState.putInt(ST_CODE, stopCode);
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        routesRecyclerView =  view.findViewById(R.id.recyclerView_routes);
        List<Direction> directions = new ArrayList<>();
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
        ((RoutesAdapter)adapter).setHighlightedPosition(stopCode);
        adapter.notifyChildChanged(groupPosition, childPosition);

    }
}
