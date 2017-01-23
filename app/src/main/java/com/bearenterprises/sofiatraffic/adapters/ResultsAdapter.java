package com.bearenterprises.sofiatraffic.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.stations.LineTimes;

import java.util.ArrayList;

/**
 * Created by thalv on 06-Dec-16.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<LineTimes> times;
    private Station station;

    public ResultsAdapter(Context context, ArrayList<LineTimes> times, Station station) {
        this.station = station;
        this.context = context;
        this.times = times;

    }

    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_card_view, parent, false);
        return new ResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnClickListeners(position);
        LineTimes vt = times.get(position);
        holder.stationName.setText(vt.getLine().getName());
        if(vt.getTimes() != null){
            holder.progressBar.setVisibility(View.GONE);
            holder.vTimes.setText(vt.getTimes());
        }

        switch(vt.getType()){
            case "1": holder.imageView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBus));
                holder.bg.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBus));
                holder.stationName.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBus));
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_white);
                holder.imageView.setImageBitmap(image);break;
            case "0": holder.imageView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTram));
                holder.bg.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTram));
                holder.stationName.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTram));
                Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tram_white);
                holder.imageView.setImageBitmap(image2);break;
            case "2": holder.imageView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolley));
                holder.bg.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolley));
                holder.stationName.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolley));
                Bitmap image3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.trolley_smaller_white);
                holder.imageView.setImageBitmap(image3);break;
        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button button;
        private TextView stationName;
        private TextView vTimes;
        private ImageView imageView;
        private TextView bg;
        private ProgressBar progressBar;
        public ViewHolder(View itemView) {
            super(itemView);
            this.stationName = (TextView) itemView.findViewById(R.id.textView_card_station_name);
            this.vTimes = (TextView) itemView.findViewById(R.id.textView_card_times);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView_transportation_type);
            this.bg = (TextView)itemView.findViewById(R.id.background);
            this.progressBar = (ProgressBar)itemView.findViewById(R.id.progressBarSingleLine);
        }

        private class RouteListener implements View.OnClickListener{
            private int position;
            public RouteListener(int position){
                this.position = position;
            }
            @Override
            public void onClick(View view) {
                LineTimes lineTimes = times.get(position);

                ((MainActivity)context).showRoute(Integer.toString(lineTimes.getLine().getType()), Integer.toString(lineTimes.getLine().getId()));
            }
        }

        public void setOnClickListeners(int position){
            RouteListener listener = new RouteListener(position);
            stationName.setOnClickListener(listener);
            imageView.setOnClickListener(listener);
            bg.setOnClickListener(listener);
        }
    }
}
