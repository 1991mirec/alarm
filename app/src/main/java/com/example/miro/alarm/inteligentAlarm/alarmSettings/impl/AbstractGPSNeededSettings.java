package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageButton;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Radius;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.receiver.GPSAlarmReceiver;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONException;

/**
 * Created by Miro on 3/19/2018.
 */

abstract class AbstractGPSNeededSettings extends Settings implements Radius {

    private static final int REQ_CODE_WAKE_UP = 70;
    private static final long ONE_MINUTE_IN_MILLISECONDS = 60000;
    protected int radius;
    protected static int isOnCount = 0;
    protected transient Context context;
    protected static PendingIntent pendingIntent = null;
    protected static LocationRequest lr;
    protected transient ImageButton imgAlarm;

    static {
        lr = new LocationRequest();
        lr.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(ONE_MINUTE_IN_MILLISECONDS)
                .setInterval(ONE_MINUTE_IN_MILLISECONDS * 45);
    }

    protected AbstractGPSNeededSettings(String name, int volume, int type, boolean isOn, String songName, Postpone postpone) {
        super(name, volume, type, isOn, songName, postpone);
    }

    protected AbstractGPSNeededSettings(String name) {
        super(name);
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    abstract void setUpLocalIntent(final Intent intent);

    protected Intent setUpIntent() {
        final ComponentName receiver = new ComponentName(context, GPSAlarmReceiver.class);
        final PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        final Intent intent = new Intent(context, GPSAlarmReceiver.class);
        setUpLocalIntent(intent);
        return intent;
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
        if (!wasOn) {
            isOnCount++;
        }
        try {
            Utils.updateAndSaveSharedPreferancesWithGeneralSettings(context, isOnCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateVisuals() {
        isOnCount--;
        imgAlarm.setImageResource(R.mipmap.alarm_black);
    }

    abstract void saveSpecific();

    protected void cancel() {
        imgAlarm.setImageResource(R.mipmap.alarm_black);
        saveSpecific();

        if (isOnCount == 0) {
            final FusedLocationProviderClient f = new FusedLocationProviderClient(context);
            f.removeLocationUpdates(pendingIntent);
            pendingIntent.cancel();
            pendingIntent = null;
        }
        try {
            Utils.updateAndSaveSharedPreferancesWithGeneralSettings(context, isOnCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
