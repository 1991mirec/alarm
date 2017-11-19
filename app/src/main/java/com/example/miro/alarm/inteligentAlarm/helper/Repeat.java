package com.example.miro.alarm.inteligentAlarm.helper;

import android.content.Context;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.TimeAlarmSettingsAdapter;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.RepeatHolder;

import java.io.Serializable;

/**
 * Created by Miro on 11/24/2016.
 */

public class Repeat implements Serializable {

    private boolean everyDay;
    private boolean noDay;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    private Context context;

    public Repeat(final Repeat repeat) {
        this.everyDay = repeat.everyDay;
        this.noDay = repeat.noDay;
        this.monday = repeat.monday;
        this.tuesday = repeat.tuesday;
        this.wednesday = repeat.wednesday;
        this.thursday = repeat.thursday;
        this.friday = repeat.friday;
        this.saturday = repeat.saturday;
        this.sunday = repeat.sunday;
    }

    public Repeat() {
        everyDay = false;
        noDay = true;
        monday = tuesday = wednesday = thursday = friday = saturday = sunday = false;
    }

    public void setContext(final Context context) {
        this.context = context;
    }

    public boolean isNoDay() {
        return noDay;
    }
    public boolean isEveryDay() {
        return everyDay;
    }

    public boolean[] getDays() {
        return new boolean[]{
                sunday,
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday
                };
    }

    public void setDays(final int which, final boolean setter) {
        switch (which) {
            case 1:
                monday = setter;
                break;
            case 2:
                tuesday = setter;
                break;
            case 3:
                wednesday = setter;
                break;
            case 4:
                thursday = setter;
                break;
            case 5:
                friday = setter;
                break;
            case 6:
                saturday = setter;
                break;
            case 0:
                sunday = setter;
                break;
        }
        if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
            noDay = true;
            everyDay = false;
        } else if (monday && tuesday && wednesday && thursday && friday && saturday && sunday) {
            everyDay = true;
            noDay = false;
        } else {
            everyDay = false;
            noDay = false;
        }
    }

    /**
     * Context needs to be set before accessing this method.
     * This method will return repeat strategy of alarm in string.
     */
    @Override
    public String toString() {
        Preconditions.checkNotNull(context);
        String repeat = "";
        if (noDay) {
            repeat = context.getString(R.string.tomorow);
        } else if (everyDay) {
            repeat = context.getString(R.string.every_day);
        } else {
            if (monday) {
                repeat += context.getString(R.string.monday) + " ";
            }
            if (tuesday) {
                repeat += context.getString(R.string.tuesday) + " ";
            }
            if (wednesday) {
                repeat += context.getString(R.string.wednesday) + " ";
            }
            if (thursday) {
                repeat += context.getString(R.string.thursday) + " ";
            }
            if (friday) {
                repeat += context.getString(R.string.friday) + " ";
            }
            if (saturday) {
                repeat += context.getString(R.string.saturday) + " ";
            }
            if (sunday) {
                repeat += context.getString(R.string.sunday) + " ";
            }
        }
        context = null;
        return repeat;
    }

    public View setVisual(final LayoutInflater inflater, final ViewGroup parent, final RepeatHolder holder) {
        context = inflater.getContext();
        final View row = inflater.inflate(R.layout.two_text_fields, parent, false);
        holder.changingText = (TextView) row.findViewById(R.id.changingTxtView_twoFields);
        holder.mainText = (TextView) row.findViewById(R.id.mainTxtView_twoFields);
        set(holder, context);

        context = null;
        return row;
    }

    public void set(final RepeatHolder holder, final Context contextLocal) {
        context =contextLocal;
        holder.mainText.setText(context.getResources().getText(R.string.repeat));
        holder.changingText.setText(toString());
        context = null;
    }
}
