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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bearenterprises.sofiatraffic.utilities.parsing.Description;

import java.util.ArrayList;

/**
 * Adapter for the slide up menu in the map tab.
 */

public class SlideUpLayoutLinesAdapter extends RecyclerView.Adapter<SlideUpLayoutLinesAdapter.ViewHolder> {
    private ArrayList<Line> lines;
    private Context context;
    private String stopCode;

    public SlideUpLayoutLinesAdapter(Context context, ArrayList<Line> lines, String stopCode) {
        this.context = context;
        this.lines = lines;
        this.stopCode = stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slide_up_layout_line_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Line l = lines.get(position);
        holder.setOnClickListener(position);

        Description desc = DbUtility.getDescription(Integer.toString(l.getType()), l.getName(), stopCode, context);
        if (desc != null){
            holder.direction.setText(desc.getDirection());
        }


        TypedValue typedValueBus = new TypedValue();
        TypedValue typedValueTrolley = new TypedValue();
        TypedValue typedValueTram = new TypedValue();

        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.iconBus, typedValueBus, true);
        theme.resolveAttribute(R.attr.iconTram, typedValueTram, true);
        theme.resolveAttribute(R.attr.iconTrolley, typedValueTrolley, true);

        int bus = typedValueBus.resourceId;
        int tram = typedValueTram.resourceId;
        int trolley = typedValueTrolley.resourceId;


        TypedValue typedValueBusColor = new TypedValue();
        TypedValue typedValueTrolleyColor = new TypedValue();
        TypedValue typedValueTramColor= new TypedValue();


        theme.resolveAttribute(R.attr.busColor, typedValueBusColor, true);
        theme.resolveAttribute(R.attr.tramColor, typedValueTramColor, true);
        theme.resolveAttribute(R.attr.trolleyColor, typedValueTrolleyColor, true);

        int colorBus = typedValueBusColor.data;
        int colorTram = typedValueTramColor.data;
        int colorTrolley = typedValueTrolleyColor.data;
        switch (l.getType()){
            case 0:

                holder.rl.setBackgroundColor(colorTram);
                Bitmap imageTram = BitmapFactory.decodeResource(context.getResources(), tram);
                holder.trType.setImageBitmap(imageTram);break;
            case 1:
                holder.rl.setBackgroundColor(colorBus);

                Bitmap imageBus = BitmapFactory.decodeResource(context.getResources(), bus);
                holder.trType.setImageBitmap(imageBus);break;
            case 2:
                holder.rl.setBackgroundColor(colorTrolley);

                Bitmap imageTrolley = BitmapFactory.decodeResource(context.getResources(), trolley);
                holder.trType.setImageBitmap(imageTrolley);break;
        }
        holder.lineName.setText(l.getName());

    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView trType;
        private TextView lineName;
        private TextView direction;
        private View itemView;
        private RelativeLayout rl;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            trType = (ImageView) itemView.findViewById(R.id.ImageViewTransportationTypeSlideUpLayout);
            lineName = (TextView) itemView.findViewById(R.id.TextViewLineName);
            direction = (TextView) itemView.findViewById(R.id.textViewMapSlideUpDirection);
            rl = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutMapSlideupLineBackground);
        }

        public void setOnClickListener(final int position){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Line l = lines.get(position);
                    CommunicationUtility.showRoute(Integer.toString(l.getType()), Integer.toString(l.getId()), (MainActivity)context);
                }
            });
        }
    }
}
