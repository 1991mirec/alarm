package com.example.miro.alarm.inteligentAlarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.MapHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.NameHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.PostponeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.RepeatHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.SongHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.TypeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.holders.VolumeHolder;
import com.example.miro.alarm.inteligentAlarm.adapters.map.MapsActivity;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Miro on 11/24/2016.
 */

public class GpsAlarmSettingsAdapter extends BaseAdapter {

    private GPSAlarmSettingsImpl settings;

    public GpsAlarmSettingsAdapter(final GPSAlarmSettingsImpl settings) {
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
                    final MapHolder mapHolder = new MapHolder();
                    final View row = inflater.inflate(R.layout.four_text_fields, parent, false);
                    mapHolder.changingTextLeft = (TextView) row.findViewById(R.id.changingTxtView1_fourFields);
                    mapHolder.mainTextLeft = (TextView) row.findViewById(R.id.mainTxtView1_fourFields);
                    mapHolder.changingTextRight = (TextView) row.findViewById(R.id.changingTxtView2_fourFields);
                    mapHolder.mainTextRight = (TextView) row.findViewById(R.id.mainTxtView2_fourFields);
                    mapHolder.mainTextLeft.setText("Position");
                    final String lat = String.format(Locale.ENGLISH, "%.2f",
                            settings.getCoordinates().latitude);
                    final String lon = String.format(Locale.ENGLISH, "%.2f",
                            settings.getCoordinates().longitude);
                    mapHolder.changingTextLeft.setText("latitude/longitude: " + lat + "/" + lon);
                    mapHolder.mainTextRight.setText("Radius");
                    double num = (double) settings.getRadius() / 1000;
                    final String radius = String.format(Locale.ENGLISH, "%.2f", num);
                    final String text = radius + " Km";
                    mapHolder.changingTextRight.setText(text);
                    convertView = row;
                    convertView.setTag(mapHolder);
                    break;
                case 1:
                    final TypeHolder typeHolder = new TypeHolder();
                    convertView = settings.getType().setVisual(inflater, parent, typeHolder);
                    convertView.setTag(typeHolder);
                    break;
                case 2:
                    final SongHolder songHolder = new SongHolder();
                    convertView = settings.getSong().setVisual(inflater, parent, songHolder);
                    convertView.setTag(songHolder);
                    break;
                case 3:
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
                case 4:
                    final PostponeHolder postponeHolder = new PostponeHolder();
                    convertView = settings.getPostpone().setVisual(inflater, parent, postponeHolder);
                    convertView.setTag(postponeHolder);
                    break;
                case 5:
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
                    final MapHolder mapHolder = (MapHolder) convertView.getTag();
                    mapHolder.mainTextLeft.setText("Position");
                    final String lat = String.format(Locale.ENGLISH, "%.2f",
                            settings.getCoordinates().latitude);
                    final String lon = String.format(Locale.ENGLISH, "%.2f",
                            settings.getCoordinates().longitude);
                    mapHolder.changingTextLeft.setText("latitude/longitude: " + lat + "/" + lon);
                    mapHolder.mainTextRight.setText("Radius");
                    double num = (double) settings.getRadius() / 1000;
                    final String radius = String.format(Locale.ENGLISH, "%.2f", num);
                    final String text = radius + " Km";
                    mapHolder.changingTextRight.setText(text);
                    break;
                case 1:
                    final TypeHolder typeHolder = (TypeHolder) convertView.getTag();
                    settings.getType().set(typeHolder, context);
                    break;
                case 2:
                    final SongHolder songHolder = (SongHolder) convertView.getTag();
                    settings.getSong().set(songHolder, context);
                    break;
                case 3:
                    final VolumeHolder volumeHolder = (VolumeHolder) convertView.getTag();
                    volumeHolder.seekBar.setProgress(settings.getVolume());
                    break;
                case 4:
                    final PostponeHolder postponeHolder = (PostponeHolder) convertView.getTag();
                    settings.getPostpone().set(postponeHolder, context);
                    break;
                case 5:
                    final NameHolder nameHolder = (NameHolder) convertView.getTag();
                    nameHolder.mainName.setText(context.getText(R.string.name));
                    nameHolder.name.setText(settings.getName());
                    break;
            }
        }


        return convertView;
    }

    public void refresh(final GPSAlarmSettingsImpl settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {if (resultCode == Activity.RESULT_OK) {
            final double longitude = (double) data.getExtras().getSerializable("longitude");
            final double latitude = (double) data.getExtras().getSerializable("latitude");
            this.settings.setLatLng(new LatLng(latitude, longitude));
            this.settings.setRadius(data.getIntExtra("radius", 0));

        }
        refresh(settings);
    }

}
