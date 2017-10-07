package com.bearenterprises.sofiatraffic.routesExpandableRecyclerView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

/**
 * Created by thalvadzhiev on 4/13/17.
 */

public class DirectionViewHolder extends ParentViewHolder {
    private TextView from, to;
    private RelativeLayout background;
    private ImageView trType;
    private Context context;
    private ImageButton showOnMap;
    public DirectionViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        from = (TextView) itemView.findViewById(R.id.textView_from_stop);
        to = (TextView) itemView.findViewById(R.id.textView_to_stop);
        background = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_tr_type);
        trType = (ImageView) itemView.findViewById(R.id.imageView_direction_tr_type);
        showOnMap = (ImageButton) itemView.findViewById(R.id.imageButton_show_on_map_group);
    }

    public void bind(final Direction direction) {
        from.setText(direction.getFrom().getName());
        to.setText(direction.getTo().getName());
        showOnMap.setFocusable(false);

        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbUtility.addLineTypes(direction.getStops(), (MainActivity) context);
                CommunicationUtility.showOnMap(direction.getStops(), (MainActivity)context);
            }
        });

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


        switch(direction.getTransportationType()){
            case "bus":
                background.setBackgroundColor(colorBus);
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_white);
                trType.setImageBitmap(image);break;
            case "tram":
                background.setBackgroundColor(colorTram);
                Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tram_white);
                trType.setImageBitmap(image2);break;
            case "trolley":
                background.setBackgroundColor(colorTrolley);
                Bitmap image3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.trolley_white);
                trType.setImageBitmap(image3);break;
        }

    }

}
