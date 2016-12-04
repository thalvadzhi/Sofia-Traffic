package com.bearenterprises.sofiatraffic.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StationNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationNameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATION_CODE = "station_code";

    // TODO: Rename and change types of parameters
    private String mStationCode;
    private TextView textView;
    private ToggleButton toggleButton;


    public StationNameFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StationNameFragment newInstance(String stationName) {
        StationNameFragment fragment = new StationNameFragment();
        Bundle args = new Bundle();
        args.putString(STATION_CODE, stationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStationCode = getArguments().getString(STATION_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_name, container, false);
        toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
        textView = (TextView) view.findViewById(R.id.station_name_text_view);

        String stationName = getStationName(mStationCode);
        if(stationName == null){
            toggleButton.setVisibility(View.GONE);
        }else{
            textView.setText(stationName);
            if (checkIfAlreadyInFavourites(mStationCode)){
                toggleButton.setChecked(true);
            }
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        //means previously was not checked
                        FavouritesModifier.save(mStationCode, getContext());
                        ((MainActivity) getActivity()).updateFavourites();
                    }else{
                        //means previously was
                        FavouritesModifier.remove(mStationCode, getContext());
                        ((MainActivity) getActivity()).updateFavourites();
                    }
                }
            });
        }






        return view;
    }

    private boolean checkIfAlreadyInFavourites(String code){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        String name = preferences.getString(code, null);
        return name != null;
    }

    private String getStationName(String code){
        DbManipulator manipulator = new DbManipulator(this.getContext());
        try {
            Cursor cursor = manipulator.readRawQuery("SELECT " + DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME + " FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + " =?", new String[]{code});
            if (cursor == null) {
                return null;
            }
            if(cursor.getCount() == 0){
                ((MainActivity)getActivity()).makeSnackbar("Не съществува такава спирка.");
                return null;
            }
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
        }finally {
            manipulator.closeDb();
        }
    }


}
