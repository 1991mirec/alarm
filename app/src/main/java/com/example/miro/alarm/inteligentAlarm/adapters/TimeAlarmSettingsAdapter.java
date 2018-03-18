package com.example.miro.alarm.inteligentAlarm.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.NameHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.PostponeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.RepeatHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.SongHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.TypeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.VolumeHolder;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;

import java.util.Calendar;

/**
 * Created by Miro on 11/24/2016.
 */

public class TimeAlarmSettingsAdapter extends BaseAdapter {

    private TimeAlarmSettingsImpl settings;
    private ViewGroup parent;

    public TimeAlarmSettingsAdapter(final TimeAlarmSettingsImpl settings) {
        this.settings = settings;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Object getItem(int position) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        switch (position) {
            case 1:
                final RepeatHolder repeatHolder = new RepeatHolder();
                return settings.getRepeat().setVisual(inflater, parent, repeatHolder);
            case 2:
                final TypeHolder typeHolder = new TypeHolder();
                return settings.getType().setVisual(inflater, parent, typeHolder);
        }
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            switch (position) {
                case 0:
                    final TimePickerHolder timePickerHolder = new TimePickerHolder();
                    convertView = inflater.inflate(R.layout.time, parent, false);

                    timePickerHolder.timePicker = (TimePicker) convertView.findViewById(R.id.timePicker);
                    timePickerHolder.timePicker.setCurrentHour(settings.getTime().get(Calendar.HOUR_OF_DAY));
                    timePickerHolder.timePicker.setCurrentMinute(settings.getTime().get(Calendar.MINUTE));

                    timePickerHolder.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            Calendar temp = Calendar.getInstance();
                            temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            temp.set(Calendar.MINUTE, minute);
                            settings.getTime().setTimeInMillis(temp.getTimeInMillis());
                        }
                    });
                    //Becuase google time picker setOnTimeChangedListener not working for am pm change
                    final TimePicker tp = timePickerHolder.timePicker;
                    final ViewGroup amPmView;
                    final ViewGroup v1 = (ViewGroup) tp.getChildAt(0);
                    final ViewGroup v2 = (ViewGroup) v1.getChildAt(0);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        ViewGroup v3 = (ViewGroup) v2.getChildAt(0);
                        amPmView = (ViewGroup) v3.getChildAt(3);
                    } else {
                        amPmView = (ViewGroup) v2.getChildAt(3);
                    }
                    amPmView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            final int houre = settings.getTime().get(Calendar.HOUR_OF_DAY);
                            if (houre >= 12) {
                                settings.getTime().set(Calendar.HOUR_OF_DAY, houre - 12);
                            }
                            return false;
                        }
                    });

                    amPmView.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            final int houre = settings.getTime().get(Calendar.HOUR_OF_DAY);
                            if (houre < 12) {
                                settings.getTime().set(Calendar.HOUR_OF_DAY, houre + 12);
                            }
                            return false;
                        }
                    });

                    convertView.setTag(timePickerHolder);
                    break;
                case 1:
                    final RepeatHolder repeatHolder = new RepeatHolder();
                    convertView = settings.getRepeat().setVisual(inflater, parent, repeatHolder);
                    convertView.setTag(repeatHolder);
                    break;
                case 2:
                    final TypeHolder typeHolder = new TypeHolder();
                    convertView = settings.getType().setVisual(inflater, parent, typeHolder);
                    convertView.setTag(typeHolder);
                    break;
                case 3:
                    final SongHolder songHolder = new SongHolder();
                    convertView = settings.getSong().setVisual(inflater, parent, songHolder);
                    convertView.setTag(songHolder);
                    break;
                case 4:
                    final VolumeHolder volumeHolder = new VolumeHolder();
                    convertView = inflater.inflate(R.layout.seek_bar, parent, false);
                    volumeHolder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar2);
                    volumeHolder.seekBar.setProgress(settings.getVolume());
                    volumeHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            settings.setVolume(seekBar.getProgress());
                        }
                    });
                    convertView.setTag(volumeHolder);
                    break;
                case 5:
                    final PostponeHolder postponeHolder = new PostponeHolder();
                    convertView = settings.getPostpone().setVisual(inflater, parent, postponeHolder);
                    convertView.setTag(postponeHolder);
                    break;
                case 6:
                    final IntelligentAlarmHolder intelligentAlarmHolder = new IntelligentAlarmHolder();
                    convertView = settings.getInteligentAlarm().setVisual(inflater, parent, intelligentAlarmHolder);
                    convertView.setTag(intelligentAlarmHolder);
                    break;
                case 7:
                    final NameHolder nameHolder = new NameHolder();
                    convertView = inflater.inflate(R.layout.text_field_and_text_entry, parent, false);
                    nameHolder.mainName = (TextView) convertView.findViewById(R.id.mainTxtView_oneView);
                    nameHolder.name = (EditText) convertView.findViewById(R.id.editTextName);
                    nameHolder.mainName.setText(context.getText(R.string.name));
                    nameHolder.name.setText(settings.getName());
                    nameHolder.name.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            settings.setName(s.toString());
                        }
                    });
                    convertView.setTag(nameHolder);
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    final TimePickerHolder timePickerHolder = (TimePickerHolder) convertView.getTag();
                    timePickerHolder.timePicker.setCurrentHour(settings.getTime().get(Calendar.HOUR_OF_DAY));
                    timePickerHolder.timePicker.setCurrentMinute(settings.getTime().get(Calendar.MINUTE));
                    break;
                case 1:
                    final RepeatHolder repeatHolder = (RepeatHolder) convertView.getTag();
                    settings.getRepeat().set(repeatHolder, context);
                    break;
                case 2:
                    final TypeHolder typeHolder = (TypeHolder) convertView.getTag();
                    settings.getType().set(typeHolder, context);
                    break;
                case 3:
                    final SongHolder songHolder = (SongHolder) convertView.getTag();
                    settings.getSong().set(songHolder, context);
                    break;
                case 4:
                    final VolumeHolder volumeHolder = (VolumeHolder) convertView.getTag();
                    volumeHolder.seekBar.setProgress(settings.getVolume());
                    break;
                case 5:
                    final PostponeHolder postponeHolder = (PostponeHolder) convertView.getTag();
                    settings.getPostpone().set(postponeHolder, context);
                    break;
                case 6:
                    final IntelligentAlarmHolder intelligentAlarmHolder = (IntelligentAlarmHolder) convertView.getTag();
                    settings.getInteligentAlarm().set(intelligentAlarmHolder, context);
                    break;
                case 7:
                    final NameHolder nameHolder = (NameHolder) convertView.getTag();
                    nameHolder.mainName.setText(context.getText(R.string.name));
                    nameHolder.name.setText(settings.getName());
                    break;
            }
        }

        return convertView;
    }

    public void refresh(final TimeAlarmSettingsImpl settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

    public static class TimePickerHolder {
        public TimePicker timePicker;
    }

    public static class IntelligentAlarmHolder {
        public TextView changingText;
        public TextView mainText;
        public Switch aSwitch;
    }

}
