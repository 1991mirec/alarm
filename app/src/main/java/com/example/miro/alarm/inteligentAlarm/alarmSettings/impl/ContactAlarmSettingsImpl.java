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

import java.io.Serializable;
import java.util.Locale;

public class ContactAlarmSettingsImpl extends Settings implements ContactAlarmSettings, Serializable {

    private Contact contact;
    private int radius;

    private transient Context context;
    private transient ImageButton imgAlarm;

    public ContactAlarmSettingsImpl(final Context context, final int id, final Contact contact) {
        super(contact.getName());
        this.contact = contact;
        this.context = context;
        this.radius = 1000;
        setId(id);
    }


    public boolean sendInvitation(Contact parameter) {
        // TODO implement me
        return false;
    }

    public Contact getContact() {
        // TODO implement me
        return contact;
    }

    public int getRadius() {
        // TODO implement me
        return radius;
    }

    public void setVisuals(final View view) {
        final TextView distance = (TextView) view.findViewById(R.id.textViewDistanceFromContact);
        final TextView nameTxtView = (TextView) view.findViewById(R.id.textViewContactName);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonContact);
        final int color = ((ColorDrawable) view.getBackground()).getColor();

        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (color == ContextCompat.getColor(context, R.color.greenBackground)) {
                    isOn ^= true;
                    setVisuals(view);
                    if (isOn) {
                        //setAlarmManager();
                    } else {
                        imgAlarm.setImageResource(R.mipmap.alarm_black);
                        //cancelAlarmOrRestart(false, 0, true);
                        //cancelAlarmOrRestart(false, 0, false);
                    }
                /*try {
                    AlarmFragment.updateAndSaveSharedPreferancesWithAlarmSettings(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                } else {
                    Toast.makeText(context, "User did not agree on sharing location yet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        if (isOn) {
            imgAlarm.setImageResource(R.mipmap.alarm_green);
        } else {
            imgAlarm.setImageResource(R.mipmap.alarm_black);
        }

        double num = (double) radius / 1000;
        final String radiusText = String.format(Locale.ENGLISH, "%.2f", num);
        final String text = radiusText + " Km";
        distance.setText(text);
        nameTxtView.setText(name);
    }

}

