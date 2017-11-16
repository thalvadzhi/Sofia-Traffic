package com.bearenterprises.sofiatraffic.fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.adapters.SlideUpLayoutLinesAdapter;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.network.RetrofitUtility;
import com.bearenterprises.sofiatraffic.utilities.Utility;
//import com.google.android.gms.vision.text.Text;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MapSearchFragment extends android.support.v4.app.Fragment {

    public MapSearchFragment() {
        // Required empty public constructor
    }

    private MapFragment mapFragment;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView stopName;
//    private TextView direction;
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
    private LinearLayout coords;
    public MapFragment getMapFragment(){
        return mapFragment;
    }

    private final String COORDINATES_TEMPLATE = "%.6f, %.6f";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map_search, container, false);

        coords = v.findViewById(R.id.coordinates);
        animationDuration = 200;

        linesForAdapter = new ArrayList<>();
        slideUpLayoutLinesAdapter = new SlideUpLayoutLinesAdapter(getContext(), linesForAdapter, null);
        listener = new OnCheckChangeListenerForLaterSetting();
        slidingUpPanelLayout = v.findViewById(R.id.SlideUpPanelLayout);
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


                }else if(previousState == SlidingUpPanelLayout.PanelState.DRAGGING && newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    slidingUpPanelLayout.setPanelHeight(backgroundColor.getHeight());
                    TransitionDrawable transition = (TransitionDrawable) backgroundColor.getBackground();
                    TransitionDrawable transitionShape = (TransitionDrawable) code.getBackground();
                    transition.startTransition(animationDuration);
                    transitionShape.reverseTransition(animationDuration);
                    code.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    stopName.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                }
            }
        });
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        slidingUpPanelLayout.setAnchorPoint(0.4f);
        backgroundColor = v.findViewById(R.id.LinearLayoutBackground);
        getTimes =  v.findViewById(R.id.ImageButtonGetTimes);
        getTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationUtility.showTimes(Integer.toString(currentStop.getCode()), (MainActivity)getActivity());
            }
        });
        favourite =  v.findViewById(R.id.ToggleButtonFavourite);


        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //means previously was not checked
                    CommunicationUtility.addFavourite(currentStop, (MainActivity) getActivity());
                }else{
                    //means previously was
                    CommunicationUtility.removeFavourite(currentStop.getCode(), (MainActivity) getActivity());
                }
            }
        });
        coordinates = v.findViewById(R.id.TextViewCoordinates);
        coordinates.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Координатите на спирката", ((TextView)view).getText());
                clipboard.setPrimaryClip(clip);
                Utility.makeSnackbar("Координатите на спирката бяха копирани!", (MainActivity)getActivity());
                return true;
            }
        });

        code = v.findViewById(R.id.TextViewSlideUpCode);
        stopName = v.findViewById(R.id.TextViewStopName);

        lines = v.findViewById(R.id.RecyclerViewLines);
        lines.setAdapter(slideUpLayoutLinesAdapter);
        lines.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        lines.setNestedScrollingEnabled(true);



        mapFragment = MapFragment.newInstance(null, null);
        Utility.changeFragment(R.id.mapContainer, mapFragment, (MainActivity)getActivity());
        PlacesFragment placesFragment = new PlacesFragment();
        Utility.changeFragment(R.id.placeSearchBarContainer, placesFragment, (MainActivity) getActivity());

        return v;
    }
    public class OnCheckChangeListenerForLaterSetting implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){
                //means previously was not checked
                CommunicationUtility.addFavourite(currentStop, (MainActivity) getActivity());
            }else{
                //means previously was
                CommunicationUtility.removeFavourite(currentStop.getCode(), ((MainActivity) getActivity()));
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
        }else{
            favourite.setChecked(false);
        }
        code.setText(Integer.toString(stop.getCode()));
        favourite.setOnCheckedChangeListener(listener);
        currentStop = stop;
        stopName.setText(stop.getName());
        coordinates.setText(String.format(COORDINATES_TEMPLATE, Float.parseFloat(stop.getLatitude()), Float.parseFloat(stop.getLongitude())));
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
        private String code;
        @Override
        protected ArrayList<Line> doInBackground(String... strings) {
            String code = strings[0];
            this.code = code;
            return RetrofitUtility.getLinesByStationCode(code, (MainActivity)getActivity());
        }

        @Override
        protected void onPostExecute(ArrayList<Line> lines){
            if(lines != null){
                slideUpLayoutLinesAdapter.notifyItemRangeRemoved(0, linesForAdapter.size());
                linesForAdapter.clear();
                linesForAdapter.addAll(lines);
                slideUpLayoutLinesAdapter.setStopCode(this.code);
                slideUpLayoutLinesAdapter.notifyItemRangeInserted(0, linesForAdapter.size());
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onViewStateRestored(Bundle b){
        super.onViewStateRestored(b);
        if(stopName.getText() == null || stopName.getText().equals("")){
            hideSlideUpPanel();
        }
    }

}
