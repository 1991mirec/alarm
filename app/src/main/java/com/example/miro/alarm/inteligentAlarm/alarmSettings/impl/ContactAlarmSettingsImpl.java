package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.ContactAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.Default;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Contact;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

import java.io.Serializable;

public class ContactAlarmSettingsImpl extends Settings implements ContactAlarmSettings, Default, Serializable {

    private Contact contact;
    private int radius;

    public ContactAlarmSettingsImpl(int volume, String song, String name, Type type, Postpone postpone, Contact contact,
                                    int radius, boolean isOn, Repeat repeat) {
        super(volume, song, name, type, postpone, isOn, repeat);
        this.radius = radius;
        this.contact = contact;
    }

    public boolean sendInvitation(Contact parameter) {
        // TODO implement me
        return false;
    }

    public Contact getContact() {
        // TODO implement me
        return contact;
    }

    public int getRadius() {
        // TODO implement me
        return radius;
    }

    @Override
    public void setDefault(final int id) {

    }

/*    public boolean setSettings(AlarmSettings alarmSettings) {
        // TODO implement me
        return false;
    }*/

}

