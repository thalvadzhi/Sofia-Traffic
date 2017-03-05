package com.bearenterprises.sofiatraffic.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;

import java.util.ArrayList;
import java.util.Arrays;

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
        holder.moreButton.setVisibility(View.GONE);
        holder.vTimes.setVisibility(View.GONE);
        if(vt.getTimes() != null){
            if (((MainActivity)context).getQueryMethod().equals(Constants.QUERY_METHOD_FAST)){
                holder.moreButton.setVisibility(View.VISIBLE);
            }
            holder.progressBar.setVisibility(View.GONE);
            holder.vTimes.setVisibility(View.VISIBLE);
            holder.vTimes.setText(vt.getTimes());
        }


        TypedValue typedValueBus = new TypedValue();
        TypedValue typedValueTrolley = new TypedValue();
        TypedValue typedValueTram = new TypedValue();

        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.busColor, typedValueBus, true);
        theme.resolveAttribute(R.attr.tramColor, typedValueTram, true);
        theme.resolveAttribute(R.attr.trolleyColor, typedValueTrolley, true);

        int colorBus = typedValueBus.data;
        int colorTram = typedValueTram.data;
        int colorTrolley = typedValueTrolley.data;


        switch(vt.getType()){
            case "1": holder.imageView.setBackgroundColor(colorBus);
                holder.bg.setBackgroundColor(colorBus);
                holder.stationName.setBackgroundColor(colorBus);
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_white);
                holder.imageView.setImageBitmap(image);break;
            case "0": holder.imageView.setBackgroundColor(colorTram);
                holder.bg.setBackgroundColor(colorTram);
                holder.stationName.setBackgroundColor(colorTram);
                Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tram_white);
                holder.imageView.setImageBitmap(image2);break;
            case "2": holder.imageView.setBackgroundColor(colorTrolley);
                holder.bg.setBackgroundColor(colorTrolley);
                holder.stationName.setBackgroundColor(colorTrolley);
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
        private TextView moreButton;
        public ViewHolder(View itemView) {
            super(itemView);
            this.stationName = (TextView) itemView.findViewById(R.id.textView_card_station_name);
            this.vTimes = (TextView) itemView.findViewById(R.id.textView_card_times);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView_transportation_type);
            this.bg = (TextView)itemView.findViewById(R.id.background);
            this.progressBar = (ProgressBar)itemView.findViewById(R.id.progressBarSingleLine);
            this.moreButton = (TextView)itemView.findViewById(R.id.more_button);
        }

        private class RouteListener implements View.OnClickListener{
            private int position;
            public RouteListener(int position){
                this.position = position;
            }
            @Override
            public void onClick(View view) {
                LineTimes lineTimes = times.get(position);

                CommunicationUtility.showRoute(Integer.toString(lineTimes.getLine().getType()), Integer.toString(lineTimes.getLine().getId()), (MainActivity)context);
            }
        }

        /*
            listener for the MORE button in the results card
         */
        private class MoreTimesListener implements View.OnClickListener{
            private int position;

            public MoreTimesListener(int position){
                this.position = position;
            }

            @Override
            public void onClick(View view) {
                moreButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Line line = times.get(position).getLine();
                CommunicationUtility.updateLineInfoSlow(station, new ArrayList<>(Arrays.asList(line)), (MainActivity)context);
            }
        }

        public void setOnClickListeners(int position){
            RouteListener listener = new RouteListener(position);
            MoreTimesListener moreTimesListener = new MoreTimesListener(position);
            stationName.setOnClickListener(listener);
            imageView.setOnClickListener(listener);
            bg.setOnClickListener(listener);
            moreButton.setOnClickListener(moreTimesListener);
        }
    }
}
