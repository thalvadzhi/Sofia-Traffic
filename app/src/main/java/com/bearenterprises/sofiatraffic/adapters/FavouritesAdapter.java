package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;

import java.util.ArrayList;

/**
 * Created by thalv on 06-Dec-16.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private ArrayList<Stop> favourites;
    private Context context;

    public FavouritesAdapter(Context context, ArrayList<Stop> favourites){
        this.context = context;
        this.favourites = favourites;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourites_card, parent, false);

        return new FavouritesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stop l = this.favourites.get(position);
        holder.textViewFavourite.setText((l.getName()).toUpperCase());
        holder.textViewCode.setText(Integer.toString(l.getCode()));
        holder.setOnClickListenerForButtonAtPosition(position);
        holder.setOnLongClickListener(position);
//        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.map);
//        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return this.favourites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewFavourite;
        private Button locationButton;
        private TextView textViewCode;
        private RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewFavourite = (TextView) itemView.findViewById(R.id.text_view_favourite2);
            this.locationButton = (Button) itemView.findViewById(R.id.image_location2);
            this.textViewCode = (TextView) itemView.findViewById(R.id.textViewFavoureCode);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)context).showTimes((String)textViewCode.getText());
                }
            });


        }

        public void setOnLongClickListener(final int position){
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setMessage("Наистина ли искате да изтриете тази любима спирка?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((MainActivity)context).removeFavourite(favourites.get(position).getCode());
                                }
                            })
                            .setNegativeButton("Не", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                    return true;
                }
            });
        }

        public void setOnClickListenerForButtonAtPosition(final int position){
            this.locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)context).showOnMap(favourites.get(position));
                }
            });
        }


    }
}
