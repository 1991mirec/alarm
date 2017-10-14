package com.example.miro.alarm.inteligentAlarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.miro.alarm.inteligentAlarm.adapters.holders.TypeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.map.MapsActivity;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;

/**
 * Created by Miro on 11/24/2016.
 */

public class GpsAlarmSettingsAdapter extends BaseAdapter {

    private GPSAlarmSettingsImpl settings;

    public GpsAlarmSettingsAdapter(final GPSAlarmSettingsImpl settings) {
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
                    final TypeHolder typeHolder = new TypeHolder();
                    convertView = settings.getType().setVisual(inflater, parent, typeHolder);
                    convertView.setTag(typeHolder);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MapsActivity.class);
                            ((Activity) context).startActivityForResult(intent,22);
                        }
                    });
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    final TypeHolder typeHolder = (TypeHolder) convertView.getTag();
                    settings.getType().set(typeHolder, context);
                    break;
            }
        }

        return convertView;
    }

    public void refresh(final GPSAlarmSettingsImpl settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

}
