package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.bearenterprises.sofiatraffic.R;
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
        return this.transport.size();
    }

    @Override
    public Transport getItem(int i) {
        return transport.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        CheckedTextView text = (CheckedTextView) view.findViewById(android.R.id.text1);
        text.setText(getItem(i).getName());
        return view;
    }
}
