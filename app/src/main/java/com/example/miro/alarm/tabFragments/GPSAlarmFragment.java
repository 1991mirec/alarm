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
import com.example.miro.alarm.inteligentAlarm.adapters.GpsAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.main.GpsAlarmSettingActivity;

import org.json.JSONException;

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
                gpsSettings.addAll(Utils.loadSharedPreferancesWithGPSAlarmSettings(context));
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
            boolean wasOn = false;
            if (gpsSettings.get(id).isOn()) {
                wasOn = true;
            }
            gpsSettings.get(id).setAlarm(gpsSettingsReturned, true);
            gpsSettings.get(id).startPositionCheck(wasOn);
            refresh();
        }
    }

    @Override
    public void removeButton(int id) {
        gpsSettings.remove(id);
        try {
            Utils.updateAndSaveSharedPreferancesWithGPSAlarmSettings(getContext(),
                    gpsSettings);
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
            Utils.updateAndSaveSharedPreferancesWithGPSAlarmSettings(getContext(), gpsSettings);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public static void cancel(final GPSAlarmSettingsImpl settings, final int id) {
        if (gpsSettings.get(id) != null) {
            gpsSettings.get(id).setAlarm(settings, false);
            gpsSettings.get(id).updateVisuals();
        }
    }
}
