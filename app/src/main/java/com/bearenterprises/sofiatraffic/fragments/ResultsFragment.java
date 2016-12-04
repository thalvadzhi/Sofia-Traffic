package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.ResultsAdapter;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATION_NAME = "param1";
    private static final String TIMES = "param2";

    // TODO: Rename and change types of parameters
//    private TextView stationNameView;
    private ListView resultsView;
    private String stationName;
    private ArrayList<VehicleTimes> vehicleTimes;

    private OnFragmentInteractionListener mListener;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(ArrayList<VehicleTimes> vt) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(TIMES, vt);
//        args.putString(STATION_NAME, stationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            stationName = getArguments().getString(STATION_NAME);
            vehicleTimes = (ArrayList<VehicleTimes>)getArguments().getSerializable(TIMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
//        stationNameView = (TextView) view.findViewById(R.id.station_name);
//        stationNameView.setTextSize(25);
        resultsView = (ListView) view.findViewById(R.id.station_times);
//        stationNameView.setText(stationName);
        ResultsAdapter adapter = new ResultsAdapter(getActivity(), vehicleTimes);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
        swingBottomInAnimationAdapter.setAbsListView(resultsView);
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
        resultsView.setAdapter(swingBottomInAnimationAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
