package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.TimeAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.InteligentAlarm;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.receiver.TimeAlarmReceiver;
import com.example.miro.alarm.tabFragments.AlarmFragment;

import org.json.JSONException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeAlarmSettingsImpl extends Settings implements TimeAlarmSettings, Serializable {

    private static final int REQ_CODE_WAKE_UP = 70;
    private Repeat repeat;

    private InteligentAlarm inteligentAlarm;
    private Calendar time;
    private transient Context context;
    private transient ImageButton imgAlarm;


    public TimeAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        final Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        final Date time = new Date();
        calendar.setTime(time);
        this.time = calendar;
        this.repeat = new Repeat();
        this.inteligentAlarm = new InteligentAlarm("loud_alarm_buzzer.mp3", 1, false);
        setId(id);
    }

    public TimeAlarmSettingsImpl(final Context context, final int id, final Calendar time,
                                 final InteligentAlarm inteligentAlarm, final String name,
                                 final int volume, final boolean isOn, final int type,
                                 final String songName, final Repeat repeat,
                                 final Postpone postpone) {
        super(name, volume, type, isOn, songName, postpone);
        this.context = context;
        setId(id);
        this.time = time;
        this.repeat = repeat;
        this.inteligentAlarm = inteligentAlarm;
    }

    public void setRepeat(final Repeat repeat) {
        this.repeat = repeat;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void cancelAlarmOrRestart(final boolean resetAlarm, final long finalTime,
                                     final boolean isNormal) {
        Intent intent;
        long timeBeforeRealAlarm = 0;
        if (isNormal) {
            intent = setUpIntent();
        } else {
            timeBeforeRealAlarm = inteligentAlarm.getTimeBeforeRealAlaram() * Utils.ONE_MINUTE_MILISECONDS;
            intent = setUpInteligentIntent();
        }
        final PendingIntent penInt = PendingIntent.getBroadcast(context, REQ_CODE_WAKE_UP, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            if (!resetAlarm) {
                am.cancel(penInt);
                imgAlarm.setImageResource(R.mipmap.alarm_black);
            } else {
                this.time.setTimeInMillis(finalTime);
                am.setExact(AlarmManager.RTC_WAKEUP, finalTime - timeBeforeRealAlarm, penInt);
                try {
                    AlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public InteligentAlarm getInteligentAlarm() {
        return inteligentAlarm;
    }

    @Override
    public Calendar getTime() {
        return time;
    }

    public void setVisuals(final View view) {
        final TextView timeTxtView = (TextView) view.findViewById(R.id.textViewTime);
        final TextView repeatTxtView = (TextView) view.findViewById(R.id.textViewRepeat);
        final TextView nameTxtView = (TextView) view.findViewById(R.id.textViewName);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButton);

        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn ^= true;
                setVisuals(view);
                if (isOn) {
                    setAlarmManager();
                } else {
                    cancelAlarmOrRestart(false, 0, true);
                    cancelAlarmOrRestart(false, 0, false);
                }
                try {
                    AlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
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
        final int hour = time.get(java.util.Calendar.HOUR_OF_DAY);
        final int minute = time.get(java.util.Calendar.MINUTE);
        String hourString = String.valueOf(hour);
        String minuteString = String.valueOf(minute);
        if (hour < 10) {
            hourString = "0" + String.valueOf(hour);
        }
        if (minute < 10) {
            minuteString = "0" + String.valueOf(minute);
        }
        timeTxtView.setText(hourString + ":" + minuteString);
        repeat.setContext(context);
        String today_tomorow = null;
        if (repeat.toString().equals("Tomorrow")) {
            today_tomorow = "Today";
            if (time.get(Calendar.DAY_OF_MONTH) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                today_tomorow = "Tomorrow";
            }
        }

        if (today_tomorow != null) {
            repeatTxtView.setText(today_tomorow);
        } else {
            repeatTxtView.setText(repeat.toString());
        }
        nameTxtView.setText(name);
    }

    public void setAlarm(final TimeAlarmSettingsImpl alarm, final boolean isOn) {
        time = alarm.getTime();
        inteligentAlarm = alarm.getInteligentAlarm();
        volume = alarm.getVolume();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        repeat = alarm.getRepeat();
        this.isOn = isOn;
    }

    private Intent setUpIntent() {
        final Intent intent = new Intent(context, TimeAlarmReceiver.class);
        intent.putExtra("isNormal", "normal");
        intent.putExtra("name", name);
        intent.putExtra("volume", volume);
        intent.putExtra("postponeonoff", postpone.isOn());
        if (postpone.isOn()) {
            intent.putExtra("repeat_times", postpone.getTimesOfRepeat());
        }
        intent.putExtra("type", type.getType().ordinal());
        intent.putExtra("nameOfSong", song.getName());
        intent.putExtra("id", getId());
        intent.putExtra("RepeatDays", repeat);
        int houre = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        intent.putExtra("houre", houre);
        intent.putExtra("minute", minute);
        intent.setAction("intent" + id);
        return intent;
    }

    private Intent setUpInteligentIntent() {
        final Intent intentInteligent = new Intent(context, TimeAlarmReceiver.class);
        intentInteligent.putExtra("isNormal", "inteligent");
        intentInteligent.putExtra("name", name + "intel");
        intentInteligent.putExtra("id", getId());
        intentInteligent.putExtra("nameOfSong", inteligentAlarm.getSong().getName());
        intentInteligent.setAction("intent_intelligent" + id);
        return intentInteligent;
    }

    public void setAlarmManager() {
        final Intent intent = setUpIntent();
        final Intent intentInteligent = setUpInteligentIntent();
        managerSet(getTimeInMilis(repeat, time), intent, intentInteligent);
    }

    public static long getTimeInMilis(final Repeat repeat, final Calendar time) {
        long finalTime = 0;
        if (repeat.isNoDay()) {
            final long currentTime = Calendar.getInstance().getTimeInMillis();
            if (time.getTimeInMillis() <= currentTime) {
                time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS);
            }
            return time.getTimeInMillis();
        } else {
            final boolean[] days = repeat.getDays();
            long plusOneDay = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
            switch (time.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    if (days[0]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    } else if (days[1]) {
                        finalTime = plusOneDay;
                    } else if (days[2]) {
                        finalTime = plusOneDay * 2;
                    } else if (days[3]) {
                        finalTime = plusOneDay * 3;
                    } else if (days[4]) {
                        finalTime = plusOneDay * 4;
                    } else if (days[5]) {
                        finalTime = plusOneDay * 5;
                    } else if (days[6]) {
                        finalTime = plusOneDay * 6;
                    }
                    break;
                case Calendar.MONDAY:
                    if (days[0]) {
                        finalTime = plusOneDay * 6;
                    } else if (days[1]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    } else if (days[2]) {
                        finalTime = plusOneDay;
                    } else if (days[3]) {
                        finalTime = plusOneDay * 2;
                    } else if (days[4]) {
                        finalTime = plusOneDay * 3;
                    } else if (days[5]) {
                        finalTime = plusOneDay * 4;
                    } else if (days[6]) {
                        finalTime = plusOneDay * 5;
                    }
                    break;
                case Calendar.TUESDAY:
                    if (days[0]) {
                        finalTime = plusOneDay * 5;
                    } else if (days[1]) {
                        finalTime = plusOneDay * 6;
                    } else if (days[2]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    } else if (days[3]) {
                        finalTime = plusOneDay;
                    } else if (days[4]) {
                        finalTime = plusOneDay * 2;
                    } else if (days[5]) {
                        finalTime = plusOneDay * 3;
                    } else if (days[6]) {
                        finalTime = plusOneDay * 4;
                    }
                    break;
                case Calendar.WEDNESDAY:
                    if (days[0]) {
                        finalTime = plusOneDay * 4;
                    } else if (days[1]) {
                        finalTime = plusOneDay * 5;
                    } else if (days[2]) {
                        finalTime = plusOneDay * 6;
                    } else if (days[3]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    } else if (days[4]) {
                        finalTime = plusOneDay;
                    } else if (days[5]) {
                        finalTime = plusOneDay * 2;
                    } else if (days[6]) {
                        finalTime = plusOneDay * 3;
                    }
                    break;
                case Calendar.THURSDAY:
                    if (days[0]) {
                        finalTime = plusOneDay * 3;
                    } else if (days[1]) {
                        finalTime = plusOneDay * 4;
                    } else if (days[2]) {
                        finalTime = plusOneDay * 5;
                    } else if (days[3]) {
                        finalTime = plusOneDay * 6;
                    } else if (days[4]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    } else if (days[5]) {
                        finalTime = plusOneDay;
                    } else if (days[6]) {
                        finalTime = plusOneDay * 2;
                    }
                    break;
                case Calendar.FRIDAY:
                    if (days[0]) {
                        finalTime = plusOneDay * 2;
                    } else if (days[1]) {
                        finalTime = plusOneDay * 3;
                    } else if (days[2]) {
                        finalTime = plusOneDay * 4;
                    } else if (days[3]) {
                        finalTime = plusOneDay * 5;
                    } else if (days[4]) {
                        finalTime = plusOneDay * 6;
                    } else if (days[5]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    } else if (days[6]) {
                        finalTime = plusOneDay;
                    }
                    break;
                case Calendar.SATURDAY:
                    if (days[0]) {
                        finalTime = plusOneDay;
                    } else if (days[1]) {
                        finalTime = plusOneDay * 2;
                    } else if (days[2]) {
                        finalTime = plusOneDay * 3;
                    } else if (days[3]) {
                        finalTime = plusOneDay * 4;
                    } else if (days[4]) {
                        finalTime = plusOneDay * 5;
                    } else if (days[5]) {
                        finalTime = plusOneDay * 6;
                    } else if (days[6]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(plusOneDay * 7);
                        }
                        finalTime = time.getTimeInMillis();
                    }
                    break;
            }
        }
        return finalTime;
    }

    private void managerSet(final long timeInMillis, final Intent intent,
                            final Intent intentInteligent) {
        cancelAlarmOrRestart(false, 0, true);
        cancelAlarmOrRestart(false, 0, false);
        final ComponentName receiver = new ComponentName(context, TimeAlarmReceiver.class);
        final PackageManager pm = context.getPackageManager();
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(timeInMillis);

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQ_CODE_WAKE_UP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            if (postpone.isOn()) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,
                        postpone.getMinutes() * Utils.ONE_MINUTE_MILISECONDS, pendingIntent);
            } else {
                manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }

        if (inteligentAlarm.isOn()) {
            final PendingIntent pendingIntentInteligent = PendingIntent.getBroadcast(context, REQ_CODE_WAKE_UP, intentInteligent, PendingIntent.FLAG_UPDATE_CURRENT);
            final long timeBeforeRealAlarm = inteligentAlarm.getTimeBeforeRealAlaram() * Utils.ONE_MINUTE_MILISECONDS;
            final AlarmManager managerInteligent = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (managerInteligent != null) {
                managerInteligent.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis() - timeBeforeRealAlarm, pendingIntentInteligent);
            }
        }
    }

}

