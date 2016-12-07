package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.stations.Station;

import java.util.ArrayList;

/**
 * Created by thalv on 02-Jul-16.
 */
public class FavouritesAdapter extends BaseAdapter {

    private ArrayList<Station> favourites;
    private Context context;

    public FavouritesAdapter(Context context, ArrayList<Station> favourites){
        this.context = context;
        this.favourites = favourites;
    }

    @Override
    public int getCount() {
        return this.favourites.size();
    }

    @Override
    public Station getItem(int position) {
        return favourites.get(position);
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
            convertView = inflater.inflate(R.layout.favourites_card, parent, false);
            holder = new ViewHolder();
            holder.textViewFavourite = (TextView) convertView.findViewById(R.id.text_view_favourite2);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_location2);
            holder.textViewCode = (TextView) convertView.findViewById(R.id.textViewFavoureCode);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Station l = getItem(position);
        holder.textViewFavourite.setText((l.getName()).toUpperCase());
        holder.textViewCode.setText(l.getCode());
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.map);
        holder.imageView.setImageBitmap(image);
        return convertView;
    }

    private class ViewHolder{
        TextView textViewFavourite;
        ImageView imageView;
        TextView textViewCode;
    }
}
