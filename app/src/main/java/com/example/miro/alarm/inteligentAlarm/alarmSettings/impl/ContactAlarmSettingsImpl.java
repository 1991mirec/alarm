package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.ContactAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.enums.Permission;
import com.example.miro.alarm.inteligentAlarm.helper.Contact;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ContactAlarmSettingsImpl extends AbstractGPSNeededSettings implements ContactAlarmSettings, Serializable {

    private Contact contact;
    private String distanceType;

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
    /*    if (isOn && pendingIntent == null) {
            startPositionCheck(false);
        }*/
    }

    public void sendInvitation() {
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context, context.getClass()), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("+421903480645", null, "Hello world", pi, null);
    }

    public Contact getContact() {
        return contact;
    }

    public boolean gotPermission(final View view) {
        if (contact.getPermission() == Permission.RECEIVED_PERMISSION) {
            return true;
        } else if (contact.getPermission() == Permission.I_AM_ASKED_FOR_PERMIISION) {
            try {
                JSONObject input = new JSONObject();
                JSONObject item = new JSONObject();
                item.put("my-number", Utils.MY_PHONE_NUMBER);
                item.put("number", contact.getId());
                input.put("input", item);
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = String.format("%s/grantPermission", Utils.API_PREFIX);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, input, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(context,
                                        String.format("Permission to user %s granted", contact.getName()),
                                        Toast.LENGTH_SHORT).show();
                                contact.setPermission(Permission.RECEIVED_PERMISSION);
                                setVisuals(view);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Failed to connect with server",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                queue.add(jsonObjectRequest);

            } catch (JSONException e) {
                System.out.print("problem");
            }
            return false;
        } else {
            Toast.makeText(context, "User did not agree on sharing location yet",
                    Toast.LENGTH_LONG).show();
            return true;
        }
    }

    public void setVisuals(final View view) {
        final TextView distance = (TextView) view.findViewById(R.id.textViewDistanceFromContact);
        final TextView nameTxtView = (TextView) view.findViewById(R.id.textViewContactName);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonContact);

        if (contact.getPermission() == Permission.RECEIVED_PERMISSION) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.greenBackground));
        } else if (contact.getPermission() == Permission.I_AM_ASKED_FOR_PERMIISION) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.orangeBackground));
        } else if (contact.getPermission() == Permission.NO_PERMISSION) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.redBackground));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.yellowBackground));
        }
        final ContactAlarmSettingsImpl alarmSettings = this;
        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gotPermission(view)) {
                    isOn ^= true;
                    setVisuals(view);

                    if (isOn) {
                        startPositionCheck();
                    } else {
                        //isOnCount--;
                        cancel();
                    }
                    try {
                        Utils.updateAndSaveSharedPreferancesWithContactAlarmSettingsSpecific(context,
                                alarmSettings);
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
        nameTxtView.setText(name + " " + contact.getId());
    }

    /*public void setAlarm(final ContactAlarmSettingsImpl alarm, final boolean isOn) {
        volume = alarm.getVolume();
        radius = alarm.getRadius();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();
        contact = alarm.getContact();
        radius = alarm.getRadius();
        this.isOn = isOn;
    }*/

    public void setDistanceType(final String distanceType) {
        this.distanceType = distanceType;
    }

    public String getDistanceType() {
        return distanceType;
    }

    @Override
    public List<LatLng> getCoordinates() {
        //TODO never called - does not work properly. it gets stuck used in GPSalarmREceiver
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("%s/getNumberLocation/%s", Utils.API_PREFIX, contact.getId());
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        queue.add(request);
        final ArrayList<LatLng> finalList = new ArrayList<>();
        try {
            JSONObject response = future.get(); // this will block (forever)
            final String position = response.getString("position");
            final String[] split = position.split(",");
            final LatLng latLng = new LatLng(Long.parseLong(split[0]), Long.parseLong(split[1]));
            finalList.add(latLng);
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return finalList;
    }

    @Override
    void saveSpecific() {
        try {
            Utils.updateAndSaveSharedPreferancesWithContactAlarmSettingsSpecific(context, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    void setAlarmSpecific(Settings alarm) {
        radius = ((ContactAlarmSettingsImpl)alarm).getRadius();
        contact = ((ContactAlarmSettingsImpl)alarm).getContact();
        radius = ((ContactAlarmSettingsImpl)alarm).getRadius();
    }
}

