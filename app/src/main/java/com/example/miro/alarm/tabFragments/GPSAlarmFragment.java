package com.example.miro.alarm.tabFragments;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.example.miro.alarm.receiver.GPSAlarmReceiver;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Miro on 11/22/2016.
 */

public class GPSAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    private Context context;
    private View rootView;
    private static final int REQ_CODE = 79;
    private static final int REQ_CODE_WAKE_UP = 70;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
            final Intent intent = setUpIntent(gpsSettings.get(id));
            final FusedLocationProviderClient f = new FusedLocationProviderClient(context);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    REQ_CODE_WAKE_UP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Utils.requestAccessFinePermissions(getActivity());
                return;
            }
            LocationRequest lr = new LocationRequest();
            lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            f.requestLocationUpdates(lr, pendingIntent);
            refresh();
        }
    }

    private Intent setUpIntent(final GPSAlarmSettingsImpl gpsSettings) {
        ComponentName receiver = new ComponentName(context, GPSAlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        final Intent intent = new Intent(context, GPSAlarmReceiver.class);
        //If I add following I will not receiver my current position
        /*intent.putExtra("isNormal", "normal");
        intent.putExtra("name", gpsSettings.getName());
        intent.putExtra("volume", gpsSettings.getVolume());
        intent.putExtra("latitude", gpsSettings.getCoordinates().latitude);
        intent.putExtra("longitude", gpsSettings.getCoordinates().longitude);
        intent.putExtra("radius", gpsSettings.getRadius());
        intent.putExtra("postponeonoff", gpsSettings.getPostpone().isOn());
        if (gpsSettings.getPostpone().isOn()) {
            intent.putExtra("repeat_times", gpsSettings.getPostpone().getTimesOfRepeat());
        }
        intent.putExtra("type", gpsSettings.getType().getType());
        intent.putExtra("nameOfSong", gpsSettings.getSong().getName());
        intent.putExtra("id", gpsSettings.getId());*/

        intent.setAction("intentGPS" + gpsSettings.getId());
        return intent;
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
