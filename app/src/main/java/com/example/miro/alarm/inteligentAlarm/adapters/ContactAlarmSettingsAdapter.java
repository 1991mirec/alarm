package com.example.miro.alarm.inteligentAlarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.MapHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.NameHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.PostponeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.SongHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.TypeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.VolumeHolder;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/**
 * Created by Miro on 11/24/2016.
 */

public class ContactAlarmSettingsAdapter extends BaseAdapter {

    private ContactAlarmSettingsImpl settings;

    public ContactAlarmSettingsAdapter(final ContactAlarmSettingsImpl settings) {
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

    public void refresh(final ContactAlarmSettingsImpl settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

}
