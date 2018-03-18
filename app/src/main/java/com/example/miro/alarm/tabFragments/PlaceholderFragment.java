package com.example.miro.alarm.tabFragments;

import android.support.v4.app.Fragment;

import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miro on 11/22/2016.
 */

public class PlaceholderFragment extends Fragment {

    protected static final List<TimeAlarmSettingsImpl> timeSettings = new ArrayList<>();
    protected static final List<GPSAlarmSettingsImpl> gpsSettings = new ArrayList<>();
    protected static final List<POIAlarmSettingsImpl> poiSettings = new ArrayList<>();
    protected static final List<ContactAlarmSettingsImpl> contactSettings = new ArrayList<>();

    protected static final String PREFS_NAME = "MyPrefsFile";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(PlaceholderFragment fragment) {
        return fragment;
    }
}
