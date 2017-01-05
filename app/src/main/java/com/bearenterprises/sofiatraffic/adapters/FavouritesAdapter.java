package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.FavouritesModifier;

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
        String name;
        if(l.getAlias() != null && !"".equals(l.getAlias())){
            name = l.getAlias();
        }else{
            name = l.getName();
        }
        holder.textViewFavourite.setText(name.toUpperCase());
        holder.textViewCode.setText(Integer.toString(l.getCode()));
        holder.setOnClickListenerForButtonAtPosition(position);
        holder.setOnLongClickListener(position);
        holder.setEditAliasAction(position);
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
        private ImageButton editAlias;
        private RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewFavourite = (TextView) itemView.findViewById(R.id.text_view_favourite2);
            this.locationButton = (Button) itemView.findViewById(R.id.image_location2);
            this.textViewCode = (TextView) itemView.findViewById(R.id.textViewFavoureCode);
            this.editAlias = (ImageButton) itemView.findViewById(R.id.ImageButtonEditAlias);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)context).showTimes((String)textViewCode.getText());
                }
            });


        }

        public void setEditAliasAction(final int position){
            editAlias.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText input = new EditText(context);
                    String name;
                    Stop stop = favourites.get(position);
                    if(stop.getAlias() != null && !"".equals(stop.getAlias())){
                        name = stop.getAlias();
                    }else{
                        name = stop.getName();
                    }
                    input.setText(name);
                    input.setSelectAllOnFocus(true);

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Въведето ново име на тази спирка.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String alias = input.getText().toString();
                                    if(alias != null){
                                        Stop stop = favourites.get(position);
                                        stop.setAlias(alias);
                                        FavouritesModifier.save(stop, context);
                                        notifyDataSetChanged();
                                    }else if("".equals(alias)){
                                        Stop stop = favourites.get(position);
                                        stop.setAlias(null);
                                        FavouritesModifier.save(stop, context);
                                        notifyDataSetChanged();
                                    }
                                }
                            })
                            .setNegativeButton("Отказ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No Op
                                }
                            })
                            .setView(input)
                            .show();
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
