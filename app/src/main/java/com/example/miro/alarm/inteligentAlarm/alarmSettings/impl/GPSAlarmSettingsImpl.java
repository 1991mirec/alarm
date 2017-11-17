package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.GPSAlarmSettings;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

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

    public void setVisuals(final View view) {

    }

    public int getRadius() {
        // TODO implement me
        return radius;
    }

    public LatLng getCoordinates() {
        // TODO implement me
        return new LatLng(latitude, longitude);
    }

    public void setAlarm(GPSAlarmSettingsImpl alarm) {
        volume = alarm.getVolume();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        repeat = alarm.repeat;
        isOn = true;
    }

    public void setLatLng(final LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public void setRadius(final int radius) {
        this.radius = radius;
    }
}

