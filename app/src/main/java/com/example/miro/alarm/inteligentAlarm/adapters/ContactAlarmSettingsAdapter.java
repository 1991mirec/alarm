package com.example.miro.alarm.inteligentAlarm.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.NameHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.PostponeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.RadiusDistanceHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.SongHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.TypeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.VolumeHolder;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;

/**
 * Created by Miro on 11/24/2016.
 */

public class ContactAlarmSettingsAdapter extends BaseAdapter {

    private ContactAlarmSettingsImpl settings;

    public ContactAlarmSettingsAdapter(final ContactAlarmSettingsImpl settings) {
        this.settings = settings;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            switch (position) {
                case 0:
                    final TypeHolder typeHolder = new TypeHolder();
                    convertView = settings.getType().setVisual(inflater, parent, typeHolder);
                    convertView.setTag(typeHolder);
                    break;
                case 1:
                    final SongHolder songHolder = new SongHolder();
                    convertView = settings.getSong().setVisual(inflater, parent, songHolder);
                    convertView.setTag(songHolder);
                    break;
                case 2:
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
                case 3:
                    final PostponeHolder postponeHolder = new PostponeHolder();
                    convertView = settings.getPostpone().setVisual(inflater, parent, postponeHolder);
                    convertView.setTag(postponeHolder);
                    break;
                case 4:
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
                case 5:
                    final RadiusDistanceHolder radiusDistanceHolder = new RadiusDistanceHolder();
                    convertView = inflater.inflate(R.layout.select_box_text_entry, parent, false);
                    radiusDistanceHolder.dropDownBox = (Spinner) convertView.findViewById(R.id.spinnerWithEditText);
                    radiusDistanceHolder.distance = (EditText) convertView.findViewById(R.id.editTextWithSpinner);
                    radiusDistanceHolder.distance.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            settings.setRadius(Integer.parseInt(s.toString()));
                        }
                    });
                    final String[] listOfDistances = context.getResources().getStringArray(R.array.distance);
                    radiusDistanceHolder.distance.setText(String.valueOf(settings.getRadius()));
                    ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_dropdown_item, listOfDistances);
                    radiusDistanceHolder.dropDownBox.setAdapter(distanceAdapter);
                    radiusDistanceHolder.dropDownBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                    android.R.layout.simple_spinner_dropdown_item, listOfDistances);
                            settings.setDistanceType(adapter.getItem(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            parent.setSelection(0);
                        }
                    });
                    convertView.setTag(radiusDistanceHolder);
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    final TypeHolder typeHolder = (TypeHolder) convertView.getTag();
                    settings.getType().set(typeHolder, context);
                    break;
                case 1:
                    final SongHolder songHolder = (SongHolder) convertView.getTag();
                    settings.getSong().set(songHolder, context);
                    break;
                case 2:
                    final VolumeHolder volumeHolder = (VolumeHolder) convertView.getTag();
                    volumeHolder.seekBar.setProgress(settings.getVolume());
                    break;
                case 3:
                    final PostponeHolder postponeHolder = (PostponeHolder) convertView.getTag();
                    settings.getPostpone().set(postponeHolder, context);
                    break;
                case 4:
                    final NameHolder nameHolder = (NameHolder) convertView.getTag();
                    nameHolder.mainName.setText(context.getText(R.string.name));
                    nameHolder.name.setText(settings.getName());
                    break;
                case 5:
                    final RadiusDistanceHolder radiusDistanceHolder =  (RadiusDistanceHolder) convertView.getTag();
                    final String[] listOfDistances = context.getResources().getStringArray(R.array.distance);

                    ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_dropdown_item, listOfDistances);
                    int radiusSpinnerPosition = distanceAdapter.getPosition(settings.getDistanceType());
                    radiusDistanceHolder.dropDownBox.setAdapter(distanceAdapter);
                    radiusDistanceHolder.dropDownBox.setSelection(radiusSpinnerPosition);
                    radiusDistanceHolder.distance.setText(String.valueOf(settings.getRadius()));
                    break;
            }
        }


        return convertView;
    }

    public void refresh(final ContactAlarmSettingsImpl settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

}
