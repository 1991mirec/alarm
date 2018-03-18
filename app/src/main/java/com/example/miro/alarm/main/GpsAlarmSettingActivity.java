package com.example.miro.alarm.main;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.dialog.DaysDialogFragment;
import com.example.miro.alarm.dialog.TypeDialogFragment;
import com.example.miro.alarm.inteligentAlarm.adapters.GpsAlarmSettingsAdapter;
import com.example.miro.alarm.inteligentAlarm.adapters.map.MapsActivity;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;

import java.io.Serializable;

/**
 * Created by Miro on 11/23/2016.
 */

public class GpsAlarmSettingActivity extends FragmentActivity implements View.OnClickListener,
        Serializable, ListView.OnItemClickListener, DaysDialogFragment.DaysDialogListener, TypeDialogFragment.TypesDialogListener {

    private GPSAlarmSettingsImpl settings;
    private GpsAlarmSettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        final Intent intent = getIntent();
        settings = (GPSAlarmSettingsImpl) intent.getExtras().getSerializable("gpsSettings");
        adapter = new GpsAlarmSettingsAdapter(settings);

        final ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        adapter.refresh(settings);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                final Intent intent = new Intent(parent.getContext(), MapsActivity.class);
                intent.putExtra("setting", settings);
                ((Activity) parent.getContext()).startActivityForResult(intent, 22);
                break;
            case 1:
                showDialogTypes();
                break;
        }

    }

    private void showDialogTypes() {
        // Create an instance of the dialog fragment and show it
        final DialogFragment dialog = new TypeDialogFragment(settings.getType());
        dialog.show(getFragmentManager(), "TypesDialogFragment");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                setResult(RESULT_CANCELED);
                super.finish();
                break;
            case R.id.button3:
                Intent output = new Intent();
                output.putExtra("gpsSettings", settings);
                setResult(RESULT_OK, output);
                super.finish();
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        adapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof TypeDialogFragment) {
            settings.setType(((TypeDialogFragment) dialog).getType());
            adapter.refresh(settings);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
