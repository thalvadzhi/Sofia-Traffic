package com.bearenterprises.sofiatraffic.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.NameResultAdapter;
import com.bearenterprises.sofiatraffic.restClient.Stop;

import java.util.ArrayList;


public class NameResultsFragment extends Fragment {
    private static final String ARG_STOPS = "stops";

    private ArrayList<Stop> stops;
    private RecyclerView recyclerView;
    private NameResultAdapter nameResultAdapter;


    public NameResultsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NameResultsFragment newInstance(ArrayList<Stop> stops) {
        NameResultsFragment fragment = new NameResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STOPS, stops);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stops = (ArrayList<Stop>) getArguments().getSerializable(ARG_STOPS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_name_results, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_name_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        nameResultAdapter = new NameResultAdapter(stops, getContext());
        recyclerView.setAdapter(nameResultAdapter);
        return view;
    }
}
