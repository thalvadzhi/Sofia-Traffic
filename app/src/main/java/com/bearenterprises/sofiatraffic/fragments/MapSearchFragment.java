package com.bearenterprises.sofiatraffic.fragments;


import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.SlideUpLayoutLinesAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;
import com.bearenterprises.sofiatraffic.utilities.ParseApiError;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.vision.text.Text;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MapSearchFragment extends android.support.v4.app.Fragment {

    public MapSearchFragment() {
        // Required empty public constructor
    }

    private MapFragment mapFragment;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView stopName;
    private TextView direction;
    private TextView code;
    private TextView coordinates;
    private RecyclerView lines;
    private ImageButton getTimes;
    private ToggleButton favourite;
    private ArrayList<Line> linesForAdapter;
    private SlideUpLayoutLinesAdapter slideUpLayoutLinesAdapter;
    private LinearLayout backgroundColor;
    private Stop currentStop;
    private OnCheckChangeListenerForLaterSetting listener;
    private int animationDuration;
    public MapFragment getMapFragment(){
        return mapFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_search, container, false);
        animationDuration = 200;
        // Inflate the layout for this fragment

        linesForAdapter = new ArrayList<>();
        slideUpLayoutLinesAdapter = new SlideUpLayoutLinesAdapter(getContext(), linesForAdapter);
        listener = new OnCheckChangeListenerForLaterSetting();
        slidingUpPanelLayout = (SlidingUpPanelLayout) v.findViewById(R.id.SlideUpPanelLayout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(previousState == SlidingUpPanelLayout.PanelState.COLLAPSED && newState == SlidingUpPanelLayout.PanelState.DRAGGING){
                    TransitionDrawable transition = (TransitionDrawable) backgroundColor.getBackground();
                    TransitionDrawable transitionShape = (TransitionDrawable) code.getBackground();
                    transition.reverseTransition(animationDuration);
                    transitionShape.reverseTransition(animationDuration);
                    code.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                    stopName.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    direction.setTextColor(ContextCompat.getColor(getContext(), R.color.white));


                }else if(previousState == SlidingUpPanelLayout.PanelState.DRAGGING && newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    slidingUpPanelLayout.setPanelHeight(backgroundColor.getHeight());
                    TransitionDrawable transition = (TransitionDrawable) backgroundColor.getBackground();
                    TransitionDrawable transitionShape = (TransitionDrawable) code.getBackground();
                    transition.startTransition(animationDuration);
                    transitionShape.reverseTransition(animationDuration);
                    code.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    stopName.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                    direction.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                }
            }
        });
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        slidingUpPanelLayout.setAnchorPoint(0.4f);
        backgroundColor = (LinearLayout) v.findViewById(R.id.LinearLayoutBackground);
        getTimes = (ImageButton) v.findViewById(R.id.ImageButtonGetTimes);
        getTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showTimes(Integer.toString(currentStop.getCode()));
            }
        });
        favourite = (ToggleButton) v.findViewById(R.id.ToggleButtonFavourite);


        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //means previously was not checked
                    ((MainActivity) getActivity()).addFavourite(currentStop);
                }else{
                    //means previously was
                    ((MainActivity) getActivity()).removeFavourite(currentStop.getCode());
                }
            }
        });
        coordinates = (TextView) v.findViewById(R.id.TextViewCoordinates);
        coordinates.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Координатите на спирката", ((TextView)view).getText());
                clipboard.setPrimaryClip(clip);
                ((MainActivity)getActivity()).makeSnackbar("Координатите на спирката бяха копирани!");
                return true;
            }
        });

        code = (TextView) v.findViewById(R.id.TextViewSlideUpCode);
        stopName = (TextView) v.findViewById(R.id.TextViewStopName);
        direction = (TextView) v.findViewById(R.id.TextViewDirection);
        lines = (RecyclerView) v.findViewById(R.id.RecyclerViewLines);
        lines.setAdapter(slideUpLayoutLinesAdapter);
        lines.setLayoutManager(new GridLayoutManager(getContext(), 6));

        mapFragment = MapFragment.newInstance(null, null);
        ((MainActivity)getActivity()).changeFragment(R.id.mapContainer, mapFragment);
        PlacesFragment placesFragment = new PlacesFragment();
        ((MainActivity)getActivity()).changeFragment(R.id.placeSearchBarContainer, placesFragment);

        return v;
    }
    public class OnCheckChangeListenerForLaterSetting implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){
                //means previously was not checked
                ((MainActivity) getActivity()).addFavourite(currentStop);
            }else{
                //means previously was
                ((MainActivity) getActivity()).removeFavourite(currentStop.getCode());
            }
        }
    }

    private boolean checkIfAlreadyInFavourites(String code){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_FAVOURITES, Context.MODE_PRIVATE);
        String name = preferences.getString(code, null);
        return name != null;
    }
    public void showMoreInfoInSlideUp(Stop stop){
        favourite.setOnCheckedChangeListener(null);
        if(checkIfAlreadyInFavourites(Integer.toString(stop.getCode()))){
            favourite.setChecked(true);
        }
        code.setText(Integer.toString(stop.getCode()));
        favourite.setOnCheckedChangeListener(listener);
        currentStop = stop;
        stopName.setText(stop.getName());
        direction.setText(stop.getDirection());

        coordinates.setText(stop.getLatitude() + ", " + stop.getLongtitude());
        GetLinesOnStop getLinesOnStop = new GetLinesOnStop();
        getLinesOnStop.execute(Integer.toString(stop.getCode()));
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        slidingUpPanelLayout.setPanelHeight(backgroundColor.getHeight());
    }


    public void hideSlideUpPanel(){
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void collapseSlideUpPanel(){
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public class GetLinesOnStop extends AsyncTask<String, Void, ArrayList<Line>>{

        @Override
        protected void onPreExecute(){
            linesForAdapter.clear();
            slideUpLayoutLinesAdapter.notifyDataSetChanged();
        }
        @Override
        protected ArrayList<Line> doInBackground(String... strings) {
            String code = strings[0];
            return ((MainActivity)getActivity()).getLinesByStationCode(code);
        }

        @Override
        protected void onPostExecute(ArrayList<Line> lines){
            if(lines != null){
                linesForAdapter.clear();
                linesForAdapter.addAll(lines);
                slideUpLayoutLinesAdapter.notifyItemRangeInserted(0, lines.size());
            }
        }
    }

}
