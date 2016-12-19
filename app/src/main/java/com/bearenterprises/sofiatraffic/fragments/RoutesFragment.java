package com.bearenterprises.sofiatraffic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.bearenterprises.sofiatraffic.views.AnimatedExpandableListView;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.RoutesAdapter;
import com.bearenterprises.sofiatraffic.fragments.communication.StationTimeShow;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;

import java.util.ArrayList;

public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROUTES = "routes";
    private static final String TR_TYPE = "tr_type";

    // TODO: Rename and change types of parameters
    private String transportationType;
    private ArrayList<ArrayList<Stop>> routes;


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
        final AnimatedExpandableListView routesListView = (AnimatedExpandableListView) view.findViewById(R.id.routesListView);
        final RoutesAdapter adapter = new RoutesAdapter(this.routes, transportationType, getContext());
        routesListView.setAdapter(adapter);

        routesListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (routesListView.isGroupExpanded(groupPosition)) {
                    routesListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    routesListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });


        routesListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int child, long l) {
                Stop station = adapter.getChild(group, child);
                ((StationTimeShow) getActivity()).showTimes(Integer.toString(station.getCode()));
                return true;
            }
        });

        return view;
    }



}
