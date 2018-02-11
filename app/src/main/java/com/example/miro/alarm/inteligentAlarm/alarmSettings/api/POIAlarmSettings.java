package com.example.miro.alarm.inteligentAlarm.alarmSettings.api;

import com.example.miro.alarm.inteligentAlarm.helper.Radius;

/**
 * Created by Miro on 2/10/2018.
 */

public interface POIAlarmSettings extends Radius {

    String getPoiType();

    void setPoiType(final String poiType);
}
