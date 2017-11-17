package com.example.miro.alarm.tabFragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

/**
 * Created by Miro on 11/22/2016.
 */

public class POIAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/

        ((ViewGroup) rootView).addView(initiateButtons());
        return rootView;
    }

    @Override
    public LinearLayout initiateButtons() {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Set the layout full width, full height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        if (poiSettings.isEmpty()) {
            final POIAlarmSettingsImpl poiAlarmSettings = new POIAlarmSettingsImpl(1, "mySong",
                    "Banka", Type.BOTH, new Postpone(1, 1, false), false, new Repeat());

            linearLayout.addView(createButton(poiAlarmSettings.getName()));
            poiSettings.add(poiAlarmSettings);
        } else {
            for (final POIAlarmSettingsImpl settings : poiSettings) {
                linearLayout.addView(createButton(settings.getName()));
            }
        }
        return linearLayout;
    }

    @Override
    public Button createButton(final String name) {
        final Button button = new Button(getActivity());
        button.setLayoutParams(new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        button.setText(name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return button;
    }

    @Override
    public void removeButton(int id) {
        gpsSettings.remove(id);
    }
}