package com.bearenterprises.sofiatraffic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.adapters.FavouritesAdapter;
import com.bearenterprises.sofiatraffic.callback.OnStartDragListener;
import com.bearenterprises.sofiatraffic.callback.ReorderCallback;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bearenterprises.sofiatraffic.utilities.favourites.FavouritesModifier;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class FavouritesFragment extends Fragment implements OnStartDragListener {

    private RecyclerView v;

    private FavouritesAdapter adapter;
    private ArrayList<Stop> favouriteStations;
    private ItemTouchHelper touchHelper;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    /**
     * Use this comparator to sort favourite stations by favourite index
     */
    private class StopFavouriteIndexComparator implements Comparator<Stop> {

        @Override
        public int compare(Stop s1, Stop s2) {
            return s1.getFavouriteIndex().compareTo(s2.getFavouriteIndex());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        favouriteStations = new ArrayList<>();
        Gson gson = new Gson();
        int idx = 0;
        for (String key : all.keySet()) {
            if (!key.equals(Constants.KEY_LAST_UPDATE)) {
                Stop st = gson.fromJson(all.get(key).toString(), Stop.class);
                if (st.getFavouriteIndex() == null) {
                    st.setFavouriteIndex(idx);
                }
                if(st.getLineTypes() == null){
                    ArrayList<Stop> stationByCode = DbUtility.getStationsByCode(Integer.toString(st.getCode()), (MainActivity) getActivity());
                    if(stationByCode != null && stationByCode.size() > 0){
                        st.setLineTypes(stationByCode.get(0).getLineTypes());
                    }
                }
                favouriteStations.add(st);
                idx++;
            }
        }
        Collections.sort(favouriteStations, new StopFavouriteIndexComparator());
        adapter = new FavouritesAdapter(getContext(), favouriteStations, this);
        v = (RecyclerView) view.findViewById(R.id.list_view_favourites);
        v.setAdapter(adapter);
        v.setLayoutManager(new LinearLayoutManager(getContext()));

        //allowing reordering of favourites
        ItemTouchHelper.Callback callback = new ReorderCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(v);
        return view;
    }


    public void addFavourite(Stop station) {
        FavouritesModifier.save(station, getContext());
        favouriteStations.add(station);
        this.adapter.notifyItemInserted(favouriteStations.size() - 1);
    }

    public void removeFavourite(int code) {
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


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).hideSoftKeyboad();
    }
}
