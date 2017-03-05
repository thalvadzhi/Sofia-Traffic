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
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;

import java.util.ArrayList;

/**
 * Created by thalv on 19-Dec-16.
 */

public class SlideUpLayoutLinesAdapter extends RecyclerView.Adapter<SlideUpLayoutLinesAdapter.ViewHolder> {
    private ArrayList<Line> lines;
    private Context context;

    public SlideUpLayoutLinesAdapter(Context context, ArrayList<Line> lines) {
        this.context = context;
        this.lines = lines;
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
        switch (l.getType()){
            case 0:
                Bitmap imageTram = BitmapFactory.decodeResource(context.getResources(), tram);
                holder.trType.setImageBitmap(imageTram);break;
            case 1:
                Bitmap imageBus = BitmapFactory.decodeResource(context.getResources(), bus);
                holder.trType.setImageBitmap(imageBus);break;
            case 2:
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
        private View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            trType = (ImageView) itemView.findViewById(R.id.ImageViewTransportationTypeSlideUpLayout);
            lineName = (TextView) itemView.findViewById(R.id.TextViewLineName);
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
