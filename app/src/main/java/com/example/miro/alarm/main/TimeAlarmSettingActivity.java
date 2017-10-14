package com.example.miro.alarm.main;

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
import com.example.miro.alarm.inteligentAlarm.adapters.TimeAlarmSettingsAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;

import java.io.Serializable;

/**
 * Created by Miro on 11/23/2016.
 */

public class TimeAlarmSettingActivity extends FragmentActivity implements View.OnClickListener,
        Serializable, ListView.OnItemClickListener, DaysDialogFragment.DaysDialogListener, TypeDialogFragment.TypesDialogListener {

    private TimeAlarmSettingsImpl settings;
    private TimeAlarmSettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        final Intent intent = getIntent();
        settings = (TimeAlarmSettingsImpl) intent.getExtras().getSerializable("timeSetting");
        adapter = new TimeAlarmSettingsAdapter(settings);

        final ListView listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(this);
        adapter.refresh(settings);
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
                output.putExtra("timeSettings", settings);
                setResult(RESULT_OK, output);
                super.finish();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 1:
                showDialogDays();
                break;
            case 2:
                showDialogTypes();

        }
    }

    private void showDialogTypes() {
        // Create an instance of the dialog fragment and show it
        final DialogFragment dialog = new TypeDialogFragment(settings.getType());
        dialog.show(getFragmentManager(), "TypesDialogFragment");
    }

    public void showDialogDays() {
        // Create an instance of the dialog fragment and show it
        final DialogFragment dialog = new DaysDialogFragment(settings.getRepeat());
        dialog.show(getFragmentManager(), "DaysDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof DaysDialogFragment) {
            settings.setRepeat(((DaysDialogFragment) dialog).getRepeat());
            adapter.refresh(settings);
        } else if (dialog instanceof TypeDialogFragment) {
            settings.setType(((TypeDialogFragment) dialog).getType());
            adapter.refresh(settings);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
