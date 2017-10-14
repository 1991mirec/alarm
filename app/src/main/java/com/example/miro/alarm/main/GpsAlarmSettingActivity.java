package com.example.miro.alarm.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.GpsAlarmSettingsAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;

import java.io.Serializable;

/**
 * Created by Miro on 11/23/2016.
 */

public class GpsAlarmSettingActivity extends FragmentActivity implements View.OnClickListener,
        Serializable, ListView.OnItemClickListener {

    private GPSAlarmSettingsImpl settings;
    private GpsAlarmSettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        final Intent intent = getIntent();
        settings = (GPSAlarmSettingsImpl) intent.getExtras().getSerializable("gpsSetting");
        adapter = new GpsAlarmSettingsAdapter(settings);

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
                output.putExtra("gpsSettings", settings);
                setResult(RESULT_OK, output);
                super.finish();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
