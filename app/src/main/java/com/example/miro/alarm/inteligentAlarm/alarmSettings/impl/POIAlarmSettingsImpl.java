package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.POIAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.tabFragments.POIAlarmFragment;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class POIAlarmSettingsImpl extends Settings implements POIAlarmSettings, Serializable {

    private transient Context context;
    private transient ImageButton imgAlarm;
    private int radius;
    private String poiType;
    private String distanceType;


    public POIAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        setId(id);
        poiType = "Bank";
        this.radius = 1000;
        this.distanceType = context.getResources().getString(R.string.kilometers);
    }

    public POIAlarmSettingsImpl(final Context context, final int id, final String name,
                                final int volume, final boolean isOn, final int type,
                                final String songName, final Postpone postpone,
                                final String poiType, final int radius, final String distanceType) {
        super(name, volume, type, isOn, songName, postpone);
        this.context = context;
        this.poiType = poiType;
        this.radius = radius;
        this.distanceType = distanceType;
        setId(id);
    }

    public void setVisuals(final View view) {
        final TextView name = (TextView) view.findViewById(R.id.textViewNamePOI);
        final TextView radiusTextBox = (TextView) view.findViewById(R.id.poiRadius);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonPOI);
        final ImageView imagePoi= (ImageView) view.findViewById(R.id.imagePOIType);
        imagePoi.setImageResource(view.getResources().getIdentifier(poiType.toLowerCase(),
                "drawable", context.getPackageName()));
        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn ^= true;
                setVisuals(view);

                try {
                    POIAlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if (isOn) {
            imgAlarm.setImageResource(R.mipmap.alarm_green);
        } else {
            imgAlarm.setImageResource(R.mipmap.alarm_black);
        }

        name.setText(poiType);
        double num = (double) radius;
        if (distanceType.equals(context.getResources().getString(R.string.meters))) {
            num = (double) radius / 1000;
        }

        final String radiusText = String.format(Locale.ENGLISH, "%.2f", num);
        final String text = radiusText + " Km";
        radiusTextBox.setText(text);
    }

    @Override
    public String getPoiType() {
        return poiType;
    }

    @Override
    public void setPoiType(final String poiType) {
        this.poiType = poiType;
    }

    public ArrayList<String> getListOfPoiItems() {
        //TODO request to get latest list of items for POI
        final ArrayList<String> poiTypes = new ArrayList<>();
        poiTypes.add("Bank");
        poiTypes.add("ATM");
        return poiTypes;
    }

    public void setAlarm(final POIAlarmSettingsImpl alarm) {
        volume = alarm.getVolume();
        radius = alarm.getRadius();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        distanceType = alarm.getDistanceType();
        poiType = alarm.getPoiType();
        isOn = true;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(final String distanceType) {
        this.distanceType = distanceType;
    }
}

