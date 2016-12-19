package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.FavouritesAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FavouritesFragment extends Fragment {

    private RecyclerView v;

    private FavouritesAdapter adapter;
    private ArrayList<Stop> favouriteStations;

    public FavouritesFragment() {
        // Required empty public constructor
    }


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
                Stop st = gson.fromJson(all.get(key).toString(), Stop.class);
                favouriteStations.add(st);
            }
        }
        adapter = new FavouritesAdapter(getContext(), favouriteStations);

        v = (RecyclerView) view.findViewById(R.id.list_view_favourites);
        v.setAdapter(adapter);
        v.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    public void addFavourite(Stop station){
        FavouritesModifier.save(station, getContext());
        favouriteStations.add(station);
        this.adapter.notifyItemInserted(favouriteStations.size() - 1);
    }

    public void removeFavourite(int code){
        FavouritesModifier.remove(code, getContext());
        Iterator<Stop> i = favouriteStations.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Stop st = i.next();
            if (st.getCode().equals(code)) {
                i.remove();
                this.adapter.notifyItemRemoved(idx);
                break;
            }
            idx++;
        }

    }


}
