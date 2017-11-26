package com.example.miro.alarm.inteligentAlarm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;

/**
 * Created by Miro on 11/24/2016.
 */

public class PoiAlarmSettingsAdapter extends BaseAdapter {

    private POIAlarmSettingsImpl settings;

    public PoiAlarmSettingsAdapter(final POIAlarmSettingsImpl settings) {
        this.settings = settings;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            switch (position) {
                case 0:

                    break;

            }
        } else {
            switch (position) {
                case 0:

                    break;

            }
        }


        return convertView;
    }

    public void refresh(final POIAlarmSettingsImpl settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

}
