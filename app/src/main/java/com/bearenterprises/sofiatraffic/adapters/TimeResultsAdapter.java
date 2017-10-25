package com.bearenterprises.sofiatraffic.adapters;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bearenterprises.sofiatraffic.utilities.parsing.Description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Used for the time results when searching by stop code
 */

public class TimeResultsAdapter extends RecyclerView.Adapter<TimeResultsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LineTimes> times;
    private Stop stop;

    public TimeResultsAdapter(Context context, ArrayList<LineTimes> times, Stop stop) {
        this.stop = stop;
        this.context = context;
        this.times = times;

    }

    @Override
    public TimeResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_card_view, parent, false);
        return new TimeResultsAdapter.ViewHolder(view);
    }

    int lastPosition = -1;

    @Override
    public void onViewAttachedToWindow(final TimeResultsAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        final long delayTime = 0;
        holder.itemView.setVisibility(View.INVISIBLE);

        if (holder.getLayoutPosition() > lastPosition) {
            holder.itemView.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.itemView.setVisibility(View.VISIBLE);
                    ObjectAnimator tr = ObjectAnimator.ofFloat(holder.itemView, "translationY", 300, 0);
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.play(tr);
                    animSet.setInterpolator(new DecelerateInterpolator(2));

                    animSet.setDuration(300);
                    animSet.start();

                }
            }, delayTime);

            lastPosition = holder.getLayoutPosition();
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param times either time as in HH:mm or remaining time
     * @return the first time in bigger font size
     */
    private CharSequence getFirstTimeBiggerFont(ArrayList<String> times) {
        if (times == null || times.size() == 0) {
            return new SpannableString("");
        }

        String first = times.get(0);
        String joined = TextUtils.join("  ", times.subList(1, times.size()));
        String result = null;
        if (times.size() == 1) {
            result = first;
        } else {
            result = first + "\n" + joined;
        }
        SpannableString formatted = new SpannableString(result);
        formatted.setSpan(new RelativeSizeSpan(1.3f), 0, first.length(), 0);
        formatted.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, first.length(), 0);
        return formatted;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnClickListeners(position);
        holder.exclamationMark.setVisibility(View.VISIBLE);
        holder.exclamationMarkTouchArea.setVisibility(View.VISIBLE);
        LineTimes vt = times.get(position);
        String direction = vt.getRouteName();
        if(!vt.isSchedule()){
            holder.exclamationMark.setVisibility(View.GONE);
            holder.exclamationMarkTouchArea.setVisibility(View.GONE);
        }
        Description desc = null;
        if (direction == null) {
            desc = DbUtility.getDescription(Integer.toString(vt.getLine().getType()), vt.getLine().getName(), Integer.toString(stop.getCode()), context);
            if (desc != null) {
                direction = desc.getDirection();
            }
        }
        if (direction != null) {
            holder.dir.setText(direction.toUpperCase());
        }
        holder.stopName.setText(vt.getLine().getName());
        holder.moreButton.setVisibility(View.GONE);
        holder.vTimes.setVisibility(View.GONE);
        if (vt.getTimes() != null) {
            if (((MainActivity) context).getQueryMethod().equals(Constants.QUERY_METHOD_FAST)) {
                holder.moreButton.setVisibility(View.VISIBLE);
            }
            holder.progressBar.setVisibility(View.GONE);
            holder.vTimes.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String time_mode = sharedPreferences.getString(context.getResources().getString(R.string.key_time_mode), context.getResources().getString(R.string.time_mode_default));
            if (time_mode.equals(context.getResources().getString(R.string.time_mode_time_value))) {
                if (vt.getTimesList() != null) {
                    CharSequence ss = getFirstTimeBiggerFont(vt.getTimesList());
                    holder.vTimes.setText(ss);
                }

            } else {
                if (vt.getRemainingTimesList() != null) {
                    CharSequence ss = getFirstTimeBiggerFont(vt.getRemainingTimesList());
                    holder.vTimes.setText(ss);
                }
            }
        }


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


        switch (vt.getType()) {
            case "1":
                holder.imageView.setBackgroundColor(colorBus);
                holder.bg.setBackgroundColor(colorBus);
                holder.stopName.setBackgroundColor(colorBus);
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_white);
                holder.imageView.setImageBitmap(image);
                break;
            case "0":
                holder.imageView.setBackgroundColor(colorTram);
                holder.bg.setBackgroundColor(colorTram);
                holder.stopName.setBackgroundColor(colorTram);
                Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tram_white);
                holder.imageView.setImageBitmap(image2);
                break;
            case "2":
                holder.imageView.setBackgroundColor(colorTrolley);
                holder.bg.setBackgroundColor(colorTrolley);
                holder.stopName.setBackgroundColor(colorTrolley);
                Bitmap image3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.trolley_white);
                holder.imageView.setImageBitmap(image3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView stopName;
        private TextView vTimes;
        private ImageView imageView;
        private View bg;
        private ProgressBar progressBar;
        private TextView moreButton;
        private TextView dir;
        private ImageView exclamationMark;
        private RelativeLayout exclamationMarkTouchArea;

        public ViewHolder(View itemView) {
            super(itemView);
            this.stopName = itemView.findViewById(R.id.textView_card_line_name);
            this.vTimes = itemView.findViewById(R.id.textView_card_times);
            this.imageView = itemView.findViewById(R.id.imageView_transportation_type);
            this.bg = itemView.findViewById(R.id.background);
            this.progressBar = itemView.findViewById(R.id.progressBarSingleLine);
            this.moreButton = itemView.findViewById(R.id.more_button);
            this.dir = itemView.findViewById(R.id.dir_alt);
            this.exclamationMark = itemView.findViewById(R.id.exclamation_mark);
            this.exclamationMarkTouchArea = itemView.findViewById(R.id.exclamation_mark_touch_area);
        }

        /**
         * This listener is used when one clicks on the line indicator in the time results card.
         */
        private class LineIndicatorClickListener implements View.OnClickListener {
            private int position;

            public LineIndicatorClickListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View view) {
                if (position >= 0 && position < times.size()) {
                    LineTimes lineTimes = times.get(position);
                    CommunicationUtility.showRoute(lineTimes.getLine(), stop.getCode(), (MainActivity) context);
                }
            }
        }

        /**
         * listener for the MORE button in the results card
         */
        private class MoreTimesListener implements View.OnClickListener {
            private int position;

            public MoreTimesListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View view) {
                moreButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if (position < times.size() && position >= 0) {
                    Line line = times.get(position).getLine();
                    CommunicationUtility.updateLineInfoSlow(stop, new ArrayList<>(Arrays.asList(line)), (MainActivity) context);
                }
            }
        }

        private class ExclamationMarkListener implements View.OnClickListener {

            @Override
            public void onClick(View view) {
                Tooltip.TooltipView tooltip = Tooltip.make(context, new Tooltip.Builder(1)
                        .anchor(view, Tooltip.Gravity.TOP)
                        .text(context.getString(R.string.exclamation_mark_tooltip_warning_bg))
                        .maxWidth(1000)
                        .withArrow(true)
                        .withStyleId(R.style.ToolTipLayoutStyle)
                        .closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME, 5000)
                        .build());
                tooltip.show();

            }
        }

        public void setOnClickListeners(int position) {
            LineIndicatorClickListener listener = new LineIndicatorClickListener(position);
            MoreTimesListener moreTimesListener = new MoreTimesListener(position);
            stopName.setOnClickListener(listener);
            imageView.setOnClickListener(listener);
            bg.setOnClickListener(listener);
            moreButton.setOnClickListener(moreTimesListener);
//            exclamationMark.setOnClickListener(new ExclamationMarkListener());
            exclamationMarkTouchArea.setOnClickListener(new ExclamationMarkListener());
        }
    }
}
