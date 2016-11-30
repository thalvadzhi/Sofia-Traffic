package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;

/**
 * Created by thalv on 30-Nov-16.
 */

public class RoutesAdapter extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<Station>> routes;
    private Context context;

    public RoutesAdapter(ArrayList<ArrayList<Station>> routes, Context context) {
        this.routes = routes;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return routes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return routes.get(groupPosition).size();
    }

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
        ImageButton button = (ImageButton) convertView.findViewById(R.id.locationGroup);
        TextView firstStop = (TextView) convertView.findViewById(R.id.firstStop);
        TextView lastStop = (TextView) convertView.findViewById(R.id.lastStop);
        button.setFocusable(false);
        ArrayList<Station> route = getGroup(i);
        String firstStopName = route.get(0).getName();
        String lastStopName = route.get(route.size() - 1).getName();
        firstStop.setText(firstStopName);
        lastStop.setText(lastStopName);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater in = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = in.inflate(R.layout.route_item, null);
        }
        ImageButton button = (ImageButton) convertView.findViewById(R.id.locationItem);
        button.setFocusable(false);
        TextView stopName = (TextView) convertView.findViewById(R.id.stopName);
        Station station = getChild(groupPosition, childPosition);
        String nameCode = station.getName() + " " + station.getCode();
        stopName.setText(nameCode);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
