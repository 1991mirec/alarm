package com.example.miro.alarm.inteligentAlarm.helper;

import java.io.Serializable;

class SwitchOnOff implements Serializable{

    protected boolean isTurnedOn;

    SwitchOnOff(boolean isTurnedOn) {
        super();
        this.isTurnedOn = isTurnedOn;
    }

}

