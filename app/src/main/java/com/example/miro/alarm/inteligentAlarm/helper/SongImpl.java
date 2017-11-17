package com.example.miro.alarm.inteligentAlarm.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.TimeAlarmSettingsAdapter;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.SongHolder;

import java.io.Serializable;

/**
 * Created by Miro on 11/26/2016.
 */

public class SongImpl implements Serializable {

    private final String name;

    public SongImpl(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public View setVisual(final LayoutInflater inflater, final ViewGroup parent, final SongHolder songHolder) {
        View row = inflater.inflate(R.layout.two_text_fields, parent, false);
        songHolder.changingText = (TextView) row.findViewById(R.id.changingTxtView_twoFields);
        songHolder.mainText = (TextView) row.findViewById(R.id.mainTxtView_twoFields);
        final Context context = inflater.getContext();
        set(songHolder, context);
        return row;
    }

    public void set(final SongHolder songHolder, final Context context) {
        songHolder.changingText.setText(name);
        songHolder.mainText.setText(context.getText(R.string.alarm_tone));
    }
}
