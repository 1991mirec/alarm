package com.example.miro.alarm.inteligentAlarm.alarmSettings;

import android.support.test.espresso.core.deps.guava.base.Preconditions;

import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.inteligentAlarm.helper.SongImpl;
import com.example.miro.alarm.inteligentAlarm.helper.TypeImpl;

import java.io.Serializable;

public class Settings implements Serializable {

    protected int volume;
    protected SongImpl song;
    protected String name;
    protected TypeImpl type;
    protected Postpone postpone;
    protected Repeat repeat;
    protected boolean isOn;
    protected int id;

    public void setRepeat(final Repeat repeat) {
        this.repeat = repeat;
    }

    public void setType(final TypeImpl type) {
        this.type = type;
    }

    public void setName(final String name) {
        this.name = name;
    }

    protected Settings(final int volume, final String song, final String name, final Type type,
                       final Postpone postpone, final boolean isOn, final Repeat repeat) {
        super();
        this.isOn = isOn;
        this.volume = volume;
        this.song = new SongImpl(song);
        this.name = name;
        this.type = new TypeImpl(type);
        this.postpone = postpone;
        this.repeat = repeat;

    }

    protected Settings() {

    }

    public boolean isOn() {
        return isOn;
    }

    public int getVolume() {
        // TODO implement me
        return volume;
    }

    public Repeat getRepeat() {
        return repeat;
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

