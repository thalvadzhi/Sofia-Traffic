package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.AnimatedExpandableListView;
import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.fragments.MapFragment;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;

/**
 * Created by thalv on 30-Nov-16.
 */

public class RoutesAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private ArrayList<ArrayList<Station>> routes;
    private boolean[] animationStateFirst, animationStateSecond;
    private Context context;
    private String transportationType;

    public RoutesAdapter(ArrayList<ArrayList<Station>> routes,String trType, Context context) {
        this.routes = routes;
        this.context = context;
        this.transportationType = trType;
    }

    @Override
    public int getGroupCount() {
        return routes.size();
    }

//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return routes.get(groupPosition).size();
//    }

    @Override
    public ArrayList<Station> getGroup(int groupPosition) {
        return routes.get(groupPosition);
    }

    @Override
    public Station getChild(int groupPosition, int childPosition) {
        return routes.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater in = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = in.inflate(R.layout.group_direction, null);
        }
        RelativeLayout rl = (RelativeLayout)convertView.findViewById(R.id.group);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.locationGroup);
        TextView firstStop = (TextView) convertView.findViewById(R.id.firstStop);
        TextView lastStop = (TextView) convertView.findViewById(R.id.lastStop);
        button.setFocusable(false);
        if(getGroupCount() == 1){
            //case you want nearest stops
            rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorPrimary));
            firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
            firstStop.setText("Спирките около мен");
            lastStop.setVisibility(View.GONE);
        }else{
            //case you want a route
            ArrayList<Station> route = getGroup(i);
            String firstStopName = route.get(0).getName();
            String lastStopName = route.get(route.size() - 1).getName();
            firstStop.setText("от " + firstStopName);
            lastStop.setText("до " + lastStopName);


            switch (transportationType){
                case Constants.BUS:
                    rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBusGroup));
                    firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.colorBusGroupTest));
                    lastStop.setTextColor(ContextCompat.getColor(this.context, R.color.colorBusGroupTest));
                    break;
                case Constants.TRAM:
                    rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTramGroup));
                    firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.colorTramGroupText));
                    lastStop.setTextColor(ContextCompat.getColor(this.context, R.color.colorTramGroupText));
                    break;
                case Constants.TROLLEY:
                    rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolleyGroup));
                    firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.colorTrolleyGroupText));
                    lastStop.setTextColor(ContextCompat.getColor(this.context, R.color.colorTrolleyGroupText));
                    break;
            }

        }

        final int position = i;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOnMap(getGroup(position));
            }
        });

        return convertView;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater in = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = in.inflate(R.layout.route_item, null);
        }
        RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.childLayout);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.locationItem);
        button.setFocusable(false);
        TextView stopName = (TextView) convertView.findViewById(R.id.stopName);
        Station station = getChild(groupPosition, childPosition);
        String nameCode = station.getName() + " " + station.getCode();
        stopName.setText(nameCode);
        if(childPosition % 2 == 0){
            rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.white));
        }else{
            rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.grey));
        }
        final int grPos = groupPosition;
        final int chPos = childPosition;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Station> station = new ArrayList<Station>();
                station.add(getChild(grPos, chPos));
                showOnMap(station);
            }
        });
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return this.routes.get(groupPosition).size();
    }

    private void showOnMap(ArrayList<Station> stations){
        MapFragment f = MapFragment.newInstance(stations, null);
        ((MainActivity)this.context).changeFragmentAddBackStack(R.id.location_container, f);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
