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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bearenterprises.sofiatraffic.utilities.parsing.Description;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Used for the time results when searching by stop code
 */

public class TimeResultsAdapter extends RecyclerView.Adapter<TimeResultsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<LineTimes> times;
    private Stop stop;

    public TimeResultsAdapter(Context context, ArrayList<LineTimes> times, Stop stop) {
        this.stop = stop;
        this.context = context;
        this.times = times;

    }

    @Override
    public TimeResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_card_view, parent, false);
        return new TimeResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnClickListeners(position);
        LineTimes vt = times.get(position);
        Description desc = DbUtility.getDescription(Integer.toString(vt.getLine().getType()), vt.getLine().getName(), Integer.toString(stop.getCode()), context);
        if (desc != null){
            holder.dir.setText(desc.getDirection());
        }
        holder.stopName.setText(vt.getLine().getName());
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
                holder.stopName.setBackgroundColor(colorBus);
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_white);
                holder.imageView.setImageBitmap(image);break;
            case "0": holder.imageView.setBackgroundColor(colorTram);
                holder.bg.setBackgroundColor(colorTram);
                holder.stopName.setBackgroundColor(colorTram);
                Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tram_white);
                holder.imageView.setImageBitmap(image2);break;
            case "2": holder.imageView.setBackgroundColor(colorTrolley);
                holder.bg.setBackgroundColor(colorTrolley);
                holder.stopName.setBackgroundColor(colorTrolley);
                Bitmap image3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.trolley_smaller_white);
                holder.imageView.setImageBitmap(image3);break;
        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView stopName;
        private TextView vTimes;
        private ImageView imageView;
        private View bg;
        private ProgressBar progressBar;
        private TextView moreButton;
        private TextView dir;
        public ViewHolder(View itemView) {
            super(itemView);
            this.stopName = (TextView) itemView.findViewById(R.id.textView_card_line_name);
            this.vTimes = (TextView) itemView.findViewById(R.id.textView_card_times);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView_transportation_type);
            this.bg = itemView.findViewById(R.id.background);
            this.progressBar = (ProgressBar)itemView.findViewById(R.id.progressBarSingleLine);
            this.moreButton = (TextView)itemView.findViewById(R.id.more_button);
            this.dir = (TextView) itemView.findViewById(R.id.dir_alt);
        }

        /**
         * This listener is used when one clicks on the line indicator in the time results card.
         */
        private class LineIndicatorClickListener implements View.OnClickListener{
            private int position;
            public LineIndicatorClickListener(int position){
                this.position = position;
            }
            @Override
            public void onClick(View view) {
                LineTimes lineTimes = times.get(position);
                CommunicationUtility.showRoute(Integer.toString(lineTimes.getLine().getType()), Integer.toString(lineTimes.getLine().getId()), (MainActivity)context);
            }
        }

        /**
         *  listener for the MORE button in the results card
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
                CommunicationUtility.updateLineInfoSlow(stop, new ArrayList<>(Arrays.asList(line)), (MainActivity)context);
            }
        }

        public void setOnClickListeners(int position){
            LineIndicatorClickListener listener = new LineIndicatorClickListener(position);
            MoreTimesListener moreTimesListener = new MoreTimesListener(position);
            stopName.setOnClickListener(listener);
            imageView.setOnClickListener(listener);
            bg.setOnClickListener(listener);
            moreButton.setOnClickListener(moreTimesListener);
        }
    }
}
