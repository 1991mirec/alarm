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
import com.example.miro.alarm.inteligentAlarm.adapters.TimeAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.InteligentAlarm;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.main.TimeAlarmSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Miro on 11/22/2016.
 */

public class AlarmFragment extends PlaceholderFragment implements FragmentSetter {

    private Context context;
    private View rootView;
    private static final int REQ_CODE = 69;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if (timeSettings.size() == 0) {
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
        final TimeAlarmSettingsImpl timeAlarmSettings = new TimeAlarmSettingsImpl(context,
                timeSettings.size());
        timeSettings.add(timeAlarmSettings);

        refresh();
    }

    @Override
    public String toString() {
        return "" + timeSettings.size();
    }

    @Override
    public LinearLayout initiateButtons() {
        final ListView listView = (ListView) rootView.findViewById(R.id.listViewAlarm);

        if (timeSettings.isEmpty()) {
            addButton();
        }

        final TimeAlarmAdapter adapter = new TimeAlarmAdapter(context, R.layout.alarm_buttons,
                timeSettings.toArray(new TimeAlarmSettingsImpl[timeSettings.size()]));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TimeAlarmSettingActivity.class);
                intent.putExtra("timeSetting", timeSettings.get(position));
                startActivityForResult(intent, REQ_CODE);
            }
        });
        listView.setAdapter(adapter);
        return null;
    }

    @Override
    public void removeButton(final int id) {
        timeSettings.remove(id);
        try {
            updateAndSaveSharedPreferancesWithAlarmSettings(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            final TimeAlarmSettingsImpl timeSettingsReturned = (TimeAlarmSettingsImpl) data.getExtras()
                    .getSerializable("timeSettings");

            final int id = timeSettingsReturned.getId();
            timeSettings.get(id).setAlarm(timeSettingsReturned, true);
            timeSettings.get(id).setAlarmManager();

            refresh();
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

    public static void cancel(final int id, final Repeat repeatDays, final int houre,
                              final int minute, final boolean isNormal) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, houre);
        time.set(Calendar.MINUTE, minute);
        final long finalTime = TimeAlarmSettingsImpl.getTimeInMilis(repeatDays, time);
        if (timeSettings.get(id) != null) {
            timeSettings.get(id).cancelAlarmOrRestart(true, finalTime, isNormal);
        }
    }

    public static void updateAndSaveSharedPreferancesWithAlarmSettings(final Context context)
            throws JSONException {
        final JSONArray listOfSettings = new JSONArray();
        for(final TimeAlarmSettingsImpl settings: timeSettings){
            final JSONObject obj = new JSONObject();
            obj.put("time", settings.getTime().getTimeInMillis());
            obj.put("name", settings.getName());
            obj.put("songName", settings.getSong().getName());
            obj.put("type", settings.getType().getType().ordinal());
            final JSONObject repeatObj = new JSONObject();
            repeatObj.put("monday", settings.getRepeat().getDays()[1]);
            repeatObj.put("tuesday", settings.getRepeat().getDays()[2]);
            repeatObj.put("wednesday", settings.getRepeat().getDays()[3]);
            repeatObj.put("thursday", settings.getRepeat().getDays()[4]);
            repeatObj.put("friday", settings.getRepeat().getDays()[5]);
            repeatObj.put("saturday", settings.getRepeat().getDays()[6]);
            repeatObj.put("sunday", settings.getRepeat().getDays()[0]);
            obj.put("repeat", repeatObj);
            obj.put("volume", settings.getVolume());
            final JSONObject postpone = new JSONObject();
            postpone.put("isOn", settings.getPostpone().isOn());
            postpone.put("minutes", settings.getPostpone().getMinutes());
            postpone.put("timesOfRepeat", settings.getPostpone().getTimesOfRepeat());
            obj.put("postpone", postpone);
            obj.put("isOn", settings.isOn());
            final JSONObject inteligentAlarm = new JSONObject();
            inteligentAlarm.put("isOn", settings.getInteligentAlarm().isOn());
            inteligentAlarm.put("songName", settings.getInteligentAlarm().getSong().getName());
            inteligentAlarm.put("minutesBeforeReal", settings.getInteligentAlarm()
                    .getTimeBeforeRealAlaram());
            obj.put("inteligentAlarm", inteligentAlarm);
            listOfSettings.put(settings.getId(), obj);
        }
        final JSONObject mainObj = new JSONObject();
        mainObj.put("timeAlarmSettings", listOfSettings);
        final String stringToSave = mainObj.toString();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("timeSettings", stringToSave).apply();
    }

    private void loadSharedPreferancesWithAlarmSettings() throws JSONException {
        final SharedPreferences sharedPreferences = getContext().getApplicationContext()
                .getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        final String main = sharedPreferences.getString("timeSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONArray array = mainObject.getJSONArray("timeAlarmSettings");
            for (int i = 0, size = array.length(); i < size; i++)
            {
                final long time = ((JSONObject) array.get(i)).getLong("time");
                final String name = ((JSONObject) array.get(i)).getString("name");
                final String songName = ((JSONObject) array.get(i)).getString("songName");
                final int type = ((JSONObject) array.get(i)).getInt("type");
                final int volume = ((JSONObject) array.get(i)).getInt("volume");
                final boolean isOn = ((JSONObject) array.get(i)).getBoolean("isOn");
                final JSONObject repeat = ((JSONObject) array.get(i)).getJSONObject("repeat");
                final Repeat repeatObj = new Repeat();
                repeatObj.setDays(1, repeat.getBoolean("monday"));
                repeatObj.setDays(2, repeat.getBoolean("tuesday"));
                repeatObj.setDays(3, repeat.getBoolean("wednesday"));
                repeatObj.setDays(4, repeat.getBoolean("thursday"));
                repeatObj.setDays(5, repeat.getBoolean("friday"));
                repeatObj.setDays(6, repeat.getBoolean("saturday"));
                repeatObj.setDays(0, repeat.getBoolean("sunday"));

                final JSONObject postpone = ((JSONObject) array.get(i)).getJSONObject("postpone");
                final boolean postponeIsOn = postpone.getBoolean("isOn");
                final int postponeMinutes = postpone.getInt("minutes");
                final int postponeTimes = postpone.getInt("timesOfRepeat");
                final Postpone postponeObj = new Postpone(postponeTimes, postponeMinutes,
                        postponeIsOn);
                final JSONObject IntelligentAlarmObj = ((JSONObject) array.get(i))
                        .getJSONObject("inteligentAlarm");
                final boolean intelligentIsOn = IntelligentAlarmObj.getBoolean("isOn");
                final String intelligentSongName = IntelligentAlarmObj.getString("songName");
                final int intelligentMinutesBeforeReal = IntelligentAlarmObj.getInt("minutesBeforeReal");
                final InteligentAlarm inteligentAlarm = new InteligentAlarm(intelligentSongName,
                        intelligentMinutesBeforeReal, intelligentIsOn);
                final Calendar calTime = Calendar.getInstance();
                calTime.setTimeInMillis(time);
                final TimeAlarmSettingsImpl timeSettingsReturned =
                        new TimeAlarmSettingsImpl(getContext(), i, calTime, inteligentAlarm,
                                name, volume, isOn, type, songName, repeatObj, postponeObj);
                timeSettings.add(timeSettingsReturned);
            }
        }

    }

}
