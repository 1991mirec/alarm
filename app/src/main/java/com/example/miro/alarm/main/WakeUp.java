package com.example.miro.alarm.main;

import android.content.Context;
import android.content.Intent;
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
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.tabFragments.AlarmFragment;
import com.example.miro.alarm.tabFragments.GPSAlarmFragment;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Created by Miro on 12/3/2016.
 */

public class WakeUp extends FragmentActivity {

    private MediaPlayer mediaPlayer;
    private int volume;
    final private Timer scheduleTimer = new Timer();
    final private Timer scheduleTimerVibration = new Timer();
    Vibrator vib;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wakeup);
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
                if ("normal".contentEquals(isNormal)) {
                    AlarmFragment.cancel(id, repeatDays, houre, minute, true);
                } else if ("gps".contentEquals(isNormal)) {
                    GPSAlarmFragment.cancel(id);
                } else {
                    AlarmFragment.cancel(id, repeatDays, houre, minute, false);
                }
                scheduleTimer.cancel();
                scheduleTimerVibration.cancel();
                mediaPlayer.stop();
                vib.cancel();
                mediaPlayer.release();
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

    public void startVibrating(){
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
}
