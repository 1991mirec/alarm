package com.example.miro.alarm.inteligentAlarm.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.TimeAlarmSettingsAdapter;

import java.io.Serializable;

public class InteligentAlarm extends SwitchOnOff implements Serializable {

    private int timeBeforeRealAlarm;
    private SongImpl song;

    public InteligentAlarm(String song, int timeBeforeRealAlarm, boolean isTurnedOn) {
        super(isTurnedOn);
        this.song = new SongImpl(song);
        this.timeBeforeRealAlarm = timeBeforeRealAlarm;
    }

    public SongImpl getSong() {
        // TODO implement me
        return song;
    }

    public int getTimeBeforeRealAlaram() {
        // TODO implement me
        return timeBeforeRealAlarm;
    }

    public boolean isOn() {
        return isTurnedOn;
    }
    private void setCheckedChange(final boolean isChecked) {
        isTurnedOn = isChecked;
    }

    public View setVisual(final LayoutInflater inflater, final ViewGroup parent, final TimeAlarmSettingsAdapter.IntelligentAlarmHolder intelligentAlarmHolder) {
        final View row = inflater.inflate(R.layout.two_text_fields_and_switch, parent, false);
        final Context context = inflater.getContext();
        intelligentAlarmHolder.mainText = (TextView) row.findViewById(R.id.maintxtViewSwitch);
        intelligentAlarmHolder.changingText = (TextView) row.findViewById(R.id.changingTxtViewSwitch);
        intelligentAlarmHolder.aSwitch = (Switch) row.findViewById(R.id.switch3);
        intelligentAlarmHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckedChange(isChecked);
            }
        });

        set(intelligentAlarmHolder, context);
        return row;
    }

    public void set(final TimeAlarmSettingsAdapter.IntelligentAlarmHolder intelligentAlarmHolder, final Context context) {
        final String minutes = Utils.getMinutes(timeBeforeRealAlarm, context);

        intelligentAlarmHolder.mainText.setText(context.getText(R.string.intelligent_alarm));
        intelligentAlarmHolder.changingText.setText(timeBeforeRealAlarm + minutes + ", " + song.getName());
        intelligentAlarmHolder.aSwitch.setChecked(super.isTurnedOn);
    }

}

