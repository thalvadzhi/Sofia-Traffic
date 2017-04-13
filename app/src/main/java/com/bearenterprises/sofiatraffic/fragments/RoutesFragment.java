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
import com.bearenterprises.sofiatraffic.restClient.second.Stop;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROUTES = "routes";
    private static final String TR_TYPE = "tr_type";

    // TODO: Rename and change types of parameters
    private String transportationType;
    private ArrayList<ArrayList<Stop>> routes;
    private RecyclerView routesRecyclerView;


    public RoutesFragment() {
        // Required empty public constructor
    }


    public static RoutesFragment newInstance(ArrayList<ArrayList<Stop>> routes, String type) {
        RoutesFragment fragment = new RoutesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ROUTES, routes);
        args.putString(TR_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routes = (ArrayList<ArrayList<Stop>>) getArguments().getSerializable(ROUTES);
            transportationType = getArguments().getString(TR_TYPE);
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
        final RoutesAdapter alternativeAdapter = new RoutesAdapter(directions, getContext());

        routesRecyclerView.setAdapter(alternativeAdapter);
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
