package com.bearenterprises.sofiatraffic.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thalv on 02-Dec-16.
 */

public class TransportationTypeAdapter extends BaseAdapter {
    private Context context;
    private List<String> choices;

    public TransportationTypeAdapter(Context context, List<String> choices) {
        this.context = context;
        this.choices = choices;
    }

    @Override
    public int getCount() {
        return choices.size();
    }

    @Override
    public String getItem(int i) {
        return choices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        view = inflater.inflate(R.layout.spinner_item, null);
        TextView text = (TextView) view.findViewById(R.id.textViewSpinnerItem);
        text.setText(getItem(position));

        return view;
    }
}
