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

public class Postpone extends SwitchOnOff implements Serializable {

    private int timesOfRepeat;
    private int minutes;

    public Postpone(int timesOfRepeat, int minutes, boolean isTurnedOn) {
        super(isTurnedOn);
        this.timesOfRepeat = timesOfRepeat;
        this.minutes = minutes;
    }

    public int getMinutes() {
        // TODO implement me
        return minutes;
    }

    public int getTimesOfRepeat() {
        // TODO implement me
        return timesOfRepeat;
    }

    public boolean isOn() {
        return  isTurnedOn;
    }
    private void setCheckedChange(final boolean isChecked) {
        isTurnedOn = isChecked;
    }

    public View setVisual(final LayoutInflater inflater, final ViewGroup parent, final TimeAlarmSettingsAdapter.PostponeHolder postponeHolder) {
        final View row = inflater.inflate(R.layout.two_text_fields_and_switch, parent, false);
        final Context context = inflater.getContext();
        postponeHolder.mainText = (TextView) row.findViewById(R.id.maintxtViewSwitch);
        postponeHolder.changingText = (TextView) row.findViewById(R.id.changingTxtViewSwitch);
        postponeHolder.aSwitch = (Switch) row.findViewById(R.id.switch3);
        postponeHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckedChange(isChecked);
            }
        });
        set(postponeHolder, context);

        return row;
    }

    public void set(final TimeAlarmSettingsAdapter.PostponeHolder postponeHolder, final Context context) {
        final String times = context.getString(R.string.times);

        final String minutesString = Utils.getMinutes(minutes, context);
        postponeHolder.mainText.setText(context.getText(R.string.postpone_alarm));
        postponeHolder.changingText.setText(minutes + " " + minutesString + ", " + timesOfRepeat + " " + times);
        postponeHolder.aSwitch.setChecked(super.isTurnedOn);
    }

}

