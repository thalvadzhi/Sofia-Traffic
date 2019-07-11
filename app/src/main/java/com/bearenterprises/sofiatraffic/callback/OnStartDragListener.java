package com.bearenterprises.sofiatraffic.callback;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by thalvadzhiev on 4/23/17.
 */

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
