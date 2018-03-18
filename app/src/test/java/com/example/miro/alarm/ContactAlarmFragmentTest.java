package com.example.miro.alarm;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.inteligentAlarm.helper.TypeImpl;
import com.example.miro.alarm.main.TimeAlarmSettingActivity;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.robolectric.Robolectric.buildActivity;

/**
 * Created by Miro on 3/10/2018.
 */
@RunWith(RobolectricTestRunner.class)
public class ContactAlarmFragmentTest {

    private ActivityController<TimeAlarmSettingActivity> timeAlarmSettingActivityActivityController;
    @Mock
    private TimeAlarmSettingsImpl timeAlarmSettings;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        final Repeat repeat = new Repeat();
        Mockito.when(timeAlarmSettings.getRepeat()).thenReturn(repeat);
        final TypeImpl type = new TypeImpl(Type.BOTH);
        Mockito.when(timeAlarmSettings.getType()).thenReturn(type);
        Intent intent = new Intent();
        intent.putExtra("timeSettings", timeAlarmSettings);

        timeAlarmSettingActivityActivityController = buildActivity(TimeAlarmSettingActivity.class, intent).create();
    }

    @Test
    public void onItemRepeatClick() throws Exception {
        final ListView listView = (ListView) timeAlarmSettingActivityActivityController.get().findViewById(R.id.listView1);
        listView.getAdapter().getView(1, null, listView);
        listView.performItemClick((View) listView.getAdapter().getItem(1), 1,
                listView.getAdapter().getItemId(1));
        Mockito.verify(timeAlarmSettings, Mockito.atLeastOnce()).getRepeat();
        Mockito.verify(timeAlarmSettings, Mockito.never()).getType();
    }

    @Test
    public void onItemTypeClick() throws Exception {
        final ListView listView = (ListView) timeAlarmSettingActivityActivityController.get().findViewById(R.id.listView1);
        listView.getAdapter().getView(2, null, listView);
        listView.performItemClick((View) listView.getAdapter().getItem(2), 2,
                listView.getAdapter().getItemId(2));
        Mockito.verify(timeAlarmSettings, Mockito.atLeastOnce()).getType();
        Mockito.verify(timeAlarmSettings, Mockito.never()).getRepeat();

    }

    @Test
    public void onClickOKCancelTest() throws Exception {
        Button button = (Button) timeAlarmSettingActivityActivityController.get().findViewById(R.id.button3);
        button.performClick();
        Assert.assertTrue(timeAlarmSettingActivityActivityController.get().isFinishing());
        button = (Button) timeAlarmSettingActivityActivityController.get().findViewById(R.id.button1);
        button.performClick();
        Assert.assertTrue(timeAlarmSettingActivityActivityController.get().isFinishing());
    }
}
