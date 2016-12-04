package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;

import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class ResultsAdapter extends BaseAdapter {

    private ArrayList<VehicleTimes> times;
    private Context context;
    private boolean[] animationState;

    public ResultsAdapter(Context context, ArrayList<VehicleTimes> times) {
        this.times = times;
        this.context = context;
        animationState = new boolean[times.size()];
    }

    @Override
    public int getCount() {
       return this.times.size();
    }

    @Override
    public VehicleTimes getItem(int position) {
        return times.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(this.context);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.results_card_view, parent, false);
            holder = new ViewHolder();
            //holder.button = (Button) convertView.findViewById(R.id.button_vehicle);
//            holder.times = (TextView) convertView.findViewById(R.id.times);
            holder.stationName = (TextView) convertView.findViewById(R.id.textView_card_station_name);
            holder.times = (TextView) convertView.findViewById(R.id.textView_card_times);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_transportation_type);
            holder.bg = (TextView)convertView.findViewById(R.id.background);
            if(!animationState[position]){
                animationState[position] = true;
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_in);
//                animation.
                animation.setStartOffset(position*250);
                convertView.startAnimation(animation);
            }
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        VehicleTimes vt = getItem(position);
        holder.stationName.setText(vt.getLine());
        holder.times.setText(vt.getTimes());

        switch(vt.getType()){
            case "1": holder.imageView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBus));
                holder.bg.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBus));
                holder.stationName.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorBus));
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_white);
                holder.imageView.setImageBitmap(image);break;
            case "0": holder.imageView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTram));
                holder.bg.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTram));
                holder.stationName.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTram));
                Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tram);
                holder.imageView.setImageBitmap(image2);break;
            case "2": holder.imageView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolley));
                holder.bg.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolley));
                holder.stationName.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorTrolley));
                Bitmap image3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.trolley);
                holder.imageView.setImageBitmap(image3);break;
        }
        return convertView;
    }

    private class ViewHolder{
        Button button;
        TextView stationName;
        TextView times;
        ImageView imageView;
        TextView bg;
    }
}
