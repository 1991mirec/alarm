package com.example.miro.alarm.tabFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.miro.alarm.inteligentAlarm.adapters.PoiAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;
import com.example.miro.alarm.main.PoiAlarmSettingActivity;

import java.util.Set;

/**
 * Created by Miro on 11/22/2016.
 */

public class POIAlarmFragment extends PlaceholderFragment implements FragmentSetter {

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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        addButton();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public LinearLayout initiateButtons() {
        final ListView listView = (ListView) rootView.findViewById(R.id.listViewAlarm);

        if (poiSettings.isEmpty()) {
            addButton();
        }

        final PoiAlarmAdapter adapter = new PoiAlarmAdapter(context, R.layout.poi_buttons,
                poiSettings.toArray(new POIAlarmSettingsImpl[poiSettings.size()]));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PoiAlarmSettingActivity.class);
                intent.putExtra("poiSetting", poiSettings.get(position));
                startActivityForResult(intent, REQ_CODE);
            }
        });
        listView.setAdapter(adapter);
        return null;
    }

    @Override
    public void addButton() {
        final POIAlarmSettingsImpl poiAlarmSettings = new POIAlarmSettingsImpl(context,
                poiSettings.size());
        poiSettings.add(poiAlarmSettings);

        refresh();
    }

    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void removeButton(int id) {
        poiSettings.remove(id);
    }

}
