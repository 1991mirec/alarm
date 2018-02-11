package com.example.miro.alarm.inteligentAlarm.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.enums.Type;

/**
 * Created by Miro on 11/26/2016.
 */

public class Utils {

    public static final long ONE_MINUTE_MILISECONDS = 60000;
    public static final long ONE_DAY_MILISECONDS = 86400000;

    private Utils() {
        throw new UnsupportedOperationException("This is utility class");
    }

    public static String typeToString(final Type type, final Context context) {
        if (type.equals(Type.BOTH)) {
            return context.getString(R.string.both);
        } else if (type.equals(Type.SOUND)) {
            return context.getString(R.string.sound);
        } else {
            return context.getString(R.string.vibration);
        }
    }

    public static String getMinutes(final int time, final Context context){
        if (time == 1) {
            return context.getString(R.string.minute);
        } else {
            return context.getString(R.string.minutes);
        }
    }
    public static void requestAccessFinePermissions(final Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
    }
}
