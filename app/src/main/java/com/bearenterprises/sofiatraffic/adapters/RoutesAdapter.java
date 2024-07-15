package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.Direction;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.DirectionViewHolder;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.StopViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

/**
 * Created by thalvadzhiev on 4/13/17.
 */

public class RoutesAdapter extends ExpandableRecyclerAdapter<Direction, Stop, DirectionViewHolder, StopViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private int highlightedStopCode = -1;

    public RoutesAdapter(@NonNull List<Direction> parentList, Context context) {
        super(parentList);
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    public void setHighlightedPosition(int highlightedStopCode){
        this.highlightedStopCode = highlightedStopCode;
    }

    @NonNull
    @Override
    public DirectionViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View directionView = mInflater.inflate(R.layout.direction_view, parentViewGroup, false);
        return new DirectionViewHolder(directionView, context);
    }

    @NonNull
    @Override
    public StopViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View stopView = mInflater.inflate(R.layout.route_item, childViewGroup, false);
        return new StopViewHolder(stopView, context);
    }

    @Override
    public void onBindParentViewHolder(@NonNull DirectionViewHolder parentViewHolder, int parentPosition, @NonNull Direction parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull StopViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Stop child) {
        boolean highlighted = highlightedStopCode == child.getCode();
//        childViewHolder.setIsRecyclable(!highlighted);
        childViewHolder.bind(child, highlighted);


    }

}
