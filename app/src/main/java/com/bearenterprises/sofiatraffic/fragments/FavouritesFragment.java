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
import com.bearenterprises.sofiatraffic.fragments.communication.StationTimeShow;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;

import java.util.ArrayList;
import java.util.Map;

public class FavouritesFragment extends Fragment {

    private ListView v;

    private FavouritesAdapter adapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("view created", "CREATED");
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
                StationTimeShow timeShow = (StationTimeShow) getActivity();
                timeShow.showTimes(st.getCode());
            }
        });
        Log.i("V", (v==null)+"");
        return view;
    }

    public void updateFavourites(MainActivity activity){
        Log.i("A", "B");
        SharedPreferences sp = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        ArrayList<Station> stations = new ArrayList<>();
        for(String key : all.keySet()){
            if (!key.equals(Constants.KEY_LAST_UPDATE)) {
                stations.add(new Station((String) all.get(key), key, null, null));
            }
        }
        adapter = new FavouritesAdapter(getActivity(), stations);
        this.v.setAdapter(adapter);
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

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.i("Attached", "maybe?");
        Log.i("Fragment", (v==null)+"");
    }


}
