package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPSAlarmSettingsImpl extends AbstractGPSNeededSettings implements  Serializable {

    private double latitude;
    private double longitude;

    public GPSAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        this.latitude = 45;
        this.longitude = 45;
        this.radius = 1000;
        setId(id);
    }

    public GPSAlarmSettingsImpl(final Context context, final int id, final String name,
                                final int volume, final boolean isOn, final int type,
                                final String songName, final Postpone postpone, int radius,
                                double latitude,
                                double longitude) {
        super(name, volume, type, isOn, songName, postpone);
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        setId(id);
/*        if (isOn && pendingIntent == null) {
            startPositionCheck(false);
        }*/
    }

    public void setVisuals(final View view) {
        final TextView distance = (TextView) view.findViewById(R.id.textViewDistance);
        final TextView latLngTxtView = (TextView) view.findViewById(R.id.textViewLatLngGPS);
        final TextView nameTxtView = (TextView) view.findViewById(R.id.textViewNameGPS);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonGPS);
        final GPSAlarmSettingsImpl alarmSettings = this;
        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn ^= true;
                setVisuals(view);
                if (isOn) {
                    startPositionCheck();
                } else {
                    //isOnCount--;
                    cancel();
                }
                try {
                    Utils.updateAndSaveSharedPreferancesWithGPSAlarmSettingsSpecific(context,
                            alarmSettings);
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
        final String latLngText = String.format(Locale.ENGLISH, "Latitude: %.2f  Longitude: %.2f", latitude, longitude);
        latLngTxtView.setText(latLngText);
        double num = (double) radius / 1000;
        final String radiusText = String.format(Locale.ENGLISH, "%.2f", num);
        final String text = radiusText + " Km";
        distance.setText(text);
        nameTxtView.setText(name);
    }

    @Override
    public List<LatLng> getCoordinates() {
        List<LatLng> finalList = new ArrayList<>();
        finalList.add(new LatLng(latitude, longitude));
        return finalList;
    }

/*    public void setAlarm(final GPSAlarmSettingsImpl alarm, final boolean isOn) {
        volume = alarm.getVolume();

        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();

        this.isOn = isOn;
    }*/

    public void setLatLng(final LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    @Override
    void saveSpecific() {
        try {
            Utils.updateAndSaveSharedPreferancesWithGPSAlarmSettingsSpecific(context, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    void setAlarmSpecific(Settings alarm) {
        radius = ((GPSAlarmSettingsImpl)alarm).getRadius();
        setLatLng(((GPSAlarmSettingsImpl)alarm).getCoordinates().get(0));
    }

}

