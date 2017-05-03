package com.bearenterprises.sofiatraffic.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;

import java.util.List;

/**
 * Created by thalvadzhiev on 5/3/17.
 */

public class NameResultAdapter extends RecyclerView.Adapter<NameResultAdapter.ViewHolder> {

    private List<Stop> stops;
    private Context context;

    public NameResultAdapter(List<Stop> stops, Context context) {
        this.stops = stops;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_result_item, parent, false);
        return new NameResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stop stop = stops.get(position);
        holder.stopName.setText(stop.getName());
        holder.code.setText(Integer.toString(stop.getCode()));

        holder.setOnLocationClicked(position);
    }

    int lastPosition = -1;
    @Override
    public void onViewAttachedToWindow(final ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        final long delayTime = 0;
        holder.itemView.setVisibility(View.INVISIBLE);

        if (holder.getLayoutPosition() > lastPosition) {
            holder.itemView.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.itemView.setVisibility(View.VISIBLE);
                    ObjectAnimator tr = ObjectAnimator.ofFloat(holder.itemView, "translationY", 300, 0);
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.play(tr);
                    animSet.setInterpolator(new DecelerateInterpolator(2));

                    animSet.setDuration(300);
                    animSet.start();

                }
            }, delayTime);

            lastPosition = holder.getLayoutPosition();
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView stopName, code;
        private ImageView location;

        public ViewHolder(View itemView) {
            super(itemView);
            stopName = (TextView) itemView.findViewById(R.id.name_search_result_stop_name);
            code = (TextView) itemView.findViewById(R.id.name_search_result_stop_code);
            location = (ImageView) itemView.findViewById(R.id.imageView_name_result_location);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunicationUtility.showTimes(code.getText().toString(), (MainActivity)context);
                }
            });
        }

        public void setOnLocationClicked(final int position){
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunicationUtility.showOnMap(stops.get(position), (MainActivity)context);
                }
            });
        }
    }
}
