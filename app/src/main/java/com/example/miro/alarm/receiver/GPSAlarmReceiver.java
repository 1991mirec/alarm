package com.example.miro.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.Settings;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.main.WakeUp;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miro on 11/17/2017.
 */

public class GPSAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //TODO make sure that intent action is not third party. see on receive warning
        /*try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        final List<GPSAlarmSettingsImpl> gpsAlarmSettings = new ArrayList<>();
        final List<ContactAlarmSettingsImpl> contactAlarmSettings = new ArrayList<>();
        final List<POIAlarmSettingsImpl> poiAlarmSettings = new ArrayList<>();
        try {
            gpsAlarmSettings.addAll(Utils.loadSharedPreferancesWithGPSAlarmSettings(context));
            contactAlarmSettings.addAll(Utils.loadSharedPreferancesWithContactAlarmSettings(context));
            poiAlarmSettings.addAll(Utils.loadSharedPreferancesWithPOIAlarmSettings(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final LocationResult lr = LocationResult.extractResult(intent);
        if (lr != null) {
        final JSONObject jsonObject1 = new JSONObject();
        final JSONObject item = new JSONObject();
        final String location = lr.getLastLocation().getLatitude() + "," + lr.getLastLocation().getLongitude();
        try {
            //Toast.makeText(context, location, Toast.LENGTH_LONG).show();
            item.put("location", location);
            item.put("number", Utils.MY_PHONE_NUMBER);
            jsonObject1.put("input", item);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue1 = Volley.newRequestQueue(context);
        String url1 = String.format("%s/setNumberLocation", Utils.API_PREFIX);
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest
                (Request.Method.PUT, url1, jsonObject1, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue1.add(jsonObjectRequest1);

            for (final GPSAlarmSettingsImpl gpsSettings : gpsAlarmSettings) {
                if (gpsSettings.isOn()) {
                    final double centerLa = gpsSettings.getCoordinates().get(0).latitude;
                    final double centerLo = gpsSettings.getCoordinates().get(0).longitude;
                    double radius = gpsSettings.getRadius();
                    float[] distance = new float[2];
                    Location.distanceBetween(lr.getLastLocation().getLatitude(),
                            lr.getLastLocation().getLongitude(), centerLa, centerLo, distance);
                    if (distance[0] < radius) {

                        context.startActivity(setUpIntent(context, gpsSettings, "gps"));
                    }
                }
            }
            for (final ContactAlarmSettingsImpl contactSettings : contactAlarmSettings) {
                if (contactSettings.isOn()) {
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = String.format("%s/getNumberLocation/%s", Utils.API_PREFIX, contactSettings.getId());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    final ArrayList<LatLng> finalList = new ArrayList<>();
                                    try {
                                        final String position = response.getString("position");
                                        final String[] split = position.split(",");
                                        final LatLng latLng = new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                                        finalList.add(latLng);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    final double centerLa = finalList.get(0).latitude;
                                    final double centerLo = finalList.get(0).longitude;
                                    double radius = contactSettings.getRadius();
                                    float[] distance = new float[2];
                                    Location.distanceBetween(lr.getLastLocation().getLatitude(),
                                            lr.getLastLocation().getLongitude(), centerLa, centerLo, distance);
                                    if (contactSettings.getDistanceType().equals(context.getResources().getString(R.string.kilometers))) {
                                        radius = radius * 1000;
                                    }
                                    if (distance[0] < radius) {

                                        context.startActivity(setUpIntent(context, contactSettings, "contact"));
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    queue.add(jsonObjectRequest);
                }
            }
            for (final POIAlarmSettingsImpl poiSettings : poiAlarmSettings) {
                if (poiSettings.isOn()) {
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = String.format("%s/getPoiTypeLocation/%s", Utils.API_PREFIX, poiSettings.getPoiType());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    final ArrayList<LatLng> finalList = new ArrayList<>();
                                    final JSONArray positions;
                                    try {
                                        positions = response.getJSONArray("positions");
                                        for (int i = 0; i < positions.length(); i++) {
                                            final JSONObject jsonObject = positions.getJSONObject(i);
                                            final String latitude = jsonObject.getString("latitude");
                                            final String longitude = jsonObject.getString("longitude");
                                            final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                            finalList.add(latLng);
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    double radius = poiSettings.getRadius();
                                    for (LatLng latLng : finalList) {
                                        final double centerLa = latLng.latitude;
                                        final double centerLo = latLng.longitude;
                                        float[] distance = new float[2];
                                        Location.distanceBetween(lr.getLastLocation().getLatitude(),
                                                lr.getLastLocation().getLongitude(), centerLa, centerLo, distance);
                                        if (poiSettings.getDistanceType().equals(context.getResources().getString(R.string.kilometers))) {
                                            radius = radius * 1000;
                                        }
                                        if (distance[0] < radius) {
                                            context.startActivity(setUpIntent(context, poiSettings, "poi"));
                                            break;
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    queue.add(jsonObjectRequest);
                }
            }
        }
    }

    private Intent setUpIntent(final Context context, final Settings settings, final String isNromal) {
        final Intent intent = new Intent(context, WakeUp.class);
        intent.putExtra("isNormal", isNromal);
        intent.putExtra("name", settings.getName());
        intent.putExtra("volume", settings.getVolume());
        intent.putExtra("postponeonoff", settings.getPostpone().isOn());
        if (settings.getPostpone().isOn()) {
            intent.putExtra("repeat_times", settings.getPostpone().getTimesOfRepeat());
        }
        intent.putExtra("type", settings.getType().getType().ordinal());
        intent.putExtra("nameOfSong", settings.getSong().getName());
        intent.putExtra("id", settings.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
