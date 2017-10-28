package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;

import java.util.ArrayList;

/**
 * An adapter for the drop down menu showing line names.
 */

public class LineNamesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Line> transport;
    public LineNamesAdapter(Context context, ArrayList<Line> transport) {
        this.context = context;
        this.transport = transport;
    }

    @Override
    public int getCount() {
        return this.transport.size() + 1;
    }

    @Override
    public Line getItem(int i) {
        if(i == 0){
            return new Line(0, 0, Constants.LINE_ID_DEFAULT);
        }
        return transport.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i - 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        view = inflater.inflate(R.layout.spinner_item, null);
        TextView text = view.findViewById(R.id.textViewSpinnerItem);
        text.setText(getItem(i).getName());
        return view;
    }
}
