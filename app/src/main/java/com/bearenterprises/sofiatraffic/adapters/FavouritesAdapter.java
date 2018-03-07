package com.bearenterprises.sofiatraffic.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.callback.OnStartDragListener;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.favourites.FavouritesModifier;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by thalv on 06-Dec-16.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> implements ReorderAdapter {

    private ArrayList<Stop> favourites;
    private Context context;
    private OnStartDragListener onStartDragListener;

    public FavouritesAdapter(Context context, ArrayList<Stop> favourites, OnStartDragListener onStartDragListener){
        this.context = context;
        this.favourites = favourites;
        this.onStartDragListener = onStartDragListener;
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

        // if the user has set an Alias use it, otherwise use the normal name
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
        holder.setOnDragStartListener(holder);
    }

    @Override
    public int getItemCount() {
        return this.favourites.size();
    }

    /**
     * When one moves a card, a reordering of the underlying collection is needed.
     * This updates the collection and then every favouriteIndex, for every Stop.
     * Finally the changes are saved to shared preferences.
     * @param fromPosition
     * @param toPosition
     * @return
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favourites, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favourites, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        updateFavouriteIndices();
        FavouritesModifier.removeAll(context);
        FavouritesModifier.save(favourites, context);
        return true;
    }

    /**
     * After a reordering of favourite cards their favouriteIndex must be updated.
     */
    private void updateFavouriteIndices(){
        for(int i = 0; i < favourites.size(); i++){
            favourites.get(i).setFavouriteIndex(i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView stopName;
        private Button locationButton;
        private TextView textViewCode;
        private ImageButton editAlias;
        private ImageButton coordinates;
        private RelativeLayout relativeLayout;
        private ImageButton reorderHandle;
        public ViewHolder(View itemView) {
            super(itemView);
            this.stopName = itemView.findViewById(R.id.textView_favourites_stop_name);
            this.locationButton = itemView.findViewById(R.id.button_favourites_location);
            this.textViewCode = itemView.findViewById(R.id.textView_favourites_code);
            this.editAlias = itemView.findViewById(R.id.imageButton_edit_alias);
            this.coordinates = itemView.findViewById(R.id.imageButton_copy_coordinates);
            this.reorderHandle = itemView.findViewById(R.id.imageButton_order_handle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunicationUtility.showTimes((String)textViewCode.getText(), (MainActivity)context);
                }
            });


        }

        public void setOnDragStartListener(final ViewHolder holder){
            reorderHandle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        onStartDragListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }

        public void setEditAliasAction(final int position){
            // allows the user to edit the name of the stop
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
            // when long pressing on a favourite stop a dialog will show up
            // which will allow the deletion of the stop
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setMessage("Наистина ли искате да изтриете тази любима спирка?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(position >= 0 && position < favourites.size()) {
                                        CommunicationUtility.removeFavourite(favourites.get(position).getCode(), (MainActivity) context);
                                    }
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
            //show stop on the map
            this.locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position >= 0 && position < favourites.size()){
                        CommunicationUtility.showOnMap(favourites.get(position), false, (MainActivity)context);
                    }
                }
            });

            // copy the coordinates of a stop
            this.coordinates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Stop s = favourites.get(position);
                    String coords = s.getLatitude() + "," + s.getLongitude();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Координатите на спирката", coords);
                    clipboard.setPrimaryClip(clip);
                    Utility.makeSnackbar("Координатите на спирката бяха копирани!", (MainActivity)context);
                }
            });
        }


    }
}
