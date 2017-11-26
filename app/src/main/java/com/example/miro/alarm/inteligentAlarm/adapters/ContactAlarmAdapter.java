package com.example.miro.alarm.inteligentAlarm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;

/**
 * Created by Miro on 11/26/2016.
 */

public class ContactAlarmAdapter extends ArrayAdapter<ContactAlarmSettingsImpl>{

    private final Context context;
    private final int resource;
    private final ContactAlarmSettingsImpl[] data;

    public ContactAlarmAdapter(final Context context, final int resource,
                               final ContactAlarmSettingsImpl[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.data = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") final View view = inflater.inflate(resource, parent, false);
        data[position].setVisuals(view);
        return view;
    }
}
