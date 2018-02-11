package com.example.miro.alarm.tabFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.GpsAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.main.GpsAlarmSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Miro on 11/22/2016.
 */

public class GPSAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    private Context context;
    private View rootView;
    private static final int REQ_CODE = 79;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if (gpsSettings.size() == 0) {
            try {
                loadSharedPreferancesWithAlarmSettings();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initiateButtons();

        return rootView;
    }


    public void addButton() {
        final GPSAlarmSettingsImpl gpsAlarmSettings = new GPSAlarmSettingsImpl(context,
                gpsSettings.size());
        gpsSettings.add(gpsAlarmSettings);

        refresh();
    }

    @Override
    public LinearLayout initiateButtons() {
        final ListView listView = (ListView) rootView.findViewById(R.id.listViewAlarm);

        if (gpsSettings.isEmpty()) {
            addButton();
        }

        final GpsAlarmAdapter adapter = new GpsAlarmAdapter(context, R.layout.gps_buttons,
                gpsSettings.toArray(new GPSAlarmSettingsImpl[gpsSettings.size()]));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GpsAlarmSettingActivity.class);
                intent.putExtra("gpsSettings", gpsSettings.get(position));
                startActivityForResult(intent, REQ_CODE);
            }
        });
        listView.setAdapter(adapter);
        return null;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            final GPSAlarmSettingsImpl gpsSettingsReturned = (GPSAlarmSettingsImpl) data.getExtras()
                    .getSerializable("gpsSettings");

            final int id = gpsSettingsReturned.getId();
            gpsSettings.get(id).setAlarm(gpsSettingsReturned);
            gpsSettings.get(id).startPositionCheck();
            refresh();
        }
    }

    /**
     * This should cancel the alarm because we either got to the place or
     * we manualy changed it to turn of this alarm
     * @param id id of a static list in {@link PlaceholderFragment} with gpsSettings
     */
    public static void cancel(final int id){
        gpsSettings.get(id).cancel();
    }



    @Override
    public void removeButton(int id) {
        gpsSettings.remove(id);
        try {
            updateAndSaveSharedPreferancesWithAlarmSettings(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        addButton();
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        try {
            updateAndSaveSharedPreferancesWithAlarmSettings(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public static void updateAndSaveSharedPreferancesWithAlarmSettings(final Context context)
            throws JSONException {
        final JSONArray listOfSettings = new JSONArray();
        for(final GPSAlarmSettingsImpl settings: gpsSettings){
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

    private void loadSharedPreferancesWithAlarmSettings() throws JSONException {
        final SharedPreferences sharedPreferences = getContext().getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String main = sharedPreferences.getString("gpsSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONArray array = mainObject.getJSONArray("gpsAlarmSettings");
            for (int i = 0, size = array.length(); i < size; i++)
            {
                final String name = ((JSONObject) array.get(i)).getString("name");
                final String songName = ((JSONObject) array.get(i)).getString("songName");
                final int type = ((JSONObject) array.get(i)).getInt("type");
                final int radius = ((JSONObject) array.get(i)).getInt("radius");
                final double longitude = ((JSONObject) array.get(i)).getDouble("longitude");
                final double latitude= ((JSONObject) array.get(i)).getDouble("latitude");
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

    }
}
