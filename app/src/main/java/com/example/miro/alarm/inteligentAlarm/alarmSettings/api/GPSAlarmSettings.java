package com.example.miro.alarm.inteligentAlarm.alarmSettings.api;


import com.example.miro.alarm.inteligentAlarm.enums.ConnectionType;
import com.example.miro.alarm.inteligentAlarm.helper.Radius;
import com.google.android.gms.maps.model.LatLng;

public interface GPSAlarmSettings extends Radius{

    LatLng getCoordinates();
}

