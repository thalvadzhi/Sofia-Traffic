package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;

/**
 * Created by thalv on 10-Jul-16.
 */
public class ClosestStationsAdapter extends RecyclerView.Adapter<ClosestStationsAdapter.ViewHolder > {

    private ArrayList<Station> closestStations;
    private Context context;
    private int lastPosition = -1;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageButton mImageButton;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.stopName);
            mImageButton = (ImageButton) view.findViewById(R.id.locationItem);
        }
    }

    public ClosestStationsAdapter(ArrayList<Station> stations, Context context){
        this.closestStations = stations;
        this.context = context;
    }

    @Override
    public ClosestStationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Station station = closestStations.get(position);
        holder.mTextView.setText(station.getName());
        holder.itemView.setTag(station);
    }

    @Override
    public int getItemCount() {
        return closestStations.size();
    }

}
