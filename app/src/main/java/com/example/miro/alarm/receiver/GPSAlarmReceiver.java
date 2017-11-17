package com.example.miro.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.example.miro.alarm.main.WakeUp;
import com.google.android.gms.location.LocationResult;

/**
 * Created by Miro on 11/17/2017.
 */

public class GPSAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Intent wakeUpIntent = new Intent(context, WakeUp.class);
        LocationResult lr = LocationResult.extractResult(wakeUpIntent);
        if (lr != null) {
            final double centerLa = wakeUpIntent.getDoubleExtra("latitude", 1);
            final double centerLo = wakeUpIntent.getDoubleExtra("longitude", 1);
            final int radius = wakeUpIntent.getIntExtra("radius", 1);
            float[] distance = new float[2];
            Location.distanceBetween(lr.getLastLocation().getLatitude(),
                    lr.getLastLocation().getLongitude(), centerLa, centerLo, distance);
            if (distance[0] < radius) {
                wakeUpIntent.putExtras(intent.getExtras());
                context.startActivity(wakeUpIntent);
            }
        }
    }
}
