package com.bearenterprises.sofiatraffic.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.favourites.FavouritesModifier;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

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
                .inflate(R.layout.favourites_card_new, parent, false);

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


        holder.stopName.setText(name);
        holder.textViewCode.setText(Integer.toString(l.getCode()));

        holder.setOnClickListenerForButtonAtPosition(position);
        holder.setOnLongClickListener(position);
        holder.setEditAliasAction(position);
    }

    @Override
    public int getItemCount() {
        return this.favourites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView stopName;
        private Button locationButton;
        private TextView textViewCode;
        private ImageButton editAlias;
        private TextView coordinates;
        private RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.stopName = (TextView) itemView.findViewById(R.id.textView_favourites_stop_name);
            this.locationButton = (Button) itemView.findViewById(R.id.button_favourites_location);
            this.textViewCode = (TextView) itemView.findViewById(R.id.textView_favourites_code);
            this.editAlias = (ImageButton) itemView.findViewById(R.id.imageButton_edit_alias);
            this.coordinates = (TextView) itemView.findViewById(R.id.copy_coordinates_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunicationUtility.showTimes((String)textViewCode.getText(), (MainActivity)context);
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
                                    CommunicationUtility.removeFavourite(favourites.get(position).getCode(), (MainActivity)context);
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
                    CommunicationUtility.showOnMap(favourites.get(position), (MainActivity)context);
                }
            });

            this.coordinates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Stop s = favourites.get(position);
                    String coords = s.getLatitude() + "," + s.getLongtitude();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Координатите на спирката", coords);
                    clipboard.setPrimaryClip(clip);
                    Utility.makeSnackbar("Координатите на спирката бяха копирани!", (MainActivity)context);
                }
            });
        }


    }
}
