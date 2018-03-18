package com.example.miro.alarm.inteligentAparm.AlarmSettings.impl;

import android.content.Context;
import android.content.Intent;

import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.TimeAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.InteligentAlarm;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;
import java.util.Calendar;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by Miro on 3/9/2018.
 */
@RunWith(RobolectricTestRunner.class)
public class TimeApalrmSettingsImplTest {

    private Method method;
    private TimeAlarmSettingsImpl timeAlarmSettings;
    private TimeAlarmSettingsImpl timeAlarmSettings2;
    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(context.getString(anyInt())).thenReturn("Wake up!!!");
        when(context.getPackageName()).thenReturn(this.getClass().getPackage().getName());
        int id = 1;
        //Initialize with default settings
        timeAlarmSettings = new TimeAlarmSettingsImpl(context, id);
        //Initialize with preset settings
        id = 2;
        final InteligentAlarm inteligentAlarm = new InteligentAlarm("song_name", 5, false);
        final Repeat repeat = new Repeat();
        repeat.setDays(1, true);
        timeAlarmSettings2 = new TimeAlarmSettingsImpl(context, id, Calendar.getInstance(),
                inteligentAlarm, "Alarm name", 2, false, 2, "song_name6", repeat,
                new Postpone(4, 5, true));
    }

    @Test
    public void setAlarmTest() throws Exception {
        timeAlarmSettings.setAlarm(timeAlarmSettings2, timeAlarmSettings2.isOn());
        Assert.assertEquals(timeAlarmSettings.getName(), timeAlarmSettings2.getName());
        Assert.assertEquals(timeAlarmSettings.getTime(), timeAlarmSettings2.getTime());
        Assert.assertNotEquals(timeAlarmSettings.getId(), timeAlarmSettings2.getId());
    }

    @Test
    public void getTimeInMillisTest() throws Exception {
        Repeat repeat = Mockito.mock(Repeat.class);
        Mockito.when(repeat.isNoDay()).thenReturn(true);
        final Calendar instance = Calendar.getInstance();
        final Calendar instanceCompare = Calendar.getInstance();
        final long timeInMilis = TimeAlarmSettingsImpl.getTimeInMilis(repeat, instance);
        instanceCompare.setTimeInMillis(timeInMilis);
        Assert.assertEquals(instance.get(Calendar.DAY_OF_MONTH),
                instanceCompare.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void setUpInteligentIntentTest() throws Exception {
        method = TimeAlarmSettingsImpl.class.getDeclaredMethod("setUpInteligentIntent");
        method.setAccessible(true);
        Intent intent = (Intent) method.invoke(timeAlarmSettings);
        Assert.assertNotNull(intent.getAction());
        Assert.assertNotNull(intent.getExtras());
        Assert.assertFalse(intent.getExtras().isEmpty());
        Assert.assertNotEquals("normal", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("inteligent", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("Wake up!!!intel", intent.getExtras().getString("name"));
        Assert.assertEquals(1, intent.getExtras().getInt("id"));
        Assert.assertEquals("loud_alarm_buzzer.mp3", intent.getExtras().getString("nameOfSong"));
        Assert.assertEquals("intent_intelligent1", intent.getAction());

        intent = (Intent) method.invoke(timeAlarmSettings2);
        Assert.assertNotNull(intent.getAction());
        Assert.assertNotNull(intent.getExtras());
        Assert.assertFalse(intent.getExtras().isEmpty());
        Assert.assertNotEquals("normal", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("inteligent", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("Alarm nameintel", intent.getExtras().getString("name"));
        Assert.assertEquals(2, intent.getExtras().getInt("id"));
        Assert.assertEquals("song_name", intent.getExtras().getString("nameOfSong"));
        Assert.assertEquals("intent_intelligent2", intent.getAction());
    }

    @Test
    public void setUpIntentTest() throws Exception {
        method = TimeAlarmSettingsImpl.class.getDeclaredMethod("setUpIntent");
        method.setAccessible(true);
        Intent intent = (Intent) method.invoke(timeAlarmSettings);
        Assert.assertNotNull(intent.getAction());
        Assert.assertNotNull(intent.getExtras());
        Assert.assertFalse(intent.getExtras().isEmpty());
        Assert.assertNotEquals("inteligent", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("normal", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("Wake up!!!", intent.getExtras().getString("name"));
        Assert.assertEquals(1, intent.getExtras().getInt("id"));
        Assert.assertEquals(1, intent.getExtras().getInt("volume"));
        Assert.assertEquals(false, intent.getExtras().getBoolean("postponeonoff"));
        Assert.assertEquals("loud_alarm_buzzer.mp3", intent.getExtras().getString("nameOfSong"));
        Assert.assertEquals(0, intent.getExtras().getInt("repeat_times"));
        Assert.assertEquals(2, intent.getExtras().getInt("type"));
        Assert.assertTrue(intent.getExtras().getInt("houre") <=
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        Assert.assertTrue(intent.getExtras().getInt("minute") <=
                Calendar.getInstance().get(Calendar.MINUTE));
        Assert.assertEquals("intent1", intent.getAction());

        intent = (Intent) method.invoke(timeAlarmSettings2);
        Assert.assertNotNull(intent.getAction());
        Assert.assertNotNull(intent.getExtras());
        Assert.assertFalse(intent.getExtras().isEmpty());
        Assert.assertNotEquals("inteligent", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("normal", intent.getExtras().getString("isNormal"));
        Assert.assertEquals("Alarm name", intent.getExtras().getString("name"));
        Assert.assertEquals(2, intent.getExtras().getInt("id"));
        Assert.assertEquals(2, intent.getExtras().getInt("volume"));
        Assert.assertEquals(true, intent.getExtras().getBoolean("postponeonoff"));
        Assert.assertEquals("song_name6", intent.getExtras().getString("nameOfSong"));
        Assert.assertEquals(4, intent.getExtras().getInt("repeat_times"));
        Assert.assertEquals(2, intent.getExtras().getInt("type"));
        Assert.assertTrue(intent.getExtras().getInt("houre") <=
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        Assert.assertTrue(intent.getExtras().getInt("minute") <=
                Calendar.getInstance().get(Calendar.MINUTE));
        Assert.assertEquals("intent2", intent.getAction());
    }

    @After
    public void tearDown() throws Exception {
        if (method != null) {
            method.setAccessible(false);
            method = null;
        }
    }

}
