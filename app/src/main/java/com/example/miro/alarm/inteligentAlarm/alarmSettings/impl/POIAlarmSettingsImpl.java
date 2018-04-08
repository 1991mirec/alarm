package com.example.miro.alarm.inteligentAlarm.alarmSettings.impl;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.api.POIAlarmSettings;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class POIAlarmSettingsImpl extends AbstractGPSNeededSettings implements POIAlarmSettings, Serializable {

    private String poiType;
    private String distanceType;


    public POIAlarmSettingsImpl(final Context context, final int id) {
        super(context.getString(R.string.default_alarm));
        this.context = context;
        setId(id);
        poiType = "ATM";
        this.radius = 1000;
        this.distanceType = context.getResources().getString(R.string.kilometers);
    }

    public POIAlarmSettingsImpl(final Context context, final int id, final String name,
                                final int volume, final boolean isOn, final int type,
                                final String songName, final Postpone postpone,
                                final String poiType, final int radius, final String distanceType) {
        super(name, volume, type, isOn, songName, postpone);
        this.context = context;
        this.poiType = poiType;
        this.radius = radius;
        this.distanceType = distanceType;
        setId(id);
/*        if (isOn && pendingIntent == null) {
            startPositionCheck(false);
        }*/
    }

    public void setVisuals(final View view) {
        final TextView name = (TextView) view.findViewById(R.id.textViewNamePOI);
        final TextView radiusTextBox = (TextView) view.findViewById(R.id.poiRadius);
        imgAlarm = (ImageButton) view.findViewById(R.id.imageButtonPOI);
        final ImageView imagePoi = (ImageView) view.findViewById(R.id.imagePOIType);
        imagePoi.setImageResource(view.getResources().getIdentifier(poiType.toLowerCase(),
                "drawable", context.getPackageName()));
        final POIAlarmSettingsImpl poiAlarmSettings = this;
        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn ^= true;
                setVisuals(view);
                if (isOn) {
                    startPositionCheck();
                } else {
                    //isOnCount--;
                    cancel();
                }
                try {
                    Utils.updateAndSaveSharedPreferancesWithPOIAlarmSettingsSpecific(context, poiAlarmSettings);
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

        name.setText(poiType);
        double num = (double) radius;
        if (distanceType.equals(context.getResources().getString(R.string.meters))) {
            num = (double) radius / 1000;
        }

        final String radiusText = String.format(Locale.ENGLISH, "%.2f", num);
        final String text = radiusText + " Km";
        radiusTextBox.setText(text);
    }

    @Override
    public String getPoiType() {
        return poiType;
    }

    @Override
    public void setPoiType(final String poiType) {
        this.poiType = poiType;
    }

    public ArrayList<String> getListOfPoiItems() {
        //TODO request to get latest list of items for POI
        final ArrayList<String> poiTypes = new ArrayList<>();
        poiTypes.add("Bank");
        poiTypes.add("ATM");
        return poiTypes;
    }

    /*public void setAlarm(final POIAlarmSettingsImpl alarm, final boolean isOn) {
        volume = alarm.getVolume();
        radius = alarm.getRadius();
        song = alarm.getSong();
        name = alarm.getName();
        type = alarm.getType();
        postpone = alarm.getPostpone();

        this.isOn = isOn;
    }*/

    @Override
    void setAlarmSpecific(Settings alarm) {
        radius = ((POIAlarmSettingsImpl) alarm).getRadius();
        distanceType = ((POIAlarmSettingsImpl) alarm).getDistanceType();
        poiType = ((POIAlarmSettingsImpl) alarm).getPoiType();
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(final String distanceType) {
        this.distanceType = distanceType;
    }

    @Override
    public List<LatLng> getCoordinates() {
        //TODO never called - does not work properly. it gets stuck used in GPSalarmREceiver
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = String.format("%s/getPoiTypeLocation/%s", Utils.API_PREFIX, poiType);
        final ArrayList<LatLng> finalList = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONArray positions;
                        try {
                            positions = response.getJSONArray("positions");
                            for (int i = 0; i < positions.length(); i++) {
                                final JSONObject jsonObject = positions.getJSONObject(i);
                                final String latitude = jsonObject.getString("latitude");
                                final String longitude = jsonObject.getString("longitude");
                                final LatLng latLng = new LatLng(Long.parseLong(latitude), Long.parseLong(longitude));
                                finalList.add(latLng);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(jsonObjectRequest);

        return finalList;
    }

    @Override
    void saveSpecific() {
        try {
            Utils.updateAndSaveSharedPreferancesWithPOIAlarmSettingsSpecific(context, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

