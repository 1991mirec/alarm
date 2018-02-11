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
import com.example.miro.alarm.inteligentAlarm.adapters.PoiAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.main.PoiAlarmSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Miro on 11/22/2016.
 */

public class POIAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    private Context context;
    private View rootView;
    private static final int REQ_CODE = 79;
    private static final int REQ_CODE_WAKE_UP = 70;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if (poiSettings.size() == 0) {
            try {
                loadSharedPreferancesWithAlarmSettings();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initiateButtons();

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        addButton();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            final POIAlarmSettingsImpl poiSettingsReturned = (POIAlarmSettingsImpl) data.getExtras()
                    .getSerializable("poiSettings");

            final int id = poiSettingsReturned.getId();
            poiSettings.get(id).setAlarm(poiSettingsReturned);
            refresh();
        }
    }

    @Override
    public LinearLayout initiateButtons() {
        final ListView listView = (ListView) rootView.findViewById(R.id.listViewAlarm);

        if (poiSettings.isEmpty()) {
            addButton();
        }

        final PoiAlarmAdapter adapter = new PoiAlarmAdapter(context, R.layout.poi_buttons,
                poiSettings.toArray(new POIAlarmSettingsImpl[poiSettings.size()]));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PoiAlarmSettingActivity.class);
                intent.putExtra("poiSettings", poiSettings.get(position));
                startActivityForResult(intent, REQ_CODE);
            }
        });
        listView.setAdapter(adapter);
        return null;
    }

    @Override
    public void addButton() {
        final POIAlarmSettingsImpl poiAlarmSettings = new POIAlarmSettingsImpl(context,
                poiSettings.size());
        poiSettings.add(poiAlarmSettings);

        refresh();
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

    @Override
    public void removeButton(int id) {
        poiSettings.remove(id);
        try {
            updateAndSaveSharedPreferancesWithAlarmSettings(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateAndSaveSharedPreferancesWithAlarmSettings(final Context context)
            throws JSONException {
        final JSONArray listOfSettings = new JSONArray();
        for(final POIAlarmSettingsImpl settings: poiSettings){
            final JSONObject obj = new JSONObject();
            obj.put("name", settings.getName());
            obj.put("songName", settings.getSong().getName());
            obj.put("type", settings.getType().getType().ordinal());
            obj.put("radius", settings.getRadius());
            obj.put("volume", settings.getVolume());
            obj.put("distanceType", settings.getDistanceType());
            final JSONObject postpone = new JSONObject();
            postpone.put("isOn", settings.getPostpone().isOn());
            postpone.put("minutes", settings.getPostpone().getMinutes());
            postpone.put("timesOfRepeat", settings.getPostpone().getTimesOfRepeat());
            obj.put("postpone", postpone);
            obj.put("isOn", settings.isOn());
            obj.put("poiType", settings.getPoiType());
            listOfSettings.put(settings.getId(), obj);
        }
        final JSONObject mainObj = new JSONObject();
        mainObj.put("poiAlarmSettings", listOfSettings);
        final String stringToSave = mainObj.toString();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("poiSettings", stringToSave).apply();
    }

    private void loadSharedPreferancesWithAlarmSettings() throws JSONException {
        final SharedPreferences sharedPreferences = getContext().getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String main = sharedPreferences.getString("poiSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONArray array = mainObject.getJSONArray("poiAlarmSettings");
            for (int i = 0, size = array.length(); i < size; i++) {
                final String name = ((JSONObject) array.get(i)).getString("name");
                final String songName = ((JSONObject) array.get(i)).getString("songName");
                final int type = ((JSONObject) array.get(i)).getInt("type");
                final int volume = ((JSONObject) array.get(i)).getInt("volume");
                final int radius = ((JSONObject) array.get(i)).getInt("radius");
                final boolean isOn = ((JSONObject) array.get(i)).getBoolean("isOn");
                final String poiTypeObj = ((JSONObject) array.get(i)).getString("poiType");
                final JSONObject postpone = ((JSONObject) array.get(i)).getJSONObject("postpone");
                final boolean postponeIsOn = postpone.getBoolean("isOn");
                final int postponeMinutes = postpone.getInt("minutes");
                final int postponeTimes = postpone.getInt("timesOfRepeat");
                final String distanceType = ((JSONObject) array.get(i)).getString("distanceType");
                final Postpone postponeObj = new Postpone(postponeTimes, postponeMinutes,
                        postponeIsOn);
                final POIAlarmSettingsImpl contactSettingsReturned =
                        new POIAlarmSettingsImpl(context, i, name, volume, isOn, type, songName,
                                postponeObj, poiTypeObj, radius, distanceType);
                poiSettings.add(contactSettingsReturned);
            }
        }
    }

}
