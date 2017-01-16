package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.views.AnimatedExpandableListView;
import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;

import java.util.ArrayList;

/**
 * Created by thalv on 30-Nov-16.
 */

public class RoutesAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private ArrayList<ArrayList<Stop>> routes;
    private boolean[] animationStateFirst, animationStateSecond;
    private Context context;
    private String transportationType;

    public RoutesAdapter(ArrayList<ArrayList<Stop>> routes, String trType, Context context) {
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
    public ArrayList<Stop> getGroup(int groupPosition) {
        return routes.get(groupPosition);
    }

    @Override
    public Stop getChild(int groupPosition, int childPosition) {
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
            rl.setBackgroundResource(R.drawable.rounded_edges_primary_color);
//            rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorPrimary));
            firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
            firstStop.setText("Спирките около мен");
            lastStop.setVisibility(View.GONE);
        }else{
            //case you want a route
            ArrayList<Stop> route = getGroup(i);
            String firstStopName = route.get(0).getName();
            String lastStopName = route.get(route.size() - 1).getName();
            firstStop.setText("от " + firstStopName.toUpperCase());
            lastStop.setText("до " + lastStopName.toUpperCase());


            switch (transportationType){
                case Constants.BUS:
                    rl.setBackgroundResource(R.drawable.rounded_edges_bus);
//                    rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBusGroup));
                    firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
                    lastStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
                    break;
                case Constants.TRAM:
                    rl.setBackgroundResource(R.drawable.rounded_edges_tram);
//                    rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTramGroup));
                    firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
                    lastStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
                    break;
                case Constants.TROLLEY:
                    rl.setBackgroundResource(R.drawable.rounded_edges_trolley);
//                    rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolleyGroup));
                    firstStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
                    lastStop.setTextColor(ContextCompat.getColor(this.context, R.color.white));
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
        TextView stopDirection = (TextView) convertView.findViewById(R.id.textViewRouteDirection);
        TextView stopCode = (TextView)convertView.findViewById(R.id.textViewRouteCode);
        Stop station = getChild(groupPosition, childPosition);
        String stationName = station.getName();
        String direction = station.getDirection();

        stopName.setText(stationName);
        if(getGroupCount() == 1 && direction != null){
            stopDirection.setText(direction);
        }else{

            stopDirection.setVisibility(View.GONE);
        }

        stopCode.setText(Integer.toString(station.getCode()));

        rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.white));
//        if(childPosition % 2 == 0){
//        }else{
//            rl.setBackgroundColor(ContextCompat.getColor(this.context, R.color.grey));
//        }
        final int grPos = groupPosition;
        final int chPos = childPosition;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Stop> station = new ArrayList<Stop>();
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

    private void showOnMap(ArrayList<Stop> stations){
        ((MainActivity)context).showOnMap(stations);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
