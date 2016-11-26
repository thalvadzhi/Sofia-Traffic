package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.FavouritesAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavouritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView v;

    private OnFragmentInteractionListener mListener;
    private FavouritesAdapter adapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        ArrayList<Station> stations = new ArrayList<>();
        for(String key : all.keySet()){
            if (!key.equals(Constants.KEY_LAST_UPDATE)) {
                stations.add(new Station((String) all.get(key), key, null, null));
            }
        }
        adapter = new FavouritesAdapter(getActivity(), stations);

        v = (ListView) view.findViewById(R.id.list_view_favourites);
        v.setAdapter(adapter);
        v.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setMessage("Наистина ли искате да изтриете тази любима спирка?")
                        .setPositiveButton("Да", new FavouriteDeleterListener(parent, position))
                        .setNegativeButton("Не", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return true;
            }
        });
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Station st = (Station) parent.getItemAtPosition(position);
                MainActivity main = (MainActivity) getActivity();
                main.setPage(0);
                SearchFragment s = SearchFragment.newInstance(0);
                Log.i("GGG", "TUK PONE?");
                s.getData(st.getCode(), getFragmentManager(), (MainActivity)getActivity());
            }
        });
        return view;
    }

    public void updateFavourites(){
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        ArrayList<Station> stations = new ArrayList<>();
        for(String key : all.keySet()){
            if (!key.equals(Constants.KEY_LAST_UPDATE)) {
                stations.add(new Station((String) all.get(key), key, null, null));
            }
        }
        adapter = new FavouritesAdapter(getActivity(), stations);
        v.setAdapter(adapter);
    }

    public class FavouriteDeleterListener implements DialogInterface.OnClickListener{

        private AdapterView<?> parent;
        private int position;
        public FavouriteDeleterListener(AdapterView<?> parent, int position){
            this.parent = parent;
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Station station = (Station)this.parent.getItemAtPosition(this.position);
            FavouritesModifier.remove(station.getCode(), getContext());
            ((MainActivity)getActivity()).notifyDatasetChanged();
        }
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
