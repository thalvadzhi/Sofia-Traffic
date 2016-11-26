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
        public ImageView mImageview;
        public CardView cv;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text_view_favourite2);
            mImageview = (ImageView) view.findViewById(R.id.image_location2);
            cv = (CardView) view.findViewById(R.id.cv);
        }
    }

    public ClosestStationsAdapter(ArrayList<Station> stations, Context context){
        this.closestStations = stations;
        this.context = context;
    }

    @Override
    public ClosestStationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourites_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(closestStations.get(position).getName());
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.map);
        holder.mImageview.setImageBitmap(image);

        setAnimation(holder.cv, position);
    }

    @Override
    public int getItemCount() {
        return closestStations.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            animation.setStartOffset(position * 150);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
