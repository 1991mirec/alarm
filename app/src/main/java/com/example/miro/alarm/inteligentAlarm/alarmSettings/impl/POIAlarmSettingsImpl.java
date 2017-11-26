package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

import java.io.Serializable;

public class POIAlarmSettingsImpl extends Settings implements Serializable {

    private transient Context context;
    private transient ImageButton imgAlarm;

    public POIAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        setId(id);
    }

    public void setVisuals(final View view) {

    }
}

