package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.FavouritesAdapter;
import com.bearenterprises.sofiatraffic.adapters.FavouritesRecyclerAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.fragments.communication.StationTimeShow;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FavouritesFragment extends Fragment {

    private RecyclerView v;

    private FavouritesRecyclerAdapter adapter;
    private ArrayList<Station> favouriteStations;

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
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        favouriteStations = new ArrayList<>();
        Gson gson = new Gson();
        for(String key : all.keySet()){
            if (!key.equals(Constants.KEY_LAST_UPDATE)) {
                Station st = gson.fromJson(all.get(key).toString(), Station.class);
                favouriteStations.add(st);
            }
        }
        adapter = new FavouritesRecyclerAdapter(getContext(), favouriteStations);

        v = (RecyclerView) view.findViewById(R.id.list_view_favourites);
        v.setAdapter(adapter);
        v.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    public void addFavourite(Station station){
        FavouritesModifier.save(station, getContext());
        favouriteStations.add(station);
        this.adapter.notifyItemInserted(favouriteStations.size() - 1);
    }

    public void removeFavourite(String code){
        FavouritesModifier.remove(code, getContext());
        Iterator<Station> i = favouriteStations.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Station st = i.next();
            if (st.getCode().equals(code)) {
                i.remove();
                this.adapter.notifyItemRemoved(idx);
                break;
            }
            idx++;
        }

    }


}
