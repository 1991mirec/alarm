package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.GPSAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.enums.ConnectionType;

import java.io.Serializable;

public class GPSAlarmSettingsImpl extends Settings implements GPSAlarmSettings, Serializable {

    private int radius;
    private int coordinates;
    private ConnectionType conectionType;

    private transient Context context;

    private transient ImageButton imgAlarm;

    public GPSAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        setId(id);
    }

    public void setVisuals(final View view){

    }

    public int getRadius() {
        // TODO implement me
        return radius;
    }

/*    public boolean setSettings(AlarmSettings alarmSettings) {
        // TODO implement me
        return false;
    }*/

    public ConnectionType getConectionType() {
        // TODO implement me
        return conectionType;
    }

    public int getCoordinates() {
        // TODO implement me
        return coordinates;
    }

}

