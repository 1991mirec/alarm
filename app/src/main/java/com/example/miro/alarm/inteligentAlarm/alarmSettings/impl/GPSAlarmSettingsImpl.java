package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.GPSAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.receiver.GPSAlarmReceiver;
import com.example.miro.alarm.tabFragments.PlaceholderFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.Serializable;
import java.util.Locale;

public class GPSAlarmSettingsImpl extends Settings implements GPSAlarmSettings, Serializable {

    private static final int REQ_CODE_WAKE_UP = 70;
    private static final long ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static PendingIntent pendingIntent = null;
    private int radius;
    private double latitude;
    private double longitude;

    private transient Context context;
    private transient ImageButton imgAlarm;

    private static int isOnCount = 0;
    private static LocationRequest lr;

    static {
        lr = new LocationRequest();
        lr.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(ONE_MINUTE_IN_MILLISECONDS)
                .setInterval(ONE_MINUTE_IN_MILLISECONDS * 45);
    }


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
        if (isOn && pendingIntent == null) {
            startPositionCheck(false);
        }
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
                    startPositionCheck(false);
                } else {
                    isOnCount--;
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

    public int getRadius() {
        return radius;
    }

    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    public void setAlarm(final GPSAlarmSettingsImpl alarm, final boolean isOn) {
        volume = alarm.getVolume();
        radius = alarm.getRadius();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        setLatLng(alarm.getCoordinates());
        this.isOn = isOn;
    }

    public void setLatLng(final LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public void setRadius(final int radius) {
        this.radius = radius;
    }

    private void cancel() {
        imgAlarm.setImageResource(R.mipmap.alarm_black);
        try {
            Utils.updateAndSaveSharedPreferancesWithGPSAlarmSettingsSpecific(context, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isOnCount == 0) {
            final FusedLocationProviderClient f = new FusedLocationProviderClient(context);
            f.removeLocationUpdates(pendingIntent);
            pendingIntent.cancel();
            pendingIntent = null;
        }
        try {
            Utils.updateAndSaveSharedPreferancesWithGeneralSettings(context, pendingIntent, isOnCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Intent setUpIntent() {
        final ComponentName receiver = new ComponentName(context, GPSAlarmReceiver.class);
        final PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return new Intent(context, GPSAlarmReceiver.class);
    }

    public void startPositionCheck(boolean wasOn) {
        final FusedLocationProviderClient f = new FusedLocationProviderClient(context);
        if (pendingIntent == null) {
            final Intent intent = setUpIntent();
            pendingIntent = PendingIntent.getBroadcast(context,
                    REQ_CODE_WAKE_UP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Utils.requestAccessFinePermissions((Activity) context);
                return;
            }

            f.requestLocationUpdates(lr, pendingIntent);
        } else {
            f.requestLocationUpdates(lr, pendingIntent);
        }
        if (!wasOn){
            isOnCount++;
        }
        try {
            Utils.updateAndSaveSharedPreferancesWithGeneralSettings(context, pendingIntent, isOnCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateVisuals() {
        isOnCount--;
        imgAlarm.setImageResource(R.mipmap.alarm_black);
    }
}

