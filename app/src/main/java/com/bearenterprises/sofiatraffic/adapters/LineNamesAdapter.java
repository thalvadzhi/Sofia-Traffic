package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Transport;

import java.util.ArrayList;

import static android.R.attr.resource;

/**
 * Created by thalv on 30-Nov-16.
 */

public class LineNamesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Transport> transport;
    public LineNamesAdapter(Context context, ArrayList<Transport> transport) {
        this.context = context;
        this.transport = transport;
    }

    @Override
    public int getCount() {
        return this.transport.size() + 1;
    }

    @Override
    public Transport getItem(int i) {
        if(i == 0){
            return new Transport(0, 0, Constants.LINE_ID_DEFAULT);
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
        TextView text = (TextView) view.findViewById(R.id.textViewSpinnerItem);
        text.setText(getItem(i).getName());
        return view;
    }
}
