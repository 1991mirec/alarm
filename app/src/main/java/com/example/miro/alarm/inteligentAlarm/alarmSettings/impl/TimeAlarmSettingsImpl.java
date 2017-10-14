package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.TimeAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.InteligentAlarm;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.receiver.TimeAlarmReceiver;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeAlarmSettingsImpl extends Settings implements TimeAlarmSettings, Serializable {

    private static final int REQ_CODE_WAKE_UP = 70;


    private InteligentAlarm inteligentAlarm;
    private Calendar time;
    private transient AlarmManager manager[] = new AlarmManager[8]; // days of week plus whole week
    private transient AlarmManager managerInteligent[] = new AlarmManager[8];
    private transient PendingIntent pendingIntent[] = new PendingIntent[8];
    private transient PendingIntent pendingIntentInteligent[] = new PendingIntent[8];
    private transient Context context;

    private transient ImageButton imgAlarm;

    public TimeAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        for (int i = 0; i < 8; i++) {
            this.manager[i] = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            this.managerInteligent[i] = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        final Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        final Date time = new Date();
        calendar.setTime(time);
        this.time = calendar;
        this.inteligentAlarm = new InteligentAlarm("loud_alarm_buzzer.mp3", 1, false);
        setId(id);
    }

    public void cancelAlarm(final int ids, final boolean visualOff) {System.out.println("cacnle" + ids);
        if (ids == 0) {
            isOn = false;
            imgAlarm.setImageResource(R.mipmap.alarm_black);
            if (pendingIntent[ids] != null && manager[ids] != null) {
                System.out.println("cacnle" + ids);
                manager[ids].cancel(pendingIntent[ids]);
            }
        } else {
            if (pendingIntent[ids] != null && manager[ids] != null) {
                if (visualOff) {
                    manager[ids].cancel(pendingIntent[ids]);
                } else {
                    manager[ids].setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis() - Utils.ONE_MINUTE_MILISECONDS + Utils.ONE_DAY_MILISECONDS * 7, pendingIntent[ids]);
                }
            }
        }
        System.out.println(isOn);
    }

    public void cancelInteligentAlarm(final int ids, final boolean visualOff) {
        if (pendingIntentInteligent[ids] != null && managerInteligent[ids] != null) {
            if (ids == 0) {
                managerInteligent[ids].cancel(pendingIntentInteligent[ids]);
            } else {

                if (visualOff) {
                    managerInteligent[ids].cancel(pendingIntentInteligent[ids]);
                } else {
                    managerInteligent[ids].setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis() - Utils.ONE_MINUTE_MILISECONDS + Utils.ONE_DAY_MILISECONDS * 7, pendingIntentInteligent[ids]);
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
                    for (int i = 0; i < 8; i++) {
                        cancelAlarm(i, true);
                        cancelInteligentAlarm(i, true);
                    }
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
        repeatTxtView.setText(repeat.toString());

        nameTxtView.setText(name);
    }

    public void setAlarm(TimeAlarmSettingsImpl alarm) {
        time = alarm.getTime();
        inteligentAlarm = alarm.getInteligentAlarm();
        volume = alarm.getVolume();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        repeat = alarm.repeat;
        isOn = true;
    }

    public void setAlarmManager() {
        if (repeat.isNoDay()) {
            if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS);
            }
        }
        final Intent intent = new Intent(context, TimeAlarmReceiver.class);
        final Intent intentInteligent = new Intent(context, TimeAlarmReceiver.class);
        intent.putExtra("isNormal", "normal");
        intentInteligent.putExtra("isNormal", "inteligent");
        intent.putExtra("name", name);
        intentInteligent.putExtra("name", name + "intel");
        intent.putExtra("volume", volume);
        final boolean repeatingIsOn = postpone.isOn();

        intent.putExtra("postponeonoff", repeatingIsOn);

        if (repeatingIsOn) {
            intent.putExtra("repeat_times", postpone.getTimesOfRepeat());
        }
        intent.putExtra("vol", 1);
        intent.putExtra("type", type.getType());
        intent.putExtra("nameOfSong", song.getName());

        intent.putExtra("id", getId());
        intentInteligent.putExtra("id", getId());

        final boolean intelligentIsOn = inteligentAlarm.isOn();
       /* if (repeat.isNoDay()) {
            if (intelligentIsOn) {
                intentInteligent.setAction("intent_intelligent" + id);
                intentInteligent.putExtra("nameOfSong", inteligentAlarm.getSong().getName());
                pendingIntentInteligent[0] = PendingIntent.getBroadcast(context, REQ_CODE_WAKE_UP, intentInteligent, PendingIntent.FLAG_UPDATE_CURRENT);
                final long timeBeforeRealAlarm = inteligentAlarm.getTimeBeforeRealAlaram() * Utils.ONE_MINUTE_MILISECONDS;
                managerInteligent[0].setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis() - Utils.ONE_MINUTE_MILISECONDS - timeBeforeRealAlarm, pendingIntentInteligent[0]);

            } else {
                cancelInteligentAlarm(0);
            }
        }*/
        intent.setAction("intent" + id);

        if (repeat.isNoDay()) {
            managerSet(repeatingIsOn, time.getTimeInMillis(), 0, intent, intelligentIsOn, intentInteligent);
        } else {
            final boolean[] days = repeat.getDays();
            switch (time.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY: System.out.println("is sunday");
                    if (days[0]) {System.out.println("is sunday set");
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {System.out.println("is higher number");
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 1, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 1, intent, intelligentIsOn, intentInteligent);
                            System.out.println("is lower number");
                        }
                    } else if (days[1]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 2, intent, intelligentIsOn, intentInteligent);
                    } else if (days[2]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 3, intent, intelligentIsOn, intentInteligent);
                    } else if (days[3]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 4, intent, intelligentIsOn, intentInteligent);
                    } else if (days[4]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 5, intent, intelligentIsOn, intentInteligent);
                    } else if (days[5]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 6, intent, intelligentIsOn, intentInteligent);
                    } else if (days[6]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 7, intent, intelligentIsOn, intentInteligent);
                    }
                    break;
                case Calendar.MONDAY:
                    if (days[0]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 1, intent, intelligentIsOn, intentInteligent);
                    } else if (days[1]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 2, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 2, intent, intelligentIsOn, intentInteligent);
                        }
                    } else if (days[2]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 3, intent, intelligentIsOn, intentInteligent);
                    } else if (days[3]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 4, intent, intelligentIsOn, intentInteligent);
                    } else if (days[4]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 5, intent, intelligentIsOn, intentInteligent);
                    } else if (days[5]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 6, intent, intelligentIsOn, intentInteligent);
                    } else if (days[6]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 7, intent, intelligentIsOn, intentInteligent);
                    }
                    break;
                case Calendar.TUESDAY:
                    if (days[0]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 1, intent, intelligentIsOn, intentInteligent);
                    } else if (days[1]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 2, intent, intelligentIsOn, intentInteligent);
                    } else if (days[2]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 3, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 3, intent, intelligentIsOn, intentInteligent);
                        }
                    } else if (days[3]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 4, intent, intelligentIsOn, intentInteligent);
                    } else if (days[4]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 5, intent, intelligentIsOn, intentInteligent);
                    } else if (days[5]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 6, intent, intelligentIsOn, intentInteligent);
                    } else if (days[6]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 7, intent, intelligentIsOn, intentInteligent);
                    }
                    break;
                case Calendar.WEDNESDAY:
                    if (days[0]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 1, intent, intelligentIsOn, intentInteligent);
                    } else if (days[1]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 2, intent, intelligentIsOn, intentInteligent);
                    } else if (days[2]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 3, intent, intelligentIsOn, intentInteligent);
                    } else if (days[3]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 4, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 4, intent, intelligentIsOn, intentInteligent);
                        }
                    } else if (days[4]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 5, intent, intelligentIsOn, intentInteligent);
                    } else if (days[5]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 6, intent, intelligentIsOn, intentInteligent);
                    } else if (days[6]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 7, intent, intelligentIsOn, intentInteligent);
                    }
                    break;
                case Calendar.THURSDAY:
                    if (days[0]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 1, intent, intelligentIsOn, intentInteligent);
                    } else if (days[1]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 2, intent, intelligentIsOn, intentInteligent);
                    } else if (days[2]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 3, intent, intelligentIsOn, intentInteligent);
                    } else if (days[3]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 4, intent, intelligentIsOn, intentInteligent);
                    } else if (days[4]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 5, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 5, intent, intelligentIsOn, intentInteligent);
                        }
                    } else if (days[5]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 6, intent, intelligentIsOn, intentInteligent);
                    } else if (days[6]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 7, intent, intelligentIsOn, intentInteligent);
                    }
                    break;
                case Calendar.FRIDAY:
                    if (days[0]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 1, intent, intelligentIsOn, intentInteligent);
                    } else if (days[1]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 2, intent, intelligentIsOn, intentInteligent);
                    } else if (days[2]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 3, intent, intelligentIsOn, intentInteligent);
                    } else if (days[3]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 4, intent, intelligentIsOn, intentInteligent);
                    } else if (days[4]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 5, intent, intelligentIsOn, intentInteligent);
                    } else if (days[5]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 6, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 6, intent, intelligentIsOn, intentInteligent);
                        }
                    } else if (days[6]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 7, intent, intelligentIsOn, intentInteligent);
                    }
                    break;
                case Calendar.SATURDAY:
                    if (days[0]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS;
                        managerSet(repeatingIsOn, finalTime, 1, intent, intelligentIsOn, intentInteligent);
                    } else if (days[1]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 2;
                        managerSet(repeatingIsOn, finalTime, 2, intent, intelligentIsOn, intentInteligent);
                    } else if (days[2]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 3;
                        managerSet(repeatingIsOn, finalTime, 3, intent, intelligentIsOn, intentInteligent);
                    } else if (days[3]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 4;
                        managerSet(repeatingIsOn, finalTime, 4, intent, intelligentIsOn, intentInteligent);
                    } else if (days[4]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 5;
                        managerSet(repeatingIsOn, finalTime, 5, intent, intelligentIsOn, intentInteligent);
                    } else if (days[5]) {
                        long finalTime = time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 6;
                        managerSet(repeatingIsOn, finalTime, 6, intent, intelligentIsOn, intentInteligent);
                    } else if (days[6]) {
                        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            time.setTimeInMillis(time.getTimeInMillis() + Utils.ONE_DAY_MILISECONDS * 7);
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 7, intent, intelligentIsOn, intentInteligent);
                        } else {
                            managerSet(repeatingIsOn, time.getTimeInMillis(), 7, intent, intelligentIsOn, intentInteligent);
                        }
                    }
                    break;
            }
        }
    }

    private void managerSet(final boolean repeatingIsOn, final long timeInMillis, final int ids, final Intent intent, final boolean intelligentIsOn, final Intent intentInteligent) {

        System.out.println( ids);
        pendingIntent[ids] = PendingIntent.getBroadcast(context, REQ_CODE_WAKE_UP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (repeatingIsOn) {
            manager[ids].setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis - Utils.ONE_MINUTE_MILISECONDS,
                    postpone.getMinutes() * Utils.ONE_MINUTE_MILISECONDS, pendingIntent[ids]);
        } else {
            manager[ids].setExact(AlarmManager.RTC_WAKEUP, timeInMillis - Utils.ONE_MINUTE_MILISECONDS, pendingIntent[ids]);
        }

        if (intelligentIsOn) {
            intentInteligent.setAction("intent_intelligent" + id);
            intentInteligent.putExtra("nameOfSong", inteligentAlarm.getSong().getName());
            pendingIntentInteligent[ids] = PendingIntent.getBroadcast(context, REQ_CODE_WAKE_UP, intentInteligent, PendingIntent.FLAG_UPDATE_CURRENT);
            final long timeBeforeRealAlarm = inteligentAlarm.getTimeBeforeRealAlaram() * Utils.ONE_MINUTE_MILISECONDS;
            managerInteligent[ids].setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis() - Utils.ONE_MINUTE_MILISECONDS - timeBeforeRealAlarm, pendingIntentInteligent[ids]);

        } else {
            cancelInteligentAlarm(ids, true);
        }
    }

}

