package com.bearenterprises.sofiatraffic.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowAllOnMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowAllOnMapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_STOPS = "stops";

    // TODO: Rename and change types of parameters
    private ArrayList<Stop> stops;
    private TextView showOnMapText;
    private ImageView showOnMapButton;

    public ShowAllOnMapFragment() {
        // Required empty public constructor
    }

    public static ShowAllOnMapFragment newInstance(ArrayList<Stop> stops) {
        ShowAllOnMapFragment fragment = new ShowAllOnMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STOPS, stops);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stops = (ArrayList<Stop>)getArguments().getSerializable(ARG_STOPS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_all_on_map, container, false);
        showOnMapText = view.findViewById(R.id.show_all_name_results_on_map);
        showOnMapButton = view.findViewById(R.id.show_all_name_results_on_map_button);

        if(showOnMapButton == null || showOnMapText == null){
            return view;
        }

        if (stops == null){
            return view;
        }

        if(stops.size() == 0){
            showOnMapText.setText("Няма намерени спирки");
            showOnMapButton.setVisibility(View.GONE);
        }else{
            showOnMapText.setText("Всички на картата");
            showOnMapButton.setVisibility(View.VISIBLE);
            showOnMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbUtility.addLineTypes(stops, (MainActivity)getActivity());
                    CommunicationUtility.showOnMap(stops, false, (MainActivity)getActivity());
                }
            });
        }
        return view;
    }

}
