package com.example.miro.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.main.WakeUp;
import com.google.android.gms.location.LocationResult;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miro on 11/17/2017.
 */

public class GPSAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String gpsAlarmType = intent.getStringExtra("gpsAlarmType");
        if ("gps".equals(gpsAlarmType)) {
            final List<GPSAlarmSettingsImpl> gpsAlarmSettings = new ArrayList<>();
            try {
                gpsAlarmSettings.addAll(Utils.loadSharedPreferancesWithGPSAlarmSettings(context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final LocationResult lr = LocationResult.extractResult(intent);
            if (lr != null) {
                for (final GPSAlarmSettingsImpl gpsSettings : gpsAlarmSettings) {
                    if (gpsSettings.isOn()) {
                        final double centerLa = gpsSettings.getCoordinates().latitude;
                        final double centerLo = gpsSettings.getCoordinates().longitude;
                        final int radius = gpsSettings.getRadius();
                        float[] distance = new float[2];
                        Toast.makeText(context, "I am here again" + radius + " " + centerLa + " " + centerLo, Toast.LENGTH_LONG).show();
                        Location.distanceBetween(lr.getLastLocation().getLatitude(),
                                lr.getLastLocation().getLongitude(), centerLa, centerLo, distance);
                        if (distance[0] < radius) {

                            context.startActivity(setUpGPSIntent(context, gpsSettings));
                        }
                    }
                }
            }
        }
    }

    private Intent setUpGPSIntent(final Context context, final GPSAlarmSettingsImpl gpsSettings) {
        final Intent intent = new Intent(context, WakeUp.class);
        intent.putExtra("isNormal", "gps");
        intent.putExtra("name", gpsSettings.getName());
        intent.putExtra("volume", gpsSettings.getVolume());
        intent.putExtra("radius", gpsSettings.getRadius());
        intent.putExtra("postponeonoff", gpsSettings.getPostpone().isOn());
        if (gpsSettings.getPostpone().isOn()) {
            intent.putExtra("repeat_times", gpsSettings.getPostpone().getTimesOfRepeat());
        }
        intent.putExtra("type", gpsSettings.getType().getType().ordinal());
        intent.putExtra("nameOfSong", gpsSettings.getSong().getName());
        intent.putExtra("id", gpsSettings.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
