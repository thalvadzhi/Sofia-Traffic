package com.bearenterprises.sofiatraffic.routesExpandableRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

/**
 * Created by thalvadzhiev on 4/13/17.
 */

public class StopViewHolder extends ChildViewHolder {
    private TextView stopName, code;
    private ImageButton showOnMap;
    private Context context;
    public StopViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        showOnMap = (ImageButton) itemView.findViewById(R.id.locationItem);
        stopName = (TextView) itemView.findViewById(R.id.stopName);
        code = (TextView) itemView.findViewById(R.id.textViewRouteCode1);
        this.context = context;
        //find views
    }

    public void bind(final Stop stop) {
        stopName.setText(stop.getName());
        code.setText(stop.getCode() + "");
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicationUtility.showTimes(""+ stop.getCode(), (MainActivity)context);
            }
        });
        showOnMap.setFocusable(false);
        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicationUtility.showOnMap(stop, (MainActivity)context);
            }
        });

    }
}
