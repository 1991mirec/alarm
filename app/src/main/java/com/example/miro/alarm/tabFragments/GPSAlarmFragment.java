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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.GpsAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;
import com.example.miro.alarm.main.GpsAlarmSettingActivity;

/**
 * Created by Miro on 11/22/2016.
 */

public class GPSAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    private Context context;
    private View rootView;
    private Activity activity;
    private static final int REQ_CODE = 79;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initiateButtons();
        activity = getActivity();

        return rootView;
    }


    private void addButton() {
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
                intent.putExtra("gpsSetting", gpsSettings.get(position));
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
            //gpsSettings.get(id).setAlarmManager();

            refresh();
        }
    }

    @Override
    public Button createButton(final String name) {return null;
        /*final Button button = new Button(getActivity());
        button.setLayoutParams(new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        button.setText(name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return button;*/
    }

    @Override
    public void removeButton(int id) {
        gpsSettings.remove(id);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        addButton();
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
