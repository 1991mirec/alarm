package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

import java.io.Serializable;

public class POIAlarmSettingsImpl extends Settings implements Serializable {

    public POIAlarmSettingsImpl(int volume, String song, String name, Type type, Postpone postpone,
                                boolean isOn, Repeat repeat) {
        super(volume,song,name,type,postpone, isOn, repeat);
    }

}

