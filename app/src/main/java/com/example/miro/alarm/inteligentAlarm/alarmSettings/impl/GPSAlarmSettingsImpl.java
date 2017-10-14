package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.Default;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.GPSAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.enums.ConnectionType;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

import java.io.Serializable;

public class GPSAlarmSettingsImpl extends Settings implements GPSAlarmSettings, Default, Serializable {

    private int radius;
    private int coordinates;
    private ConnectionType conectionType;

    public GPSAlarmSettingsImpl(int volume, String song, String name, Type type, Postpone postpone, int radius,
                                int coordinates, ConnectionType conectionType, boolean isOn, Repeat repeat) {
        super(volume,song,name,type,postpone, isOn, repeat);
        this.radius = radius;
        this.coordinates = coordinates;
        this.conectionType = conectionType;
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

    @Override
    public void setDefault(final int id) {

    }
}

