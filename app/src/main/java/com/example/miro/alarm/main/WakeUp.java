package com.example.miro.alarm.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.receiver.GPSAlarmReceiver;
import com.example.miro.alarm.receiver.TimeAlarmReceiver;
import com.example.miro.alarm.tabFragments.AlarmFragment;
import com.example.miro.alarm.tabFragments.GPSAlarmFragment;
import com.example.miro.alarm.tabFragments.PlaceholderFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.common.base.Preconditions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Created by Miro on 12/3/2016.
 */

public class WakeUp extends FragmentActivity {

    private static final int REQ_CODE_WAKE_UP = 70;

    private MediaPlayer mediaPlayer;
    private int volume;
    final private Timer scheduleTimer = new Timer();
    final private Timer scheduleTimerVibration = new Timer();
    Context context;
    Vibrator vib;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wakeup);
        context = getBaseContext();
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final Intent intent = getIntent();
        final String name = intent.getExtras().getString("name");
        final String isNormal = intent.getExtras().getString("isNormal");
        final Repeat repeatDays = (Repeat) intent.getSerializableExtra("RepeatDays");
        final int houre = intent.getIntExtra("houre", 0);
        final int minute = intent.getIntExtra("minute", 0);
        if ("normal".contentEquals(isNormal) || "gps".contentEquals(isNormal)) {
            volume = intent.getExtras().getInt("volume");
        } else {
            volume = 10;
        }
        final int id = intent.getExtras().getInt("id");
        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleTimer.cancel();
                scheduleTimerVibration.cancel();
                mediaPlayer.stop();
                vib.cancel();
                mediaPlayer.release();
                try {
                    if ("normal".contentEquals(isNormal)) { // clock alarm
                        final List<TimeAlarmSettingsImpl> timeAlarmSettings = new ArrayList<>();
                        timeAlarmSettings.addAll(Utils.loadSharedPreferancesWithAlarmSettingsClock(context));

                        final TimeAlarmSettingsImpl settings = timeAlarmSettings.get(id);
                        final Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, houre);
                        time.set(Calendar.MINUTE, minute);
                        final long finalTime = TimeAlarmSettingsImpl.getTimeInMilis(repeatDays, time);

                        if (settings != null) {
                            if (settings.getRepeat().isNoDay()) {
                                cancelAlarmOrRestart(false, finalTime, true, settings);
                                settings.setAlarm(settings, false);

                            } else {
                                cancelAlarmOrRestart(true, finalTime, true, settings);
                            }
                        }

                        Utils.updateAndSaveSharedPreferancesWithAlarmSettingsClockSpecific(context, settings);

                        AlarmFragment.cancel(settings, id);

                    } else if ("gps".contentEquals(isNormal)) { //gps alarm
                        final List<GPSAlarmSettingsImpl> gpsAlarmSettings = new ArrayList<>();

                        gpsAlarmSettings.addAll(Utils.loadSharedPreferancesWithGPSAlarmSettings(context));
                        final GPSAlarmSettingsImpl settings = gpsAlarmSettings.get(id);
                        settings.setAlarm(gpsAlarmSettings.get(id), false);
                        Utils.updateAndSaveSharedPreferancesWithGPSAlarmSettingsSpecific(context, settings);
                        cancelGPS();
                        GPSAlarmFragment.cancel(settings, id);
                    } else { //inteligent alarm
                        final List<TimeAlarmSettingsImpl> timeAlarmSettings = new ArrayList<>();
                        timeAlarmSettings.addAll(Utils.loadSharedPreferancesWithAlarmSettingsClock(context));
                        final TimeAlarmSettingsImpl settings = timeAlarmSettings.get(id);
                        final Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, houre);
                        time.set(Calendar.MINUTE, minute);
                        final long finalTime = TimeAlarmSettingsImpl.getTimeInMilis(settings.getRepeat(), time);
                        cancelAlarmOrRestart(false, finalTime, false, settings);
                        Utils.updateAndSaveSharedPreferancesWithAlarmSettingsClockSpecific(context, settings);

                        //AlarmFragment.cancel(id, repeatDays, houre, minute, false);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
                WakeUp.super.finish();
            }
        });

        final boolean repeatingIsOn = intent.getExtras().getBoolean("postponeonoff");

        int timesOfRepeat = 0;
        if (repeatingIsOn) {
            timesOfRepeat = intent.getExtras().getInt("repeat_times");
        }
        final Type type = Type.values()[intent.getExtras().getInt("type")];

        final String songName = intent.getExtras().getString("nameOfSong");

        final String[] song = songName.split(Pattern.quote("."));


        final TextView alarmTypeView = (TextView) findViewById(R.id.textViewAlarmType);
        final TextView alarmNameView = (TextView) findViewById(R.id.textViewAlarmName);
        alarmTypeView.setText("TIME ALARM");

        alarmNameView.setText(name);
        final int raw = this.getResources().getIdentifier(song[0], "raw", this.getPackageName());
        mediaPlayer = MediaPlayer.create(this, raw);
        mediaPlayer.setVolume(((float) volume / 100), ((float) volume / 100));
        mediaPlayer.setLooping(true);


        if (type == Type.BOTH) {
            startVibrating();
            mediaPlayer.start();
        } else if (type == Type.VIBRATION) {
            startVibrating();
        } else {
            mediaPlayer.start();
        }
        if ("inteligent".contentEquals(isNormal)) {
            scheduleTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    volume += 10;
                    mediaPlayer.setVolume(((float) volume / 100), ((float) volume / 100));
                }
            }, 5000, 5000);
        }

    }

    public void startVibrating() {
        scheduleTimerVibration.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                vib.vibrate(500);
            }
        }, 1000, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                vib.cancel();
                mediaPlayer.stop();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void cancelAlarmOrRestart(final boolean resetAlarm, final long finalTime,
                                     final boolean isNormal, final TimeAlarmSettingsImpl settings) {
        Intent intent;
        long timeBeforeRealAlarm = 0;
        if (isNormal) {
            intent = setUpIntent(settings);
        } else {
            timeBeforeRealAlarm = settings.getInteligentAlarm().getTimeBeforeRealAlaram() * Utils.ONE_MINUTE_MILISECONDS;
            intent = setUpInteligentIntent(settings);
        }
        final PendingIntent penInt = PendingIntent.getBroadcast(getBaseContext(), REQ_CODE_WAKE_UP, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            if (!resetAlarm) {
                am.cancel(penInt);
                penInt.cancel();
            } else {
                settings.getTime().setTimeInMillis(finalTime);
                am.setExact(AlarmManager.RTC_WAKEUP, finalTime - timeBeforeRealAlarm, penInt);
            }
        }
    }

    private void cancelGPS() throws JSONException {
        Map<String, Object> map = Utils.loadSharedPreferancesWithgeneralSettings(context);

        int isOn = (int) map.get("isOn");
        isOn--;
        if (isOn == 0) {
            final FusedLocationProviderClient f = new FusedLocationProviderClient(context);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    REQ_CODE_WAKE_UP, setUpIntentGPS(), PendingIntent.FLAG_UPDATE_CURRENT);
            f.removeLocationUpdates(pendingIntent);
            pendingIntent.cancel();
        }
        Utils.updateAndSaveSharedPreferancesWithGeneralSettings(context, isOn);
    }

    private Intent setUpIntentGPS() {
        final ComponentName receiver = new ComponentName(context, GPSAlarmReceiver.class);
        final PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return new Intent(context, GPSAlarmReceiver.class);
    }

    private Intent setUpIntent(final TimeAlarmSettingsImpl settings) {
        final Intent intent = new Intent(getBaseContext(), TimeAlarmReceiver.class);
        intent.putExtra("isNormal", "normal");
        intent.putExtra("name", settings.getName());
        intent.putExtra("volume", volume);
        intent.putExtra("postponeonoff", settings.getPostpone().isOn());
        if (settings.getPostpone().isOn()) {
            intent.putExtra("repeat_times", settings.getPostpone().getTimesOfRepeat());
        }
        intent.putExtra("type", settings.getType().getType().ordinal());
        intent.putExtra("nameOfSong", settings.getSong().getName());
        intent.putExtra("id", settings.getId());
        intent.putExtra("RepeatDays", settings.getRepeat());
        int houre = settings.getTime().get(Calendar.HOUR_OF_DAY);
        int minute = settings.getTime().get(Calendar.MINUTE);
        intent.putExtra("houre", houre);
        intent.putExtra("minute", minute);
        intent.setAction("intent" + settings.getId());
        return intent;
    }

    private Intent setUpInteligentIntent(final TimeAlarmSettingsImpl settings) {
        final Intent intentInteligent = new Intent(getBaseContext(), TimeAlarmReceiver.class);
        intentInteligent.putExtra("isNormal", "inteligent");
        intentInteligent.putExtra("name", settings.getName() + "intel");
        intentInteligent.putExtra("id", settings.getId());
        intentInteligent.putExtra("nameOfSong", settings.getInteligentAlarm().getSong().getName());
        intentInteligent.setAction("intent_intelligent" + settings.getId());
        return intentInteligent;
    }
}
