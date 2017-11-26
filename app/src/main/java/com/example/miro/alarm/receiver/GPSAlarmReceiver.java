package com.example.miro.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.example.miro.alarm.main.WakeUp;
import com.google.android.gms.location.LocationResult;

/**
 * Created by Miro on 11/17/2017.
 */

public class GPSAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I am here again", Toast.LENGTH_LONG).show();
        final int id = intent.getExtras().getInt("id");
        if (("intentGPS" + id).equals(intent.getAction())) {
            LocationResult lr = LocationResult.extractResult(intent);
            if (lr != null) {
                final Intent wakeUpIntent = new Intent(context, WakeUp.class);
                final double centerLa = intent.getDoubleExtra("latitude", 1);
                final double centerLo = intent.getDoubleExtra("longitude", 1);
                final int radius = intent.getIntExtra("radius", 1);
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
}
