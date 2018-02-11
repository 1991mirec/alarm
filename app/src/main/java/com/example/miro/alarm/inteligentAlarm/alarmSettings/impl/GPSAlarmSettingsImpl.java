package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.GPSAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.InteligentAlarm;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.tabFragments.AlarmFragment;
import com.example.miro.alarm.tabFragments.GPSAlarmFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

public class GPSAlarmSettingsImpl extends Settings implements GPSAlarmSettings, Serializable {

    private int radius;
    private double latitude;
    private double longitude;

    private transient Context context;

    private transient ImageButton imgAlarm;

    public GPSAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        this.latitude = 45;
        this.longitude = 45;
        this.radius = 1000;
        setId(id);
    }

    public GPSAlarmSettingsImpl(final Context context, final int id, final String name,
                                final int volume, final boolean isOn, final int type,
                                final String songName, final Postpone postpone, int radius,
                                double latitude,
                                double longitude) {
        super(name, volume, type, isOn, songName, postpone);
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        setId(id);
    }

    public void setVisuals(final View view) {
        final TextView distance = (TextView) view.findViewById(R.id.textViewDistance);
        final TextView latLngTxtView = (TextView) view.findViewById(R.id.textViewLatLngGPS);
        final TextView nameTxtView = (TextView) view.findViewById(R.id.textViewNameGPS);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonGPS);

        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn ^= true;
                setVisuals(view);

                try {
                    GPSAlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if (isOn) {
            imgAlarm.setImageResource(R.mipmap.alarm_green);
        } else {
            imgAlarm.setImageResource(R.mipmap.alarm_black);
        }
        final String latLngText = "Latitude: " + latitude + "  Longitude: " + longitude;
        latLngTxtView.setText(latLngText);
        double num = (double) radius / 1000;
        final String radiusText = String.format(Locale.ENGLISH, "%.2f", num);
        final String text = radiusText + " Km";
        distance.setText(text);
        nameTxtView.setText(name);
    }

    public int getRadius() {
        // TODO implement me
        return radius;
    }

    public LatLng getCoordinates() {
        // TODO implement me
        return new LatLng(latitude, longitude);
    }

    public void setAlarm(final GPSAlarmSettingsImpl alarm) {
        volume = alarm.getVolume();
        radius = alarm.getRadius();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        setLatLng(alarm.getCoordinates());
        isOn = true;
    }

    public void setLatLng(final LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public void setRadius(final int radius) {
        this.radius = radius;
    }

    public void cancel() {
        isOn = false;
        imgAlarm.setImageResource(R.mipmap.alarm_black);
        try {
            GPSAlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

