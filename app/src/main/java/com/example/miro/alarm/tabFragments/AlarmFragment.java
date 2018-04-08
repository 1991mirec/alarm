package com.example.miro.alarm.tabFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.main.TimeAlarmSettingActivity;
import com.google.common.base.Preconditions;

import org.json.JSONException;

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
                timeSettings.addAll(Utils.loadSharedPreferancesWithAlarmSettingsClock(context));
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
                intent.putExtra("timeSettings", timeSettings.get(position));
                startActivityForResult(intent, REQ_CODE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeButton((int) id);
                refresh();
                return true;
            }
        });
        listView.setAdapter(adapter);
        return null;
    }

    @Override
    public void removeButton(final int id) {
        timeSettings.remove(id);
        try {
            Utils.updateAndSaveSharedPreferancesWithAlarmSettingsClock(getContext(), timeSettings);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Preconditions.checkNotNull( data.getExtras());
            final TimeAlarmSettingsImpl timeSettingsReturned = (TimeAlarmSettingsImpl) data.getExtras()
                    .getSerializable("timeSettings");
            Preconditions.checkNotNull(timeSettingsReturned);
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
            Utils.updateAndSaveSharedPreferancesWithAlarmSettingsClock(getContext(), timeSettings);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public static void cancel(final TimeAlarmSettingsImpl settings, final int id) {
        if (timeSettings.get(id) != null) {
            timeSettings.get(id).setAlarm(settings, false);
            timeSettings.get(id).updateVisuals();
        }
    }

}
