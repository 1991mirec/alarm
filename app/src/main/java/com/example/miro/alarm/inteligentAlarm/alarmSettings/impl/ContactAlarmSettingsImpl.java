package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.ContactAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.Contact;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.tabFragments.ContactAlarmFragment;

import org.json.JSONException;

import java.io.Serializable;
import java.util.Locale;

public class ContactAlarmSettingsImpl extends Settings implements ContactAlarmSettings, Serializable {

    private Contact contact;
    private int radius;
    private int color;
    private String distanceType;

    private transient Context context;
    private transient ImageButton imgAlarm;

    public ContactAlarmSettingsImpl(final Context context, final int id, final Contact contact) {
        super(contact.getName());
        this.contact = contact;
        this.context = context;
        this.radius = 1000;
        this.distanceType = context.getResources().getString(R.string.kilometers);
        setId(id);
    }

    public ContactAlarmSettingsImpl(final Context context, final int id, final String name,
                                    final int volume, final boolean isOn, final int type,
                                    final String songName, final Postpone postpone,
                                    final Contact contact, int radius, final String distanceType) {
        super(name, volume, type, isOn, songName, postpone);
        this.context = context;
        this.radius = radius;
        this.contact = contact;
        this.distanceType = distanceType;
        setId(id);
    }

    public boolean sendInvitation(Contact parameter) {
        // TODO implement me
        return false;
    }

    public Contact getContact() {
        return contact;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean gotPermission() {
        if(color == ContextCompat.getColor(context, R.color.greenBackground)){
            return true;
        } else {
            Toast.makeText(context, "User did not agree on sharing location yet",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void setVisuals(final View view) {
        final TextView distance = (TextView) view.findViewById(R.id.textViewDistanceFromContact);
        final TextView nameTxtView = (TextView) view.findViewById(R.id.textViewContactName);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonContact);
        color = ((ColorDrawable) view.getBackground()).getColor();

        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gotPermission()) {
                    isOn ^= true;
                    setVisuals(view);
                    if (isOn) {
                        //setAlarmManager();
                    } else {
                        imgAlarm.setImageResource(R.mipmap.alarm_black);
                        //cancelAlarmOrRestart(false, 0, true);
                        //cancelAlarmOrRestart(false, 0, false);
                    }
                    try {
                        ContactAlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (isOn) {
            imgAlarm.setImageResource(R.mipmap.alarm_green);
        } else {
            imgAlarm.setImageResource(R.mipmap.alarm_black);
        }

        double num = (double) radius;
        if (distanceType.equals(context.getResources().getString(R.string.meters))) {
            num = (double) radius / 1000;
        }
        final String radiusText = String.format(Locale.ENGLISH, "%.2f", num);
        final String text = radiusText + " Km";
        distance.setText(text);
        nameTxtView.setText(name);
    }

    public void setAlarm(ContactAlarmSettingsImpl alarm) {
        volume = alarm.getVolume();
        radius = alarm.getRadius();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        contact = alarm.getContact();
        radius = alarm.getRadius();
        isOn = true;
    }

    public void setDistanceType(final String distanceType) {
        this.distanceType = distanceType;
    }

    public String getDistanceType() {
        return distanceType;
    }
}

