package com.example.miro.alarm.inteligentAlarm.helper;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.enums.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Miro on 11/26/2016.
 */

public class Utils {

    public static final long ONE_MINUTE_MILISECONDS = 60000;
    public static final long ONE_DAY_MILISECONDS = 86400000;
    public static String MY_PHONE_NUMBER;
    public static final String API_PREFIX= "http://192.168.0.103:15001";

    private static final String PREFS_NAME = "MyPrefsFile";

    private Utils() {
        throw new UnsupportedOperationException("This is utility class");
    }

    static String typeToString(final Type type, final Context context) {
        if (type.equals(Type.BOTH)) {
            return context.getString(R.string.both);
        } else if (type.equals(Type.SOUND)) {
            return context.getString(R.string.sound);
        } else {
            return context.getString(R.string.vibration);
        }
    }

    static String getMinutes(final int time, final Context context) {
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

    public static List<TimeAlarmSettingsImpl> loadSharedPreferancesWithAlarmSettingsClock(final Context context) throws JSONException {
        final List<TimeAlarmSettingsImpl> timeSettings = new ArrayList<>();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String main = sharedPreferences.getString("timeSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONArray array = mainObject.getJSONArray("timeAlarmSettings");
            for (int i = 0, size = array.length(); i < size; i++) {
                final long time = ((JSONObject) array.get(i)).getLong("time");
                final String name = ((JSONObject) array.get(i)).getString("name");
                final String songName = ((JSONObject) array.get(i)).getString("songName");
                final int type = ((JSONObject) array.get(i)).getInt("type");
                final int volume = ((JSONObject) array.get(i)).getInt("volume");
                final boolean isOn = ((JSONObject) array.get(i)).getBoolean("isOn");
                final JSONObject repeat = ((JSONObject) array.get(i)).getJSONObject("repeat");
                final Repeat repeatObj = new Repeat();
                repeatObj.setDays(1, repeat.getBoolean("monday"));
                repeatObj.setDays(2, repeat.getBoolean("tuesday"));
                repeatObj.setDays(3, repeat.getBoolean("wednesday"));
                repeatObj.setDays(4, repeat.getBoolean("thursday"));
                repeatObj.setDays(5, repeat.getBoolean("friday"));
                repeatObj.setDays(6, repeat.getBoolean("saturday"));
                repeatObj.setDays(0, repeat.getBoolean("sunday"));

                final JSONObject postpone = ((JSONObject) array.get(i)).getJSONObject("postpone");
                final boolean postponeIsOn = postpone.getBoolean("isOn");
                final int postponeMinutes = postpone.getInt("minutes");
                final int postponeTimes = postpone.getInt("timesOfRepeat");
                final Postpone postponeObj = new Postpone(postponeTimes, postponeMinutes,
                        postponeIsOn);
                final JSONObject IntelligentAlarmObj = ((JSONObject) array.get(i))
                        .getJSONObject("inteligentAlarm");
                final boolean intelligentIsOn = IntelligentAlarmObj.getBoolean("isOn");
                final String intelligentSongName = IntelligentAlarmObj.getString("songName");
                final int intelligentMinutesBeforeReal = IntelligentAlarmObj.getInt("minutesBeforeReal");
                final InteligentAlarm inteligentAlarm = new InteligentAlarm(intelligentSongName,
                        intelligentMinutesBeforeReal, intelligentIsOn);
                final Calendar calTime = Calendar.getInstance();
                calTime.setTimeInMillis(time);
                final TimeAlarmSettingsImpl timeSettingsReturned =
                        new TimeAlarmSettingsImpl(context, i, calTime, inteligentAlarm,
                                name, volume, isOn, type, songName, repeatObj, postponeObj);
                timeSettings.add(timeSettingsReturned);
            }
        }
        return timeSettings;
    }

    public static void updateAndSaveSharedPreferancesWithAlarmSettingsClock(final Context context,
                                                                            final List<TimeAlarmSettingsImpl> timeSettings)
            throws JSONException {
        final JSONArray listOfSettings = new JSONArray();
        for (final TimeAlarmSettingsImpl settings : timeSettings) {
            final JSONObject obj = new JSONObject();
            obj.put("time", settings.getTime().getTimeInMillis());
            obj.put("name", settings.getName());
            obj.put("songName", settings.getSong().getName());
            obj.put("type", settings.getType().getType().ordinal());
            final JSONObject repeatObj = new JSONObject();
            repeatObj.put("monday", settings.getRepeat().getDays()[1]);
            repeatObj.put("tuesday", settings.getRepeat().getDays()[2]);
            repeatObj.put("wednesday", settings.getRepeat().getDays()[3]);
            repeatObj.put("thursday", settings.getRepeat().getDays()[4]);
            repeatObj.put("friday", settings.getRepeat().getDays()[5]);
            repeatObj.put("saturday", settings.getRepeat().getDays()[6]);
            repeatObj.put("sunday", settings.getRepeat().getDays()[0]);
            obj.put("repeat", repeatObj);
            obj.put("volume", settings.getVolume());
            final JSONObject postpone = new JSONObject();
            postpone.put("isOn", settings.getPostpone().isOn());
            postpone.put("minutes", settings.getPostpone().getMinutes());
            postpone.put("timesOfRepeat", settings.getPostpone().getTimesOfRepeat());
            obj.put("postpone", postpone);
            obj.put("isOn", settings.isOn());
            final JSONObject inteligentAlarm = new JSONObject();
            inteligentAlarm.put("isOn", settings.getInteligentAlarm().isOn());
            inteligentAlarm.put("songName", settings.getInteligentAlarm().getSong().getName());
            inteligentAlarm.put("minutesBeforeReal", settings.getInteligentAlarm()
                    .getTimeBeforeRealAlaram());
            obj.put("inteligentAlarm", inteligentAlarm);
            listOfSettings.put(settings.getId(), obj);
        }
        final JSONObject mainObj = new JSONObject();
        mainObj.put("timeAlarmSettings", listOfSettings);
        final String stringToSave = mainObj.toString();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("timeSettings", stringToSave).apply();
    }

    public static void updateAndSaveSharedPreferancesWithAlarmSettingsClockSpecific(final Context context,
                                                                                    final TimeAlarmSettingsImpl timeSetting)
            throws JSONException {
        final List<TimeAlarmSettingsImpl> timeAlarmSettings = loadSharedPreferancesWithAlarmSettingsClock(context);
        timeAlarmSettings.get(timeSetting.getId()).setAlarm(timeSetting, timeSetting.isOn());
        updateAndSaveSharedPreferancesWithAlarmSettingsClock(context, timeAlarmSettings);
    }

    public static void updateAndSaveSharedPreferancesWithGPSAlarmSettings(final Context context,
                                                                          final List<GPSAlarmSettingsImpl> gpsSettings)
            throws JSONException {
        final JSONArray listOfSettings = new JSONArray();
        for (final GPSAlarmSettingsImpl settings : gpsSettings) {
            final JSONObject obj = new JSONObject();
            obj.put("name", settings.getName());
            obj.put("songName", settings.getSong().getName());
            obj.put("type", settings.getType().getType().ordinal());
            obj.put("radius", settings.getRadius());
            obj.put("longitude", settings.getCoordinates().longitude);
            obj.put("latitude", settings.getCoordinates().latitude);
            obj.put("volume", settings.getVolume());
            final JSONObject postpone = new JSONObject();
            postpone.put("isOn", settings.getPostpone().isOn());
            postpone.put("minutes", settings.getPostpone().getMinutes());
            postpone.put("timesOfRepeat", settings.getPostpone().getTimesOfRepeat());
            obj.put("postpone", postpone);
            obj.put("isOn", settings.isOn());
            listOfSettings.put(settings.getId(), obj);
        }
        final JSONObject mainObj = new JSONObject();
        mainObj.put("gpsAlarmSettings", listOfSettings);
        final String stringToSave = mainObj.toString();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("gpsSettings", stringToSave).apply();
    }

    public static void updateAndSaveSharedPreferancesWithGeneralSettings(final Context context,
                                                                         final PendingIntent intent,
                                                                         final int isOnCount)
            throws JSONException {
        final JSONObject obj = new JSONObject();
        obj.put("gpsIsOnCount", isOnCount);

        final JSONObject mainObj = new JSONObject();
        mainObj.put("general", obj);
        final String stringToSave = mainObj.toString();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("generalSettings", stringToSave).apply();
    }

    public static Map<String, Object> loadSharedPreferancesWithgeneralSettings(final Context context) throws JSONException {
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, Object> map = new HashMap<>();
        final String main = sharedPreferences.getString("generalSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONObject obj = mainObject.getJSONObject("general");
            final int isOn = obj.getInt("gpsIsOnCount");
            map.put("isOn", isOn);
        }
        return map;
    }

    public static List<GPSAlarmSettingsImpl> loadSharedPreferancesWithGPSAlarmSettings(final Context context) throws JSONException {
        final List<GPSAlarmSettingsImpl> gpsSettings = new ArrayList<>();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String main = sharedPreferences.getString("gpsSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONArray array = mainObject.getJSONArray("gpsAlarmSettings");
            for (int i = 0, size = array.length(); i < size; i++) {
                final String name = ((JSONObject) array.get(i)).getString("name");
                final String songName = ((JSONObject) array.get(i)).getString("songName");
                final int type = ((JSONObject) array.get(i)).getInt("type");
                final int radius = ((JSONObject) array.get(i)).getInt("radius");
                final double longitude = ((JSONObject) array.get(i)).getDouble("longitude");
                final double latitude = ((JSONObject) array.get(i)).getDouble("latitude");
                final int volume = ((JSONObject) array.get(i)).getInt("volume");
                final boolean isOn = ((JSONObject) array.get(i)).getBoolean("isOn");


                final JSONObject postpone = ((JSONObject) array.get(i)).getJSONObject("postpone");
                final boolean postponeIsOn = postpone.getBoolean("isOn");
                final int postponeMinutes = postpone.getInt("minutes");
                final int postponeTimes = postpone.getInt("timesOfRepeat");
                final Postpone postponeObj = new Postpone(postponeTimes, postponeMinutes,
                        postponeIsOn);
                final GPSAlarmSettingsImpl gpsSettingsReturned =
                        new GPSAlarmSettingsImpl(context, i, name, volume, isOn, type, songName,
                                postponeObj, radius, latitude, longitude);
                gpsSettings.add(gpsSettingsReturned);
            }
        }
        return gpsSettings;
    }

    public static void updateAndSaveSharedPreferancesWithGPSAlarmSettingsSpecific(final Context context,
                                                                                  final GPSAlarmSettingsImpl gpsSettings)
            throws JSONException {
        final List<GPSAlarmSettingsImpl> gpsAlarmSettings = loadSharedPreferancesWithGPSAlarmSettings(context);
        gpsAlarmSettings.get(gpsSettings.getId()).setAlarm(gpsSettings, gpsSettings.isOn());
        updateAndSaveSharedPreferancesWithGPSAlarmSettings(context, gpsAlarmSettings);
    }
}
