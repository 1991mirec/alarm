package com.example.miro.alarm.inteligentAlarm.alarmSettings.api;


import com.example.miro.alarm.inteligentAlarm.enums.ConnectionType;
import com.example.miro.alarm.inteligentAlarm.helper.Radius;

public interface GPSAlarmSettings extends Radius{

    ConnectionType getConectionType();

    int getCoordinates();
}

