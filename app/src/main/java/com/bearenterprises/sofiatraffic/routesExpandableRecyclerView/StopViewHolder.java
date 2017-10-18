package com.bearenterprises.sofiatraffic.routesExpandableRecyclerView;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

/**
 * Created by thalvadzhiev on 4/13/17.
 */

public class StopViewHolder extends ChildViewHolder {
    private TextView stopName, code;
    private ImageButton showOnMap;
    private Context context;
    private RelativeLayout rl;
    public StopViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        showOnMap =  itemView.findViewById(R.id.locationItem);
        stopName =  itemView.findViewById(R.id.stopName);
        code =  itemView.findViewById(R.id.textViewRouteCode1);
        rl =  itemView.findViewById(R.id.childLayout);
        this.context = context;
        //find views
    }

    public void bind(final Stop stop, boolean highlighted) {
        rl.setBackgroundResource(0);
        if(highlighted){
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            @ColorInt int colorFrom = typedValue.data;
            int colorTo = Color.TRANSPARENT;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(2000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    rl.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
//            rl.setBackgroundColor(Color.RED);
        }
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
                DbUtility.addLineTypes(stop, (MainActivity)context);
                CommunicationUtility.showOnMap(stop, (MainActivity)context);
            }
        });

    }
}
