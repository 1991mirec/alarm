package com.example.miro.alarm.inteligentAlarm.alarmSettings;

import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.SongImpl;
import com.example.miro.alarm.inteligentAlarm.helper.TypeImpl;
import com.google.common.base.Preconditions;

import java.io.Serializable;

public class Settings implements Serializable {

    protected int volume;
    protected SongImpl song;
    protected String name;
    protected TypeImpl type;
    protected Postpone postpone;
    protected boolean isOn;
    protected int id;

    public void setType(final TypeImpl type) {
        this.type = type;
    }

    public void setName(final String name) {
        this.name = name;
    }

    protected Settings(final int volume, final String song, final String name, final Type type,
                       final Postpone postpone, final boolean isOn) {
        super();
        this.isOn = isOn;
        this.volume = volume;
        this.song = new SongImpl(song);
        this.name = name;
        this.type = new TypeImpl(type);
        this.postpone = postpone;
    }

    protected Settings(final String name) {
        volume = 1;
        this.name = name;
        song = new SongImpl("loud_alarm_buzzer.mp3");
        type = new TypeImpl(Type.BOTH);
        postpone = new Postpone(1, 1, false);

        isOn = false;
    }

    protected Settings(final String name, final int volume, final int type, final boolean isOn,
                       final String songName, final Postpone postpone) {
        this.volume = volume;
        this.name = name;
        this.song = new SongImpl(songName);
        switch (type) {
            case 0:
                this.type = new TypeImpl(Type.SOUND);
                break;
            case 1:
                this.type = new TypeImpl(Type.VIBRATION);
                break;
            case 2:
                this.type = new TypeImpl(Type.BOTH);
                break;
        }
        this.postpone = postpone;

        this.isOn = isOn;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getVolume() {
        // TODO implement me
        return volume;
    }

    public SongImpl getSong() {
        // TODO implement me
        return song;
    }

    public TypeImpl getType() {
        // TODO implement me
        return type;
    }

    public String getName() {
        // TODO implement me
        return name;
    }

    public Postpone getPostpone() {
        // TODO implement me
        return postpone;
    }

    protected void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        Preconditions.checkNotNull(id);
        return id;
    }

    public void setVolume(final int volume) {
        this.volume = volume;
    }
}

